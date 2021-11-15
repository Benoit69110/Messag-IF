package net_old.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Processus d'écoute client. Ecoute les messages en provenance du client distant.
 * @author aleconte, rdeclercq
 * */
public class ClientThread extends Thread {
	/**Ecouteur à notifier lors de chaque événement*/
	private ConnectionListener connection;
	/**Socket de connexion TCP*/
	private Socket socket;
	/**Flux d'écriture*/
	private PrintWriter out;
	/**Flux de lecture*/
	private BufferedReader in;
	/**Pseudo du client*/
	private String pseudo;
	
	/**
	 * Initialisation des attributs.
	 * @param s Socket connectée au client distant.
	 * @param cm Ecouteur à notifier.
	 * */
	public ClientThread(Socket s, ConnectionListener cm) {
		socket = s;
		connection = cm;
	}
	
	/**
	 * Tue le processus d'écoute du client.
	 * */
	public synchronized void kill() {
		// Ferme la connexion, ceci déclenchera une IOException dans la boucle principale
		try {
			socket.close();
		} catch (IOException e) {}
	}
	
	/**
	 * Boucle principale du processus.
	 * Au début, ouverture des flux de lecture et d'écriture et récupération du pseudo du client.
	 * Si tout se passe bien, le client est accepté, il faut notifier l'écouteur.
	 * Ensuite, écoute des messages du client jusqu'à la perte de la connexion.
	 * */
	public void run() {
		// Tente d'obtenir les flux d'entrée / sortie et de récupérer le pseudo
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pseudo = in.readLine();
		} catch(IOException e) {
			// En cas d'échec, ferme la connexion et laisse mourir le processus
			try {
				socket.close();
			} catch (IOException e2) {}
			return;
		}
		// En cas de réussite, accepte le client
		connection.onClientAccepted(this);
		try {
			// Attente bloquante des messages du client
			// Si des chaines null sont obtenues, c'est que le client a fermé la connexion de son côté
			String msg;
			while((msg = in.readLine()) != null) {
				connection.onClientMessage(this, msg);
			}
		} catch(IOException e) {}
		// Si le client ou le serveur a fermé la connexion, achever le processus
		connection.onClientDisconnected(this);
		kill();
	}
	
	/**
	 * Retourne une représentation permettant d'identifier le client.
	 * @return le pseudo du client et son adresse IP.
	 * */
	public synchronized String toString() {
		return pseudo + " [" + socket.getInetAddress().getHostAddress() + "]";
	}
	
	/**
	 * Permet d'envoyer un message au client distant.
	 * @param msg Le message à envoyer au client.
	 * */
	public synchronized void sendMessage(String msg) {
		out.println(msg);
	}
	
}