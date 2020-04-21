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
    	
    	// default ip and port
    	String ipAddress = "192.168.0.200";
		int port = 3000;
    	
		// parse both combinations of arguments
    	if (args.length == 2) {
    		
    		if (validateIPAddress(args[0]) & isInteger(args[1])) {
    			ipAddress = args[0];
    			port = Integer.parseInt(args[1]);
    		} else if (validateIPAddress(args[1]) & isInteger(args[0])) {
    			ipAddress = args[1];
    			port = Integer.parseInt(args[0]);
    		}
    	}
    	
    	Client client = new Client(ipAddress, port);
    	client.run();
    }
    
    @Override
    public void run() {
    	view.run();
    	connectToServer(ipAddress, port);
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
    	
    	send(REMOVE + word);
    }
    
    /**
     * Returns a random word from the dictionary
     */
    public void getRandom() {
    	send(RANDOM);
    }

    /**
     * Attempts to reconnect the client at the specified IP Address and port
     */
    public void updateConnection(String newIPAddress, String portText) {
    	
    	// Stop the client running
    	clientRunning = false;
    	
    	int parsedPort;
    	try {
    		parsedPort = Integer.parseInt(portText);
    	} catch (NumberFormatException e) {
    		parsedPort = -1;
    	}
    	final int newPort = parsedPort;
    	
    	// Make sure new port and IP are valid
    	if (newPort > 0) {
    		if (validateIPAddress(newIPAddress)) {
    			
    			// Reconnect using the new IP and port, on new thread so connecting doesn't block
    			view.showError("Connecting...");
    			Thread reconnecting = new Thread() {
    				
    				@Override
    				public void run() {
    					connectToServer(newIPAddress, newPort);
    		            if (isRunning()) {
    		            	view.showSuccess("Reconnected to server!");
    		            }
    				}
    			};
    			reconnecting.start();
    			
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
    	if (isRunning()) {
    		clientRunning = false;
        	try {
        		input.close();
        		output.close();
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
        	view.showFatalConnectionError(ipAddress, port);
            clientRunning = false;
        	return;
        }
    }
    
    /**
     * Connects the client to the server, returns true if it succeeds
     */
	private void connectToServer(String newIPAddress, int newPort) {
    	
    	// Check port is valid
    	if (newPort <= 0) {
	    	view.showError("Port number is invalid! Try another.");
    		return;
        }
    	
    	// get the IP
    	InetAddress ip;
    	try {
    		ip = InetAddress.getByName(newIPAddress);
		} catch (UnknownHostException e) {
			view.showError("IP Address not found. Change in the Advanced Features panel!");
			return;
		}
        
        // connect to server if it's started
        Socket newSocket = null;
    	try {
        	newSocket = new Socket(ip, newPort);
        } catch (IOException ce) {
        	view.showFatalConnectionError(newIPAddress, newPort);
        	return;
        }
        
        // obtain input and output streams
        DataInputStream newInput = null;
        DataOutputStream newOutput = null;
    	try {
			newInput = new DataInputStream(newSocket.getInputStream());
			newOutput = new DataOutputStream(newSocket.getOutputStream());
		} catch (IOException | NullPointerException ne) {
			view.showFatalConnectionError(newIPAddress, newPort);
			return;
		}
        
        // Success! Update internal variables
    	this.input = newInput;
    	this.output = newOutput;
        this.socket = newSocket;
        this.port = newPort;
		this.ipAddress = newIPAddress;
        clientRunning = true;
    	readMessages();
        return;
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
                        directMessage(msg);
                    } catch (IOException | NullPointerException e) {
                    	view.showFatalConnectionError(ipAddress, port);
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
    private static boolean validateIPAddress(String address) {
        String pattern = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return address.matches(pattern);
    }
    
    private static boolean isInteger(String input) {
	    try {
	        Integer.parseInt( input );
	        return true;
	    }
	    catch( NumberFormatException e ) {
	        return false;
	    }
    }
}