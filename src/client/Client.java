package client;

import java.awt.EventQueue;
import java.io.*; 
import java.net.*; 
import java.util.Scanner; 
 
/**
 * Multithreaded client
 * Created by Ryan Lewien
 * 746528
 * For Distributed Systems (COMP90015)
 * The University of Melbourne
 */
public class Client implements Runnable { 
	
	// Communication protocol
	private static final String QUERY = "GET";
	private static final String ADD = "PUT";
	private static final String REMOVE = "DEL";
	private static final String SEPARATOR = "GB&^IR%&*"; // to separate two arguments in an add query
	
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
		
		// Start GUI
		view = new ClientView(this);
    }
	
  
    public static void main(String args[]) throws UnknownHostException, IOException { 
    	
    	final int port = 1234;
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
    	System.out.println("Sending query to server:" + query);
    	send("GET " + query);
    }
    
    /**
     * Add a word to the server's dictionary
     */
    public void addWord(String word, String definition) {
    	System.out.println("Sending new word to server:" + word + ": " + definition);
    	send("PUT " + word + SEPARATOR + definition);
    }
    
    /**
     * Removes a word from the server's dictionary
     */
    public void removeWord(String word) {
    	System.out.println("Deleting word from server:" + word);
    	send("DEL " + word);
    }
    
    /**
     * Helper function to send a message to the server
     */
    private void send(String msg) {
    	if (msg.equals("QUIT")) {
        	stop();
        }
        
        try { 
            output.writeUTF(msg); 
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
    }
    
    /**
     * Connects the client to the server
     */
    private void connectToServer() {
    	
    	// get the ip
    	try {
			ip = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			view.showError("IP Address not found.");
			e.printStackTrace();
			System.exit(0);
		}
        
        // connect to server if it's started
        try {
        	socket = new Socket(ip, port);
        } catch (IOException ce) {
        	view.showError("Server not able to connect to ip: " + ip + " port number: " + port);
        	ce.printStackTrace();
        	System.exit(0); 
        }
        
        // obtain input and output streams
        try {
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			view.showError("Unable to connect to input and output streams.");
			e.printStackTrace();
			stop();
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
                        view.showResponse(msg);
                    } catch (SocketException se) {
                    	view.showError("Server not available. Closing Connection.");
                    	break;
                	} catch (IOException e) {
                        e.printStackTrace(); 
                    } 
                } 
                
                stop();
            }
        });
        
        readMessage.start(); 
    }
    
    /**
     * Checks if server is running
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