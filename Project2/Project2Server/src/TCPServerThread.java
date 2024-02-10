import java.io.*;
import java.net.Socket;

public class TCPServerThread extends Thread {
    private final String SERVER_ACK_MESSAGE = "tcp_server_ack";
    private Socket tcpSocket;
    private String line = "";
    private BufferedReader is;
    private PrintWriter os;

    public TCPServerThread(Socket socket) {
        this.tcpSocket = socket;
    }

    public void run() {
        try {
            is = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
            os = new PrintWriter(tcpSocket.getOutputStream(), true);
            line = is.readLine();
            os.println(SERVER_ACK_MESSAGE);
            os.flush();
            System.out.println("Client " + tcpSocket.getRemoteSocketAddress() + " sent : " + line);
        } catch (IOException e) {
            line = this.getClass().toString();
            System.out.println("Server Thread. Run. IO Error/ Client " + line + " terminated abruptly");
        } catch (NullPointerException e) {
            line = this.getClass().toString();
            System.out.println("Server Thread. Run.Client " + line + " Closed");
        } finally {
            try {
                System.out.println("Closing the connection");
                if (is != null) {
                    is.close();
                    System.out.println("Socket Input Stream Closed");
                }
                if (os != null) {
                    os.close();
                    System.out.println("Socket Out Closed");
                }
                if (tcpSocket != null) {
                    tcpSocket.close();
                    System.out.println("Socket Closed");
                }
            } catch (IOException ie) {
                System.out.println("Socket Close Error");
            }
        }
    }
}
