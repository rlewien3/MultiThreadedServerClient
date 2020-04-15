package server;

import java.io.*; 
import java.net.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; 
  
/**
 * Multithreaded server
 * Created by Ryan Lewien
 * 746528
 * For Distributed Systems (COMP90015)
 * The University of Melbourne
 */
public class Server implements Runnable { 
   
	private final int poolSize = 3;
	private final String dictLocation = "C:\\Users\\rlewi\\Documents\\MultiThreadedServerClient\\src\\server\\data.json";
	private int port;
	private String ipAddress;
	
	private ServerSocket serverSocket = null;
	private Socket clientSocket;
	private boolean serverRunning = false;
	private ConcurrentHashMap<String, List<Result>> dictionary;
	protected ExecutorService workerThreads = Executors.newFixedThreadPool(poolSize);
	
    public Server(int port, String ipAddress) {
    	
    	// Read in dictionary
    	DictFileReader reader = new DictFileReader();
    	dictionary = reader.readDict(dictLocation);
    	
    	this.port = port;
    	this.ipAddress = ipAddress;
    }
    
    
    public static void main(String[] args) throws IOException { 

    	// to be command line arguments later?
    	final int port = 1234;
    	final String ipAddress = "127.0.0.1";
    	
    	System.out.println("SERVER\nOn port: " + port + "\n");
    	Server server = new Server(port, ipAddress);
    	server.run();
    }
    
    
    @Override
    public void run() {
    	serverRunning = true;
    	openServerSocket();
    	delegateRequests();
    }
    
    
    /**
     * Opens the server socket at a specified port and ip address
     */
    private void openServerSocket() {
    	
    	// Get ip address
    	InetAddress ip = null;
		try {
			ip = InetAddress.getByName(ipAddress);
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
    }
    
    
    /**
     * Delegates requests to worker threads.
     */
    private void delegateRequests() {
    	// running infinite loop for getting client request 
        while (isRunning()) {
            
        	try {
	        	// Accept the incoming request 
	            clientSocket = serverSocket.accept();
	            System.out.println("New client request received: " + clientSocket); 
	            
	            // Create a new handler object for handling this request.
	            workerThreads.execute(new WorkerRunnable(clientSocket, this));
	            
        	} catch(IOException ioe) {
        		System.out.println("Exception found on accept. Ignoring. Stack Trace:"); 
                ioe.printStackTrace();
        	}
        }
        stop();
    }
    
    /**
     * Dictionary getter
     */
    public synchronized ConcurrentHashMap<String, List<Result>> getDictionary() {
    	return dictionary;
    }
    
    /**
     * Checks if server is running
     */
    public synchronized boolean isRunning() {
    	return serverRunning;
    }
    
    /**
     * Stops the server
     */
    public synchronized void stop() {
    	if (serverRunning) {
    		serverRunning = false;
    		workerThreads.shutdownNow();
        	try {
        		serverSocket.close();
        	} catch (IOException e) {
        		System.out.println("Error closing server");
        	}
    	}
    	System.out.println("Server stopped.");
    }
} 