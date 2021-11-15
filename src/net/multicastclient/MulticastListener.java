package net.multicastclient;

/**
 * Interface d'écoute d'un abonnement d'un client multicast à un groupe multicast.
 * @author aleconte, rdeclercq
 * */
public interface MulticastListener {
	/**
	 * Méthode à appeler lors de la réception d'un message.
	 * @param thread Le processus d'écoute qui a reçu le message.
	 * @param msg Le message reçu.
	 * */
	public void onReceiveMessage(ListenerThread thread, String msg);
	
	/**
	 * Méthode à appeler lors du désabonnement à un groupe multicast.
	 * @param thread Le processus d'écoute qui s'est désabonné.
	 * */
	public void onLeaveGroup(ListenerThread thread);
}
