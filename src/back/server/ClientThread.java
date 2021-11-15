/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package back.server;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {
	
	private Socket clientSocket;
	private String pseudo;
	boolean pseudoSetted=false;
	
	ClientThread(Socket s) {
		this.clientSocket = s;
	}

 	/**
  	* receives a request from client then sends an echo to the client
  	**/
	public synchronized void run() {
    	  try {
			  BufferedReader socIn = null;
			  socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    		  PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
    		  while (true) {
				  String line = socIn.readLine();
				  if(!pseudoSetted){
					  pseudo=line;
					  pseudoSetted=true;
					  System.out.println(line);
				  }else{
					  socOut.println(line);
				  }
				  System.out.println("je m'appelle "+pseudo);
				  socOut.flush();
			  }
		  } catch (Exception e) {
			  System.err.println("Error in EchoServer:" + e);
		  }
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
}

  
