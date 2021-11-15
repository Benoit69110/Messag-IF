package net.server;

/**
 * Interface d'�coute d'une connexion d'un processus d'�coute client � un client distant.
 * @author aleconte, rdeclercq
 * */
public interface ConnectionListener {
	/**
	 * M�thode � appeler lors de la connexion du client.
	 * @param client Le processus d'�coute client.
	 * */
	public void onClientAccepted(ClientThread client);
	
	/**
	 * M�thode � appeler lors de la d�connection du client.
	 * @param client Le processus d'�coute client.
	 * */
	public void onClientDisconnected(ClientThread client);
	
	/**
	 * M�thode � appeler lors de la r�ception d'un message par le client.
	 * @param client Le processus d'�coute client.
	 * @param msg Le message re�u.
	 * */
	public void onClientMessage(ClientThread client, String msg);
	
	/**
	 * M�thode � appeler lorsqu'un �v�nement particulier se produit et que l'on souhaite notifier le serveur.
	 * @param report Le message d�crivant l'�v�nement.
	 * */
	public void acknowledge(String report);
}
