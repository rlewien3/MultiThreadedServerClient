package server;

// Java implementation of  Server side 
// It contains two classes : Server and ClientHandler 
// Save file as Server.java 
  
import java.io.*; 
import java.net.*; 
  
// Server class 
public class Server  
{ 
    // Communication protocol
	private static final String QUERY = "GET";
	private static final String ADD = "PUT";
	private static final String REMOVE = "DEL";
	
    private static final int port = 1234;
     
    public static void main(String[] args) throws IOException { 

    	// create server socket if port isn't used
        ServerSocket serverSocket = null;
    	try {
        	serverSocket = new ServerSocket(port); 
        } catch (BindException be) {
        	System.out.println("Port is already being used. Quitting.");
        	System.exit(0);
        }
        
    	boolean serverRunning = true;
        Socket clientSocket; 
        System.out.println("SERVER\nOn port: " + port + "\n");
          
        // running infinite loop for getting client request 
        while (serverRunning) {
            
        	try {
	        	// Accept the incoming request 
	            clientSocket = serverSocket.accept();
	            System.out.println("New client request received: " + clientSocket); 
	            
	            // Create a new handler object for handling this request. 
	            WorkerRunnable worker = new WorkerRunnable(clientSocket); 
	            Thread t = new Thread(worker);
	            t.start();
	            
	            // End if called
	            if (!serverRunning) {
	            	System.out.println("Server closing.");
	            	break;
	            }
	            
        	} catch(IOException ioe) {
        		System.out.println("Exception found on accept. Ignoring. Stack Trace:"); 
                ioe.printStackTrace();
        	}
        }
        
        serverSocket.close();
    } 
} 