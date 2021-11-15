package net.client;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Processus pour �couter le serveur.
 * @author aleconte, redeclercq
 * */
public class ClientThread extends Thread {
	/**Ecouteur � notifier � chaque �v�nement*/
	private ConnectionListener listener;
	/**Flux de lecture sur le socket li� au serveur distant*/
	private BufferedReader in;
	/**Etat indiquant si le processus est toujours vivant*/
	private boolean running = false;
    
	/**
	 * Initialisation des attributs.
	 * @param li L'Ecouteur qui g�re cette connexion.
	 * @param br Le flux de lecture qu'il faut �couter.
	 * */
	public ClientThread(ConnectionListener li, BufferedReader br) {
		listener = li;
		in = br;
	}
	
	/**
	 * Retourne l'�tat du processus d'�coute.
	 * @return true si le processus est en cours, false s'il a �t� stopp�. Si le processus a �t� stopp�, c'est que la connexion a �t� ferm�e.
	 * */
	public synchronized boolean isRunning() {
		return running;
	}
	
	/**
	 * Tue le processus d'�coute.
	 * */
	public synchronized void kill() {
		running = false;
	}
    
	/**
	 * Boucle principale du processus d'�coute.
	 * A chaque message re�u, notifie l'�couteur.
	 * Une fois la boucle bris�e, notifie l'�couteur que la connection a �t� perdue.
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