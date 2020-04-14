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
public class Client { 
	
	// Communication protocol
	private static final String QUERY = "GET";
	private static final String ADD = "PUT";
	private static final String REMOVE = "DEL";
	
    private InetAddress ip = null;
    private Socket socket = null;
    
    private DataInputStream input; 
    private DataOutputStream output;
	
	public Client(int port) {
		
		try {
			ip = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        
        // connect to server if it's started
        try {
        	socket = new Socket(ip, port);
        } catch (IOException ce) {
        	System.out.println("Server not able to connect to ip: " + ip + " port number: " + port);
        	ce.printStackTrace();
        	System.exit(0); 
        }
        
        // obtaining input and out streams
        try {
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Unable to connect to input and output streams.");
			e.printStackTrace();
			System.exit(0); 
		}
         
        // send and receive messages
        sendMessages();
        readMessages();
    }
	
  
    public static void main(String args[]) throws UnknownHostException, IOException { 
    	
    	final int port = 1234;
    	System.out.println("CLIENT\nOn port: " + port + "\n");
    	Client client = new Client(port);
    }
    
    /**
     * Sends messages to output
     */
    private void sendMessages() {
    	Scanner scn = new Scanner(System.in); 
    	
        Thread sendMessage = new Thread(new Runnable() { 
            
        	@Override
            public void run() { 
                while (true) { 
  
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
                
                try {
                	scn.close();
                	output.close();
                	input.close();
                } catch (IOException ioe) {
                	System.out.println("Unable to close client socket.");
                }
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
                while (true) {
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
                
                try {
                	output.close();
                	input.close();
                } catch (IOException ioe) {
                	System.out.println("Unable to close client socket.");
                }
            }
        });
        
        readMessage.start(); 
    }
}