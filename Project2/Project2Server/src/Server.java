import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

public class Server extends Thread
{
	//SSL fields
    private final String SERVER_KEYSTORE_FILE = "keystore.jks";
    private final String SERVER_KEYSTORE_PASSWORD = "storepass";
    private final String SERVER_KEY_PASSWORD = "keypass";
    private SSLServerSocket sslServerSocket;
    private SSLServerSocketFactory sslServerSocketFactory;
    //TCP fields
    private ServerSocket tcpServerSocket;
    //Creating Server with two sockets
    public Server(int sslPort, int tcpPort) {
        super();
        setupSSLServer(sslPort);
        setupTCPServer(tcpPort);
    }
    //setting up unsecure TCP Server socket
    private void setupTCPServer(int port) {
		// TODO Auto-generated method stub
    	 try {
             tcpServerSocket = new ServerSocket(port);
             System.out.println("TCP server is up and running on port " + port);
         } catch (IOException e) {
             e.printStackTrace();
         }
	}
    //setting up secure SSL Server socket
	private void setupSSLServer(int port) {
		// TODO Auto-generated method stub
		try
        {
            SSLContext sc = SSLContext.getInstance("TLS");

            char ksPass[] = SERVER_KEYSTORE_PASSWORD.toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(SERVER_KEYSTORE_FILE), ksPass);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, SERVER_KEY_PASSWORD.toCharArray());
            sc.init(kmf.getKeyManagers(), null, null);


            sslServerSocketFactory = sc.getServerSocketFactory();
            sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

            System.out.println("SSL server is up and running on port " + port);
        }catch (Exception e){
            e.printStackTrace();
        }
	}
	//SSL Listen and Accept Method
    private void SSLListenAndAccept()
    {
        SSLSocket s;
        try
        {
            s = (SSLSocket) sslServerSocket.accept();
            System.out.println("An SSL connection was established with a client on the address of " + s.getRemoteSocketAddress());
            SSLServerThread st = new SSLServerThread(s);
            st.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Server Class. Connection establishment error inside listen and accept function");
        }
    }
    //TCP Listen and Accept Method
    private void TCPListenAndAccept()
    {
    	try {
            Socket socket = tcpServerSocket.accept();
            System.out.println("A TCP connection was established with a client on the address of " + socket.getRemoteSocketAddress());
            new TCPServerThread(socket).start();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server Class. Connection establishment error inside listen and accept TCP function");
        }
    }
    //Run method with both listening and accepting
    public void run() {
        new Thread(() -> {
            while (true) {
                SSLListenAndAccept();
            }
        }).start();

        new Thread(() -> {
            while (true) {
                TCPListenAndAccept();
            }
        }).start();
    } 
}
