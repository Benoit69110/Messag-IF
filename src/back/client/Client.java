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


public class Client {
    private String pseudo;
    private Socket socket;
    private int port;
    private String host;
    private PrintStream socketOut;
    private BufferedReader stdIn;
    private BufferedReader socketIn;
    private LinkedList<Conversation> conversation;

    public Client(String pseudo,String host,int port){
        if(pseudo=="" || pseudo==null){
            this.pseudo="Anonymous";
        }else{
            this.pseudo=pseudo;
        }
        this.port=port;
        this.host=host;
        try {
            socket=new Socket(host,port);
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOut= new PrintStream(socket.getOutputStream());
            socketOut.println(pseudo);
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + host);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to:"+ host);
            e.printStackTrace();
        }
    }

    public synchronized void disconnect(){
        try {
            socketOut.close();
            socketIn.close();
            stdIn.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageTo(int id,String message){

    }
    public void converse(){

    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public PrintStream getSocketOut() {
        return socketOut;
    }

    public void setSocketOut(PrintStream socketOut) {
        this.socketOut = socketOut;
    }

    public BufferedReader getStdIn() {
        return stdIn;
    }

    public void setStdIn(BufferedReader stdIn) {
        this.stdIn = stdIn;
    }

    public BufferedReader getSocketIn() {
        return socketIn;
    }

    public void setSocketIn(BufferedReader socketIn) {
        this.socketIn = socketIn;
    }

    /**
  *  main method
  *  accepts a connection, receives a message from client then sends an echo to the client
  **/
    public static void main(String[] args) throws IOException {

        // creation socket ==> connexion
        Client client=new Client("benoit","localhost",8084);


        System.out.println("Write something :");
        String line;
        boolean lifeClient=true;
        while (lifeClient) {
        	line=client.getStdIn().readLine();
        	if (line.equals(".")) break;
        	client.getSocketOut().println(line);
            String response=client.getSocketIn().readLine();
            if(response.equals("See you soon...")){
                lifeClient=false;
            }
        	System.out.println("echo: " + response);
        }
        client.disconnect();
    }
}


