package server;

// Java implementation of  Server side 
// It contains two classes : Server and ClientHandler 
// Save file as Server.java 
  
import java.io.*; 
import java.util.*; 
import java.net.*; 
  
// Server class 
public class Server  
{ 
    // Communication protocol
	private static final String QUERY = "GET";
	private static final String ADD = "PUT";
	private static final String REMOVE = "DEL";
	
    // Vector to store active clients 
    static Vector<WorkerRunnable> ar = new Vector<>(); 
    
    private static final int port = 1234;
      
    // counter for clients 
    static int i = 0;
  
    public static void main(String[] args) throws IOException  
    { 
        // server is listening on port 1234 
        ServerSocket ss = new ServerSocket(port); 
          
        Socket s; 
        
        System.out.println("SERVER\nOn port: " + port + "\n");
          
        // running infinite loop for getting 
        // client request 
        while (true) {
            // Accept the incoming request 
            s = ss.accept(); 
  
            System.out.println("New client request received : " + s); 
              
            // obtain input and output streams 
            DataInputStream input = new DataInputStream(s.getInputStream()); 
            DataOutputStream output = new DataOutputStream(s.getOutputStream()); 
              
            // Create a new handler object for handling this request. 
            System.out.println("Creating a new handler for this client..."); 
            WorkerRunnable worker = new WorkerRunnable(s, input, output); 
  
            // Create a new Thread with this object. 
            Thread t = new Thread(worker); 
            t.start(); 
        } 
    } 
} 