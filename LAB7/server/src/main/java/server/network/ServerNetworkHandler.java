package server.network;

import common.network.Req;
import common.network.Response;
import server.managers.CollectionManager;
import server.managers.CommandManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerNetworkHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ServerNetworkHandler.class);
    private static final int PORT = 12345;
    private static final int BUFFER_SIZE = 65535;
    private final CollectionManager collectionManager;
    private final CommandManager commandManager;

    private DatagramChannel channel;
    private Selector selector;
    private volatile boolean running = true;
    private final ExecutorService readPool = Executors.newFixedThreadPool(5);
    private final ExecutorService processPool = Executors.newCachedThreadPool();
    public ServerNetworkHandler(CollectionManager collectionManager, CommandManager commandManager) {
        this.collectionManager = collectionManager;
        this.commandManager = commandManager;
    }
    @Override
    public void run() {
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.bind(new InetSocketAddress(PORT));
            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);
            logger.info("Сервер запущен на порту: " + PORT);
            while (running) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isReadable()) {
                        readPool.submit(() -> handleRead(key));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Critical network error: " + e.getMessage(), e);
        }
    }
    private void handleRead(SelectionKey key) {
        try {
            DatagramChannel currentChannel = (DatagramChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            InetSocketAddress clientAddress = (InetSocketAddress) currentChannel.receive(buffer);
            if (clientAddress != null) {
                logger.info("Read pool: received packet from " + clientAddress);
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                Object obj = deserializeSafe(data);
                if (obj instanceof Req request) {
                    processPool.submit(() -> handleProcess(request, clientAddress));
                } else {
                    logger.warn("Read pool: ignored packet of type: {}", obj != null ? obj.getClass().getSimpleName() : "null");
                }
            }
        } catch (Exception e) {
            logger.error("Read pool error: " + e.getMessage());
        }
    }
    private void handleProcess(Req request, InetSocketAddress clientAddress) {
        try {
            logger.info("Process pool: executing command '{}' from {}", request.getCommandName(), clientAddress);
            Response response = commandManager.execute(request.getCommandName(), request);
            new Thread(() -> handleSend(response, clientAddress), "send-thread-" + clientAddress).start();
        } catch (Exception e) {
            logger.error("Process pool error: " + e.getMessage());
            Response errorResponse = new Response("Server error: " + e.getMessage());
            new Thread(() -> handleSend(errorResponse, clientAddress), "error-send-thread-" + clientAddress).start();
        }
    }
    private void handleSend(Response response, InetSocketAddress clientAddress) {
        try {
            byte[] responseData = serialize(response);
            ByteBuffer responseBuffer = ByteBuffer.wrap(responseData);
            channel.send(responseBuffer, clientAddress);
            logger.info("Send thread: reply sent to " + clientAddress);
        } catch (Exception e) {
            logger.error("Send thread error: " + e.getMessage());
        }
    }
    private byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        return baos.toByteArray();
    }
    private Object deserializeSafe(byte[] data) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();
            ois.close();
            return obj;
        } catch (Exception e) {
            logger.debug("Deserialization failed: {}", e.getMessage());
            return null;
        }
    }
    public void close() {
        running = false;
        try {
            if (selector != null) selector.wakeup();
            if (selector != null) selector.close();
            if (channel != null) channel.close();
        } catch (Exception e) {
            logger.error("Error closing network handler: " + e.getMessage());
        }
        readPool.shutdown();
        processPool.shutdown();
    }
}