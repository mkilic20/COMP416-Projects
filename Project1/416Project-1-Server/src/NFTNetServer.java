import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NFTNetServer {

    private static final int PORT = 4444;
    private static final ServerRate serverRate = new ServerRate(50, 60);

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(50);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("NFTNet Server is listening on port " + PORT);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected");
                    executor.submit(new ClientHandler(clientSocket, serverRate));
                } catch (IOException e) {
                    System.out.println("Exception caught when trying to listen on port "
                        + PORT + " or listening for a connection");
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}