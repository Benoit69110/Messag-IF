package back.server;

public class ClientConnectedThread {
    private ServerMultiThreaded server;
    public ClientConnectedThread(ServerMultiThreaded server){
        this.server=server;
    }

    public void sendConnectedPseudo(){
        server.broadcastPseudos();
    }
}
