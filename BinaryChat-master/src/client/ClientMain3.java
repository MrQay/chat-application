package client;

/**
 * The ClientMain class is responsible for initializing the client-side application.
 */

public class ClientMain3 {

    public static void main(String[] args) {
        ClientModel clientModel3 = new ClientModel();
        ClientNetwork clientNetwork = new ClientNetwork(clientModel3);

        MainFrame mainFrame;
        try {
            mainFrame = new MainFrame(clientModel3, clientNetwork, args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            mainFrame = new MainFrame(clientModel3, clientNetwork, "");
        }
        clientModel3.addObserver(mainFrame);
    }

}
