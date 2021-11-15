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
	/**Ecouteur � notifier � chaque �v�nement : c'est l'IHM*/
    private ConnectionListener gui;
    /**Socket TCP connect� au serveur*/
    private Socket socket;
    /**Flux de lecture*/
    private BufferedReader in;
    /**Flux d'�criture*/
    private PrintStream out;
    /**Processus d'�coute du serveur*/
    private ClientThread client;
    
    /**
     * Initialisation des attribut.
     * @param li Ecouteur � notifier � chaque �v�nement.
     * */
    public JClient(ConnectionListener li) {
        gui = li;
    }
    
    /**
     * Etablit une connexion entre le client et le serveur distant. Lance le processus d'�coute du serveur.
     * @param ip IP du serveur distant.
     * @param port Port d'�coute du serveur distant.
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
     * Retourne l'�tat de la connexion avec le serveur.
     * @return true si le client est connect�, false sinon.
     * */
    public synchronized boolean isConnected() {
        return client != null && client.isRunning();
    }
    
    /**
     * Permet d'envoyer un message au serveur distant.
     * @param msg Message � envoyer au serveur.
     * */
    public synchronized void sendMessage(String msg) {
        if(socket != null && isConnected()) {
        	out.println(msg);
        }
    }
    
    /**
     * Permet de d�connecter le client.
     * */
    public synchronized void disconnect() {
    	try {
			socket.close();
		} catch(IOException e) {}
    }
    
    /**
     * M�thode appel�e lors de la r�ception d'un message par le processus d'�coute du serveur.
     * Notifie l'IHM.
     * @param msg Le message re�u.
     * */
    @Override
    public void onReceiveMessage(String msg) {
        gui.onReceiveMessage(msg);
    }
    
    /**
     * M�thode appel�e lors de la d�connexion du processus d'�coute du serveur.
     * Ferme la connexion proprement si �a n'avait pas d�j� �t� fait, et notifie le client.
     * @param msg Le message de d�connexion.
     * */
	@Override
	public void onConnectionLost(String msg) {
		try {
			socket.close();
		} catch(Exception e) {}
		gui.onConnectionLost(msg);
	}
}

