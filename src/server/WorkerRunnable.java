package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

// ClientHandler class 
class WorkerRunnable implements Runnable  
{ 
    Scanner scn = new Scanner(System.in); 
    final DataInputStream input; 
    final DataOutputStream output; 
    Socket socket; 
      
    // constructor 
    public WorkerRunnable(Socket socket, DataInputStream input, DataOutputStream output) { 
        this.input = input; 
        this.output = output;
        this.socket = socket; 
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
                  
                /* break the string into message and recipient part 
                StringTokenizer st = new StringTokenizer(received, " "); 
                String MsgToSend = st.nextToken(); 
                String recipient = st.nextToken();
  
                // search for the recipient in the connected devices list. 
                // ar is the vector storing client of active users 
                for (WorkerRunnable mc : Server.ar)  
                {
                    // if the recipient is found, write on its 
                    // output stream 
                    if (mc.name.equals(recipient) && mc.isloggedin==true)  
                    { 
                        mc.dos.writeUTF(this.name+" : " + MsgToSend); 
                        break; 
                    } 
                } */
                
                if (received.equals("QUIT")) {
                	break;
                }
                
                output.writeUTF("Response from Server : Success!");
                
            } catch (IOException e) { 
                  
                e.printStackTrace(); 
            } 
              
        } 
        
        try { 
            // closing resources 
            this.input.close(); 
            this.output.close(); 
        } catch(IOException e) { 
            e.printStackTrace(); 
        } 
    } 
} 