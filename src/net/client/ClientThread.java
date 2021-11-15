package net.client;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Processus pour écouter le serveur.
 * @author aleconte, redeclercq
 * */
public class ClientThread extends Thread {
	/**Ecouteur à notifier à chaque événement*/
	private ConnectionListener listener;
	/**Flux de lecture sur le socket lié au serveur distant*/
	private BufferedReader in;
	/**Etat indiquant si le processus est toujours vivant*/
	private boolean running = false;
    
	/**
	 * Initialisation des attributs.
	 * @param li L'Ecouteur qui gère cette connexion.
	 * @param br Le flux de lecture qu'il faut écouter.
	 * */
	public ClientThread(ConnectionListener li, BufferedReader br) {
		listener = li;
		in = br;
	}
	
	/**
	 * Retourne l'état du processus d'écoute.
	 * @return true si le processus est en cours, false s'il a été stoppé. Si le processus a été stoppé, c'est que la connexion a été fermée.
	 * */
	public synchronized boolean isRunning() {
		return running;
	}
	
	/**
	 * Tue le processus d'écoute.
	 * */
	public synchronized void kill() {
		running = false;
	}
    
	/**
	 * Boucle principale du processus d'écoute.
	 * A chaque message reçu, notifie l'écouteur.
	 * Une fois la boucle brisée, notifie l'écouteur que la connection a été perdue.
	 * */
	public void run() {
		running = true;
		try {
			String msg;
            while((msg = in.readLine()) != null) {
            	listener.onReceiveMessage(msg);
            }
            listener.onConnectionLost("Connection lost...");
		} catch (IOException e) {
			listener.onConnectionLost("Client disconnected");
		}
		kill();
	}

}