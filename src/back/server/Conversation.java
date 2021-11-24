package back.server;

import java.util.LinkedList;

public class Conversation {
    private LinkedList<ClientThread> group;
    private int id;

    public Conversation(int id,LinkedList<ClientThread> group){
        this.id=id;
        this.group=group;
    }

    public void broadcast(String message){
        for(ClientThread client:group){
            client.getSocOut().println(message);
        }
    }



}
