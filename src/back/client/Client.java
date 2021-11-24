package back.client;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.LinkedList;
import java.util.StringTokenizer;

/***
 * Client
 * TCP client which can send and receive messages, thanks to another thread
 * @author: balgourdin, gdelambert, malami
 */
public class Client implements ConnectionListener{
    /** Passsphrase used to encrypt messages */
    private static byte[] SECRET_KEY;
    /** Algorithm used to encrypt messages */
    private static String ALGORITHM;
    /** Key used to encrypt messages */
    private SecretKeySpec sks;
    /** Pseudo of the client */
    private String pseudo;
    private boolean pseudoSetted;
    /** Socket of the client */
    private Socket socket;
    /** Listening port */
    private int port;
    /** Host of the server */
    private String host;
    /** Output stream of the client */
    private PrintStream socketOut;
    /** Input stream of the client */
    private BufferedReader socketIn;
    /** List of pseudo of connected clients */
    private LinkedList<String> pseudosConnected;
    /** Thread which receives messages */
    private ReceiverThread receive;
    private ConnectionListener conL;

    /**
     * Constructor
     * @param cL
     */
    public Client(ConnectionListener cL){
        pseudosConnected=new LinkedList<>();
        this.conL = cL;
        // Get the configuration (algorithm and passphrase) to encrypt messages
        try{
            BufferedReader fileReader=new BufferedReader(new FileReader("config/config.txt"));
            String line;
            int nbLines=0;
            while((line=fileReader.readLine())!=null){
                line=line.split("=")[1];
                if(nbLines==0){
                    SECRET_KEY=line.getBytes();
                }else if(nbLines==1){
                    ALGORITHM=line;
                }
                nbLines++;
            }
        }catch (IOException e){
            // e.printStackTrace();
        }
        // The key length has to be 16 bytes
        sks= new SecretKeySpec(SECRET_KEY, ALGORITHM);

    }

    /**
     * Method to connect a client, with a pseudo, to a server defined by its port and the host
     * @param pseudo
     * @param host
     * @param port
     */
    public synchronized void connect(String pseudo,String host,int port){
        if(pseudo=="" || pseudo==null){
            this.pseudo="Anonymous";
        }else{
            this.pseudo=pseudo;
        }
        this.pseudoSetted=false;
        this.port=port;
        this.host=host;

        try {
            // Connection to the server
            socket=new Socket(host,port);
            // Get the input stream of the socket
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Get the output stream of the socket
            socketOut= new PrintStream(socket.getOutputStream());
            // Send the pseudo encrypted
            socketOut.println(encrypt(pseudo));
            // Initialize a new thread to handle incoming messages
            receive=new ReceiverThread(this, conL);
            // Start the new thread
            receive.start();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + host);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to:"+ host);
            conL.onConnectionLost("Connection refused");
        }

    }


    /**
     *
     * @param msg Message reçu.
     */
    @Override
    public void onReceiveMessage(String msg) {
        conL.onReceiveMessage(msg);
    }

    @Override
    public void onReceivePrivateMessage(String msg) {
        conL.onReceivePrivateMessage(msg);
    }

    @Override
    public void onConnectionLost(String msg) {
        try {
            socket.close();
        } catch(Exception e) {}
        conL.onConnectionLost(msg);
    }

    /**
     * Send a message encrypted
     * @param message
     */
    public synchronized void addMessage(String message){
        if(socket!=null && isConnected()) {
            socketOut.println(encrypt(message));
        }
    }

    /**
     * Method to disconnect the client
     */
    public synchronized void disconnect(){
        try {
            socketOut.close();
            socketIn.close();
            socket.close();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }


    /**
     * Return true if the client is connected
     * @return
     */
    public synchronized boolean isConnected(){
        boolean res= socket!=null && !socket.isClosed() && receive!=null
                && receive.isRunning();
        return res;
    }

    /**
     * Display the list of pseudos connected
     */
    public void displayPseudosConnected(){
        for(String pseudo:pseudosConnected){
            System.out.println(pseudo);
        }
    }

    /**
     * Launch a conversation with different commands
     */
    public void converse(){
        try {
            String line;
            boolean lifeClient=true;
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            while (lifeClient) {
                line=stdIn.readLine();

                if (line.equals("exit")){
                    break;
                }else if(line.equals("who is connected")){
                    displayPseudosConnected();
                } else if(line.equals("connected")){
                    System.out.println(isConnected());
                }else{
                    StringTokenizer tokens=new StringTokenizer(line);
                    if(tokens.countTokens()>2 && tokens.nextToken().equals("private")) {
                        String sendTo = tokens.nextToken();
                        String msg = "";
                        while (tokens.hasMoreTokens()) {
                            msg += tokens.nextToken() + " ";
                        }
                        converseWith(sendTo,msg);
                    }else{
                        socketOut.println(encrypt(line));
                    }
                }
            }
            stdIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a message encrypted to someone defined by his pseudo
     * @param pseudoDest
     * @param message
     */
    public void converseWith(String pseudoDest,String message) {
        socketOut.println(encrypt("private " + pseudoDest + " " + message));
    }

    /**
     * @return pseudosConnected
     */
    public LinkedList<String> getPseudosConnected(){
        return pseudosConnected;
    }

    /**
     * @return pseudoSetted
     */
    public boolean getPseudoSetted() {
        return pseudoSetted;
    }

    /**
     * Reset the list of pseudos connected
     */
    public void resetPseudosConnected(){
        pseudosConnected=new LinkedList<>();
    }

    /**
     * @return pseudo
     */
    public String getPseudo() {
        return pseudo;
    }

    /**
     * Set the pseudo of the client
     * @param pseudo
     */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
        this.pseudoSetted=true;
    }

    /**
     * @return socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * @return socketIn
     */
    public BufferedReader getSocketIn() {
        return socketIn;
    }

    /**
     * Encrypt the message with the algorithm and the key generated in the constructor
     * @param valueToEncrypt plain message
     * @return message encrypted
     */
    public String encrypt(String valueToEncrypt){
        String res="";
        try{
            Cipher c=Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE,sks);
            byte[] encVal=c.doFinal(valueToEncrypt.getBytes());
            res=new String(Base64.getEncoder().encode(encVal));
        }catch (Exception e){
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Decrypt the value encrypted
     * @param encryptedValue encrypted message
     * @return message decrypted
     */
    public String decrypt(String encryptedValue){
        String decr="";
        try{
            Cipher c=Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE,sks);
            byte[] decryptedVal=Base64.getDecoder().decode(encryptedValue);
            byte[] decvValue= c.doFinal(decryptedVal);
            decr=new String(decvValue);
        }catch (Exception e){
            e.printStackTrace();
        }

        return decr;
    }

    /**
     *  main method
     *  accepts a connection, receives a message from client then sends an echo to the client
     **/
    public static void main(String[] args) throws IOException {
        // creation socket ==> connexion
        Client client=new Client(new ConnectionListener() {
            public void onReceiveMessage(String msg) {}
            public void onReceivePrivateMessage(String msg) {}
            public void onConnectionLost(String msg) {}
        });
        client.connect("greg","localhost",8084);
        client.converse();
        client.disconnect();
    }
}


