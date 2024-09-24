package server;

public class ServerMain {

    /**
     * The ServerMain class is the entry point for starting the server application.
     * It creates instances of the ServerModel, ServerView, and ServerControl classes, and connects them together.
     * It then starts the server by calling the start() method of the ServerControl.
     *
     * @author Ebrahim
     */
    public static void main(String[] args) {
        ServerModel serverModel = new ServerModel();
        ServerView serverView = new ServerView(serverModel);
        ServerControl serverControl = new ServerControl(serverModel);

        serverModel.addObserver(serverView);

        serverControl.start();
    }
}
