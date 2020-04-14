package server;

import java.io.*; 
import java.net.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap; 
  
/**
 * Multithreaded server
 * Created by Ryan Lewien
 * 746528
 * For Distributed Systems (COMP90015)
 * The University of Melbourne
 */
public class Server { 
   
	private ServerSocket serverSocket = null;
	private Socket clientSocket;
	private boolean serverRunning = true;
	private ConcurrentHashMap<String, List<Result>> dictionary;
	
    public Server(int port) {
    	
    	InetAddress ip = null;
		try {
			ip = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(0);
		}
    	
    	// create server socket if port isn't used
    	try {
        	serverSocket = new ServerSocket(port, 100, ip); 
        } catch (BindException be) {
        	System.out.println("Port is already being used. Quitting.");
        	System.exit(0);
        } catch (IOException ioe) {
        	System.out.println("ServerSocket was not able to be created. Quitting.");
        	System.exit(0);
        }
    	
    	DictFileReader reader = new DictFileReader();
    	dictionary = reader.readDict();
    	
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
	            
	            // Create a new handler object for handling this request. **TODO: Implement worker pool
	            WorkerRunnable worker = new WorkerRunnable(clientSocket, dictionary); 
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