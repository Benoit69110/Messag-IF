package gui.server;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import net.server.ClientThread;
import net.server.ConnectionListener;
import net.server.JServer;

/**
 * Interface Graphique pour le serveur TCP.
 * @author aleconte, rdeclercq
 */
public class JServerUI extends JFrame implements ConnectionListener {
	/**Zone d'affichage des messages et informations de connexion*/
	private TextArea logArea;
	/**Zone de saisie du port sur lequel on lance le serveur*/
	private TextField serverPort;
	/**Zone d'affichage de l'adresse IP de la machine*/
	private Label serverIP;
	/**Bouton pour lancer / stopper le serveur*/
	private Button startServer;
	/**Bouton pour effacer les fichiers d'historique ainsi que la zone d'affichage des messages*/
	private Button clearHistory;
	
	/**Gestionnaire du serveur côté réseau*/
	private JServer server;
	
	/**
	 * Le constructeur initialise l'interface graphique en positionnant les composants graphiques et en mettant en place la partie dynamique pour répondre aux événements.
	 * */
	public JServerUI() {
		server = new JServer(this);
		
		// Initialisation de l'IHM
		setTitle("JServerUI - INSA de Lyon");
		setSize(560, 420);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		try {
		    setIconImage(ImageIO.read(new File("icon.png")));
		}
		catch (IOException e) {}
		
		Panel north = new Panel();
		north.setLayout(new GridLayout(1, 5, 5, 5));
		north.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.add(north, BorderLayout.NORTH);
		
		Panel center = new Panel();
		center.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		center.setLayout(new BorderLayout());
		this.add(center, BorderLayout.CENTER);
		
		// IP Address
		serverIP = new Label(server.getLocalIP());
		north.add(serverIP);
		
		// Local Port
		serverPort = new TextField();
		serverPort.setText("50000");
		north.add(serverPort);
		
		// Start/Stop Button
		startServer = new Button("Start Server");
		startServer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(startServer.getText().equals("Start Server")) {
					try {
						int port = Integer.valueOf(serverPort.getText());
						if(port < 1024 || port > 65535) {
							throw new NumberFormatException();
						}
						server.start(port);
						startServer.setText("Stop Server");
						serverPort.setEditable(false);
					} catch(NumberFormatException ex) {
						write("Error: enter a port number between 1024 and 65635");
					}
				} else if(startServer.getText().equals("Stop Server")) {
					server.stop();
					startServer.setText("Start Server");
					serverPort.setEditable(true);
				}
			}
		});
		north.add(startServer);
		
		// Clear History Button
		clearHistory = new Button("Clear History");
		clearHistory.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logArea.setText("");
				server.clearHistory();
			}
		});
		north.add(clearHistory);
		
		// Log Area
		logArea = new TextArea();
		logArea.setText(server.getHistory());
		JScrollPane scrollPane = new JScrollPane(logArea);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		center.add(scrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * Ecrit une nouvelle ligne dans la zone des messages.
	 * @param msg Message à écrire dans la zone d'affichage des messages.
	 * */
	public synchronized void write(String msg) {
		logArea.append(msg + "\n");
		logArea.setCaretPosition(logArea.getDocument().getLength());
	}
	
	/**
	 * Méthode appelée lors de la connexion d'un nouveau client.
	 * Affiche un message de connexion dans la zone des messages.
	 * @param cT Le processus d'écoute client qui a été accepté.
	 * */
	@Override
	public synchronized void onClientAccepted(ClientThread cT) {
		write(cT + " has joined the chat");
	}
	
	/**
	 * Méthode appelée lors de la déconnexion d'un nouveau client.
	 * Affiche un message de déconnexion dans la zone des messages.
	 * @param cT le processus d'écoute client qui s'est déconnecté.
	 * */
	@Override
	public synchronized void onClientDisconnected(ClientThread cT) {
		write(cT + " has disconnected");
	}
	
	/**
	 * Méthode appelée lorsqu'un processus d'écoute client reçoit un message.
	 * Affiche le message reçu dans la zone des messages.
	 * @param cT Le processus d'écoute client qui a reçu un message.
	 * @param msg Le message reçu.
	 * */
	@Override
	public synchronized void onClientMessage(ClientThread cT, String msg) {
		write(cT + " : " + msg);
	}
	
	/**
	 * Méthode appelée lorsqu'un événement particulier survient dans la gestion du serveur.
	 * Affiche le rapport reçu dans la zone des messages.
	 * @param report Le message indiquant la nature de l'événement.
	 * */
	@Override
	public synchronized void acknowledge(String report) {
		write(report);
	}
	
	/**
	 * Démarre l'application.
	 * @param args Arguments non utilisés.
	 * */
	public static void main(String args[]) {
		new JServerUI().setVisible(true);
	}
	
}
