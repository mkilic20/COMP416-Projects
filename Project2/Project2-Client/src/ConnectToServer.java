import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;

/**
 * Copyright [Yahya Hassanzadeh-Nazarabadi]

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

public class ConnectToServer
{
    /*
    Name of trust file
     */
    private final String TRUST_STORE_NAME =  "truststore";
    /*
    Password to the trust store file
     */
    private final String TRUST_STORE_PASSWORD = "storepass";
    
    private SSLSocketFactory sslSocketFactory;
    private SSLSocket sslSocket;
    private BufferedReader is;
    private PrintWriter os;

    protected String serverAddress;
    protected int portSSL;
    protected int portTCP;
    private Socket tcpSocket;
    private boolean secure;
    protected int mainPort;

    public ConnectToServer(String address, int portSSL, int portTCP, boolean secure)
    {
        this.serverAddress = address;
        this.secure = secure;
        if (secure) {
        	this.mainPort=portSSL;
            System.setProperty("javax.net.ssl.trustStore", TRUST_STORE_NAME);
            System.setProperty("javax.net.ssl.trustStorePassword", TRUST_STORE_PASSWORD);
        } else {this.mainPort = portTCP;}
    }

    public void Connect()
    {
    	if (secure) {
	        try
	            {
	                sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
	                sslSocket = (SSLSocket) sslSocketFactory.createSocket(serverAddress, mainPort);
	                sslSocket.startHandshake();
	                is=new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
	                os= new PrintWriter(sslSocket.getOutputStream());
	                System.out.println("Successfully connected to server at" + sslSocket.getRemoteSocketAddress());
	            }
	        catch (Exception e)
	            {
	                e.printStackTrace();
	            }
	    }
    	else {
    		try
            {
    			tcpSocket = new Socket(serverAddress, mainPort);
    			is = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
    			os = new PrintWriter(tcpSocket.getOutputStream());
                System.out.println("Successfully connected to server at" + tcpSocket.getRemoteSocketAddress());
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.err.println("Error: no server has been found on " + serverAddress + "/" + mainPort);
            }
    	}
    }

    public void Disconnect()
    {
        try
            {
	        	if (is != null) {
	                is.close();
	            }
	            if (os != null) {
	                os.close();
	            }
	            if (secure && sslSocket != null) {
	                sslSocket.close();
	            }
	            if (!secure && tcpSocket != null) {
	                tcpSocket.close();
	            }
            }
        catch (IOException e)
            {
                e.printStackTrace();
            }
    }

    public String[] SendForAnswer(String message)
    {
    	String ciphertext= "";
    	String key = "COMP416";
    	long startTime;
    	long endTime = 0;
    	String [] returnList = new String[2];
    	for (char c: message.toCharArray()) {
    		ciphertext+= c;
    		ciphertext+= key;
    	}
        String response = new String();
        startTime = System.currentTimeMillis();
        try
        {
            os.println(ciphertext);
            os.flush();
            response = is.readLine();
            endTime = System.currentTimeMillis();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.out.println("ConnectionToServer. SendForAnswer. Socket read Error");
        }
        long delay = endTime - startTime;
        System.out.println(delay);
        returnList[0]=response;
        returnList[1]=Long.toString(delay);
        return returnList; 
    }
}
