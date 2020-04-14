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
          
        // getting localhost ip 
        InetAddress ip = InetAddress.getByName("localhost"); 
          
        // establish the connection
        Socket socket = new Socket(ip, port);
          
        // obtaining input and out streams 
        DataInputStream input = new DataInputStream(socket.getInputStream()); 
        DataOutputStream output = new DataOutputStream(socket.getOutputStream()); 
  
        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() { 
                while (true) { 
  
                    // read the message to deliver. 
                    String msg = scn.nextLine(); 
                      
                    try { 
                        // write on the output stream 
                        output.writeUTF(msg); 
                    } catch (IOException e) { 
                        e.printStackTrace(); 
                    } 
                } 
            } 
        }); 
          
        // readMessage thread 
        Thread readMessage = new Thread(new Runnable()  
        { 
            @Override
            public void run() { 
  
                while (true) { 
                    try { 
                        // read the message sent to this client 
                        String msg = input.readUTF(); 
                        System.out.println(msg); 
                    } catch (IOException e) { 
  
                        e.printStackTrace(); 
                    } 
                } 
            } 
        }); 
  
        sendMessage.start(); 
        readMessage.start(); 
  
    } 
} 