/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package back.server;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.LinkedList;

public class ServerMultiThreaded {
	private ServerSocket server;
	private int port;
	private Conversation conversation;
	private LinkedList<ClientThread> clients;
	private PrintWriter out;
	private String LOGS_FILE="logs/serverLogs.txt";
	private File conversationAll;
	private ClientConnectedThread connectedClientsThread;

	public ServerMultiThreaded(){
		clients=new LinkedList<>();
		conversationAll=new File(LOGS_FILE);
		connectedClientsThread=new ClientConnectedThread(this);
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

	public void sendHistoric(Socket client,File file){
		try{
			BufferedReader fileReader=new BufferedReader(new FileReader(file));
			out=new PrintWriter(client.getOutputStream(),true);
			String line;
			while((line=fileReader.readLine())!=null){
				line=line.split("- ")[1];
				out.println(line);
				out.flush();
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public void acceptClient(){
		System.out.println("Waiting for connexion...");
		try {
			while(true){
				Socket clientSocket = server.accept();
				//System.out.println("Connexion from:" + clientSocket.getInetAddress());
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

	public ClientConnectedThread getConnectedClientsThread(){
		return connectedClientsThread;
	}

	public void broadcast(ClientThread sender, String msg, boolean toSave){
		try{
			if (toSave) {
				saveMessage(sender,conversationAll,msg);
			}
			for(int i=0;i<clients.size();i++){
				if(clients.get(i).getPseudoSetted()){
					out=new PrintWriter(clients.get(i).getClientSocket().getOutputStream(),true);
					out.println(msg);
					out.flush();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void broadcastPseudos(){
		try{
			for(ClientThread client:clients){
				out=new PrintWriter(client.getClientSocket().getOutputStream(),true);
				out.println("Clients connected (start)");
				for(ClientThread pseudoClient:clients){
					out.println(pseudoClient.getPseudo());
				}
				out.println("Clients connected (end)");
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	public void saveMessage(ClientThread sender,File file,String message){
		DateTimeFormatter dtf= DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		LocalDateTime now=LocalDateTime.now();
		try {
			FileWriter myWriter=new FileWriter(file,true);
			myWriter.write(dtf.format(now)+" - "+message+"\n");
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getHistoricFile(){
		return conversationAll;
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

  
