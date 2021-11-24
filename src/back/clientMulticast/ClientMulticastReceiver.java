package back.clientMulticast;

import java.io.IOException;
import java.net.*;

public class ClientMulticastReceiver extends Thread{
    private InetAddress group;
    private int port;
    private MulticastSocket socket;

    public ClientMulticastReceiver(int port, InetAddress group){
        this.port=port;
        this.group=group;
    }

    public synchronized void run(){
        try{
            socket=new MulticastSocket(port);
            socket.joinGroup(group);
            while(true){
                byte[] buf=new byte[1024];
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

    public synchronized void stopThread(){
        try {
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
