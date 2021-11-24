package back.server;

public class ConnectionThread extends Thread {
    private ServerMultiThreaded server;

    public ConnectionThread(ServerMultiThreaded server) {
        this.server = server;
    }

    @Override
    public synchronized void run() {
        try{
            server.acceptClient();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void acceptConnection() {
        server.acceptClient();
    }
}
