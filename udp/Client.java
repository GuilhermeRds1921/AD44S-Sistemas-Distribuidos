import java.net.*;

public class Client {

    DatagramPacket in;
    DatagramPacket out;
    DatagramSocket socket;

    public void udp() throws Exception {

        socket = new DatagramSocket();

        InetAddress address = InetAddress.getByName("127.0.0.1");
        String mensagem = "Olá do servidor UDP!";
        byte[] buffer = mensagem.getBytes();

        out = new DatagramPacket(buffer, buffer.length, address, 5000);
        socket.send(out);
        System.out.println("Mensagem enviada ao servidor.");

        in = new DatagramPacket(new byte[1024], 1024);
        socket.receive(in);
        System.out.println("Mensagem recebida: " + new String(in.getData()));

        socket.close();
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.udp();
    }
}