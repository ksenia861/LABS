package client.network;

import common.models.City;
import common.network.Req;
import common.network.Response;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ClientNetworkHandler {
    private static final int PORT = 12345;
    private static final int TIMEOUT = 1000000000;
    private static final int BUFFER_SIZE = 65535;
    private DatagramSocket socket;
    private InetAddress serverAddress;
    public ClientNetworkHandler(String host, int port) {
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(TIMEOUT);
            serverAddress = InetAddress.getByName(host);
        } catch (Exception e) {
            System.err.println("Ошибка создания сокета: " + e.getMessage());
        }
    }
    public Response sendRequest(Req request) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(request);
            oos.flush();
            byte[] data = baos.toByteArray();
            DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, PORT);
            socket.send(packet);
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(responsePacket);
            ByteArrayInputStream bais = new ByteArrayInputStream(responsePacket.getData(), 0, responsePacket.getLength());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Response) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
    public List<City> sortByLocation(Collection<City> collection) {
        return collection.stream()
                .sorted(Comparator.comparing((City c) -> c.getCoordinates().getX())
                        .thenComparing(c -> c.getCoordinates().getY()))
                .collect(Collectors.toList());
    }
    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}