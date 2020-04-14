package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * Worker thread for a multithreaded server
 * Created by Ryan Lewien
 * 746528
 * For Distributed Systems (COMP90015)
 * The University of Melbourne
 */
class WorkerRunnable implements Runnable { 
    
	Scanner scn = new Scanner(System.in); 
    private DataInputStream input; 
    private DataOutputStream output; 
      
    // constructor 
    public WorkerRunnable(Socket clientSocket) { 
        // obtain input and output streams 
    	
    	System.out.println("Creating a new handler for this client..."); 
        
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
                  
                /* break the string into command and recipient part 
                StringTokenizer str = new StringTokenizer(received, " "); 
                String command = str.nextToken(); 
                String data = str.nextToken();
                */
                
                if (received.equals("QUIT")) {
                	// end the connection
                	break;
                }
                
                output.writeUTF("Success!");
                
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