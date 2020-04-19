package server;

import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import client.ClientView;
import common.Result; 
  
/**
 * Multithreaded server
 * Created by Ryan Lewien
 * 746528
 * For Distributed Systems (COMP90015)
 * The University of Melbourne
 */
public class Server implements Runnable { 
   
	private final int poolSize = 200;
	private int port = 0;
	private String ipAddress;
	private Thread acceptClients;
	
	private String dictLocation;
	
	private ServerSocket serverSocket = null;
	private Socket clientSocket;
	private boolean serverRunning;
	
	private ConcurrentHashMap<String, List<Result>> dictionary;
	protected ExecutorService workerThreads = Executors.newFixedThreadPool(poolSize);
	
	private ServerView view;
	
    public Server(String ipAddress) {
    	
    	this.ipAddress = ipAddress;
    	serverRunning = false;
    	view = new ServerView(this);
    }
    
    
    public static void main(String[] args) throws IOException { 

    	// to be command line arguments later?
    	final String ipAddress = "127.0.0.1";
    	
    	System.out.println("SERVER\n");
    	Server server = new Server(ipAddress);
    	server.run();
    }
    
    
    public void setPort(int port) {
    	this.port = port;
    }
    
    public void setDictLocation(String dictLocation) {
    	this.dictLocation = dictLocation;
    }
    
    
    @Override
    public void run() {

    	view.run();
    }
    
    /**
     * Starts the server
     */
    public void startServer() {
    	
    	// Read in dictionary
        dictionary = readDict(dictLocation);
        if (dictionary == null) {
        	view.showError("Failed reading the dictionary at that path. Try another!");
        	return;
        }
        view.showSuccess("Read dictionary!");
    	
    	serverRunning = true;
    	openServerSocket();
    	delegateRequests();
    	view.showSuccess("Server Running!");
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
			view.showError("IPAddress not allowed.");
		}
    	
    	// create server socket if port isn't used
    	try {
        	serverSocket = new ServerSocket(port, 100, ip); 
        } catch (BindException be) {
        	view.showError("Port is already being used.");
        } catch (IOException ioe) {
        	view.showError("ServerSocket was not able to be created.");
        }
    }
    
    
    /**
     * Delegates requests to worker threads.
     */
    private void delegateRequests() {
    	// running infinite loop for getting client request 
    	
    	Server server = this;
        
    	acceptClients = new Thread(new Runnable() { 
            
        	@Override
            public void run() { 
        		while (isRunning()) {
                	try {
        	        	// Accept the incoming request 
        	            clientSocket = serverSocket.accept();
        	            System.out.println("New client request received: " + clientSocket); 
        	            
        	            // Create a new handler object for handling this request.
        	            workerThreads.execute(new WorkerRunnable(clientSocket, server));
        	            
                	} catch (IOException ioe) {
                		System.out.println("Exception found on accept. Ignoring.");
                	}
                }
                stopServer();
            }
        });
        acceptClients.start();
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
     * Reads a dictionary file at a specified dataPath
     */
    public ConcurrentHashMap<String, List<Result>> readDict(String dataPath) {
		
    	File dataFile = FileUtils.getFile(dataPath);
		
    	// Collect file into a string
		String str = null;
		try {
			str = FileUtils.readFileToString(dataFile, "utf-8");
		} catch (IOException | NullPointerException e) {
			return null;
		}
		
	    JSONObject words = JSONObject.parseObject(str);
	    
	    // Parse JSON object into dictionary form
	    ConcurrentHashMap<String, List<Result>> dictionary = new ConcurrentHashMap<String, List<Result>>();
	    for (String word : words.keySet()) {
	    	
	    	JSONObject wordData = JSONObject.parseObject(words.get(word).toString());

	    	if (wordData.containsKey("definitions")) {
	    		dictionary.put(word, JSONArray.parseArray(wordData.get("definitions").toString(), Result.class));
	    	}
	    }
	    
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
     * @return 
     */
    public synchronized void stopServer() {
    	if (serverRunning) {
    		serverRunning = false;
    		workerThreads.shutdownNow();
    		// acceptClients.shutdown();
        	try {
        		serverSocket.close();
        	} catch (IOException e) {
        		view.showError("Error closing server");
        	}
    	}
    	view.showError("Server stopped.");
    }
} 