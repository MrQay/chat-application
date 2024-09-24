package client;

/**
 * The ClientMain class is responsible for initializing the client-side application.
 */

public class ClientMain2 {

    public static void main(String[] args) {
        ClientModel clientModel2 = new ClientModel();
        ClientNetwork clientNetwork = new ClientNetwork(clientModel2);

        MainFrame mainFrame;
        try {
            mainFrame = new MainFrame(clientModel2, clientNetwork, args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            mainFrame = new MainFrame(clientModel2, clientNetwork, "");
        }
        clientModel2.addObserver(mainFrame);
    }

}
