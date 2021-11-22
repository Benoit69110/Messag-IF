package ihm;


import back.client.Client;
import back.client.ConnectionListener;
import back.server.ServerMultiThreaded;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.PrintStream;

public class clientIHM extends JFrame implements ConnectionListener {
    private JTextArea message;
    private JPanel centerPanel;
    private TextField messageField;
    private TextField port;
    private TextField adresseIP;
    private TextField pseudoField;
    private Button connect;
    private Button clear;
    private Button send;
    JScrollPane scrollPane;

    private Client client;
    private ServerMultiThreaded serverMultiThreaded;
    private ConnectionListener connectionListener;

    public clientIHM() {
        client = new Client(this); //Recuperer le client du back
        //serverMultiThreaded = new ServerMultiThreaded();
        // Initialisation de l'IHM
        setTitle("IHM Client");
        setSize(680, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        northPanel.setBackground(new Color(28, 147, 213));
        northPanel.setPreferredSize(new Dimension(160,40));
        this.add(northPanel, BorderLayout.NORTH);

        JPanel westPanel = new JPanel();
        westPanel.setBackground(new Color(164, 217, 220));
        westPanel.setPreferredSize(new Dimension(180,100));
        this.add(westPanel, BorderLayout.WEST);

        //System.out.println(client.getPseudosConnected());
        /*for(int i=0; i<client.getPseudosConnected().size();i++){
            System.out.println(client.getPseudosConnected().get(i));
        }*/


        centerPanel = new JPanel();
        //northPanel.setBackground(new Color(28, 147, 213));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        centerPanel.setLayout(new BorderLayout());
        getClientIHM().add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        //northPanel.setBackground(new Color(28, 147, 213));
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        southPanel.setLayout(new BorderLayout());
        centerPanel.add(southPanel, BorderLayout.SOUTH);


        // Server Port
        port = new TextField();
        port.setText("8084");
        port.setPreferredSize(new Dimension(80, 24));
        northPanel.add(new Label("Server Port :"), BorderLayout.WEST);
        northPanel.add(port,BorderLayout.WEST);


        // Server IP
        adresseIP = new TextField();
        adresseIP.setText("127.0.0.1");
        adresseIP.setPreferredSize(new Dimension(80, 24));
        northPanel.add(new Label("Server IP :"));
        northPanel.add(adresseIP);


        // Clear Button
        clear = new Button("Clear");
        clear.setPreferredSize(new Dimension(80,24));
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adresseIP.setText("");
                port.setText("");
            }
        });
        northPanel.add(clear);

        // Connect Button
        connect = new Button("Connect");
        connect.setPreferredSize(new Dimension(80,24));
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                northPanel.setVisible(false);
                JButton change = new JButton("Change Port/Server IP");
                JButton disconnect = new JButton("Disconnect");
                JButton connectedClients = new JButton("Connected clients");
                westPanel.add(change);
                change.addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                northPanel.setVisible(true);
                                westPanel.removeAll();
                                centerPanel.removeAll();

                            }
                        }
                );
                westPanel.add(disconnect);
                disconnect.addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                client.disconnect();
                                northPanel.setVisible(true);
                                centerPanel.removeAll(); //******
                                westPanel.removeAll();

                            }
                        }
                );
                //Connection
                if(!client.isConnected()&& !pseudoField.getText().isEmpty()) { //Connect
                    try {

                        JButton groupChat = new JButton("Group chat");
                        westPanel.add(groupChat);

                        groupChat.addActionListener(
                                new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        client.connect(pseudoField.getText(), adresseIP.getText(), Integer.valueOf(port.getText()));
                                    }
                                });

                        write("Connected to " + adresseIP.getText() + " on port " + Integer.valueOf(port.getText()));
                        write(" ");
                        //write("WELCOME TO THE GROUP CHAT");
                        write(" ");

                        //client.getSocketOut().println(client.getPseudo());
                        //System.out.println(serverMultiThreaded.getClients());
                        /*for(int i=0;i<serverMultiThreaded.getClients().size();i++) {
                            JButton discu = new JButton("Conversation "+(i+1));
                            westPanel.add(discu, BorderLayout.SOUTH);
                        }*/
                        messageField.requestFocusInWindow();

                        //} catch (IOException ex) {
                        //write("Error : could not connect to remote host " + adresseIP.getText() + " on port " + Integer.valueOf(adresseIP.getText()));
                    } catch (NumberFormatException ex) {
                        write("Error : you must provide a correct ip address and port...");
                    }
                } else if(client.isConnected()) { //Deconnect

                    client.disconnect();
                    //connect.setText("Connect");
                    //adresseIP.setEditable(true);
                    //port.setEditable(true);
                    //pseudoField.setEditable(true);
                }
                westPanel.add(connectedClients);
                connectedClients.addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                //client.connectPrivate(pseudoField.getText(), adresseIP.getText(), Integer.valueOf(port.getText()) );

                                for(int i=0;i<client.getPseudosConnected().size();i++){
                                    if(!(client.getPseudosConnected().get(i)).equals(client.getPseudo())) {
                                        JButton connectClient = new JButton("Chat with : " + client.getPseudosConnected().get(i));
                                        connectedClients.setVisible(false);
                                        westPanel.add(connectClient, BorderLayout.SOUTH);
                                        //Open a private conversation
                                        connectClient.addActionListener(
                                                new ActionListener() {
                                                    @Override
                                                    public void actionPerformed(ActionEvent e) {

                                                    }
                                                }
                                        );
                                    }
                                }
                            }
                        });
            }
        });
        northPanel.add(connect);


        // Log Area
        message = new JTextArea();
        scrollPane = new JScrollPane(message);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerPanel.add(scrollPane, BorderLayout.CENTER);


        // Message Text Field
        messageField = new TextField("");
        JTextArea text = new JTextArea();
        messageField.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(client.isConnected() && !messageField.getText().isEmpty()) {
                        client.addMessage(messageField.getText());
                        messageField.setText("");

                    }
                }
            }
            public void keyTyped(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
        });
        southPanel.add(messageField, BorderLayout.CENTER);

        // Pseudo Text Field
        pseudoField = new TextField();
        pseudoField.setText("Anonymous");
        pseudoField.setPreferredSize(new Dimension(120, 24));
        southPanel.add(pseudoField, BorderLayout.WEST);

        // Send Button
        send = new Button("Send");
        send.setPreferredSize(new Dimension(80, 24));
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(client.isConnected() && !messageField.getText().isEmpty()) {
                    client.addMessage(messageField.getText());
                    messageField.setText("");
                }
            }
        });
        southPanel.add(send, BorderLayout.EAST);



    }

    private clientIHM getClientIHM() {
        return this;
    }

    @Override
    public void onReceiveMessage(String msg) {
        write(msg);
    }

    @Override
    public void onReceivePrivateMessage(String msg) {
        write(msg);
    }

    @Override
    public void onConnectionLost(String msg) {
        write(msg);
    }

    public synchronized void write(String msg) {
        synchronized(message) {
            while(msg.endsWith("\n")) {
                msg = msg.substring(0, msg.length()-1);
            }
            if(!msg.isEmpty()) {
                message.append(msg + "\n");
                message.setCaretPosition(message.getDocument().getLength());
            }
        }
    }


    public static void main(String[] args) {
        new clientIHM().setVisible(true);

    }


}
