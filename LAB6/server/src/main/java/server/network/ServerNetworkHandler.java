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

public class ServerNetworkHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ServerNetworkHandler.class);
    private static final int PORT = 12345;
    private static final int BUFFER_SIZE = 65535;
    private final CollectionManager collectionManager;
    private final CommandManager commandManager;
    private DatagramChannel channel;
    private Selector selector;
    private volatile boolean running = true;
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
            logger.info("Сервер запущен на порту " + PORT);
            while (running) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка в сетевом обработчике", e);
        }
    }
    private void handleRead(SelectionKey key) {
        try {
            DatagramChannel channel = (DatagramChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            InetSocketAddress clientAddress = (InetSocketAddress) channel.receive(buffer);
            if (clientAddress != null) {
                logger.info("Получен запрос от " + clientAddress);
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                Req request = deserialize(data);
                Response response = commandManager.execute(
                        request.getCommandName(),
                        request
                );
                byte[] responseData = serialize(response);
                ByteBuffer responseBuffer = ByteBuffer.wrap(responseData);
                channel.send(responseBuffer, clientAddress);
                logger.info("Ответ отправлен клиенту " + clientAddress);
            }
        } catch (Exception e) {
            logger.error("Ошибка обработки запроса", e);
        }
    }
    private byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        return baos.toByteArray();
    }
    private Req deserialize(byte[] data) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Req request = (Req) ois.readObject();
        ois.close();
        return request;
    }
    public void close() {
        running = false;
        try {
            if (selector != null) selector.close();
            if (channel != null) channel.close();
        } catch (Exception e) {
            logger.error("Ошибка при закрытии канала", e);
        }
    }
}