package back.clientMulticast;

import java.io.IOException;
import java.net.*;
/***
 * ClientMulticastReceiver
 * UDP thread which can receive messages
 * @author: balgourdin, gdelambert, malami
 */
public class ClientMulticastReceiver extends Thread{
    /** Group of multicast */
    private InetAddress group;
    /** Port of multicast */
    private int port;
    /** Socket to receive messages */
    private MulticastSocket socket;

    /**
     * Constructor
     * @param port
     * @param group
     */
    public ClientMulticastReceiver(int port, InetAddress group){
        this.port=port;
        this.group=group;
    }

    /**
     * Start the thread and join the group. Then it handles all received messages
     */
    @Override
    public synchronized void run(){
        try{
            // Socket connexion
            socket=new MulticastSocket(port);
            // Join the group
            socket.joinGroup(group);
            while(true){
                byte[] buf=new byte[1024];
                // Reception of messages
                DatagramPacket pack=new DatagramPacket(buf, buf.length);
                socket.receive(pack);
                // System.out.println("Received data from "+pack.getSocketAddress());
                String msgReceived=new String(pack.getData(),0,pack.getLength());
                if(msgReceived.equals("exit")){
                    break;
                }
                System.out.println(msgReceived);
            }
            stopThread();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method called when we want to stop the thread
     */
    public synchronized void stopThread(){
        try {
            // Leave the group
            socket.leaveGroup(group);
            socket.close();
        } catch (IOException e) {
             e.printStackTrace();
        }
    }
    public static void main(String[] args){
       //new ClientMulticastReceiver().start();
    }
}
