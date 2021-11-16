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
	private Conversation conversation;
	private LinkedList<ClientThread> clients;
	private PrintWriter out;

	public ServerMultiThreaded(){
		clients=new LinkedList<>();
	}

	public synchronized void start(int port) {
		this.port=port;
		try {
			server=new ServerSocket(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Server ready...");
		acceptClient();
	}

	public synchronized void stop() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void acceptClient(){
		System.out.println("Waiting for connexion...");
		try {
			while(true){
				Socket clientSocket = server.accept();
				System.out.println("Connexion from:" + clientSocket.getInetAddress());
				ClientThread ct=new ClientThread(clientSocket,this);
				clients.add(ct);
				ct.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public LinkedList<ClientThread> getClients(){
		return clients;
	}

	public void broadCast(ClientThread sender,String msg){
		try{
			for(int i=0;i<clients.size();i++){
				if(clients.get(i)!=sender){
					out=new PrintWriter(clients.get(i).getClientSocket().getOutputStream(),true);
					out.println(msg);
					out.flush();
				}
			}
		}catch(IOException e){
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

  
