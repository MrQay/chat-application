package server;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

    /**
     * Manages all client connection threads
     * Responsible for creating new instances of ClientConnection when a client user is connecting.
     * Adds all Validated ClientConnections to the list activeConnections.
     * Responsible for updating all ClienctConnections in activeConnections List with new activeConnection list when new client is connected and Validated
     *
     * @author Lukas
     */
public class ConnectionManager implements Runnable{
    private final static int PORT = 2023;
    private final List<ClientConnection> activeConnections;
    private boolean online;

        /**
         *
         * @pre None
         * @post Creates a new List tmpList that holds ClientConnctions objects.
         *       Creates a threadsafe synchronizedList activeConnections that holds tmpList
         */
    public ConnectionManager() {
        List<ClientConnection> tmpList = new ArrayList<>();
        this.activeConnections = Collections.synchronizedList(tmpList);
    }

    //comment
    private void startServer() {
        online = true;
        // Create a new ServerSocket object to listen for incoming client connections
        try (SocketQueue socketQueue = new SocketQueue(ConnectionManager.PORT)) {

            // Wait for incoming client connections and create a new Socket object for each one
            while (online) {

                System.out.println("Ready for new connection: ");
                Socket socket = socketQueue.getNextSocket();
                System.out.println("New client connected: " + socket);

                ClientConnection clientConnection = new ClientConnection(socket, activeConnections);
                new Thread(clientConnection).start();

                clientConnection.isConnected();

                if(clientConnection.isActive()) {
                    this.activeConnections.add(clientConnection);
                    //Update every connection with a new updated list with active connections
                    Iterator<ClientConnection> iterator = this.activeConnections.iterator();
                    while (iterator.hasNext()) {
                        ClientConnection connection = iterator.next();
                        if (connection.isActive()) {
                            connection.updateConnectionList(this.activeConnections);
                        } else {
                            iterator.remove();
                        }
                    }
                }
                System.out.println("Waiting for socket to load");
                clientConnection.isLoaded();
            }
        }catch (IOException | InterruptedException e) {
            online = false;
            System.out.println("Server is offline");
            e.printStackTrace();

        }
    }
        /**
         * @pre None
         * @post calls the method startServer which checks the next incoming connection in the SocketQueue and creates ClientConnections objects.
         */
    @Override
    public void run() {
        startServer();
    }
}
