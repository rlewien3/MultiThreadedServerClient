package server;

import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
   
	private final int poolSize = 200;
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
    	System.out.println("Dictionary size: " + dictionary.size());    	
    	this.port = port;
    	this.ipAddress = ipAddress;
    }
    
    
    public static void main(String[] args) throws IOException { 

    	// to be command line arguments later?
    	final int port = 3784;
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
     * Gets a result from the dictionary as a string, if it is in the dictionary
     * Does not return a reference
     */
    public synchronized String getResultString(String word) {
    	if (dictionary.containsKey(word)) {
    		return dictionary.get(word).toString();
    	} else {
    		return null;
    	}
    }
    
    /**
     * Gets a random word from the result from the dictionary as a string, if it is in the dictionary
     * Does not return a reference
     */
    public synchronized String getRandomWord() {
    	
    	List<String> keys = new ArrayList<String>(dictionary.keySet());
    	Random r = new Random();
    	return keys.get(r.nextInt(keys.size()));
    }
    
    /**
     * Adds a word to the dictionary, if the word is not already in it
     * Returns true if it succeeds
     */
    public synchronized boolean addWord(String word, List<Result> results) {
    	if (dictionary.containsKey(word)) {
    		return false;
    	} else {
    		dictionary.put(word, results);
    		return true;
    	}
    }
    
    /**
     * Removes a word to the dictionary, if the word is in it
     * Returns true if it succeeds
     */
    public synchronized boolean removeWord(String word) {
    	if (dictionary.containsKey(word)) {
    		dictionary.remove(word);
    		return true;
    	} else {
    		return false;
    	}
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