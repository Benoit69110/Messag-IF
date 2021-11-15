package back.client;

import java.io.BufferedReader;
import java.io.IOException;

public class ReceiverThread extends Thread{
    private BufferedReader socketIn;
    private Client client;
    public ReceiverThread(Client client){
        this.client=client;
        this.socketIn=client.getSocketIn();
    }

    public synchronized void run(){
        try {
            String msg;
            while((msg = socketIn.readLine()) != null) {
                if(msg.substring(0,14).equals("Your pseudo is ")){
                    client.setPseudo(msg.substring(15));
                }
                System.out.println(msg);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected");
        }
    }
}
