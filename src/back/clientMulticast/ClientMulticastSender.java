package back.clientMulticast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/***
 * ClientMulticastSender
 * UDP thread which can send messages
 * @author balgourdin, gdelambert, malami
 */
public class ClientMulticastSender extends Thread {
    /** Socket of the client*/
    private MulticastSocket socket;
    /** Group of multicast */
    private InetAddress group;
    /** Listening port */
    private int port;
    /** Pseudo of the client*/
    private String pseudo;
    /** Client */
    private ClientMulticast client;

    /**
     * Constructor
     * @param pseudo
     * @param port
     * @param group
     * @param client
     */
    public ClientMulticastSender(String pseudo,int port,InetAddress group,ClientMulticast client){
        this.pseudo=pseudo;
        this.port=port;
        this.group=group;
        this.client=client;
    }

    /**
     * Connect the client to the port and send messages
     */
    @Override
    public synchronized void run(){
        try{
            // Connexion on the listening port
            socket=new MulticastSocket(port);
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while((line=stdIn.readLine())!=null) {
                // Command change host
                if (line.equals("change host")) {
                    String newIp;
                    System.out.print("Choose the new IP : ");
                    newIp = stdIn.readLine();
                    while (!checkFormatIp(newIp)) {
                        System.out.print("Please choose a correct IP : ");
                        newIp = stdIn.readLine();
                    }

                    System.out.print("Choose the new port : ");
                    line = stdIn.readLine();
                    while (!checkFormatPort(line)) {
                        System.out.print("Please choose a correct port : ");
                        line = stdIn.readLine();
                    }
                    int newPort = Integer.parseInt(line);
                    client.setNewHost(newIp, newPort);
                    break;
                // Command show config
                } else if (line.equals("show config")) {
                    System.out.println("Local IP and port : " + InetAddress.getLocalHost().getHostAddress()+ ":" + port);
                    System.out.println("Group IP and port : " + group.toString() + ":" + port);
                // Command help
                }else if (line.equals("help")){
                    showCommands();
                // Commmand exit
                }else if(line.equals("exit")) {
                    byte[] buf=line.getBytes();
                    DatagramPacket pack=new DatagramPacket(buf, buf.length,group,port);
                    socket.send(pack);
                    break;
                }else{
                    // Send the message with the pseudo
                    line=pseudo+" : "+line;
                    byte[] buf=line.getBytes();
                    DatagramPacket pack=new DatagramPacket(buf, buf.length,group,port);
                    socket.send(pack);
                }
            }
            stopThread();
        }catch (Exception e){
            // e.printStackTrace();
        }
    }

    /**
     * Method called to close the socket
     */
    public synchronized void stopThread(){
        socket.close();
    }

    /**
     * Check the format of the IP, it has to be a multicast ip
     * From 224.0.0.1 to 239.255.255.254
     * @param ip
     * @return boolean
     */
    public boolean checkFormatIp(String ip){
        System.out.println(ip);
        String[] ipSplit=ip.split("\\.");

        if(ipSplit.length!=4){
            return false;
        }
        for(int i=0;i<ipSplit.length;i++){
            try{
                int number=Integer.parseInt(ipSplit[i]);
                if(number>255 || number<0){
                    return false;
                }
            }catch (Exception e){
                return false;
            }
        }
        try{
            InetAddress.getByName(ip);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * Check if the port given is a correct one
     * Between 1024 and 65535
     * @param port
     * @return
     */
    public boolean checkFormatPort(String port){
        int newPort;
        try{
            newPort=Integer.parseInt(port);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        if(newPort < 1024 || newPort > 65535) {
            return false;
        }
        return true;
    }

    /**
     * Method to display the different existing commands
     */
    public void showCommands(){
        System.out.println("*********************************************************************");
        System.out.println("help -> to see all available commands.");
        System.out.println("exit -> to leave the application.");
        System.out.println("show config -> to show your actual IP and port of the multicast.");
        System.out.println("change host -> to change the IP and the port of the multicast.\n" +
                "The new IP have to be in the range of multicast IP (from 224.0.0.1 to 239.255.255.254)");
        System.out.println("*********************************************************************");

    }
    public static void main(String[] args) {
       // new ClientMulticastSender().start();
    }
}
