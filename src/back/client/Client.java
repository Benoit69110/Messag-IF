/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package back.client;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class Client implements ConnectionListener{
    private static byte[] SECRET_KEY;
    private static String ALGORITHM;
    private SecretKeySpec sks;

    private String pseudo;
    private boolean pseudoSetted;
    private Socket socket;
    private int port;
    private String host;
    private PrintStream socketOut; // Flux de sortie en clair

    private BufferedReader socketIn; // Flux d'entrée en clair
    private LinkedList<String> pseudosConnected;
    private ReceiverThread receive;
    private ConnectionListener conL;

    public Client(ConnectionListener cL){
        pseudosConnected=new LinkedList<>();
        this.conL = cL;
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
            e.printStackTrace();
        }
        // The key length has to be 16 bytes
        sks= new SecretKeySpec(SECRET_KEY, ALGORITHM);

    }

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
            socket=new Socket(host,port);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOut= new PrintStream(socket.getOutputStream());
            socketOut.println(encrypt(pseudo));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + host);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to:"+ host);
            e.printStackTrace();
        }
        receive=new ReceiverThread(this, conL);
        receive.start();
    }




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

    public synchronized void addMessage(String message){
        if(socket!=null && isConnected()) {
            socketOut.println(encrypt(message));
        }
        //receive=new ReceiverThread(this, conL);
        //receive.start();
        //displayPseudosConnected();
    }
    public synchronized void disconnect(){
        try {
            socketOut.close();
            socketIn.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void joinConversation(int id){
    }

    public synchronized boolean isConnected(){
        boolean res= socket!=null && !socket.isClosed() && receive!=null
                && receive.isRunning();
        return res;
    }

    public void displayPseudosConnected(){
        for(String pseudo:pseudosConnected){
            System.out.println(pseudo);
        }
    }

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

    public void converseWith(String pseudoDest,String message) {
            socketOut.println(encrypt("private " + pseudoDest + " " + message));
        //receive=new ReceiverThread(this, conL);
        //receive.start();
    }

        public LinkedList<String> getPseudosConnected(){
        return pseudosConnected;
    }

    public boolean getPseudoSetted() {
        return pseudoSetted;
    }
    public void resetPseudosConnected(){
        pseudosConnected=new LinkedList<>();
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
        this.pseudoSetted=true;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public BufferedReader getSocketIn() {
        return socketIn;
    }
    public PrintStream getSocketOut() {
        return socketOut;
    }

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
        //Client client=new Client(conL);
        Client client=new Client(new ConnectionListener() {
            public void onReceiveMessage(String msg) {}
            public void onReceivePrivateMessage(String msg) {}
            public void onConnectionLost(String msg) {}
        });
        client.connect("greg","localhost",8084);
        client.converse();
        //client.converseWith("wesh");
        client.disconnect();
    }
}


