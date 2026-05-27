package server.network;

import common.network.Req;
import common.network.Response;
import server.managers.CommandManager;

import java.io.*;
import java.net.*;
import java.nio.channels.DatagramChannel;
import java.nio.ByteBuffer;

public class RequestHandler {
    private final int PORT = 12345;
    private final CommandManager commandManager;
    public RequestHandler(CommandManager commandManager) {
        this.commandManager = commandManager;
    }
    public void start() {
        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(PORT));
            channel.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(65535);
            System.out.println("Сервер запущен...");
            while (true) {
                buffer.clear();
                SocketAddress clientAddress = channel.receive(buffer);
                if (clientAddress == null) continue;
                buffer.flip();
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                Req req = deserialize(data);
                Response response = commandManager.execute(req.getCommandName(), req);
                byte[] responseData = serialize(response);
                ByteBuffer responseBuffer = ByteBuffer.wrap(responseData);
                channel.send(responseBuffer, clientAddress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Req deserialize(byte[] data) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        return (Req) ois.readObject();
    }
    private byte[] serialize(Response response) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(response);
        oos.flush();
        return baos.toByteArray();
    }
}