package server;

import utils.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a connection between a client and the server.
 * Implements the Runnable interface
 * Responsbile for creating directory holding Chat history
 * Responsible for catching incoming messages from the User
 * Responsible for saving messages to accurate history-file.
 * Responsible for Load the history and send it to the User
 * Responsible for giving the User the Userlist of connected users on the server.
 *
 * @author Baseer
 */

public class ClientConnection implements Runnable {
    private final Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private utils.ChatHistory chatHistory;
    private List<ClientConnection> clientConnections;
    private User user;

    private boolean connected;
    private boolean loaded;

    /**
     * @pre Assumes socket && connectionList && connectionManager != null
     * @param socket the socket the User connected to the server with
     * @param connectionList the current User list in server of users connected/online
     * @post boolean connected = false
     *       this.socket = socket
     *       A new Threadsafe CopyOnWriteArrayList is created which holds connectionList
     *
     */

    public ClientConnection(Socket socket, List<ClientConnection> connectionList) {
        this.connected = false;
        this.loaded = true;
        this.socket = Objects.requireNonNull(socket);
        Objects.requireNonNull(connectionList);
        this.clientConnections = new CopyOnWriteArrayList<>();
        this.clientConnections.addAll(connectionList);
    }

    /**
     * Handles the communication between a client and the server.
     * @pre None
     * @post ObjectinputStream is created with current socket
     *       Objectoutputstream is created with current socket
     *       The username from the client is captured via inputstream
     *       Validation is made where the username is checked. If it already exits in the Userlist DENIED is sent back, else OK is sent back.
     *       A new directory is created if no existed
     *       The history for the current user is loaded from file and sent to the user
     *       Info about the new user connection is broadcasted to all other users online.
     *       Waitformessage starts to loop on a new thread
     *       Closes the streams and stops the thread if method disconnet us runned or validation fails (Denied)
     */

    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream())) {
            this.objectInputStream = objectInputStream;
            this.objectOutputStream = objectOutputStream;
            Message message = (Message) objectInputStream.readObject();

            this.user = message.getSender();

            setConnected(validateUser());
            Message validationMessage;
            if(connected) {
                validationMessage = Message.builder()
                        .messageType(Message.MessageType.CLIENT_INFO)
                        .text("OK")
                        .build();
            }else {
                validationMessage = Message.builder()
                        .messageType(Message.MessageType.CLIENT_INFO)
                        .text("DENIED")
                        .build();
            }
            sendMessage(validationMessage);

            if(connected) {
                System.out.println("New client connected: " + user.getName());

                createDirectoryIfNotExists("ChatHistory");

                broadcastClientList();

                loadHistory();

                waitForMessage();

                Thread connectionLoopThread = new Thread(() -> {
                    try {
                        setLoaded(true);
                        connectionLoop();
                    } catch (InterruptedException e) {
                        System.out.println("Validation error");
                        e.printStackTrace();
                    }
                });
                connectionLoopThread.start();
                connectionLoopThread.join();
            }

            disconnectionBroadcast();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error handling client connection: " + e.getMessage());
            disconnectionBroadcast();
        } catch (InterruptedException e) {
            System.out.println("ClientConnection thread error: " + e.getMessage());
            disconnectionBroadcast();
        }
    }
    private synchronized void connectionLoop() throws InterruptedException {
        while(connected) {
            try {
                wait();
            }catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private synchronized void disconnectionBroadcast() {
        System.out.println(this.user.getName() + " disconnected!");
        if(this.clientConnections.contains(this)){
            this.clientConnections.remove(this);
        }
        broadcastClientList();
        setLoaded(true); //Sets loaded to true if the connection between client and server is disconnected while client is validated.
    }

    private boolean validateUser() {
        for (ClientConnection conn : this.clientConnections) {
            if (this.user.equals(conn.getUser())) {
                return false;
            }
        }
        return true;
    }

    private void waitForMessage() {
        new Thread(() -> { /* Create and start a new thread to handle incoming messages without blocking the main thread */
            try {
                while (connected) { /* Loop to listen for incoming messages */
                    Message message = (Message) this.objectInputStream.readObject(); /* Waiting and reads incoming messages from the client */

                    for (ClientConnection connection : clientConnections) {  /* Forward the message to the appropriate recipients */
                        if (connection != this && connection.getUser().equals(message.getReceiver())) {
                            if (message.getMessageType() == Message.MessageType.REGULAR_MESSAGE) {
                                addHistory(message);
                                connection.addHistory(message);
                            }
                            connection.sendMessage(message);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error handling client waitForMessage: " + e.getMessage());
                disconnectionBroadcast();
            }
        }).start();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveHistory() throws IOException {
        File savefile = new File("ChatHistory/" + this.user.getName() + "_chatHistory.ser");
        savefile.delete(); // Ignore if delete fails.
        savefile.createNewFile();
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(savefile))) {
            out.writeObject(this.chatHistory);
        }
    }

    private void loadHistory() throws IOException {
        this.chatHistory = new ChatHistory(this.user);
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("ChatHistory/" + this.user.getName() + "_chatHistory.ser"))) {
            this.chatHistory= (ChatHistory)in.readObject();
            Message chatHistoryMessage = Message.builder()
                    .messageType(Message.MessageType.CHAT_HISTORY)
                    .chatHistory(this.chatHistory)
                    .build();
            sendMessage(chatHistoryMessage);
        } catch (FileNotFoundException e) {
            saveHistory();
        } catch (Exception e) {
            System.out.println("Load Error: " + e.getMessage());
        }
    }


    /**
     * Called by ConnectionManager everytime a user connects to the server
     * @pre connectionList != null
     * @param connectionList, the list to be added.
     * @post adds the connectionList to this.clientConnections
     * @throws NullPointerException if Connectionlist is null;
     *
     */
    public synchronized void updateConnectionList(List<ClientConnection> connectionList) {
        this.clientConnections = connectionList;
    }

    private synchronized void broadcastClientList() {
        ArrayList<User> onlineUsers = new ArrayList<>();
        for (ClientConnection connection : this.clientConnections) {
            onlineUsers.add(connection.getUser());
        }
        Message clientListMessage = Message.builder()
                .messageType(Message.MessageType.CLIENT_LIST)
                .onlineUsers(onlineUsers)
                .build();

        for (ClientConnection connection : this.clientConnections) {
            connection.sendMessage(clientListMessage);
        }

    }

    private synchronized void sendMessage(Message message) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.out.println("Failed to send message");
        }
    }

    /**
     * Checks if client is connected to the server
     * @pre None
     * @return true if socket is NOT closed, else false.
     * @post socket is unchanged.
     */
    public synchronized boolean isActive() {
        return !socket.isClosed();
    }

    /**
     * Checks if client is connected to the server
     * @pre None
     * @return the value if connected. True || False
     * @post connected is unchanged
     */
    public synchronized boolean isConnected() throws InterruptedException {
        while (!connected && isActive()) {
            wait();
        }
        return connected;
    }

    private void createDirectoryIfNotExists(String directoryName) {
        File directory = new File(directoryName);

        // Check if the directory already exists
        if (!directory.exists()) {
            boolean success = directory.mkdir(); // Create the new directory
            if(!success) System.out.println("Failed to create directory");
        }
    }

    private synchronized void addHistory(Message message) throws IOException {
        this.chatHistory.addHistory(message.getSender(), message.getReceiver(), message);
        saveHistory();
    }

    private synchronized User getUser() {
        return this.user;
    }


    private synchronized void setLoaded(boolean loaded) {
        this.loaded = loaded;
        notifyAll();
    }

    private synchronized void setConnected(boolean connected) {
        this.connected = connected;
        notifyAll();
    }


    /**
     * ConnectionManager waits for this thread to load before loading the next connection in the queue
     * @pre None
     * @return the value if loaded. True || False
     * @post loaded is unchanged
     * @throws InterruptedException if thread.wait fails
     */
    public synchronized boolean isLoaded() throws InterruptedException {
        while (!loaded) {
            wait();
        }
        return loaded;
    }


}