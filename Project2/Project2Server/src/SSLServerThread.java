import javax.net.ssl.SSLSocket;
import java.io.*;

public class SSLServerThread extends Thread
{

    private final String SERVER_ACK_MESSAGE = "ssl_server_ack";
    private SSLSocket sslSocket;
    private String line = new String();
    private BufferedReader is;
    private PrintWriter os;
    
    public SSLServerThread(SSLSocket s)
    {
        sslSocket = s;
    }

    public void run()
    {
        try
        {
            is = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            os= new PrintWriter(sslSocket.getOutputStream());
            line = is.readLine();
            os.write(SERVER_ACK_MESSAGE);
            os.flush();
            System.out.println("Client " + sslSocket.getRemoteSocketAddress() + " sent : " + line);
        }
        
        catch (IOException e)
        {
            line = this.getClass().toString();
            System.out.println("Server Thread. Run. IO Error/ Client " + line + " terminated abruptly");
        }
        catch (NullPointerException e)
        {
            line = this.getClass().toString();
            System.out.println("Server Thread. Run.Client " + line + " Closed");
        } 
        finally
        {
            try
            {
                System.out.println("Closing the connection");
                if (is != null)
                {
                    is.close();
                    System.out.println(" Socket Input Stream Closed");
                }

                if (os != null)
                {
                    os.close();
                    System.out.println("Socket Out Closed");
                }
                if (sslSocket != null)
                {
                    sslSocket.close();
                    System.out.println("Socket Closed");
                }

            }
            catch (IOException ie)
            {
                System.out.println("Socket Close Error");
            }
        }
    }
}