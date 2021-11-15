package ihm;


import back.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class clientIHM extends JFrame {
    private TextArea message;
    private TextField messageField;
    private TextField port;
    private TextField adresseIP;
    private TextField pseudoField;
    private Button connect;
    private Button clear;
    private Button send;

    private Client client;

    public clientIHM() {
        client = new Client("benoit","localhost",8084); //Recuperer le client du back

        // Initialisation de l'IHM
        setTitle("IHM Client");
        setSize(680, 480);
        //setResizable(false);
        //setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        northPanel.setBackground(new Color(28, 147, 213));
        northPanel.setLayout(new GridLayout(6, 1, 5, 5));
        northPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        northPanel.setPreferredSize(new Dimension(100,200));
        this.add(northPanel, BorderLayout.NORTH);


        JPanel centerPanel = new JPanel();
        //northPanel.setBackground(new Color(28, 147, 213));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        centerPanel.setLayout(new BorderLayout());
        this.add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        //northPanel.setBackground(new Color(28, 147, 213));
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        southPanel.setLayout(new BorderLayout());
        this.add(southPanel, BorderLayout.SOUTH);


        // Server Port
        port = new TextField();
        port.setText("8083");
        port.setPreferredSize(new Dimension(120, 24));
        northPanel.add(new Label("Server Port :"));
        northPanel.add(port);
        //southPanel.add(pseudoField, BorderLayout.WEST);


        // Server IP
        adresseIP = new TextField();
        adresseIP.setText("127.0.0.1");
        northPanel.add(new Label("Server IP :"));
        northPanel.add(adresseIP);

        // Connect Button
        connect = new Button("Connect");
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //northPanel.setVisible(false);
            }
        });
        northPanel.add(connect);

        // Clear Button
        clear = new Button("Clear");
        clear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message = new TextArea("");
            }
        });
        northPanel.add(clear);

        // Log Area
        /*
        message = new TextArea();
        JScrollPane scrollPane = new JScrollPane(message);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
         */

        // Message Text Field
        messageField = new TextField();
        messageField.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                /*if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if(client.isConnected() && !msgField.getText().isEmpty()) {
                        client.sendMessage(msgField.getText());
                        msgField.setText("");
                    }
                }*/
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
                /*if(client.isConnected() && !msgField.getText().isEmpty()) {
                    client.sendMessage(msgField.getText());
                    msgField.setText("");
                }*/
            }
        });
        southPanel.add(send, BorderLayout.EAST);

    }

    /*public synchronized void write(String msg) {
        synchronized(message) {
            while(msg.endsWith("\n")) {
                msg = msg.substring(0, msg.length()-1);
            }
            if(!msg.isEmpty()) {
                message.append(msg + "\n");
                //message.setCaretPosition(message.getDocument().getLength());
            }
        }
    }*/

    public static void main(String[] args) {
        new clientIHM().setVisible(true);
    }


}
