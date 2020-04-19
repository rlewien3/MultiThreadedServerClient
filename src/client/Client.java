package client;

import java.io.*; 
import java.net.*;
import java.util.List;

import com.alibaba.fastjson.JSON;

import server.Result; 
 
/**
 * Multithreaded client
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
	private static final String RANDOM = "RDM"; // to ask for a random word
	private static final String SEPARATOR = "~~~"; // to separate two arguments in an add query
	// server side
	private static final String ERROR = "ERR";
	private static final String SUCCESS = "OK";
	private static final String QUERY_RESPONSE = "RES";
	
	private ClientView view;
	
    private InetAddress ip = null;
    private Socket socket = null;
    private boolean clientRunning = false;
    
    private DataInputStream input; 
    private DataOutputStream output;
    
    private int port;
    private String ipAddress;
	
	public Client(int port, String ipAddress) {
		
		this.port = port;
		this.ipAddress = ipAddress;

		view = new ClientView(this);
    }
	
  
    public static void main(String args[]) throws UnknownHostException, IOException { 
    	
    	final int port = 7364;
    	final String ipAddress = "127.0.0.1";
    	Client client = new Client(port, ipAddress);
    	
    	// Start Client behaviour
    	System.out.println("CLIENT\nOn port: " + port + "\n");
    	client.run();
    }
    
    /**
     * Query the server for a specific word
     */
    public void sendQuery(String query) {
    	
    	if (isEmpty(query)) {
    		view.showError("Make sure you search for *something*, you dingus!");
    		return;
    	}
    	
    	System.out.println("Sending query to server:" + query);
    	send(QUERY + query);
    }
    
    /**
     * Add a word to the server's dictionary
     */
    public void addWord(String word, String definition) {
    	
    	if (isEmpty(word)) {
    		view.showError("You didn't pop a word in.");
    		return;
    	} else if (isEmpty(definition)) {
    		view.showError("You need to write what the word means as well.");
    		return;
    	}
    	
    	System.out.println("Sending new word to server:" + word + ": " + definition);
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
     * Helper functions
     */
    private boolean isEmpty(String word) {
    	return word.equals("");
    }
    
    private void send(String msg) {
    	
    	// Quit client on command
    	if (msg.equals("QUIT")) {
        	stop();
        }
        
        try { 
            // clean up the query
        	output.writeUTF(msg.trim()); 
        } catch (IOException e) { 
            view.showError("Error trying to send to the server! Try again in a second. ");
        	e.printStackTrace(); 
        }
    }
    
    @Override
    public void run() {
    	view.run();
    	clientRunning = true;
    	connectToServer();
        readMessages();
        getRandom(); // get word of the day
    }
    
    /**
     * Connects the client to the server
     */
    private void connectToServer() {
    	
    	// get the IP
    	try {
			ip = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			view.showError("IP Address not found.");
			e.printStackTrace();
		}
        
        // connect to server if it's started
        try {
        	socket = new Socket(ip, port);
        } catch (IOException ce) {
        	view.showError("Server not able to connect to ip: " + ip + " port number: " + port);
        	ce.printStackTrace();
        }
        
        // obtain input and output streams
        try {
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			view.showError("Unable to connect to input and output streams.");
			e.printStackTrace();
		}
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
                    } catch (SocketException se) {
                    	view.showError("Server not available. Closing Connection.");
                    	break;
                	} catch (IOException e) {
                        e.printStackTrace(); 
                    } 
                } 
                
                //stop();
            }
        });
        
        readMessage.start(); 
    }
    
    /**
     * Determines how to react to a message
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
        
        else if (command.equals(QUERY_RESPONSE)) {
        	List<Result> results = JSON.parseArray(content, Result.class);
        	view.showResults(results);
        	view.resetToaster();
        }
        
        else {
        	view.showResponse("Unhandled response! " + message); // delete out later??????
        }
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
    public synchronized void stop() {
    	if (clientRunning) {
    		clientRunning = false;
        	try {
        		socket.close();
        	} catch (IOException e) {
        		view.showError("Error closing client");
        	}
    	}
    	System.out.println("Client stopped.");
    	System.exit(0); 
    }
}