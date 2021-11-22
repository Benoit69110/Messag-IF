/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package back.server;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.LinkedList;

public class ServerMultiThreaded implements ConnectionListener{
	private static String ALGORITHM;
	private static byte[] SECRET_KEY;
	private SecretKeySpec sks;

	private ServerSocket server;
	private int port;
	private Conversation conversation;
	private LinkedList<ClientThread> clients;
	private PrintWriter out;
	private String PATH_LOGS="logs/";
	private File conversationAll;
	private ClientConnectedThread connectedClientsThread;
	private ConnectionListener conL;

	public ServerMultiThreaded(ConnectionListener conL){
		this.conL=conL;
		clients=new LinkedList<>();
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
				line=decrypt(line);
				line=line.split("- ")[1];
				out.println(encrypt(line));
				out.flush();
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	public void addClientToFirstPrivateMessage(ClientThread newClient){
		for(ClientThread client:clients){
			if(client!=newClient){
				client.getFirstPrivateMessage().put(newClient.getPseudo(), true);
			}
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

	public void broadcastTo(ClientThread sender,ClientThread dest, String msg, boolean toSave){
		try{
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
			out=new PrintWriter(dest.getClientSocket().getOutputStream(),true);
			out.println(encrypt(msg));
			out.flush();
			out=new PrintWriter(sender.getClientSocket().getOutputStream(),true);
			out.println(encrypt(msg));
			out.flush();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void broadcast(ClientThread sender, String msg, boolean toSave, File convFile){
		try{
			if (toSave) {
				saveMessage(sender,convFile,msg);
			}
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

	public void saveMessage(ClientThread sender,File file,String message){
		DateTimeFormatter dtf= DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		LocalDateTime now=LocalDateTime.now();
		try {
			FileWriter myWriter=new FileWriter(file,true);
			myWriter.write(encrypt(dtf.format(now)+" - "+message)+"\r\n");
			myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
	public void onClientMessage(ClientThread client, String msg) {
		conL.onClientMessage(client,msg);
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
			 public void onClientMessage(ClientThread client, String msg) {}
			 public void acknowledge(String report) {}
		 });
		 server.start(8084);
		 server.acceptClient();
	 }



}

  
