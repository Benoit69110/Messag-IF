package back.server;

public class ConnectionThread {
    private ServerMultiThreaded server;
    public ConnectionThread(ServerMultiThreaded server){
        this.server=server;
    }

    public void acceptConnection(){
        server.acceptClient();
    }
}
