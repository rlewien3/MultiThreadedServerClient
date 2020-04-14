package client;

// Java implementation for multithreaded chat client 
// Save file as Client.java 
  
import java.io.*; 
import java.net.*; 
import java.util.Scanner; 
  
public class Client  
{ 
	// Communication protocol
	private static final String QUERY = "GET";
	private static final String ADD = "PUT";
	private static final String REMOVE = "DEL";
	
	final static int port = 1234;
  
    public static void main(String args[]) throws UnknownHostException, IOException  
    { 
    	System.out.println("CLIENT\nOn port: " + port + "\n");
    	
    	Scanner scn = new Scanner(System.in); 
        InetAddress ip = InetAddress.getByName("localhost");
        Socket socket = null;
        
        // connect to server if it's started
        try {
        	socket = new Socket(ip, port);
        } catch (ConnectException ce) {
        	System.out.println("Server not found at ip: " + ip + " port number: " + port);
        	System.exit(0); 
        }
          
        // obtaining input and out streams
        DataInputStream input = new DataInputStream(socket.getInputStream()); 
        DataOutputStream output = new DataOutputStream(socket.getOutputStream()); 
  
        // sendMessage thread
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
          
        // readMessage thread 
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
                	scn.close();
                	output.close();
                	input.close();
                } catch (IOException ioe) {
                	System.out.println("Unable to close client socket.");
                }
            }
        }); 
  
        sendMessage.start(); 
        readMessage.start(); 
  
    }
} 