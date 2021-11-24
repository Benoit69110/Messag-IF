package back.server;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.LinkedList;
/***
 * Server TCP
 * @author: balgourdin, gdelambert, malami
 */
public class ServerMultiThreaded implements ConnectionListener{
	/** Passsphrase used to encrypt messages */
	private static byte[] SECRET_KEY;
	/** Algorithm used to encrypt messages */
	private static String ALGORITHM;
	/** Key used to encrypt messages */
	private SecretKeySpec sks;
	/** Server socket */
	private ServerSocket server;
	/** Listening port */
	private int port;
	/** List of connected clients */
	private LinkedList<ClientThread> clients;
	/** Output stream of the sever */
	private PrintWriter out;
	private String PATH_LOGS="logs/";
	/** Historic with all the conversation */
	private File conversationAll;
	/** Thread to handle connexion */
	private ClientConnectedThread connectedClientsThread;
	private ConnectionListener conL;

	/**
	 * Constructor
	 * @param conL
	 */
	public ServerMultiThreaded(ConnectionListener conL){
		this.conL=conL;
		clients=new LinkedList<>();
		// Get the configuration (algorithm and passphrase) to encrypt messages
		try{
			BufferedReader fileReader=new BufferedReader(new FileReader("config/config.txt"));
			String line;
			int nbLines=0;
			while((line=fileReader.readLine())!=null){
				line=line.split("=")[1];
				if(nbLines==0){
					SECRET_KEY=line.getBytes();
				}else if(nbLines==1){
					ALGORITHM=line;
				}
				nbLines++;
			}
		}catch (IOException e){
			e.printStackTrace();
		}
		// The key length has to be 16 bytes
		sks= new SecretKeySpec(SECRET_KEY, ALGORITHM);

		conversationAll=new File(PATH_LOGS+"serverLogs.txt");
		connectedClientsThread=new ClientConnectedThread(this);
	}

	/**
	 * Method called to start the server
	 * @param port
	 */
	public synchronized void start(int port) {
		this.port=port;
		try {
			// Creation of the server socket
			server=new ServerSocket(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Server ready...");
		// New thread to handle connexions
		ConnectionThread connectionThread=new ConnectionThread(this);
		// Start the thread
		connectionThread.start();
		System.out.println("End server call...");
	}

	/**
	 * Stop the server and all threads
	 */
	public synchronized void stop() {
		try {
			server.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}

	/**
	 * Send a file which is basically the historic of a conversation
	 * @param client
	 * @param file
	 */
	public void sendHistoric(Socket client,File file){
		try{
			BufferedReader fileReader=new BufferedReader(new FileReader(file));
			out=new PrintWriter(client.getOutputStream(),true);
			String line;
			while((line=fileReader.readLine())!=null){
				// Decrypt the line of the file
				line=decrypt(line);
				line=line.split("- ")[1];
				// Send the line encrypted without the date
				out.println(encrypt(line));
				out.flush();
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Clear the file
	 */
	public void clearHistoric(){
		File historic = new File("logs/serverLogs.txt");
		if(historic.exists()){
			try {
				FileWriter myWriter=new FileWriter(historic);
				myWriter.write("");
				myWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add a client to the map
	 * @param newClient
	 */
	public void addClientToFirstPrivateMessage(ClientThread newClient){
		for(ClientThread client:clients){
			if(client!=newClient){
				client.getFirstPrivateMessage().put(newClient.getPseudo(), true);
			}
		}
	}

	/**
	 * Method to accept all clients ,called in the connexionThread
	 */
	public void acceptClient(){
		System.out.println("Waiting for connexion...");
		try {
			while(true){
				// Accept the connexion
				Socket clientSocket = server.accept();
				// Create the thread to communicate
				ClientThread ct=new ClientThread(clientSocket,this);
				// Add the client to the list
				clients.add(ct);
				// Start the thread
				ct.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Find a client with a pseudo
	 * @param pseudo
	 * @return
	 */
	public ClientThread getClientByPseudo(String pseudo){
		for(ClientThread client:clients){
			if(pseudo.equals(client.getPseudo())){
				return client;
			}
		}
		return null;
	}
	public LinkedList<ClientThread> getClients(){
		return clients;
	}

	public ClientConnectedThread getConnectedClientsThread(){
		return connectedClientsThread;
	}

	/**
	 * Broadcast a private message
	 * @param sender client sender
	 * @param dest client receiver
	 * @param msg message to send
	 * @param toSave true if we want to save the message
	 */
	public void broadcastTo(ClientThread sender,ClientThread dest, String msg, boolean toSave){
		try{
			// Save the private conversation in the file named "pseudoSenderPseudoReceiver"
			// or "pseudoReceiverPseudoSender"
			if (toSave) {
				String senderPseudo= sender.getPseudo();
				String destPseudo= dest.getPseudo();
				File convFile=new File(PATH_LOGS+senderPseudo+destPseudo+".txt");
				if(convFile.exists()){
					saveMessage(sender,convFile,msg);
				}else{
					convFile=new File(PATH_LOGS+destPseudo+senderPseudo+".txt");
					saveMessage(sender,convFile,msg);
				}
			}
			// Send the message encrypted to the sender and the receiver
			out=new PrintWriter(dest.getClientSocket().getOutputStream(),true);
			out.println(encrypt("private "+msg));
			out.flush();
			out=new PrintWriter(sender.getClientSocket().getOutputStream(),true);
			out.println(encrypt("private "+msg));
			out.flush();
		}catch(IOException e){
			// e.printStackTrace();
		}
	}

	/**
	 * Send a message to evrybody
	 * @param sender client sender
	 * @param msg message to send
	 * @param toSave true if we want to save the message
	 */
	public void broadcast(ClientThread sender, String msg, boolean toSave, File convFile){
		try{
			if (toSave) {
				saveMessage(sender,convFile,msg);
			}
			// Broadcast to all clients
			for(int i=0;i<clients.size();i++){
				if(clients.get(i).getPseudoSetted()){
					out=new PrintWriter(clients.get(i).getClientSocket().getOutputStream(),true);
					out.println(encrypt(msg));
					out.flush();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Broadcast pseudos of connected clients
	 */
	public void broadcastPseudos(){
		try{
			for(ClientThread client:clients){
				out=new PrintWriter(client.getClientSocket().getOutputStream(),true);
				out.println(encrypt("Clients connected (start)"));
				for(ClientThread pseudoClient:clients){
					out.println(encrypt(pseudoClient.getPseudo()));
				}
				out.println(encrypt("Clients connected (end)"));
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Save a message in a file
	 * @param sender
	 * @param file
	 * @param message
	 */
	public void saveMessage(ClientThread sender,File file,String message){
		DateTimeFormatter dtf= DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		LocalDateTime now=LocalDateTime.now();
		try {
			FileWriter myWriter=new FileWriter(file,true);
			// Write the message encrypted in the file
			myWriter.write(encrypt(dtf.format(now)+" - "+message)+"\r\n");
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Encrypt the message with the algorithm and the key generated in the constructor
	 * @param valueToEncrypt plain message
	 * @return message encrypted
	 */
	public String encrypt(String valueToEncrypt){
		String res="";
		try{
			Cipher c=Cipher.getInstance(ALGORITHM);
			c.init(Cipher.ENCRYPT_MODE,sks);
			byte[] encVal=c.doFinal(valueToEncrypt.getBytes());
			res=new String(Base64.getEncoder().encode(encVal));
		}catch (Exception e){
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * Decrypt the value encrypted
	 * @param encryptedValue encrypted message
	 * @return message decrypted
	 */
	public String decrypt(String encryptedValue){
		String decr="";
		try{
			Cipher c=Cipher.getInstance(ALGORITHM);
			c.init(Cipher.DECRYPT_MODE,sks);
			byte[] decryptedVal=Base64.getDecoder().decode(encryptedValue);
			byte[] decvValue= c.doFinal(decryptedVal);
			decr=new String(decvValue);
		}catch (Exception e){
			e.printStackTrace();
		}

		return decr;
	}

	@Override
	public void onClientAccepted(ClientThread client) {
		addClientToFirstPrivateMessage(client);
		System.out.println(client.getPseudo()+" has joined the server !");
		broadcast(client,client.getPseudo()+" has joined the server !",false, null);
		sendHistoric(client.getClientSocket(),conversationAll);
		connectedClientsThread.sendConnectedPseudo();
		conL.onClientAccepted(client);
	}

	@Override
	public void onClientDisconnected(ClientThread client) {
		System.out.println(client.getPseudo()+" has left the server.");
		clients.remove(client);
		broadcast(client,client.getPseudo()+" has left the server !",false, null);
		connectedClientsThread.sendConnectedPseudo();
		conL.onClientDisconnected(client);
	}

	@Override
	public void acknowledge(String report) {
		conL.acknowledge(report);
	}

	public File getHistoricFile(){
		return conversationAll;
	}
 	/**
  	* main method
  	* 
  	**/
	 public static void main(String args[]){
		 ServerMultiThreaded server=new ServerMultiThreaded(new ConnectionListener() {
			 public void onClientAccepted(ClientThread client) {}
			 public void onClientDisconnected(ClientThread client) {}
			 public void acknowledge(String report) {}
		 });
		 server.start(8084);
		 server.stop();
		 //server.clearHistoric(new File("logs/serverLogs.txt"));
	 }



}

  
