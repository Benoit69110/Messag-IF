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
	private boolean pseudoSetted=false;
	private BufferedReader socIn;
	private PrintStream socOut;
	
	ClientThread(Socket s) {
		this.clientSocket = s;
		try {
			socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			socOut = new PrintStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedReader getSocIn() {
		return socIn;
	}

	public void setSocIn(BufferedReader socIn) {
		this.socIn = socIn;
	}

	public PrintStream getSocOut() {
		return socOut;
	}

	public void setSocOut(PrintStream socOut) {
		this.socOut = socOut;
	}

	/**
  	* receives a request from client then sends an echo to the client
  	**/
	public synchronized void run() {
    	  try {
    		  while (true) {
				  String line = socIn.readLine();
				  if(!pseudoSetted){
					  pseudo=line;
					  pseudoSetted=true;
					  System.out.println(line);
				  }else{
					  socOut.println(line);
				  }
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

  
