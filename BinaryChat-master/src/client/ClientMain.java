package client;

/**
 * The ClientMain class is responsible for initializing the client-side application.
 *
 *
 * @author Natan
 */
public class ClientMain {
    public static void main(String[] args) {
        ClientModel clientModel = new ClientModel();
        ClientNetwork clientNetwork = new ClientNetwork(clientModel);

        MainFrame mainFrame;
        try {
            mainFrame = new MainFrame(clientModel, clientNetwork, args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            mainFrame = new MainFrame(clientModel, clientNetwork, "");
        }
        clientModel.addObserver(mainFrame);
    }
}
