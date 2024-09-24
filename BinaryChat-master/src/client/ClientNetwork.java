package client;

import utils.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * The class is responsible for managing the network connection between the client and the server.
 * It establishes the connection with the server and sends/receives
 * messages using ObjectOutputStream and ObjectInputStream.
 * It also handles the different types of messages received and updates the ClientModel accordingly.
 *
 * @author Ebrahim
 */

public class ClientNetwork {
    private final int PORT = 2023;
    private final String HOST = "127.0.0.1";
    private final ClientModel clientModel;
    private boolean connected;
    private User sender;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private Thread incomingThread;

    /**
     * Constructs a new ClientNetwork object
     * @pre assumes clientmodel != null
     * @param clientModel, the model connected to the client.
     * @post Creates an instance of ClientNetwork with the specified model
     *       Sets connected = false
     *       sets the user Sender to the user in model
     *       Calls connectToServer which starts a new thread and opens an Objectoutputstream.
     *       ConnectToServer informs the server about the new client, and then wait for messages.
     */
    public ClientNetwork(ClientModel clientModel) {
        this.connected = false;

        this.clientModel = clientModel;
    }

    /**
     * @pre None
     * @post Sets connected to true
     *       Creates a new thread to handle the connection
     *       tries to create a new socket with the hardcoded host and port
     *       Creates an outputstream
     *       sets serverstatus to allowed, meaning the connection established.
     *       runs waitforMessage() which listens ti incoming messages from the server
     *       runs sendClientInfo which send a message to the server containing the clients username
     *       Closes the streams when the connection is closed
     *       Closes the streams if no connection could be established.
     *       Sets the serverstatus to Network_error if no connection could be established.
     */
    public void connectToServer(User user) {
        connected = true;
        this.sender = user;
        Thread connectionThread = new Thread(() -> {
            try(Socket socket = new Socket(this.HOST, this.PORT);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
                this.socket = socket;
                this.objectOutputStream = objectOutputStream;
                clientModel.setServerStatus(Message.VALIDATE.ALLOWED);

                waitForMessage();
                sendClientInfo();

                Thread connectionLoopThread = new Thread(() -> {
                    try {
                        connectionLoop();
                    } catch (InterruptedException e) {
                        System.out.println("Validation error");
                        e.printStackTrace();
                    }
                });
                connectionLoopThread.start();


                connectionLoopThread.join();

            } catch (IOException e) {
                System.out.println("ClientNetwork: Connection Error");
                clientModel.setValidated(Message.VALIDATE.NETWORK_ERROR);
                disconnect();
            } catch (InterruptedException e) {
                System.out.println("ClientNetwork: Loop Error");
                e.printStackTrace();
                disconnect();
            }

        });
        connectionThread.start();
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
    private synchronized void disconnect() {
        if(connected) {
            this.connected = false;
            notifyAll();
        }
    }
    private void sendClientInfo() {
        Message message = Message.builder()
                .messageType(Message.MessageType.CLIENT_INFO)
                .sender(this.sender)
                .build();
        sendMessage(message);
    }
    private void waitForMessage() {
        // Create and start a new thread to handle incoming messages
        incomingThread = new Thread(() -> {
            try(ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream())) {
                // Loop to listen for incoming messages
                while (connected) {
                    //Read the incoming message object from the input stream and type-cast to Message class
                    Message message = (Message) objectInputStream.readObject();

                    //Checks which type of message that is received
                    if (message.getMessageType() == Message.MessageType.REGULAR_MESSAGE) {
                        System.out.println("I recieved a regular message");
                        clientModel.addLocalHistory(message);
                    }else if (message.getMessageType() == Message.MessageType.CHAT_HISTORY) {
                        System.out.println("I received a chat history");
                        try {
                            clientModel.updateChatHistory(message.getChatHistory());

                        } catch (NullPointerException e) {
                            System.out.println("ERROR fetching history");
                        }
                    } else if (message.getMessageType() == Message.MessageType.CLIENT_LIST) {
                        System.out.println("I received a client_list");
                        try {
                            clientModel.updateUserList(message.getOnlineUsers());

                        } catch (NullPointerException e) {
                            System.out.println("ERROR fetching user list");
                        }
                    } else if (message.getMessageType() == Message.MessageType.CLIENT_INFO) {
                        System.out.println("I received client info");
                        if(message.getText().equals("OK")) {
                            clientModel.setValidated(Message.VALIDATE.ALLOWED);
                        }else {
                            clientModel.setValidated(Message.VALIDATE.DENIED);
                        }
                    } else {
                        System.out.println("I received an unknown message");
                    }
                }
            } catch (SocketException e) {
                // Catch if server disconnects unexpectedly
                System.out.println("Server disconnected: " + e.getMessage());
            } catch (IOException e) {
                // Catches any IO exceptions that occur
                System.out.println("Error receiving message: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                // Catch if object is not a message
                System.out.println("Invalid message received: " + e.getMessage());
            }
            disconnect();
        });
        incomingThread.start();
    }

    /**
     * @pre message != null.
     * @param message, the message to be sent.
     * @post Tries to send the messages via the objectOutputstream to the server.
     */
    public void sendMessage(Message message) {
        try {
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            System.out.println("Exception sending message to server: " + e);
        }
    }

    /**
     * Adds a new message to the local chat history and sends it to the server.
     * @pre receiver != null. Text & File can be null
     * @param receiver, the user that are the receiver.
     * @param text, the text to be sent to the receiver
     * @param file , the file, if any, to be sent to the receiver.
     * @post Creates a message with the given constituents
     *       runs the method addLocalHistory
     *       Calls the method sendMessage via the clientnetwork class, which sends the message to the server.
     */
    public void createMessage(User receiver, String text, FileSerialized file) {
        if (receiver == null) {
            throw new NullPointerException("Receiver cannot be null");
        }
        Message message = Message.builder()
                .sender(clientModel.getUser())
                .receiver(receiver)
                .file(file)
                .text(text)
                .messageType(Message.MessageType.REGULAR_MESSAGE)
                .build();

        clientModel.addLocalHistory(message);
        sendMessage(message);
    }

}
