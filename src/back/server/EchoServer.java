/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package back.server;

import java.io.*;
import java.net.*;

public class EchoServer  {
 	/**
  	* receives a request from client then sends an echo to the client
  	* @param clientSocket the client socket
  	**/
	static void doService(Socket clientSocket,int nbMessages) {
		try {

			BufferedReader socIn = null;
			socIn = new BufferedReader(
				new InputStreamReader(clientSocket.getInputStream()));

			PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
			boolean lifeClient=true;
			while (lifeClient) {
			  String line = socIn.readLine();
			  if(line.equals("bye")){
				  lifeClient=false;
				  socOut.println("See you soon...");
				  clientSocket.close();
				  System.out.println(clientSocket.getInetAddress()+" has been disconnected");
			  }else{
				  nbMessages++;
				  socOut.println("Message n°"+nbMessages+" : "+line);
			  }
			}
	  	} catch (Exception e) {
			System.err.println("Error in EchoServer:" + e);
	  	}
   	}
  
 	/**
  	 * main method
	 *  args[0] = port
  	**/
	 public static void main(String args[]){
        ServerSocket listenSocket;
		if (args.length != 1) {
			  System.out.println("Usage: java EchoServer <EchoServer port>");
			  System.exit(1);
		}
		try {
			int port=Integer.parseInt(args[0]);
			listenSocket = new ServerSocket(port); //port
			System.out.println("EchoServer listening on "+port);
			while (true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("connexion from:" + clientSocket.getInetAddress());
				doService(clientSocket,0);
			}
		} catch (Exception e) {
			System.err.println("Error in EchoServer:" + e);
		}
  	}
  }

  
