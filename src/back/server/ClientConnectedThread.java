package back.server;

import java.util.HashMap;

public class ClientConnectedThread {
    private ServerMultiThreaded server;
    public ClientConnectedThread(ServerMultiThreaded server){
        this.server=server;
    }

    public void sendConnectedPseudo(){
        server.broadcastPseudos();
    }
}
