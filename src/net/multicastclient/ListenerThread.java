package net.multicastclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Processus d'écoute d'un groupe multicast, lié à un abonnement multicast.
 * @author aleconte, rdeclercq
 * */
public class ListenerThread extends Thread {
	/**Ecouteur à notifier à chaque événement*/
	private MulticastListener listener;
	/**Socket Multicast abonné au groupe*/
	private MulticastSocket socket;
	/**Adresse du groupe*/
	private InetAddress groupAddr;
	/**port du groupe*/
	private int groupPort;
	
	/**
	 * Initialisation des attributs.
	 * @param li Ecouteur à notifier.
	 * @param s Socket Multicast abonné au groupe défini par (addr, port).
	 * @param addr Adresse du groupe multicast.
	 * @param port port du groupe multicast.
	 * */
	public ListenerThread(MulticastListener li, MulticastSocket s, InetAddress addr, int port) {
		listener = li;
		socket = s;
		groupAddr = addr;
		groupPort = port;
	}
	
	/**
	 * Tue le processus d'écoute.
	 * */
	public synchronized void kill() {
		try {
			socket.leaveGroup(groupAddr);
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Description textuelle du groupe auquel le processus est abonné.
	 * @return Une définition du groupe auquel le processus est abonné : groupAddr et groupPort.
	 * */
	public String toString() {
		return groupAddr.toString() + " : " + groupPort;
	}
	
	/**
	 * Boucle principale du processus.
	 * Ecoute le groupe et notifie l'Ecouteur chaque fois qu'un message est reçu, ou quand la connexion est rompue.
	 * */
	public void run() {
		try {
			for(;;) {
				byte[] buffer = new byte[256];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				String msg = new String(buffer, 0, packet.getLength());
				listener.onReceiveMessage(this, msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		kill();
		listener.onLeaveGroup(this);
	}
	
}
