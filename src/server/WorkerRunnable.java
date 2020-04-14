package server;

import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Worker thread for a multithreaded server
 * Created by Ryan Lewien
 * 746528
 * For Distributed Systems (COMP90015)
 * The University of Melbourne
 */
class WorkerRunnable implements Runnable { 
    
	// Communication protocol
	private static final String QUERY = "GET";
	private static final String ADD = "PUT";
	private static final String REMOVE = "DEL";
	private static final String PROTOCOL_INSTRUCTIONS = "Begin with either " + QUERY + ", " + ADD + " or " + REMOVE + " and then follow with your word/ definition.";
	private static final String ADD_PROTOCOL_INSTRUCTIONS = "The correct form is <word>~~~<new definition>.";
	
	Scanner scn = new Scanner(System.in); 
    private DataInputStream input; 
    private DataOutputStream output; 
    
    ConcurrentHashMap<String, List<Result>> dictionary;
      
    // constructor 
    public WorkerRunnable(Socket clientSocket, ConcurrentHashMap<String, List<Result>> dictionary) { 
        
    	this.dictionary = dictionary;
    	
    	System.out.println("Creating a new handler for this client..."); 
        
    	// obtain input and output streams 
		try {
    		input = new DataInputStream(clientSocket.getInputStream()); 
            output = new DataOutputStream(clientSocket.getOutputStream()); 
    	} catch (IOException ioe) {
    		System.out.println("Exception found on opening client input and output streams. Ignoring."); 
            ioe.printStackTrace(); 
    	}
    }
  
    @Override
    public void run() { 
  
    	System.out.println("New worker running!");
    	
        String received; 
        while (true) { 
            
        	try { 
                // receive the string 
                received = input.readUTF(); 
                System.out.println("Received: " + received);
                
                if (received.equals("QUIT")) {
                	// end the connection
                	break;
                }
                
                String reply = null;
                
                // break the string into command and recipient part 
                String[] splitReceived = received.split(" ", 2);
                if (splitReceived.length == 2) {
                	
                	String command = splitReceived[0];
                    String message = splitReceived[1].toLowerCase();
                    
                    // handle queries
                    if (command.equals(QUERY)) {
                    	
                    	System.out.println("Looking for " + message + " in dictionary...");
                    	if (dictionary.containsKey(message)) {
                    		reply = dictionary.get(message).toString();
                    	} else {
                    		reply = "Word not found.";
                    	}
                    }
                    
                    // handle additions
                    else if (command.equals(ADD)) {
                    	
                    	String[] splitMessage = message.split("~~~", 2);
                    	
                    	if (splitMessage.length == 2) {
                    		String word = splitMessage[0];
                            String definition = splitMessage[1];
                        	
                        	if (dictionary.containsKey(word)) {
                        		// add definition to word?
                        		reply = "Dictionary already contains the word " + word;
                        	} else {
                        		
                        		// inputting multiple definitions at once?
                        		
                        		Result newResult = new Result();
                        		newResult.setDefinition(definition);
                        		
                        		List<Result> newResults = new ArrayList<Result>(1);
                        		newResults.add(newResult);
                        		
                        		dictionary.put(word, newResults);
                        		reply = word + " successfully added to the dictionary!";
                        	}
                    	} else {
                    		reply = "Invalid adding query! " + ADD_PROTOCOL_INSTRUCTIONS;
                    	}
                        
                    }
                    
                    // handle removals
                    else if (command.equals(REMOVE)) {
                    	if (!dictionary.containsKey(message)) {
                    		reply = "Dictionary does not contain the word " + message;
                    	} else {
                    		dictionary.remove(message);
                    		reply = message + " successfully removed to the dictionary!";
                    	}
                    	
                    } else {
                    	reply = "Invalid query! " + PROTOCOL_INSTRUCTIONS;
                    }
                } else {
                	reply = "Invalid query! " + PROTOCOL_INSTRUCTIONS; 
                }
                
                // Send reply
                output.writeUTF(reply);
                
        	} catch (SocketException se) {
            	System.out.println("Client closed without quitting. Closing worker. ");
            	break;
            } catch (IOException e) {
                e.printStackTrace(); 
            }
        } 
        
        try { 
            // closing resources 
            this.input.close(); 
            this.output.close(); 
        } catch(IOException e) { 
        	System.out.println("Error found stopping server socket"); 
        	e.printStackTrace(); 
        }
    }
}