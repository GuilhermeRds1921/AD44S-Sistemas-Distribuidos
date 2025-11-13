import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Conectado ao servidor " + host + ":" + port);

            while (true) {
                System.out.print("\nDigite: número1  operação(+ - * /) número2 ou 'exit' para sair: ");
                String linha = scanner.nextLine();
                if ("exit".equalsIgnoreCase(linha.trim())) {
                    out.println("0 exit 0");
                    System.out.println("Saindo...");
                    break;
                }
                out.println(linha);
                String resposta = in.readLine();
                System.out.println("\nServidor respondeu: " + resposta);
            }

        } catch (IOException e) {
            System.err.println("Erro no cliente: " + e.getMessage());
        }
    }
}
