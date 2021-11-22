package ihm.server;

import back.server.ClientThread;
import back.server.ServerMultiThreaded;
import ihm.widgets.Button;
import ihm.widgets.Label;
import ihm.widgets.Panel;
import ihm.widgets.TextArea;
import ihm.widgets.TextField;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import back.server.ConnectionListener;

public class ServerIhm extends JFrame implements ConnectionListener{
    /**
     * Zone d'affichage des messages et informations de connexion
     */
    private TextArea logArea = new TextArea();
    /**
     * Zone de saisie du port sur lequel on lance le serveur
     */
    private TextField serverPort;
    /**
     * Zone d'affichage de l'adresse IP de la machine
     */
    private Label serverIP;
    /**
     * Bouton pour lancer / stopper le serveur
     */
    private Button startServer;
    /**
     * Bouton pour effacer les fichiers d'historique ainsi que la zone d'affichage des messages
     */
    private Button clearHistory;

    public ServerIhm() {
        ServerMultiThreaded server = new ServerMultiThreaded(this);
        // Initialisation de l'IHM
        setTitle("Server - INSA de Lyon");
        setSize(560, 420);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
            setIconImage(ImageIO.read(new File("icon.png")));
        } catch (IOException e) {
        }

        Panel north = new Panel();
        north.setLayout(new GridLayout(1, 5, 5, 5));
        north.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(north, BorderLayout.NORTH);

        Panel center = new Panel();
        center.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        center.setLayout(new BorderLayout());
        this.add(center, BorderLayout.CENTER);

        // Log Area
        logArea = new TextArea();
        //logArea.setText(server.getHistory());
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        center.add(scrollPane, BorderLayout.CENTER);

        // IP Address
        serverIP = new Label("10.10.0.9"/*server.getLocalIP()*/);
        north.add(serverIP);

        // Local Port
        serverPort = new TextField();
        serverPort.setText("50000");
        north.add(serverPort);

        // Start/Stop Button
        startServer = new Button("Start Server");
        startServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(startServer.getText().equals("Start Server")) {
                    try {
                        int port = Integer.valueOf(serverPort.getText());
                        write("Server started ... ");
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
                    write("Server stoped");
                    serverPort.setEditable(true);
                }
            }
        });
        north.add(startServer);

        // Clear historique Button
        clearHistory = new Button("Clear historique");
        clearHistory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //server.clearHistory();
                write("Clear history");
            }
        });
        north.add(clearHistory);
    }

    /**
     * Ecrit une nouvelle ligne dans la zone des messages.
     * @param msg Message à écrire dans la zone d'affichage des messages.
     * */
    public synchronized void write(String msg) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss" );
        String date = simpleDateFormat.format(new Date());
        logArea.append("["+date+"]" +msg + "\n");
    }

    @Override
    public void onClientAccepted(ClientThread client) {
        write("Hi "+client.getPseudo()+" !");
    }

    @Override
    public void onClientDisconnected(ClientThread client) {
        write("See you soon "+client.getPseudo()+" !");
    }

    @Override
    public void onClientMessage(ClientThread client, String msg) {
        write("["+client.getPseudo()+"]"+msg);
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
        new ServerIhm().setVisible(true);
    }
}
