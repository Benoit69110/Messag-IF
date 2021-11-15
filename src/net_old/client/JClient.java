package net_old.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Gestionnaire de la connexion avec le serveur distant.
 * @author aleconte, rdeclercq
 * */
public class JClient implements ConnectionListener {
	/**Ecouteur à notifier à chaque événement : c'est l'IHM*/
    private ConnectionListener gui;
    /**Socket TCP connecté au serveur*/
    private Socket socket;
    /**Flux de lecture*/
    private BufferedReader in;
    /**Flux d'écriture*/
    private PrintStream out;
    /**Processus d'écoute du serveur*/
    private ClientThread client;
    
    /**
     * Initialisation des attribut.
     * @param li Ecouteur à notifier à chaque événement.
     * */
    public JClient(ConnectionListener li) {
        gui = li;
    }
    
    /**
     * Etablit une connexion entre le client et le serveur distant. Lance le processus d'écoute du serveur.
     * @param ip IP du serveur distant.
     * @param port Port d'écoute du serveur distant.
     * @param pseudo Pseudo du client sur le serveur.
     * */
    public synchronized void connect(String ip, int port, String pseudo) throws IOException {
        socket = new Socket(ip, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintStream(socket.getOutputStream());
        out.println(pseudo);
        client = new ClientThread(this, in);
        client.start();
    }
    
    /**
     * Retourne l'état de la connexion avec le serveur.
     * @return true si le client est connecté, false sinon.
     * */
    public synchronized boolean isConnected() {
        return client != null && client.isRunning();
    }
    
    /**
     * Permet d'envoyer un message au serveur distant.
     * @param msg Message à envoyer au serveur.
     * */
    public synchronized void sendMessage(String msg) {
        if(socket != null && isConnected()) {
        	out.println(msg);
        }
    }
    
    /**
     * Permet de déconnecter le client.
     * */
    public synchronized void disconnect() {
    	try {
			socket.close();
		} catch(IOException e) {}
    }
    
    /**
     * Méthode appelée lors de la réception d'un message par le processus d'écoute du serveur.
     * Notifie l'IHM.
     * @param msg Le message reçu.
     * */
    @Override
    public void onReceiveMessage(String msg) {
        gui.onReceiveMessage(msg);
    }
    
    /**
     * Méthode appelée lors de la déconnexion du processus d'écoute du serveur.
     * Ferme la connexion proprement si ça n'avait pas déjà été fait, et notifie le client.
     * @param msg Le message de déconnexion.
     * */
	@Override
	public void onConnectionLost(String msg) {
		try {
			socket.close();
		} catch(Exception e) {}
		gui.onConnectionLost(msg);
	}
}

