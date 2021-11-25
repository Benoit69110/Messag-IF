package back.server;

/***
 * ClientConnectedThread
 * Thread which broadcast the list of pseudos connected
 * @author balgourdin, gdelambert, malami
 */
public class ClientConnectedThread {
    /** Server */
    private ServerMultiThreaded server;

    /**
     * Constructor
     * @param server
     */
    public ClientConnectedThread(ServerMultiThreaded server){
        this.server=server;
    }


    public void sendConnectedPseudo(){
        server.broadcastPseudos();
    }
}
