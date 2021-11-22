package back.client;

import java.io.BufferedReader;
import java.io.IOException;

public class ReceiverThread extends Thread{
    private BufferedReader socketIn;
    private BufferedReader socketInEncrypted;
    private Client client;
    private ConnectionListener listener;
    private boolean pseudoSetted=false;
    private boolean running=false;

    private final String BEGIN_RECEIVE_PSEUDOS="Clients connected (start)";
    private final String END_RECEIVE_PSEUDOS="Clients connected (end)";

    public ReceiverThread(Client client, ConnectionListener conL){
        this.client=client;
        this.socketIn=client.getSocketIn();
        this.listener = conL;
    }

    public boolean isRunning(){
        return running;
    }

    public synchronized void run(){
        running=true;
        try {
            String msg;
            boolean receivePseudosConnected=false;
            while(socketIn!=null &&(msg = socketIn.readLine()) != null) {
                msg=client.decrypt(msg);
                if(!client.getPseudoSetted()) {
                    if (msg.contains("Your pseudo is")) {
                        client.setPseudo(msg.substring(15));
                    }
                }
                if(msg.equals(BEGIN_RECEIVE_PSEUDOS)){
                    receivePseudosConnected=true;
                    client.resetPseudosConnected();
                }

                if(!receivePseudosConnected){
                    System.out.println(msg);
                    listener.onReceiveMessage(msg);
                }else if(!msg.equals(BEGIN_RECEIVE_PSEUDOS) && !msg.equals(END_RECEIVE_PSEUDOS)){
                    client.getPseudosConnected().add(msg);
                }

                if(msg.equals(END_RECEIVE_PSEUDOS)){
                    receivePseudosConnected=false;
                }

            }
            listener.onConnectionLost("Connection lost...");
        } catch (IOException e) {
            System.out.println("You have been disconnected");
            client.disconnect();
            running=false;
            //System.exit(1);
        }
        running=false;
    }
}
