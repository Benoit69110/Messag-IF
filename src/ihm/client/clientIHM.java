/***
 * clientIHM
 * Date: 24/11/21
 * Authors: balgourdin, gdelambert, malami
 */

package ihm.client;
import back.client.Client;
import back.client.ConnectionListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class clientIHM extends JFrame implements ConnectionListener {
    private JTextArea message;
    private JPanel centerPanel;
    private JPanel southPanel;
    private JPanel northPanel;
    private JPanel westPanel;
    private TextField messageField;
    private TextField port;
    private TextField adresseIP;
    private TextField pseudoField;
    private Button connect;
    private Button clear;
    private Button send;
    private JButton connectClient;
    private JButton connectedClients;
    private JButton disconnect;
    private JButton update;
    private JButton change;
    private ArrayList <JButton> listConnectClient;
    private JScrollPane scrollPane;
    private int p=0;
    private Client client;

    public clientIHM() {
        /**Create a client*/
        client = new Client(this);
        /**Initialisation of the frame*/
        setTitle("IHM Client");
        setSize(680, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        northPanel = new JPanel();
        northPanel.setBackground(new Color(28, 147, 213));
        northPanel.setPreferredSize(new Dimension(160,40));
        this.add(northPanel, BorderLayout.NORTH);

        westPanel = new JPanel();
        westPanel.setBackground(new Color(164, 217, 220));
        westPanel.setPreferredSize(new Dimension(180,100));
        this.add(westPanel, BorderLayout.WEST);

        centerPanel = new JPanel();
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        centerPanel.setLayout(new BorderLayout());
        getClientIHM().add(centerPanel, BorderLayout.CENTER);

        southPanel = new JPanel();
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
                change = new JButton("Change Port/Server IP");
                disconnect = new JButton("Disconnect");
                connectedClients = new JButton("Connected clients");
                westPanel.add(change);
                change.addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if(client.isConnected()){
                                    client.disconnect();
                                }
                                new clientIHM().setVisible(true);
                                dispose();
                            }
                        }
                );
                westPanel.add(disconnect);
                disconnect.addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                new clientIHM().setVisible(true);
                                client.disconnect();
                                dispose();
                            }
                        }
                );
                //Connection
                if(!client.isConnected()&& !pseudoField.getText().isEmpty()) { //Connect
                    try {
                        client.connect(pseudoField.getText(), adresseIP.getText(), Integer.valueOf(port.getText()));
                        pseudoField.setEditable(false);
                        messageField.requestFocusInWindow();
                    } catch (NumberFormatException ex) {
                        write("Error : you must provide a correct ip address and port...");
                    }
                } else if(client.isConnected()) { //Disconnect
                    client.disconnect();
                }
                westPanel.add(connectedClients);
                // Add all JButtons of connectedClients in a list
                listConnectClient = new ArrayList<>();
                connectedClients.addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if(client.getPseudosConnected().size()==0 ||
                                        client.getPseudosConnected().size()==1) {
                                    System.out.println("No one is connected");
                                }
                                    for (p = 0; p < client.getPseudosConnected().size(); p++) {
                                        if (!(client.getPseudosConnected().get(p)).equals(client.getPseudo())
                                        && !((client.getPseudosConnected().get(p)).equals("Anonymous"))) {
                                            connectedClients.setVisible(false);
                                            connectClient = new JButton("Send to : " + client.getPseudosConnected().get(p));
                                            listConnectClient.add(connectClient);
                                            westPanel.add(connectClient, BorderLayout.CENTER);
                                            //Open a private conversation
                                            connectClient.addActionListener(
                                                    new ActionListener() {
                                                        @Override
                                                        public void actionPerformed(ActionEvent e) {
                                                            write("");
                                                            write("");

                                                            if(!messageField.getText().isEmpty()) {
                                                                client.converseWith(e.getActionCommand().substring(10), messageField.getText());
                                                            }else{
                                                                System.out.println("No message written");
                                                            }
                                                        }
                                                    }
                                            );
                                        }
                                    }
                                    update = new JButton("update connection list");
                                    update.setBackground(new Color(164, 217, 220));
                                    westPanel.add(update);
                                    update.addActionListener(
                                        new ActionListener() {
                                            @Override
                                            public void actionPerformed(ActionEvent e) {
                                                for(int i=0;i<listConnectClient.size();i++){
                                                    listConnectClient.get(i).setVisible(false);
                                                    update.setVisible(false);
                                                    connectedClients.setVisible(true);
                                                }
                                            }
                                        });
                                p = 0;
                            }
                        });
                    for(int i=0; i<listConnectClient.size();i++) {
                        westPanel.add(listConnectClient.get(i));
                    }
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
        send = new Button("Send to all");
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

    /**This method is used to write the message in the groupChat*/
    @Override
    public void onReceiveMessage(String msg) {
        write(msg);
    }

    /**This method is used to write private messages*/
    @Override
    public void onReceivePrivateMessage(String msg) {
        write("* "+msg+"*");
    }

    /**This method is used to write private messages*/
    @Override
    public void onConnectionLost(String msg) {
        write(msg);
        connectedClients.setVisible(false);
        disconnect.setVisible(false);
        southPanel.setVisible(false);
    }

    /**
     * This method is used to write a message in the frame
     * @param msg
     */
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
