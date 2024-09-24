package client;

import java.util.ArrayList;
import java.util.Objects;

import utils.*;

/**
 * The `ClientModel` class represents the model component in the client-server architecture. Responsible for
 * maintaining the state of the client, including the user list, chat history, and user login status.
 *
 * @author Natan
 */
public class ClientModel implements Observable {

    private final ObservableSupport obs = new ObservableSupport();
    private ChatHistory chatHistory;
    private ArrayList<User> userList = new ArrayList<>();
    private User user;
    private Message.VALIDATE validated;
    private Message.VALIDATE serverOnline;
    private boolean loggedIn;

    private String currentReceiver;

    /**
     * Creates a new `ClientModel` object with default values for its instance variables.
     * @pre None
     * @post Sets boolean LoggedIn = false,
     *       User user = null
     */
    public ClientModel(){
        this.loggedIn = false;
        this.user = null;
    }
    /**
     * Updates the user list with the specified list of users.
     * @pre userList != null
     * @param userList, the Arraylist to be added.
     * @post Sets this.userList = userList
     *       Calls the method update on all Observers in obs.
     * @throws IllegalArgumentException if userList = null;
     */
    public synchronized void updateUserList(ArrayList<User> userList ) {
            this.userList = userList;
            this.obs.update();
        }

    /**
     * Updates the chat history with the specified chat history.
     * @pre chathistory != null
     * @param chatHistory, the instance of chatHistory class to be added.
     * @post Sets this.chatHistory = chatHistory
     *       Calls the method update on all Observers in obs.
     * @throws IllegalArgumentException if chathistory = null;
     */
    public synchronized void updateChatHistory(ChatHistory chatHistory) {
            this.chatHistory = chatHistory;
            this.obs.update();
        }

    /**
     * Adds an observer to the class ObservableSupport
     * @pre assumes obs != null
     * @param obs, the observer to be added.
     * @post The instance of ObservableSupport obs adds the observer
     */
    @Override
    public void addObserver(Observer obs) {
        this.obs.addObserver(obs);
    }


    /**
     * Removes an observer from the class ObservableSupport
     * @param obs, the observer to be removed.
     * @pre assumes obs != null
     * @post The instance of ObservableSupport obs removes the observer
     */
    @Override
    public synchronized void removeObserver(Observer obs) {
        this.obs.removeObserver(obs);
    }


    /**
     * @pre None
     * @return returns the ArrayList<User> this.userList
     * @post this.userlist is unchanged
     * @throws NullPointerException if userList = null
     */
    public synchronized ArrayList<User> getUserList() {
        return this.userList;
    }
    /**
     * @pre assumes message != null.
     * @param message, the message to be added to the local history.
     * @post Adds the message to the chatHistory class.
     *       Calls the method update on all Observers in obs.
     */
    public synchronized void addLocalHistory(Message message) {
        this.chatHistory.addHistory(message.getSender(), message.getReceiver(), message);
        this.obs.update();
    }
    /**
     * @pre receiver != null
     * @param receiver, the users history to be received.
     * @return an ArrayList<Message> containing all the messages exchanged between the current user and the given receiver,
     *         or an empty list if no history exists.
     * @post chatHistory is unchanged
     * @throws NullPointerException if receiver = null
     *
     */
    public ArrayList<Message> getHistory(User receiver) {
        return this.chatHistory.getHistory(receiver);
    }


    /**
     * Checks if the specified username is valid and sets the current user to that username if it is.
     * @pre ensures Username != null.
     * @param username, the username to be checked.
     * @post  A new instance of User is created with the username = username
     *        A instance of ClientNetwork is created.
     *        A new thread is created that runs the method waitForValidation.
     *        That method runs until the method setValidated run Notifyall
     */
    public void checkUser(String username) {

        if(!username.isEmpty()){

            this.user = new User(username);
            this.chatHistory = new ChatHistory(user);
            Thread waitThread = new Thread(() -> {
                try {
                    waitForValidation();
                } catch (InterruptedException e) {
                    System.out.println("Validation error");
                    e.printStackTrace();
                }
            });
            waitThread.start();

            try {
                waitThread.join();
            } catch (InterruptedException e) {
                System.out.println("Validation error");
                e.printStackTrace();
            }
        }
    }
    private synchronized void waitForValidation() throws InterruptedException {
        wait();
    }

    /**
     * Set the validated status of the user.
     * @pre assumes val != null
     * @param val, the Validate enum to be set.
     * @post Sets this.Validated = val
     *       Calls the method set_serverstatus which sets this.serveronline = val
     *       Sets loggedIn = true if val = Allowed
     *       NotifyAll threads.
     * @throws NullPointerException if val = null
     */
    public synchronized void setValidated(Message.VALIDATE val) {
        if (val == null) {
            throw new NullPointerException("Val cannot be null");
        }
        this.validated = val;
        setServerStatus(val);
        if(validated == Message.VALIDATE.ALLOWED) {
            this.loggedIn = true;
            this.obs.update();
        }
        notifyAll();
    }

    /**
     * @pre assumes val != null
     * @param val, the Validate enum to be set.
     * @post Sets this.serveronline = val
     * @throws NullPointerException = null;
     */
    public void setServerStatus(Message.VALIDATE val){
        if (val == null) {
            throw new NullPointerException("Val cannot be null");
        }
        this.serverOnline = val;
    }
    /**
     * @pre None
     * @return message.validate = ALLOWED || DENIED || NETWORK_ERROR || null
     * @post this.serveronline is unchanged
     */
    public Message.VALIDATE getServerStatus(){
        if (this.serverOnline == null) {
            throw new NullPointerException("Val is null");
        }
        return this.serverOnline;
    }
    /**
     * @pre None
     * @return return loggedin = false || true || null
     * @post this.loggedIn is unchanged
     */
    public boolean loggedIn() {
        return loggedIn;
    }
    /**
     *  @pre none
     *  @return return User = this.User || null
     *  @post the User is unchanged
     *  @Throws NullPointerException if this.User is null;
     */
    public User getUser() {
        if (this.user == null){
            throw new NullPointerException("User is null");
        }
        return this.user;
    }


    /**
     *  @pre none
     *  @return return String = name of the current receiver || "" if current receiver is null
     *  @post currentReceiver is unchanged
     */
    public synchronized String getReceiver(){
        return Objects.requireNonNullElse(this.currentReceiver, "");

    }

    /**
     *  @pre Assumes rec != null
     *  @post sets currentReceiver = rec
     *        run update on all observers
     */
    public synchronized void setReceiver(String rec){
        this.currentReceiver = rec;
        this.obs.update();
    }

}