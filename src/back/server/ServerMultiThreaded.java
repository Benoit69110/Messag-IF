/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package back.server;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class ServerMultiThreaded {
	private ServerSocket server;
	private int port;
	private LinkedList<Conversation> conversations;
	private LinkedList<ClientThread> clients;

	public ServerMultiThreaded(){
	}

	public synchronized void start(int port) {
		this.port=port;
		clients=new LinkedList<>();
		try {
			server=new ServerSocket(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Server ready...");
	}

	public synchronized void stop() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void acceptClient(){
		try {
			while(true){
				Socket clientSocket = server.accept();
				System.out.println("Connexion from:" + clientSocket.getInetAddress());
				ClientThread ct=new ClientThread(clientSocket);
				clients.add(ct);
				ct.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


 	/**
  	* main method
  	* 
  	**/
	 public static void main(String args[]){
		 ServerMultiThreaded server=new ServerMultiThreaded();
		 server.start(8084);
		 server.acceptClient();
	 }
  }

  
