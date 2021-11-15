package net_old.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Processus d'�coute client. Ecoute les messages en provenance du client distant.
 * @author aleconte, rdeclercq
 * */
public class ClientThread extends Thread {
	/**Ecouteur � notifier lors de chaque �v�nement*/
	private ConnectionListener connection;
	/**Socket de connexion TCP*/
	private Socket socket;
	/**Flux d'�criture*/
	private PrintWriter out;
	/**Flux de lecture*/
	private BufferedReader in;
	/**Pseudo du client*/
	private String pseudo;
	
	/**
	 * Initialisation des attributs.
	 * @param s Socket connect�e au client distant.
	 * @param cm Ecouteur � notifier.
	 * */
	public ClientThread(Socket s, ConnectionListener cm) {
		socket = s;
		connection = cm;
	}
	
	/**
	 * Tue le processus d'�coute du client.
	 * */
	public synchronized void kill() {
		// Ferme la connexion, ceci d�clenchera une IOException dans la boucle principale
		try {
			socket.close();
		} catch (IOException e) {}
	}
	
	/**
	 * Boucle principale du processus.
	 * Au d�but, ouverture des flux de lecture et d'�criture et r�cup�ration du pseudo du client.
	 * Si tout se passe bien, le client est accept�, il faut notifier l'�couteur.
	 * Ensuite, �coute des messages du client jusqu'� la perte de la connexion.
	 * */
	public void run() {
		// Tente d'obtenir les flux d'entr�e / sortie et de r�cup�rer le pseudo
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pseudo = in.readLine();
		} catch(IOException e) {
			// En cas d'�chec, ferme la connexion et laisse mourir le processus
			try {
				socket.close();
			} catch (IOException e2) {}
			return;
		}
		// En cas de r�ussite, accepte le client
		connection.onClientAccepted(this);
		try {
			// Attente bloquante des messages du client
			// Si des chaines null sont obtenues, c'est que le client a ferm� la connexion de son c�t�
			String msg;
			while((msg = in.readLine()) != null) {
				connection.onClientMessage(this, msg);
			}
		} catch(IOException e) {}
		// Si le client ou le serveur a ferm� la connexion, achever le processus
		connection.onClientDisconnected(this);
		kill();
	}
	
	/**
	 * Retourne une repr�sentation permettant d'identifier le client.
	 * @return le pseudo du client et son adresse IP.
	 * */
	public synchronized String toString() {
		return pseudo + " [" + socket.getInetAddress().getHostAddress() + "]";
	}
	
	/**
	 * Permet d'envoyer un message au client distant.
	 * @param msg Le message � envoyer au client.
	 * */
	public synchronized void sendMessage(String msg) {
		out.println(msg);
	}
	
}