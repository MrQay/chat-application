package utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Keeps a hashmap that stores a chat history array for each user(key)
 * that has communicated with the current user on this client.
 *
 * @author Ebrahim
 */
public class ChatHistory implements Serializable {
    private final User user;
    private final HashMap<User, ArrayList<Message>> chatHistory;

    /**
     * @pre Assumes user != null
     * @param user, the user to be associated with the chat history.
     * @post Sets this.User = user
     *       Creates a new Hashmap chatHistory
     */
    public ChatHistory(User user) {
        this.user = Objects.requireNonNull(user);
        System.out.println("Chat history created to  " +  user.getName());
        chatHistory = new HashMap<>();
    }

    private void createNewHistory(User receiver) {
        chatHistory.put(receiver, new ArrayList<Message>());
    }

    /**
     * @pre assumes receiver != null
     * @param receiver, the users chat history to be retrieved
     * @return a hashmap containing all the current user's chat history with the given receiver.
     *         If no history exists with given receiver, an empty Arraylist is returned.
     * @post chatHistory is unchanged.
     */
    public ArrayList<Message> getHistory(User receiver) {
        if  (chatHistory.get(receiver) == null) {
            return new ArrayList<Message>();
        }else
            return chatHistory.get(receiver);
    }

    /**
     * @pre assumes message != null
     * @param message, the message to be added to the chat history.
     * @param sender, User sending the message
     * @param receiver, User receiving the message
     * @post Adds the message to the chathistory. Always save the history with the username the current user is chatting with as key,
     *       or creates a new history array if none exists.
     */
    public void addHistory(User sender, User receiver, Message message) {
        User userKey = receiver;
        if(receiver.equals(this.user)) { //Checks if the message is sent by or received to the current user
            userKey = sender;
        }
        try {
            chatHistory.get(userKey).add(message);
        }catch (NullPointerException e) {
            createNewHistory(userKey); //Creates a new history array if the sender and receiver has no chat history
            chatHistory.get(userKey).add(message);
        }
    }
}
