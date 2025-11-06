import java.net.*;

public class Server {

    DatagramPacket in;
    DatagramPacket out;
    DatagramSocket socket;

    public void udp() throws Exception {

        socket = new DatagramSocket(5000);
        in = new DatagramPacket(new byte[1024], 1024);
        System.out.println("Servidor UDP aguardando mensagens...");

        socket.receive(in);
        System.out.println("Mensagem recebida: " + new String(in.getData(), 0, in.getLength()));

        String mensagem = "Olá do servidor UDP!";
        byte[] buffer = mensagem.getBytes();
        out = new DatagramPacket(buffer, buffer.length, in.getAddress(), in.getPort());
        socket.send(out);
        System.out.println("Mensagem enviada de volta ao cliente.");

        socket.close();
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.udp();
    }
}