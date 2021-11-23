package back.clientMulticast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientMulticast {
    private String pseudo;
    private int port;
    private InetAddress group;
    ClientMulticastSender senderThread;
    ClientMulticastReceiver receiverThread;

    public ClientMulticast(){
        port=5000;
        group=null;
        senderThread=null;
        receiverThread=null;

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Choose your pseudo : ");
        try{
            pseudo=stdIn.readLine();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Your pseudo is "+pseudo+". Now you can share your life...");
        setNewHost("225.4.5.6",port);
    }

    public synchronized void setNewHost(String host, int port){
        if(receiverThread!=null){
            receiverThread.stop();
        }

        try {
            group=InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            System.out.println("An error occurred");
            System.exit(1);
        }
        this.port=port;
        senderThread=new ClientMulticastSender(pseudo,port,group,this);
        senderThread.start();
        receiverThread=new ClientMulticastReceiver(port,group);
        receiverThread.start();
    }
    public static void main(String[] args){
        new ClientMulticast();
    }
}
