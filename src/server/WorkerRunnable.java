package server;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import common.Result;

/**
 * Worker thread for a multithreaded dictionary server
 * Created by Ryan Lewien
 * 746528
 * For Distributed Systems (COMP90015)
 * The University of Melbourne
 */
class WorkerRunnable implements Runnable { 
    
	// Communication Protocol
	// client side
	private static final String QUERY = "GET";
	private static final String ADD = "PUT";
	private static final String REMOVE = "DEL";
	private static final String RANDOM = "RDM"; // to ask for a random word
	private static final String SEPARATOR = "~~~"; // to separate two arguments in an add query
	// server side
	private static final String ERROR = "ERR ";
	private static final String SUCCESS = "OK ";
	private static final String QUERY_RESPONSE = "RES ";
	private static final String RDM_RESPONSE = "RDM ";
	
	Scanner scn = new Scanner(System.in); 
    private DataInputStream input; 
    private DataOutputStream output; 
    
    private Server server;
      
    public WorkerRunnable(Socket clientSocket, Server server) { 
        
    	this.server = server;
        
    	// obtain input and output streams 
		try {
    		input = new DataInputStream(clientSocket.getInputStream()); 
            output = new DataOutputStream(clientSocket.getOutputStream()); 
    	} catch (IOException ioe) {
            System.out.println("clientSocket closed.");
    	}
    }
  
    @Override
    public void run() { 
  
    	while (true) {
	        String received; 
	
	        // receive the string 
	    	try { 
	            received = input.readUTF(); 
	    	
	        // close the worker input and output if unable to receive
	    	} catch (IOException se) {
	        	try {
					input.close();
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        	return;
	    	}
	        
	        String reply = null;
	        
	        // handle random word requests (single word request)
	        if (received.equals(RANDOM)) {
	        	reply = randomWord();
	        } else {
	        	// handle multiple word requests
		        String[] splitReceived = received.split(" ", 2);
		        if (splitReceived.length == 2) {
		        	
		        	String command = splitReceived[0];
		            String message = splitReceived[1].toLowerCase();
		            
		            // handle queries
		            if (command.equals(QUERY)) {
		            	reply = queryWord(message);
		            }
		            
		            // handle additions
		            else if (command.equals(ADD)) {
		            	reply = addWord(message);
		            }
		            
		            // handle removals
		            else if (command.equals(REMOVE)) {
		            	reply = removeWord(message);
		            
		            } else {
		            	reply = ERROR + "Invalid query!";
		            }
		        } else {
		        	reply = ERROR + "Invalid query!"; 
		        }
		    }
	        
	        // Send reply
	        try {
				output.writeUTF(reply);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    /**************************************************************************************************
     * 
     * 										Helper Methods
     * 
     *************************************************************************************************/
    
    /**
     * Asks the dictionary for the definition of a queried word
     */
    private String queryWord(String word) {
    	
    	String result = server.getResultString(word);
    	
    	if (result != null) {
    		return QUERY_RESPONSE + word + SEPARATOR + result;
    	} else {
    		// word wasn't in the dictionary
    		return ERROR + "\"" + word + "\" not found in the dictionary. Try a different word!";
    	}
    }
    
    /**
     * Adds a word to the dictionary, if it isn't in the dictionary already
     */
    private String addWord(String message) {
    	String[] splitMessage = message.split(SEPARATOR, 2);
    	
    	if (splitMessage.length == 2) {
    		String word = splitMessage[0];
            String definition = splitMessage[1];
    		
            // wrap definition into an array of results to put in dictionary
    		Result newResult = new Result();
    		newResult.setDefinition(definition);
    		List<Result> newResults = new ArrayList<Result>(1);
    		newResults.add(newResult);
            
    		boolean success = server.addWord(word, newResults);
    		
        	if (success) {
        		return SUCCESS + "\"" + word + "\" successfully added to the dictionary!";
        	} else {
        		return ERROR + "Dictionary already contains the word \"" + word + "\"";
        	}
    	} else {
    		return ERROR + "Invalid adding query!";
    	}
    }
    
    /**
     * Removes a word from the dictionary, if it's in the dictionary
     */
    private String removeWord(String word) {
    	
    	boolean success = server.removeWord(word);
    	
    	if (success) {
    		return SUCCESS + word + " successfully removed from the dictionary!";
    	} else {
    		return ERROR + "Dictionary does not contain the word \"" + word + "\"";
    	}
    }
    
    /**
     * Gets a random word and definition from the dictionary
     */
    private String randomWord() {

    	// Keep getting random words to make sure the result hasn't been deleted
    	String result = null;
    	String word = null;
    	while (result == null) {
    		word = server.getRandomWord();
    		result = server.getResultString(word);
    	}
    	
    	String response = RDM_RESPONSE + word + SEPARATOR + result;
    	
    	return response;
    }
}