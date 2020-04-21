package server;

import java.io.*; 
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.Result; 
  
/**
 * Multithreaded server for a dictionary client
 * Created by Ryan Lewien
 * 746528
 * For Distributed Systems (COMP90015)
 * The University of Melbourne
 */
public class Server implements Runnable { 
   
	private final int poolSize = 200;
	private String dictLocation;
	private int port;
	
	private ExecutorService workerThreads;
	private Thread acceptClients;
	
	private boolean serverRunning;
	
	private ServerSocket serverSocket = null;
	private ArrayList<Socket> clientSockets;
	private ConcurrentHashMap<String, List<Result>> dictionary;
	private ServerView view;
	
	
    public Server() {
    	serverRunning = false;
    	view = new ServerView(this);
    }
    
    
    public static void main(String[] args) throws IOException { 

    	Server server = new Server();
    	server.run();
    }
    
    @Override
    public void run() {
    	view.run();
    }
    
    /**************************************************************************************************
     * 
     * 									Called from Server View
     * 
     *************************************************************************************************/
    
    public void setPort(String portText) {
    	try {
    		this.port = Integer.parseInt(portText);
    	} catch (NumberFormatException e) {
    		this.port = -1;
    	}
    }
    
    public void setDictLocation(String dictLocation) {
    	this.dictLocation = dictLocation;
    }
    
    /**
     * Starts the server, relies on port and dictionary location already being set
     */
    public void startServer() {
    	
    	// Check port is valid
    	if (port <= 0) {
        	view.showError("Port number is invalid! Try another.");
        	return;
        }
    	
    	// Read in dictionary, if valid
        dictionary = readDict(dictLocation);
        if (dictionary == null) {
        	view.showError("Failed reading the dictionary at that path. Try another!");
        	return;
        }
        
        workerThreads = Executors.newFixedThreadPool(poolSize);
        
    	if (openServerSocket()) {
    		// Server socket is open!
    		serverRunning = true;
    		delegateRequests();
    		view.showRunning();
    	};
    }
    
    
    /**************************************************************************************************
     * 
     * 									  Public Server Methods
     * 
     *************************************************************************************************/
    
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
     * @return 
     */
    public synchronized void stopServer() {
		
    	try {
    		workerThreads.shutdown();
    		
    		// Close all client sockets
    		for (Socket clientSocket : clientSockets) {
    			clientSocket.close();
    		}
    		serverSocket.close();
    	} catch (IOException | NullPointerException e) {
    		view.showError("Error stopping server");
    	}

		serverRunning = false;
    	view.showError("Server stopped.");
    }
    
    
    /**************************************************************************************************
     * 
     * 										Helper Methods
     * 
     *************************************************************************************************/
    
    /**
     * Reads a dictionary file at a specified dataPath
     */
    private ConcurrentHashMap<String, List<Result>> readDict(String dataPath) {
		
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
     * Opens the server socket at a specified port
     * Returns whether it succeeds
     */
    private boolean openServerSocket() {
    	
    	// create server socket if port isn't used
    	try {
        	serverSocket = new ServerSocket(port, 100); 
        } catch (BindException be) {
        	view.showError("Port is already being used.");
        	return false;
        } catch (IOException ioe) {
        	view.showError("ServerSocket was not able to be created.");
        	return false;
        }
    	
    	return true;
    }
    
    
    /**
     * Delegates requests to worker threads.
     */
    private void delegateRequests() {

    	clientSockets = new ArrayList<Socket>();
    	Server server = this;
    	acceptClients = new Thread() {
    		
    		@Override
    	    public void run() { 

    	        while (isRunning()) {
    	            
    	        	Socket newClientSocket;
    	        	try { 
    	            	// Accept the incoming request 
    		            newClientSocket = serverSocket.accept();
    		            clientSockets.add(newClientSocket);
    		            
    		            // Create a new handler object for handling this request.
        	            workerThreads.execute(new WorkerRunnable(newClientSocket, server));
    	            } catch (IOException ioe) {
    	        		System.out.println("Exception found on accept. Ignoring.");
    	        	}
    	         } 
    	    }
    	};
        acceptClients.start();
    }
}