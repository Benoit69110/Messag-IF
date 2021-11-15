package net_old.client;

/**
 * Interface d'écoute de la connexion d'un client avec un serveur distant.
 * @author aleconte, rdeclercq
 * */
public interface ConnectionListener {
	/**
	 * Méthode à appeler lors de la réception d'un message.
	 * @param msg Message reçu.
	 * */
	public void onReceiveMessage(String msg);
	
	/**
	 * Méthode à appeler lors de la déconnexion du client.
	 * @param msg Message de déconnexion.
	 * */
	public void onConnectionLost(String msg);
}
