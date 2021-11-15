package net_old.client;

/**
 * Interface d'�coute de la connexion d'un client avec un serveur distant.
 * @author aleconte, rdeclercq
 * */
public interface ConnectionListener {
	/**
	 * M�thode � appeler lors de la r�ception d'un message.
	 * @param msg Message re�u.
	 * */
	public void onReceiveMessage(String msg);
	
	/**
	 * M�thode � appeler lors de la d�connexion du client.
	 * @param msg Message de d�connexion.
	 * */
	public void onConnectionLost(String msg);
}
