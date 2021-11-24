package back.clientMulticast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
/***
 * ClientMulticast
 * UDP client which can send and receive messages, thanks to another thread
 * @author: balgourdin, gdelambert, malami
 */
public class ClientMulticast {
    /** Pseudo of the client */
    private String pseudo;
    /** Listnening port of the server */
    private int port;
    /** Group of multicast */
    private InetAddress group;
    /** Thread to send messages */
    private ClientMulticastSender senderThread;
    /** Thread to receive messages */
    private ClientMulticastReceiver receiverThread;

    /**
     * Constructor
     */
    public ClientMulticast(){
        port=5000;
        group=null;
        senderThread=null;
        receiverThread=null;
        // Define the pseudo of the client
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Choose your pseudo : ");
        try{
            pseudo=stdIn.readLine();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Your pseudo is "+pseudo+". Now you can share your life...");
        // Join a pre defined group
        setNewHost("225.4.5.6",port);
    }

    /**
     * Method to change of multicast group
     * @param host
     * @param port
     */
    public synchronized void setNewHost(String host, int port){
        if(receiverThread!=null){
            receiverThread.stop();
        }
        // Get the new address of the group
        try {
            group=InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            System.out.println("An error occurred");
            System.exit(1);
        }
        this.port=port;
        // Create a new sender thread with the new port and group
        senderThread=new ClientMulticastSender(pseudo,port,group,this);
        // Start the thread
        senderThread.start();
        // Create a new receiver thread with the new port and group
        receiverThread=new ClientMulticastReceiver(port,group);
        // Start the thread
        receiverThread.start();
    }
    public static void main(String[] args){
        new ClientMulticast();
    }
}
