import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CoinGeckoClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 4444;

	public CoinGeckoClient(String address, int port) throws IOException {
        socket = new Socket(address, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendListRequest() throws IOException, JSONException {
        JSONObject request = new JSONObject();
        request.put("Method", "REQUEST");
        request.put("Command", "LIST");
        out.println(request.toString());
    }

    public void sendNFTRequest(String id) throws IOException, JSONException {
        JSONObject request = new JSONObject();
        request.put("Method", "REQUEST");
        request.put("Command", "NFT");
        request.put("ID", id);
        out.println(request.toString());
    }

    public String receiveResponse() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
    
    public SocketAddress getServerAddress() {
		return socket.getRemoteSocketAddress();
	}
    
    //print statements
    public static void printList(String jsonResponse) throws JSONException {
        JSONObject response = new JSONObject(jsonResponse);
        //System.out.println(response);
        JSONArray nfts = response.getJSONArray("Body");
        for (int i = 0; i < nfts.length(); i++) {
            JSONObject nft = nfts.getJSONObject(i);
            String symbol = nft.optString("symbol", "N/A");
            String name = nft.optString("name", "N/A");
            String platformId = nft.optString("asset_platform_id", "N/A");
            String contractAddress = nft.optString("contract_address", "N/A");
            System.out.println("Symbol: "+ symbol+ " Name: "+ name+ " Platform ID: "+ platformId+ " Contract Address: "+contractAddress);
        }
    }

    public static void printNFT(String jsonResponse) throws JSONException {
        JSONObject nft = new JSONObject(jsonResponse);
        //System.out.println(nft);
        String name = nft.getJSONObject("Body").optString("name", "N/A");
        String platformId = nft.getJSONObject("Body").optString("asset_platform_id", "N/A");
        double priceInUsd = nft.getJSONObject("Body").getJSONObject("floor_price").optDouble("usd", 0.00);
        System.out.println("Name: " + name + "\nPlatform ID: " + platformId + "\nPrice in USD: $" + String.format("%.2f", priceInUsd));
    }
    
    public static void main(String[] args) {
        try (BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            CoinGeckoClient client = new CoinGeckoClient(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected server socket: " + client.getServerAddress());
            String input;
            while (true) {
            	System.out.println("Commands: LIST ,NFT <NFT ID>, TYPE");
                input = consoleReader.readLine();

                if (input.trim().equalsIgnoreCase("TYPE")) {
                    break;
                }

                String[] inputs = input.split("\\s+", 2);
                String command = inputs[0].trim().toUpperCase();
                String response;
                switch (command) {
                    case "LIST":
                        client.sendListRequest();
                        response = client.receiveResponse();
                        printList(response);
                        break;
                    case "NFT":
                    	if (inputs.length == 2) {
                    		String id = inputs[1].trim();
                            client.sendNFTRequest(id);
                            response = client.receiveResponse();
                            printNFT(response);
                            break;
                    	} else{
                    		continue;                        
                    	}
                    default:
                        System.out.println("Use proper commands");
                        continue;
                }
            }
            client.close();
            System.out.println("Client is closing");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Error :"+e.getMessage());
        }
    }

}
