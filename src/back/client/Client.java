/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package back.client;

import back.server.Conversation;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class Client implements ConnectionListener{
    private String pseudo;
    private boolean pseudoSetted;
    private Socket socket;
    private int port;
    private String host;
    private PrintStream socketOut;
    private BufferedReader socketIn;
    private LinkedList<String> pseudosConnected;
    private LinkedList<Conversation> conversation;
    private ReceiverThread receive;
    private ConnectionListener conL;

    public Client(ConnectionListener cL){
        pseudosConnected=new LinkedList<>();
        this.conL = cL;
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
            //socketOut.println(pseudo);

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
    public void onConnectionLost(String msg) {
        try {
            socket.close();
        } catch(Exception e) {}
        conL.onConnectionLost(msg);
    }

    public synchronized void addMessage(String message){
        if(socket!=null && isConnected())
            socketOut.println(message);
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
                    socketOut.println(line);
                }

            }
            stdIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void converseClient(String mess){
        socketOut.println(mess);
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

    public PrintStream getSocketOut() {
        return socketOut;
    }

    public BufferedReader getSocketIn() {
        return socketIn;
    }

    /**
     *  main method
     *  accepts a connection, receives a message from client then sends an echo to the client
     **/
    public static void main(String[] args) throws IOException {
        // creation socket ==> connexion
        //Client client=new Client(conL);
        //client.connect("greg","localhost",8084);
        //client.converse();
        //client.converseClient("wesh");
        //client.disconnect();
    }
}


