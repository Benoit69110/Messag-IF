package gui_old.multicastclient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import gui_old.widgets.Button;
import gui_old.widgets.ComboBox;
import gui_old.widgets.Label;
import gui_old.widgets.Panel;
import gui_old.widgets.TextArea;
import gui_old.widgets.TextField;
import net_old.multicastclient.ListenerThread;
import net_old.multicastclient.MulticastListener;

/**
 * Interface Graphique pour le client Multicast distribu�.
 * @author aleconte, rdeclercq
 * */
public class JMulticastClientUI extends JFrame implements MulticastListener {
	/**Zone d'affichage des messages*/
	private TextArea msgArea;
	/**Zone de saisie du port de groupe multicast*/
	private TextField groupPortField;
	/**Zone de saisie de l'IP du groupe multicast*/
	private TextField groupIPField;
	/**Zone de saisie d'un message*/
	private TextField msgField;
	/**Zone de saisie du pseudo*/
	private TextField pseudoField;
	/**Bouton pour s'abonner � un groupe multicast*/
	private Button join;
	/**Bouton pour r�silier un abonnement � un groupe multicast*/
	private Button leave;
	/**Bouton pour effacer la zone d'affichage des messages*/
	private Button clear;
	/**Bouton pour envoyer un message*/
	private Button send;
	/**Boite de s�lection des groupes multicasts auxquels le client est abonn�*/
	private ComboBox<ListenerThread> listeners;
	private MulticastListener thisManager = this;
	
	/**
	 * Le constructeur initialise l'interface graphique en positionnant les composants graphiques et en mettant en place la partie dynamique pour r�pondre aux �v�nements.
	 * */
	public JMulticastClientUI() {
		// Initialisation de l'IHM
		setTitle("JMulticastClientUI - INSA de Lyon");
		setSize(640, 480);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		try {
		    setIconImage(ImageIO.read(new File("icon.png")));
		}
		catch (IOException e) {}
		
		Panel north = new Panel();
		north.setLayout(new BorderLayout());
		this.add(north, BorderLayout.NORTH);
		
		Panel upperNorth = new Panel();
		upperNorth.setLayout(new GridLayout(1, 6, 5, 5));
		upperNorth.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		north.add(upperNorth, BorderLayout.NORTH);
		
		Panel lowerNorth = new Panel();
		lowerNorth.setLayout(new BorderLayout());
		lowerNorth.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		north.add(lowerNorth, BorderLayout.SOUTH);
		
		Panel center = new Panel();
		center.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		center.setLayout(new BorderLayout());
		this.add(center, BorderLayout.CENTER);
		
		Panel south = new Panel();
		south.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		south.setLayout(new BorderLayout());
		this.add(south, BorderLayout.SOUTH);
		
		// Server Port
		groupPortField = new TextField();
		groupPortField.setText("50000");
		upperNorth.add(new Label("Group Port :"));
		upperNorth.add(groupPortField);
		
		// Server IP
		groupIPField = new TextField();
		groupIPField.setText("224.0.0.1");
		upperNorth.add(new Label("Group IP :"));
		upperNorth.add(groupIPField);
		
		// Join Button
		join = new Button("Join");
		join.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
				 	InetAddress groupAddr = InetAddress.getByName(groupIPField.getText());
					int groupPort = Integer.valueOf(groupPortField.getText());
					MulticastSocket socket = new MulticastSocket(groupPort);
					socket.joinGroup(groupAddr);
					ListenerThread l = new ListenerThread(thisManager, socket, groupAddr, groupPort);
					l.start();
					synchronized(listeners) {
						listeners.addItem(l);
					}
					write("You have joined group " + groupAddr + " (port " + groupPort + ")");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
			}
		});
		upperNorth.add(join);
		
		// Clear Button
		clear = new Button("Clear");
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		upperNorth.add(clear);
		
		Panel lnGroupPanel = new Panel();
		lnGroupPanel.setLayout(new BorderLayout());
		lnGroupPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		lnGroupPanel.setPreferredSize(new Dimension(100, 24));
		lnGroupPanel.add(new Label("Groups : "));
		lowerNorth.add(lnGroupPanel, BorderLayout.WEST);
		
		// ListenerThreads ComboBox
		Panel lnLinstenerPanel = new Panel();
		lnLinstenerPanel.setLayout(new BorderLayout());
		lnLinstenerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		listeners = new ComboBox<ListenerThread>();
		listeners.setFocusable(false);
		lnLinstenerPanel.add(listeners);
		lowerNorth.add(lnLinstenerPanel, BorderLayout.CENTER);
		
		// Leave Button
		Panel lnLeavePanel = new Panel();
		lnLeavePanel.setLayout(new BorderLayout());
		lnLeavePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		lnLeavePanel.setPreferredSize(new Dimension(100, 24));
		leave = new Button("Leave");
		leave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(listeners.getSelectedItem() != null) {
					ListenerThread thread = (ListenerThread) listeners.getSelectedItem();
					thread.kill();
					listeners.removeItem(thread);
				}
			}
		});
		lnLeavePanel.add(leave);
		lowerNorth.add(lnLeavePanel, BorderLayout.EAST);
		
		// Log Area
		msgArea = new TextArea();
		JScrollPane scrollPane = new JScrollPane(msgArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		center.add(scrollPane, BorderLayout.CENTER);
		
		// Message Text Field
		msgField = new TextField();
		msgField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					send(pseudoField.getText() + " : " + msgField.getText());
				}
			}
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
		});
		south.add(msgField, BorderLayout.CENTER);
		
		// Pseudo Text Field
		pseudoField = new TextField();
		pseudoField.setText("Anonymous");
		pseudoField.setPreferredSize(new Dimension(120, 24));
		south.add(pseudoField, BorderLayout.WEST);
		
		// Send Button
		send = new Button("Send");
		send.setPreferredSize(new Dimension(80, 24));
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				send(pseudoField.getText() + " : " + msgField.getText());
			}
		});
		south.add(send, BorderLayout.EAST);
		
	}
	
	/**
	 * Envoi du message au groupe multicast dont les coordonn�es sont actuellement saisies dans les champs groupIPField et groupPortField.
	 * @param msg
	 * */
	public synchronized void send(String msg) {
		MulticastSocket socket = null;
		try {
			InetAddress groupAddr = InetAddress.getByName(groupIPField.getText());
			int groupPort = Integer.valueOf(groupPortField.getText());
			socket = new MulticastSocket(groupPort);
			DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.getBytes().length, groupAddr, groupPort);
			socket.send(packet);
			msgField.setText("");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(socket != null) {
			socket.close();
		}
	}
	
	/**
	 * Ecrit une nouvelle ligne dans la zone des messages.
	 * @param msg Message � �crire dans la zone d'affichage des messages.
	 * */
	public synchronized void write(String msg) {
		msgArea.append(msg + "\n");
		msgArea.setCaretPosition(msgArea.getDocument().getLength());
	}
	
	/**
	 * Efface le contenu de la zone d'affichage des messages.
	 * */
	public synchronized void clear() {
		msgArea.setText("");
	}
	
	/**
	 * M�thode appel�e lors de la r�ception d'un message.
	 * Ecriture du message dans la zone d'affichage des messages.
	 * @param thread Le processus d'�coute qui a re�u le message: il est li� � un abonnement � un groupe multicast.
	 * @param msg Le message re�u par le processus.
	 * */
	@Override
	public void onReceiveMessage(ListenerThread thread, String msg) {
		write("From " + thread + " # " + msg);
	}
	
	/**
	 * M�thode appel�e lors de la r�siliation d'un abonnement � un groupe multicast.
	 * Ecriture d'un message d'avertissement dans la zone d'affichage des messages.
	 * @param thread Le processus d'�coute qui a r�sili� son abonnement multicast.
	 * */
	@Override
	public void onLeaveGroup(ListenerThread thread) {
		write("You have left group " + thread);
	}
	
	/**
	 * D�marra l'application.
	 * @param args Arguments inutilis�s.
	 * */
	public static void main(String[] args) {
		new JMulticastClientUI().setVisible(true);
	}
	
}
