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
     * Log area 
     */
    private TextArea logArea = new TextArea();
    /**
     * Server's Port text field
     */
    private TextField serverPort;
    /**
     * Label with the server's IP
     */
    private Label serverIP;
    /**
     * Button to start/stop the server
     */
    private Button startServer;
    /**
     * Button to clear conversation history
     */
    private Button clearHistory;


    /**
     * Constructor, on call, display servers panel and set up button action
     */
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
        serverIP = new Label("127.0.0.1"/*server.getLocalIP()*/);
        north.add(serverIP);

        // Local Port
        serverPort = new TextField();
        serverPort.setText("5000");
        north.add(serverPort);

        // Start/Stop Button
        startServer = new Button("Start Server");
        startServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(startServer.getText().equals("Start Server")) {
                    try {
                        System.out.println(serverPort.getText());
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
        clearHistory = new Button("Clear historic");
        clearHistory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                server.clearHistoric();
                write("Clear history");
            }
        });
        north.add(clearHistory);
    }

    /**
     * Write date and nex line in the texte area
     * @param msg message to write in the text area.
     * */
    public synchronized void write(String msg) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss" );
        String date = simpleDateFormat.format(new Date());
        logArea.append("["+date+"]" +msg + "\n");
    }

    /**
     * Called when a client disconnect
     * @param client client thread
     */
    @Override
    public void onClientAccepted(ClientThread client) {
        write("Hi "+client.getPseudo()+" !");
    }

    /**
     * Called when a client connect
     * @param client client thread
     */
    @Override
    public void onClientDisconnected(ClientThread client) {
        write("See you soon "+client.getPseudo()+" !");
    }

    /**
     * Method called when the server reach unused situation display the report
     * @param report describe the event.
     * */
    @Override
    public synchronized void acknowledge(String report) {
        write(report);
    }

    /**
     * Start the server application
     * @param args : unsused.
     * */
    public static void main(String args[]) {
        new ServerIhm().setVisible(true);
    }
}
