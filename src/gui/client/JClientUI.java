package gui.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import gui.widgets.Button;
import gui.widgets.Label;
import gui.widgets.Panel;
import gui.widgets.TextArea;
import gui.widgets.TextField;
import net.client.ConnectionListener;
import net.client.JClient;

/**
 * Interface Graphique pour le client TCP.
 * @author aleconte, rdeclercq
 */
public class JClientUI extends JFrame implements ConnectionListener {
	/**Zone d'affichage des messages*/
	private TextArea msgArea;
	/**Zone de saisie du port du serveur distant*/
	private TextField serverPort;
	/**Zone de saisie de l'adresse IP du serveur distant*/
	private TextField serverIP;
	/**Zone de saisie d'un message*/
	private TextField msgField;
	/**Zone de saisie du pseudo*/
	private TextField pseudoField;
	/**Bouton de connexion / déconnexion au serveur*/
	private Button connect;
	/**Bouton pour effacer la zone d'affichage des messages*/
	private Button clear;
	/**Zone d'affichage des messagesBouton pour envoyer un message*/
	private Button send;
	
	/**Gestionnaire de la connexion TCP avec le serveur*/
	private JClient client;
	
	/**
	 * Le constructeur initialise l'interface graphique en positionnant les composants graphiques et en mettant en place la partie dynamique pour répondre aux événements.
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
		
		Panel north = new Panel();
		north.setLayout(new GridLayout(1, 6, 5, 5));
		north.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.add(north, BorderLayout.NORTH);
		
		Panel center = new Panel();
		center.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		center.setLayout(new BorderLayout());
		this.add(center, BorderLayout.CENTER);
		
		Panel south = new Panel();
		south.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		south.setLayout(new BorderLayout());
		this.add(south, BorderLayout.SOUTH);
		
		// Server Port
		serverPort = new TextField();
		serverPort.setText("50000");
		north.add(new Label("Server Port :"));
		north.add(serverPort);
		
		// Server IP
		serverIP = new TextField();
		serverIP.setText("127.0.0.1");
		north.add(new Label("Server IP :"));
		north.add(serverIP);
		
		// Connect Button
		connect = new Button("Connect");
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
		clear = new Button("Clear");
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
		north.add(clear);
		
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
	 * @param msg Message à écrire dans la zone d'affichage des messages.
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
	 * Méthode appelée à la réception d'un message en provenance du serveur.
	 * Ecrit le message dans la zone d'affichage des messages.
	 * @param msg Le message reçu.
	 * */
	@Override
	public void onReceiveMessage(String msg) {
		write(msg);
	}
	
	/**
	 * Méthode appelée lors de la déconnexion du client.
	 * Met à jour l'interface graphique pour permettre une nouvelle connexion.
	 * Ecrit le message de déconnexion dans la zone d'affichage des messages.
	 * @param msg Message de déconnexion.
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
	 * Démarre l'application.
	 * @param args Arguments, non utilisés.
	 * */
	public static void main(String[] args) {
		new JClientUI().setVisible(true);
	}
	
}
