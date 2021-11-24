package back.server;

public class ConnectionThread extends Thread {
    private ServerMultiThreaded server;
    public ConnectionThread(ServerMultiThreaded server){
        this.server=server;
    }

    @Override
    public void run(){
        server.acceptClient();
    }

    public void acceptConnection(){
        server.acceptClient();
    }
}
