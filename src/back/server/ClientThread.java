package back.server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.StringTokenizer;


/***
 * ClientThread
 * Thread which manage the conversation of a client
 * @author balgourdin, gdelambert, malami
 */
public class ClientThread extends Thread {
	/** Remote socket of the client*/
	private Socket clientSocket;
	/** Pseudo of the client */
	private String pseudo;
	private boolean pseudoSetted=false;
	/** Input stream of the client */
	private BufferedReader socIn;
	/** Output stream of the client*/
	private PrintStream socOut;
	/** Server */
	private ServerMultiThreaded server;
	private HashMap<String,Boolean> firstPrivateMessage;
	private String PATH_LOGS="logs/";

	/**
	 * Constructor
	 * @param s
	 * @param server
	 */
	ClientThread(Socket s,ServerMultiThreaded server) {
		firstPrivateMessage=new HashMap<>();
		this.clientSocket = s;
		this.server=server;
		// Get streams of the socket
		try {
			socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			socOut = new PrintStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Manage the broadcast of message when the thread is running
	 */
	@Override
	public void run(){
		try{

			BufferedReader br=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String IP=clientSocket.getRemoteSocketAddress().toString();
			System.out.println("Connexion du client IP : "+IP);

			String line;
			while((line=br.readLine())!=null){//Get the message received
				if(line instanceof String){
					// Decrypt the message
					line=server.decrypt(line);
					if(!pseudoSetted){
						// Check if the pseudo is not already used
						if(pseudoExist(line)){
							socOut.println(server.encrypt("This pseudo is already used. Disconnect and choose another pseudo."));
						}else {
							pseudo = line;
							pseudoSetted = true;
							socOut.println(server.encrypt("Your pseudo is "+pseudo));
							server.onClientAccepted(this);
						}
					}else{
						// Check if the message is a private one
						StringTokenizer tokens=new StringTokenizer(line);
						if(tokens.countTokens()>2 && tokens.nextToken().equals("private")){
							String sendTo=tokens.nextToken();
							if(pseudoExist(sendTo)){
								ClientThread clientDest= server.getClientByPseudo(sendTo);
								String msg="";
								while(tokens.hasMoreTokens()){
									msg+=tokens.nextToken()+" ";
								}
								// Send the message to the client
								server.broadcastTo(this,clientDest,pseudo+" : "+msg,true);
							}
						}else{
							// Send the message to everyone
							server.broadcast(this,pseudo+" : "+line,true,server.getHistoricFile());
						}
					}
				}
			}
		}catch (Exception e){
			System.err.println("Error in Server:" + e);
			// e.printStackTrace();
		}
		server.onClientDisconnected(this);
	}

	/**
	 * Check if the pseudo already exists
	 * @param pseudo
	 * @return
	 */
	public boolean pseudoExist(String pseudo){
		boolean pseudoExist=false;
		for(ClientThread client:server.getClients()){
			if(client.getPseudo()!=null && client!=this && /*client.getPseudo().equals("Anonymous")
					&&*/ client.getPseudo().equals(pseudo)){
				pseudoExist=true;
			}
		}
		return pseudoExist;
	}

	public HashMap<String,Boolean> getFirstPrivateMessage() {
		return firstPrivateMessage;
	}

	public boolean getPseudoSetted() {
		return pseudoSetted;
	}

	public String getPseudo() {
		return pseudo;
	}


	public Socket getClientSocket() {
		return clientSocket;
	}

	public PrintStream getSocOut() {
		return socOut;
	}

}

  
