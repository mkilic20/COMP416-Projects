
public class ServerMain
{
    public static void main(String args[])
    {
    	int TCPPORT = 6666;
    	int SSLPORT = 5555;
    	
        new Server(SSLPORT,TCPPORT).start();
    }

}
