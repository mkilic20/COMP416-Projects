import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean running = true;
    private ServerRate serverRate;
    private final HttpClient httpClient = HttpClient.newHttpClient();


    public ClientHandler(Socket socket, ServerRate serverRate) {
        this.clientSocket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Error: "+ e.getMessage());
            running = false;
        }
       this.serverRate = serverRate;
    }

    public void run() {
        try {
            clientSocket.setSoTimeout(120000); // 2 mins
            while (running) {
                String inputLine;
                JSONObject requestJson;
                while ((inputLine = in.readLine()) != null) {
                    try {
                    	//System.out.println(inputLine);
                        requestJson = new JSONObject(inputLine);
                        processRequest(requestJson);
                    } catch (JSONException e) {
                        try {
							sendResponse("Wrong Format", e.getMessage());
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void processRequest(JSONObject requestJson) throws JSONException {
        String method = requestJson.getString("Method");
        //System.out.println("method:" +method);
        if (method.equalsIgnoreCase("REQUEST")) {
        	handleRequest(requestJson);
        }
        else {sendResponse("404", "Unknown message type");}
    }

    private void handleRequest(JSONObject requestJson) throws JSONException {
        if (serverRate.attemptLogin()) {
        	//System.out.println("we are in handlerequest");
            String command = requestJson.getString("Command");
            //System.out.println("no error in command");
            //System.out.println("we are in handlerequest");
            try {
                switch (command) {
                    case "LIST":
                    	JSONArray nft_list = fetchListOfNFTs();
                    	//System.out.println(nft_list);
                        sendResponse(nft_list);
                        break;
                    case "NFT":
                    	System.out.println(requestJson);
                    	String id = requestJson.getString("ID");
                        JSONObject nftDetails = fetchNFTData(id);
                        System.out.println("data: "+ nftDetails );
                        sendResponse(nftDetails);;
                        break;
                    default:
                        sendResponse("NO_ACTION", "Invalid action");
                        break;
                }
            } catch (Exception e) {
                sendResponse("HANDLING ERROR",e.getMessage());
            }
        } else {
            sendResponse("LIMIT EXCEEDED", "There are more than 50 clients");
        }
    }

    private void sendResponse(String errorCode, String errorMessage) throws JSONException {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("Method", "ERROR");
        errorResponse.put("ErrorCode", errorCode);
        errorResponse.put("ErrorMessage", errorMessage);
        out.println(errorResponse.toString());
    }

    private void sendResponse(JSONObject responseData) throws JSONException {
        JSONObject response = new JSONObject();
        response.put("Header", new JSONObject().put("Method", "RESPONSE"));
        response.put("Body", responseData);
        out.println(response.toString());
    }
    
    private void sendResponse(JSONArray responseData) throws JSONException {
    	JSONObject response = new JSONObject();
        response.put("Method", "RESPONSE");
        response.put("Body", responseData);
        out.println(response.toString());
    }
    
    private JSONArray fetchListOfNFTs() throws Exception {
    	//System.out.println("in fetch");
        String endpoint = "/nfts/list";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.coingecko.com/api/v3" + endpoint))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
        	System.out.println(new JSONArray(response.body()));
        	return new JSONArray(response.body());
        } else {
            throw new Exception("Error: " + response.statusCode());
        }
    }

    private JSONObject fetchNFTData(String id) throws Exception {
        String endpoint = "/nfts/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.coingecko.com/api/v3" + endpoint))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return new JSONObject(response.body());
        } else {
            throw new Exception("Error: " + response.statusCode());
        }
    }
}
