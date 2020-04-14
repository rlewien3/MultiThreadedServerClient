package server;

import java.io.*; 
import java.net.*; 
  
/**
 * Multithreaded server
 * Created by Ryan Lewien
 * 746528
 * For Distributed Systems (COMP90015)
 * The University of Melbourne
 */
public class Server { 
    
	// Communication protocol
	private static final String QUERY = "GET";
	private static final String ADD = "PUT";
	private static final String REMOVE = "DEL";
    
	private ServerSocket serverSocket = null;
	private Socket clientSocket;
	private boolean serverRunning = true;
	
    public Server(int port) {
    	
    	// create server socket if port isn't used
    	try {
        	serverSocket = new ServerSocket(port); 
        } catch (BindException be) {
        	System.out.println("Port is already being used. Quitting.");
        	System.exit(0);
        } catch (IOException ioe) {
        	System.out.println("ServerSocket was not able to be created. Quitting.");
        	System.exit(0);
        }
    	
    	delegateRequests();
    }
    
    
    public static void main(String[] args) throws IOException { 

    	final int port = 1234;
    	System.out.println("SERVER\nOn port: " + port + "\n");
    	Server server = new Server(port);
    } 
    
    
    /**
     * Delegates requests to worker threads.
     */
    public void delegateRequests() {
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
        
        try {
			serverSocket.close();
		} catch (IOException e) {
			System.out.println("Unable to close ServerSocket.");
			e.printStackTrace();
		}
    }
} 