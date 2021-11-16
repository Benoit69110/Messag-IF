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
	private ServerMultiThreaded server;
	
	ClientThread(Socket s,ServerMultiThreaded server) {
		this.clientSocket = s;
		this.server=server;
		try {
			socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			socOut = new PrintStream(clientSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(){
		try{
			//Récupération du message reçu par le socket sous forme d'une chaine de caracteres
			BufferedReader br=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			//Récupération de l'adresse IP du socket connecté
			String IP=clientSocket.getRemoteSocketAddress().toString();
			System.out.println("Connexion du client IP : "+IP);

			//Boucle infinie pour recevoir et renvoyer les informations
			String line;
			while((line=br.readLine())!=null){//Récupération du message envoyé
				if(line instanceof String){
					if(!pseudoSetted){
						boolean pseudoExist=false;
						for(ClientThread client:server.getClients()){
							if(client!=this && client.getPseudo().equals(line)){
								pseudoExist=true;
							}
						}
						if(pseudoExist){
							socOut.println("This pseudo is already used. Choose another one :");
						}else {
							pseudo = line;
							pseudoSetted = true;
							System.out.println(pseudo+" has joined the server !");
							socOut.println("Your pseudo is "+pseudo);
							server.broadcast(this,pseudo+" has joined the server !",false);
							server.sendHistoric(clientSocket,server.getHistoricFile());
							server.getConnectedClientsThread().sendConnectedPseudo();
						}
					}else {
						server.broadcast(this,pseudo+" : "+line,true);
					}
				}
			}
		}catch(SocketException e){
			System.out.println(pseudo+" has left the server.");
			server.getClients().remove(this);
			server.getConnectedClientsThread().sendConnectedPseudo();
		}catch (Exception e){
			System.err.println("Error in Server:" + e);
		}
		System.out.println(pseudo+" has left the server.");
		server.getClients().remove(this);
		server.getConnectedClientsThread().sendConnectedPseudo();
	}

	public boolean getPseudoSetted() {
		return pseudoSetted;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}
	public BufferedReader getSocIn() {
		return socIn;
	}

	public PrintStream getSocOut() {
		return socOut;
	}

}

  
