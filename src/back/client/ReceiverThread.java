package back.client;

import java.io.BufferedReader;
import java.io.IOException;

/***
 * ReceiverThread
 * Thread which handle received messages of a client
 * @author balgourdin, gdelambert, malami
 */
public class ReceiverThread extends Thread{
    /** Input stream of the client */
    private BufferedReader socketIn;
    /** Client linked to this thread*/
    private Client client;
    private ConnectionListener listener;
    private boolean pseudoSetted=false;
    private boolean running=false;

    private final String BEGIN_RECEIVE_PSEUDOS="Clients connected (start)";
    private final String END_RECEIVE_PSEUDOS="Clients connected (end)";

    /**
     * Constructor
     * @param client
     * @param conL
     */
    public ReceiverThread(Client client, ConnectionListener conL){
        this.client=client;
        this.socketIn=client.getSocketIn();
        this.listener = conL;
    }

    /**
     * Method to know if the thread is running
     * @return
     */
    public boolean isRunning(){
        return running;
    }

    /**
     * When the thread is running : reception of all messages
     */
    @Override
    public synchronized void run(){
        running=true;
        try {
            String msg;
            boolean receivePseudosConnected=false;
            while(socketIn!=null &&(msg = socketIn.readLine()) != null) {
                // Decrypt the message received
                msg=client.decrypt(msg);
                if(!client.getPseudoSetted()) {
                    // Set the pseudo if the server accept it
                    if (msg.contains("Your pseudo is")) {
                        client.setPseudo(msg.substring(15));
                    }
                }
                // When the server sends the list of connected pseudos we reset the list
                if(msg.equals(BEGIN_RECEIVE_PSEUDOS)){
                    receivePseudosConnected=true;
                    client.resetPseudosConnected();
                }

                // We are not receiving the different pseudos
                if(!receivePseudosConnected){
                    String privateMsg=msg.split(" ")[0];
                    // Check if the message is a private one
                    if(privateMsg.equals("private")){
                        listener.onReceivePrivateMessage(msg);
                    }else{
                        listener.onReceiveMessage(msg);
                    }
                    System.out.println(msg);
                }else if(!msg.equals(BEGIN_RECEIVE_PSEUDOS) && !msg.equals(END_RECEIVE_PSEUDOS)){
                    // Add the pseudo to the list
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
