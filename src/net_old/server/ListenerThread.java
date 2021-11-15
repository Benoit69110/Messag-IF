package net_old.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Processus d'écoute pour accepter de nouveaux clients.
 * @author aleconte, rdeclercq
 * */
public class ListenerThread extends Thread {
	/**Socket d'écoute pour accepter les nouveaux clients*/
	private ServerSocket listener;
	/**Etat du processus*/
	private boolean running;
	/**Ecouteur à notifier à chaque événement*/
	private ConnectionListener connection;
	
	/**
	 * Démarrage de l'écoute pour accepter de nouveaux clients.
	 * @param port Port sur lequel démarrer l'écoute du ServerSocket.
	 * @param li Ecouteur à notifier.
	 * */
	public ListenerThread(int port, ConnectionListener li) throws IOException {
		listener = new ServerSocket(port);
		connection = li;
	}
	
	/**
	 * Retourne l'état du processus.
	 * @return true si le processus est en vie.
	 * */
	public synchronized boolean isRunning() {
		return running;
	}
	
	/**
	 * Tue le processus.
	 * */
	public synchronized void kill() {
		try {
			listener.close();
		} catch(IOException e) {}
		running = false;
	}
	
	/**
	 * Boucle principale du processus.
	 * Accepte les clients qui désirent se connecter, et lance un nouveau processus d'écoute client pour chaque client qui se connecte.
	 * */
	@Override
	public void run() {
		running = true;
		try {
			for(;;) {
				Socket client = listener.accept();
				new ClientThread(client, connection).start();
			}
		} catch(IOException e) {
			connection.acknowledge("Server is down...");
		}
		kill();
	}
}