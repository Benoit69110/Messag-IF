/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package back.client;

import java.io.*;
import java.net.*;



public class Client {
    private String pseudo;
    private Socket socket;
    private int port;
    private String host;
    private PrintStream socketOut;
    private BufferedReader stdIn;
    private BufferedReader socketIn;

    public Client(String pseudo,int port,String host){
        this.pseudo=pseudo;
        this.port=port;
        this.host=host;
        try {
            socket=new Socket(host,port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    /**
  *  main method
  *  accepts a connection, receives a message from client then sends an echo to the client
  **/
    public static void main(String[] args) throws IOException {

        Socket echoSocket = null;
        PrintStream socOut = null;
        BufferedReader stdIn = null;
        BufferedReader socIn = null;

        if (args.length != 2) {
          System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port>");
          System.exit(1);
        }

        try {
      	    // creation socket ==> connexion
            echoSocket = new Socket(args[0],new Integer(args[1]).intValue());
	        socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
	        socOut= new PrintStream(echoSocket.getOutputStream());
	        stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + args[0]);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "+ "the connection to:"+ args[0]);
            System.exit(1);
        }

        System.out.println("Client connected to "+args[0]+" on "+args[1]);
        System.out.println("Write something :");
        String line;
        boolean lifeClient=true;
        while (lifeClient) {
        	line=stdIn.readLine();
        	if (line.equals(".")) break;
        	socOut.println(line);
            String response=socIn.readLine();
            if(response.equals("See you soon...")){
                lifeClient=false;
            }
        	System.out.println("echo: " + response);
        }
        socOut.close();
        socIn.close();
        stdIn.close();
        echoSocket.close();
    }
}


