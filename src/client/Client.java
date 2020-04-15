package client;

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
    }
	
  
    public static void main(String args[]) throws UnknownHostException, IOException { 
    	
    	final int port = 1234;
    	final String ipAddress = "127.0.0.1";
    	
    	System.out.println("CLIENT\nOn port: " + port + "\n");
    	Client client = new Client(port, ipAddress);
    	client.run();
    }
    
    @Override
    public void run() {
    	clientRunning = true;
    	connectToServer();
    	sendMessages();
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
			System.out.println("IP Address not found.");
			e.printStackTrace();
			System.exit(0);
		}
        
        // connect to server if it's started
        try {
        	socket = new Socket(ip, port);
        } catch (IOException ce) {
        	System.out.println("Server not able to connect to ip: " + ip + " port number: " + port);
        	ce.printStackTrace();
        	System.exit(0); 
        }
        
        // obtain input and output streams
        try {
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Unable to connect to input and output streams.");
			e.printStackTrace();
			stop();
		}
    }
    
    /**
     * Sends messages to output
     */
    private void sendMessages() {
    	Scanner scn = new Scanner(System.in); 
    	
        Thread sendMessage = new Thread(new Runnable() { 
            
        	@Override
            public void run() { 
                while (isRunning()) { 
  
                	// Read input from user
                    String msg = scn.nextLine();
                    
                    if (msg.equals("QUIT")) {
                    	break;
                    }
                    
                    try { 
                        output.writeUTF(msg); 
                    } catch (IOException e) { 
                        e.printStackTrace(); 
                    }
                }
                
                // Closing the client
                scn.close();
                stop();
            }
        });
        
        sendMessage.start();
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
                    } catch (SocketException se) {
                    	System.out.println("Server not available. CLosing Connection.");
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
        		System.out.println("Error closing client");
        	}
    	}
    	System.out.println("Client stopped.");
    	System.exit(0); 
    }
}