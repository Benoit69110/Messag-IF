package back.server;
/***
 * Thread to accept client connexion
 * @author: balgourdin, gdelambert, malami
 */
public class ConnectionThread extends Thread {
    /** Server */
    private ServerMultiThreaded server;

    /**
     * Constructor
     * @param server
     */
    public ConnectionThread(ServerMultiThreaded server) {
        this.server = server;
    }

    /**
     * Accept client on the server
     */
    @Override
    public synchronized void run() {
        try{
            server.acceptClient();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
