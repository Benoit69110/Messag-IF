package back.server;

/***
 * Listener interface to communicate between the back and the front
 * @author: balgourdin, gdelambert, malami
 */
public interface ConnectionListener {

    /**
     * Method called on a client connexion
     * @param client
     * */
    public void onClientAccepted(ClientThread client);

    /**
     * Method called when a client disconnect
     * @param client
     * */
    public void onClientDisconnected(ClientThread client);

    /**
     * Method called to aknowledge the front of an event
     * @param report
     * */
    public void acknowledge(String report);
}