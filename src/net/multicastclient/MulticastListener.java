package net.multicastclient;

/**
 * Interface d'�coute d'un abonnement d'un client multicast � un groupe multicast.
 * @author aleconte, rdeclercq
 * */
public interface MulticastListener {
	/**
	 * M�thode � appeler lors de la r�ception d'un message.
	 * @param thread Le processus d'�coute qui a re�u le message.
	 * @param msg Le message re�u.
	 * */
	public void onReceiveMessage(ListenerThread thread, String msg);
	
	/**
	 * M�thode � appeler lors du d�sabonnement � un groupe multicast.
	 * @param thread Le processus d'�coute qui s'est d�sabonn�.
	 * */
	public void onLeaveGroup(ListenerThread thread);
}
