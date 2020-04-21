package client;

import java.io.*; 
import java.net.*;
import java.util.List;

import com.alibaba.fastjson.JSON;

import common.Result; 
 
/**
 * Dictionary client for use with a multithreaded server
 * Created by Ryan Lewien
 * 746528
 * For Distributed Systems (COMP90015)
 * The University of Melbourne
 */
public class Client implements Runnable { 
	
	// Communication protocol
	// client side
	private static final String QUERY = "GET ";
	private static final String ADD = "PUT ";
	private static final String REMOVE = "DEL ";
	private static final String RANDOM = "RDM "; // to ask for a random word
	private static final String SEPARATOR = "~~~"; // to separate two arguments in an add query
	// server side
	private static final String ERROR = "ERR";
	private static final String SUCCESS = "OK";
	private static final String QUERY_RESPONSE = "RES";
	private static final String RDM_RESPONSE = "RDM";
	
	private ClientView view;
	
    private InetAddress ip = null;
    private Socket socket = null;
    private boolean clientRunning = false;
    
    private DataInputStream input; 
    private DataOutputStream output;
    
    private int port;
    private String ipAddress;
	
    
	public Client(String ipAddress, int port) {
		
		this.ipAddress = ipAddress;
		this.port = port;
		view = new ClientView(this);
    }
	
  
    public static void main(String args[]) throws UnknownHostException, IOException { 
    	
    	final String ipAddress = "192.168.0.208";
    	final int port = 3784;
    	
    	Client client = new Client(ipAddress, port);
    	client.run();
    }
    
    @Override
    public void run() {
    	view.run();
    	clientRunning = true;
    	connectToServer();
        readMessages();
        getRandom(); // get word of the day
    }
    
    /**************************************************************************************************
     * 
     * 									  Public Client Methods
     * 
     *************************************************************************************************/
    
    /**
     * Query the server for a specific word
     */
    public void sendQuery(String query) {
    	
    	if (isEmpty(query)) {
    		view.showError("Make sure you search for *something*, you dingus!");
    		return;
    	}

    	send(QUERY + query);
    }
    
    /**
     * Add a word to the server's dictionary
     */
    public void addWord(String word, String definition) {
    	
    	if (isEmpty(word)) {
    		view.showError("You didn't pop a term in.");
    		return;
    	} else if (isEmpty(definition)) {
    		view.showError("What does \"" + word + "\" mean? You need a definition as well.");
    		return;
    	}
    	
    	send(ADD + word.replace("~", "") + SEPARATOR + definition);
    }
    
    /**
     * Removes a word from the server's dictionary
     */
    public void removeWord(String word) {
    	if (isEmpty(word)) {
    		view.showError("You didn't pop a word in.");
    		return;
    	}
    	
    	System.out.println("Deleting word from server:" + word);
    	send(REMOVE + word);
    }
    
    /**
     * Returns a random word from the dictionary
     */
    public void getRandom() {
    	send(RANDOM);
    }
    
    /**
     *  Attempts to reconnect to the server, starting the client's connections
     */
    public void reconnectServer() {
    	if (clientRunning == false) {
    		
    		System.out.println("Reconnecting to server");
    		connectToServer();
    		readMessages();
            getRandom();
            
            if (clientRunning) {
            	view.showSuccess("Reconnected to server!");
            }
    	}
    }
    
    /**
     * Sets the client's port and IP Address, and reconnects it at the new port and IP
     */
    public void updateConnection(String newIPAddress, String portText) {
    	
    	int newPort;
    	try {
    		newPort = Integer.parseInt(portText);
    	} catch (NumberFormatException e) {
    		newPort = -1;
    	}
    	
    	// Make sure new IpAddress
    	 {
    		
    	}
    	
    	// Make sure new port is valid
    	if (newPort > 0) {
    		
    		// Make sure IP is valid
    		if (validateIPAddress(newIPAddress)) {
    			this.port = newPort;
    			this.ipAddress = newIPAddress;
        		clientRunning = false;
        		reconnectServer();
    		} else { 
    			view.showError("Invalid IP Address! It must be in IPv4 address format.");
        	}
    	} else {
    		view.showError("Invalid Port! It must be a number greater than 0.");
    	}
    }
    
    public int getPort() {
    	return port;
    }
    
    public String getIPAddress() {
    	return ipAddress;
    }
    
    /**
     * Checks if client is running
     */
    public synchronized boolean isRunning() {
    	return clientRunning;
    }
    
    /**
     * Stops the client
     */
    public synchronized void stopClient() {
    	if (clientRunning) {
    		clientRunning = false;
        	try {
        		socket.close();
        	} catch (IOException | NullPointerException e) {
        		view.showError("Error closing client");
        	}
    	}
    	System.out.println("Client stopped.");
    }
    
    /**************************************************************************************************
     * 
     * 									  	Helper Methods
     * 
     *************************************************************************************************/
    
    /**
     * Sends a generic message to the server
     */
    private void send(String msg) {
        
        try { 
            // clean up the query
        	output.writeUTF(msg.trim());
        } catch (IOException | NullPointerException e) { 
            view.showError("Error connecting at IP: " + ipAddress + ", port: " + port + ". Click here to try again!");
            clientRunning = false;
        	return;
        }
    }
    
    /**
     * Connects the client to the server
     */
    private void connectToServer() {
    	
    	// Check port is valid
    	if (port <= 0) {
        	view.showError("Port number is invalid! Try another.");
        	return;
        }
    	
    	// get the IP
    	try {
			ip = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			view.showError("IP Address not found. Change in the Advanced Features panel!");
			clientRunning = false;
			return;
		}
        
        // connect to server if it's started
        try {
        	socket = new Socket(ip, port);
        } catch (IOException ce) {
        	view.showError("Error connecting at IP: " + ipAddress + ", port: " + port + ". Click here to try again!");
        	clientRunning = false;
        	return;
        }
        
        // obtain input and output streams
        try {
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			view.showError("Unable to connect to input and output streams. Click here to try again!");
			clientRunning = false;
			return;
		}
        
        clientRunning = true;
    }
    
    /**
     * Reads messages from input
     */
    private void readMessages () {
        Thread readMessage = new Thread(new Runnable() { 
            
        	@Override
            public void run() { 
                while (isRunning()) {
                    try {
                        // read the message sent to this client 
                        String msg = input.readUTF();
                        System.out.println("Response from Server: " + msg);
                        directMessage(msg);
                    } catch (IOException e) {
                    	view.showError("Error connecting at IP: " + ipAddress + ", port: " + port + ". Click here to try again!");
                    	clientRunning = false;
                    	break;
                    }
                } 
            }
        });
        
        readMessage.start(); 
    }
    
    /**
     * Determines how to react to a message read in
     */
    private void directMessage(String message) {

    	String[] splitMessage = message.split(" ", 2);
    	String command = splitMessage[0];
        String content = splitMessage[1];
        
        if (command.equals(ERROR)) {
        	view.showError(content);
        } 
        
        else if (command.equals(SUCCESS)) {
        	view.showSuccess(content);
        } 
        
        else if (command.equals(QUERY_RESPONSE) | command.equals(RDM_RESPONSE)) {
        	
        	// separate out original word
        	String[] splitContent = content.split(SEPARATOR, 2);
        	String word = splitContent[0];
            String result = splitContent[1];
        	
        	List<Result> results = JSON.parseArray(result, Result.class);
        	
        	// Let view know it's a random message
        	if (command.equals(RDM_RESPONSE)) {
        		view.showResults(word, results, true);
        	} else {
        		view.showResults(word, results, false);
        	}
        	view.resetToaster();
        }
    }
    
    private boolean isEmpty(String word) {
    	return word.equals("");
    }
    
    /**
     * Validates an IP address form
     * Courtesy of Stackoverflow user Samthebest: https://stackoverflow.com/Questions/5667371/validate-ipv4-address-in-java
     */
    private boolean validateIPAddress(String ip) {
        String pattern = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return ip.matches(pattern);
    }
}