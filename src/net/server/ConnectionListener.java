package net.server;

/**
 * Interface d'écoute d'une connexion d'un processus d'écoute client à un client distant.
 * @author aleconte, rdeclercq
 * */
public interface ConnectionListener {
	/**
	 * Méthode à appeler lors de la connexion du client.
	 * @param client Le processus d'écoute client.
	 * */
	public void onClientAccepted(ClientThread client);
	
	/**
	 * Méthode à appeler lors de la déconnection du client.
	 * @param client Le processus d'écoute client.
	 * */
	public void onClientDisconnected(ClientThread client);
	
	/**
	 * Méthode à appeler lors de la réception d'un message par le client.
	 * @param client Le processus d'écoute client.
	 * @param msg Le message reçu.
	 * */
	public void onClientMessage(ClientThread client, String msg);
	
	/**
	 * Méthode à appeler lorsqu'un événement particulier se produit et que l'on souhaite notifier le serveur.
	 * @param report Le message décrivant l'événement.
	 * */
	public void acknowledge(String report);
}
