package net_old.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.LinkedList;

/**
 * Gestionnaire du serveur. Le JServer s'occupe de démarrer / éteindre le serveur et de gérer les clients connectés au serveur.
 * @author aleconte, rdeclercq
 * */
public class JServer implements ConnectionListener {
	/**Ecouteur à notifier à chaque événement : c'est l'IHM*/
	private ConnectionListener gui;
	/**Processus d'écoute pour accepter de nouveaux clients*/
	private ListenerThread listener;
	/**Liste des clients actuellement connectés au serveur*/
	private LinkedList<ClientThread> clients;
	
	/**Chemin relatif vers le fichier qui conserve l'historique de la conversation du serveur*/
	private static final String LOGS_FILE = "serverlogs.log";
	
	/**
	 * Initialisation des attributs.
	 * @param li Ecouteur à notifier.
	 * */
	public JServer(ConnectionListener li) {
		gui = li;
	}
	
	/**
	 * Démarre le serveur. Lance le processus d'écoute pour accepter les nouveaux clients.
	 * @param port Port sur lequel démarrer l'écoute.
	 * */
	public synchronized void start(int port) {
		if(listener != null && listener.isRunning()) {
			gui.acknowledge("Server is already running !");
		} else {
			try {
				clients = new LinkedList<ClientThread>();
				listener = new ListenerThread(port, this);
				listener.start();
				gui.acknowledge("Server started on port " + port);
			} catch (IOException e) {
				e.printStackTrace();
				gui.acknowledge("Server could not be started on port " + port);
			}
		}
	}
	
	/**
	 * Stop le serveur. Déconnecte tous les clients, vide la liste des clients et tue le processus d'écoute qui accepte les clients.
	 * */
	public synchronized void stop() {
		if(listener != null && clients != null) {
			listener.kill();
			synchronized(clients) {
				for(ClientThread cT : clients) {
					cT.kill();
				}
				clients.clear();
			}
		}
	}
	
	/**
	 * Retourne l'état du serveur.
	 * @return true si le serveur est en état de marche, s'il accepte des clients.
	 * */
	public synchronized boolean isRunning() {
		return listener.isRunning();
	}
	
	/**
	 * Détruit l'historique des conversations du serveur stocké sur le disque.
	 * */
	public synchronized void clearHistory() {
		try {
			File logFile = new File(LOGS_FILE);
			PrintWriter logWriter = new PrintWriter(new FileOutputStream(logFile), true);
			logWriter.append("");
			logWriter.close();
		} catch (FileNotFoundException e) {
			gui.acknowledge("Error : logs are not available");
		}
	}
	
	/**
	 * Ajoute une nouvelle entrée à l'historique des messages.
	 * @param msg Le message à ajouter à l'historique.
	 * */
	public synchronized void writeHistory(String msg) {
		try {
			File logFile = new File(LOGS_FILE);
			PrintWriter logWriter = new PrintWriter(new FileOutputStream(logFile, true), true);
			logWriter.append(msg + "\n");
			logWriter.close();
		} catch (FileNotFoundException e) {
			gui.acknowledge("Error : logs are not available");
		}
		
	}
	
	/**
	 * Retourne le contenu du fichier qui conserve l'historique des messages.
	 * @return L'historique de la conversation.
	 * */
	public synchronized String getHistory() {
		String log = new String();
		try {
			File logFile = new File("serverlogs.log");
			BufferedReader logReader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
			String line;
			while((line = logReader.readLine()) != null) {
				log += (line + "\n");
			}
			logReader.close();
		} catch (IOException e) {
			gui.acknowledge("Error : logs are not available");
			stop();
		}
		if(log.equals("\n")) {
			log = new String();
		}
		return log;
	}
	
	/**
	 * Permet d'obtenir l'adresse IP de la machine qui héberge le serveur.
	 * @return L'IP locale.
	 * */
	public static String getLocalIP() {
		try {
			return Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return new String("Unknown Host");
		}
	}
	
	/**
	 * Méthode appelée lors d'un événement particulier.
	 * Notifie l'écouteur.
	 * @param report
	 * */
	@Override
	public synchronized void acknowledge(String report) {
		gui.acknowledge(report);
	}

	/**
	 * Méthode appelée lors de la connexion d'un nouveau client.
	 * Notifie les autres clients et l'écouteur.
	 * @param cT Le processus d'écoute client qui vient de se connecter.
	 * */
	@Override
	public synchronized void onClientAccepted(ClientThread cT) {
		cT.sendMessage(getHistory());
		synchronized(clients) {
			clients.add(cT);
			for(ClientThread c : clients) {
				c.sendMessage(cT + " has joined the chat");
			}
		}
		gui.onClientAccepted(cT);		
	}

	/**
	 * Méthode appelée lors de la déconnexion d'un client.
	 * Notifie les autres clients et l'écouteur.
	 * @param cT Le processus d'écoute client qui vient de se déconnecter.
	 * */
	@Override
	public synchronized void onClientDisconnected(ClientThread cT) {
		cT.kill();
		synchronized(clients) {
			clients.remove(cT);
			for(ClientThread c : clients) {
				c.sendMessage(cT + " has left the chat");
			}
		}
		gui.onClientDisconnected(cT);
	}

	/**
	 * Méthode appelée lorsqu'un processus d'écoute client reçoit un message.
	 * Notifie les autres clients et l'écouteur, et ajoute une entrée dans l'historique de la conversation.
	 * @param cT Le processus d'écoute client qui a reçu un message.
	 * @param msg Le message reçu.
	 * */
	@Override
	public synchronized void onClientMessage(ClientThread cT, String msg) {
		synchronized(clients) {
			for(ClientThread c : clients) {
				c.sendMessage(cT + " : " + msg);
			}
		}
		writeHistory(cT + " : " + msg);
		gui.onClientMessage(cT, msg);
	}
	
}
