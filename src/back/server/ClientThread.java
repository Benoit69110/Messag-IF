/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package back.server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.StringTokenizer;

public class ClientThread extends Thread {
	private Socket clientSocket;
	private String pseudo;
	private boolean pseudoSetted=false;
	private BufferedReader socIn;
	private PrintStream socOut;
	private ServerMultiThreaded server;
	private HashMap<String,Boolean> firstPrivateMessage;
	private String PATH_LOGS="logs/";
	
	ClientThread(Socket s,ServerMultiThreaded server) {
		firstPrivateMessage=new HashMap<>();
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
					line=server.decrypt(line);
					if(!pseudoSetted){
						if(pseudoExist(line)){
							socOut.println(server.encrypt("This pseudo is already used. Disconnect and choose another pseudo."));
						}else {
							pseudo = line;
							pseudoSetted = true;
							socOut.println(server.encrypt("Your pseudo is "+pseudo));
							server.onClientAccepted(this);
						}
					}else{
						StringTokenizer tokens=new StringTokenizer(line);
						if(tokens.countTokens()>2 && tokens.nextToken().equals("private")){
							String sendTo=tokens.nextToken();
							if(pseudoExist(sendTo)){
								ClientThread clientDest= server.getClientByPseudo(sendTo);
								String msg="";
								while(tokens.hasMoreTokens()){
									msg+=tokens.nextToken()+" ";
								}
								server.broadcastTo(this,clientDest,pseudo+" : "+msg,true);
							}
						}else{
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

  
