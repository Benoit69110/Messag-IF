package back.client;

/**
 * Listener interface to communicate between the back and the front
 * @author balgourdin, gdelambert, malami
 * */
public interface ConnectionListener {
    /**
     * Method called when the client receive a message
     * @param msg Message receive.
     * */
    public void onReceiveMessage(String msg);

    /**
     * Method called when a client has been disconnected
     * @param msg
     * */
    public void onConnectionLost(String msg);

    /**
     * Method called when a client receive a private message
     * @param msg
     */
    public void onReceivePrivateMessage(String msg);
}