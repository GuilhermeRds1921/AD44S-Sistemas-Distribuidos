import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        int port = 12345;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor ouvindo na porta " + port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Cliente conectado: " + clientSocket.getRemoteSocketAddress());
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                    String request;
                    while ((request = in.readLine()) != null) {
                        System.out.println("Recebido: " + request);
                        
                        String[] parts = request.split(" ");
                        if (parts.length != 3) {
                            out.println("ERRO: formato inválido");
                            continue;
                        }
                        double n1 = Double.parseDouble(parts[0]);
                        String op = parts[1];
                        double n2 = Double.parseDouble(parts[2]);
                        double result = 0;
                        
                        switch (op) {
                            case "+":
                                result = n1 + n2;
                                break;
                            case "-":
                                result = n1 - n2;
                                break;
                            case "*":
                                result = n1 * n2;
                                break;
                            case "/":
                                if (n2 == 0) {
                                    out.println("ERRO: divisão por zero");
                                    continue;
                                }
                                result = n1 / n2;
                                break;
                            case "exit":
                                out.println("Saindo");
                                break;
                            default:
                                out.println("ERRO: operação desconhecida");
                                continue;
                        }
                        out.println("RESULTADO: " + result);
                    }
                } catch (IOException e) {
                    System.err.println("Erro no cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}
