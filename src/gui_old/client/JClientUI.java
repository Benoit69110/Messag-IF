package gui_old.client;

import net_old.client.ConnectionListener;
import net_old.client.JClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

/**
 * Interface Graphique pour le client TCP.
 * @author aleconte, rdeclercq
 */
public class JClientUI extends JFrame implements ConnectionListener {
	/**Zone d'affichage des messages*/
	private JTextArea msgArea;
	/**Zone de saisie du port du serveur distant*/
	private JTextField serverPort;
	/**Zone de saisie de l'adresse IP du serveur distant*/
	private JTextField serverIP;
	/**Zone de saisie d'un message*/
	private JTextField msgField;
	/**Zone de saisie du pseudo*/
	private JTextField pseudoField;
	/**Bouton de connexion / d�connexion au serveur*/
	private JButton connect;
	/**Bouton pour effacer la zone d'affichage des messages*/
	private JButton clear;
	/**Zone d'affichage des messagesBouton pour envoyer un message*/
	private JButton send;
	
	/**Gestionnaire de la connexion TCP avec le serveur*/
	private JClient client;
	
	/**
	 * Le constructeur initialise l'interface graphique en positionnant les composants graphiques et en mettant en place la partie dynamique pour r�pondre aux �v�nements.
	 * */
	public JClientUI() {
		client = new JClient(this);
		
		// Initialisation de l'IHM
		setTitle("JClientUI - INSA de Lyon");
		setSize(680, 480);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		try {
		    setIconImage(ImageIO.read(new File("icon.png")));
		}
		catch (IOException e) {}
		
		JPanel north = new JPanel();
		north.setLayout(new GridLayout(1, 6, 5, 5));
		north.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.add(north, BorderLayout.NORTH);

		JPanel center = new JPanel();
		center.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		center.setLayout(new BorderLayout());
		this.add(center, BorderLayout.CENTER);

		JPanel south = new JPanel();
		south.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		south.setLayout(new BorderLayout());
		this.add(south, BorderLayout.SOUTH);
		
		// Server Port
		serverPort = new JTextField();
		serverPort.setText("50000");
		north.add(new Label("Server Port :"));
		north.add(serverPort);
		
		// Server IP
		serverIP = new JTextField();
		serverIP.setText("127.0.0.1");
		north.add(new Label("Server IP :"));
		north.add(serverIP);
		
		// Connect Button
		connect = new JButton("Connect");
		connect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!client.isConnected() && connect.getText().equals("Connect") && !pseudoField.getText().isEmpty()) {
					try {
						client.connect(serverIP.getText(), Integer.valueOf(serverPort.getText()), pseudoField.getText());
						write("Connected to " + serverIP.getText() + " on port " + Integer.valueOf(serverPort.getText()));
						connect.setText("Disconnect");
						serverIP.setEditable(false);
						serverPort.setEditable(false);
						pseudoField.setEditable(false);
						msgField.requestFocusInWindow();
					} catch (IOException ex) {
						write("Error : could not connect to remote host " + serverIP.getText() + " on port " + Integer.valueOf(serverPort.getText()));
					} catch (NumberFormatException ex) {
						write("Error : you must provide a correct ip address and port...");
					}
				} else if(client.isConnected() && connect.getText().equals("Disconnect")) {
					client.disconnect();
					connect.setText("Connect");
					serverIP.setEditable(true);
					serverPort.setEditable(true);
					pseudoField.setEditable(true);
				}
			}
		});
		north.add(connect);
		
		// Clear Button
		clear = new JButton("Clear");
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		north.add(clear);
		
		// Log Area
		msgArea = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(msgArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		center.add(scrollPane, BorderLayout.CENTER);
		
		// Message Text Field
		msgField = new JTextField();
		msgField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					if(client.isConnected() && !msgField.getText().isEmpty()) {
						client.sendMessage(msgField.getText());
						msgField.setText("");
					}
				}
			}
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
		});
		south.add(msgField, BorderLayout.CENTER);
		
		// Pseudo Text Field
		pseudoField = new JTextField();
		pseudoField.setText("Anonymous");
		pseudoField.setPreferredSize(new Dimension(120, 24));
		south.add(pseudoField, BorderLayout.WEST);
		
		// Send Button
		send = new JButton("Send");
		send.setPreferredSize(new Dimension(80, 24));
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(client.isConnected() && !msgField.getText().isEmpty()) {
					client.sendMessage(msgField.getText());
					msgField.setText("");
				}
			}
		});
		south.add(send, BorderLayout.EAST);
		
	}
	
	/**
	 * Ecrit une nouvelle ligne dans la zone des messages.
	 * @param msg Message � �crire dans la zone d'affichage des messages.
	 * */
	public synchronized void write(String msg) {
		synchronized(msgArea) {
			while(msg.endsWith("\n")) {
				msg = msg.substring(0, msg.length()-1);
			}
			if(!msg.isEmpty()) {
				msgArea.append(msg + "\n");
				msgArea.setCaretPosition(msgArea.getDocument().getLength());
			}
		}
	}
	
	/**
	 * Efface le contenu de la zone d'affichage des messages.
	 * */
	public synchronized void clear() {
		synchronized(msgArea) {
			msgArea.setText("");
		}
	}
	
	/**
	 * M�thode appel�e � la r�ception d'un message en provenance du serveur.
	 * Ecrit le message dans la zone d'affichage des messages.
	 * @param msg Le message re�u.
	 * */
	@Override
	public void onReceiveMessage(String msg) {
		write(msg);
	}
	
	/**
	 * M�thode appel�e lors de la d�connexion du client.
	 * Met � jour l'interface graphique pour permettre une nouvelle connexion.
	 * Ecrit le message de d�connexion dans la zone d'affichage des messages.
	 * @param msg Message de d�connexion.
	 * */
	@Override
	public void onConnectionLost(String msg) {
		connect.setText("Connect");
		serverIP.setEditable(true);
		serverPort.setEditable(true);
		pseudoField.setEditable(true);
		write(msg);
	}
	
	/**
	 * D�marre l'application.
	 * @param args Arguments, non utilis�s.
	 * */
	public static void main(String[] args) {
		new JClientUI().setVisible(true);
	}
	
}
