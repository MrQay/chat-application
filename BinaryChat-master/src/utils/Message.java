package utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/** The Message class´s purpose is to hold different information that is sent between the client and the server.
 *  The message class can create different types of messages, and is determined of the Enum MessageType.
 *  MessageType = Regular_message: Holds information belonging to chat-messages sent between two users, such as sender,receiver,text,file
 *  MessageType = Chat_history: Holds a whole class "Chathistory" and can be used when getting chathistory from server
 *  MessageType = Client_list: Holds an Arraylist containing users online. Used for updating the clients userlist
 *  MessageType = Client_Info, used for sending the username belonging to the client to the server when connecting
 *  MessageType = Exit, used for disconnecting the connection between server and client.
 *  MessageType = VALIDATE. Used in validation process.
 *
 * @author Lukas
 */
public class Message implements Serializable {

    private String time;
    /**
     * Enumerates the types of messages that can be sent.
     */
    public enum MessageType {
        REGULAR_MESSAGE, CHAT_HISTORY, CLIENT_LIST, CLIENT_INFO
    }
    /**
     * Enumerates the possible validation results when validating a user.
     */
    public enum VALIDATE {
        ALLOWED, DENIED, NETWORK_ERROR
    }
    private final MessageType messageType;
    private final ArrayList<User> onlineUsers;
    private final ChatHistory chatHistory;
    private final User sender;
    private final User receiver;
    private final String text;
    private final FileSerialized file;

    /**
     * @return A new instance of the Message.Builder class.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @pre None
     * @param sender, the user serving as sender
     * @param receiver, the user serving as receiver
     * @param file, the picture to be added to the message.
     * @param text, the text to be added to the message.
     * @param messageType, the enum MessageType to identify the message
     * @param onlineUsers, the arraylist containing users online on the server
     * @param chatHistory, the instance of the class Chathistory to be added to the message.
     * @post Constructs a new Message instance with the given parameters. Which some can be null
     *       Creates a new locale, language = Swedish, Country = Sweden
     *       Creates new dateformat = current timeformat in locale (Sweden)
     *       Set the time to current time when the message was created
     */
    public Message(User sender, User receiver, FileSerialized file, String text, MessageType messageType, ArrayList<User> onlineUsers, ChatHistory chatHistory) {
        this.sender = sender;
        this.receiver = receiver;
        this.file = file;
        this.text = text;
        this.messageType = messageType;
        this.onlineUsers = onlineUsers;
        this.chatHistory = chatHistory;

        Locale locale = new Locale("se", "SE");
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT, locale);
        this.time = dateFormat.format(new Date()); // Time format 00:00:00
    }
    /**
     * @pre None
     * @return The time at which the message was sent.
     * @post Time is unchanged
     */
    public String getTime() {
        return time;
    }

    /**
     * @pre None
     * @return The User that sent the message
     * @post sender is unchanged
     */
    public User getSender() {
        return sender;
    }

    /**
     * @pre None
     * @return The User that is the receiver
     * @post receiver is unchanged
     */
    public User getReceiver() {
        return receiver;
    }

    /**
     * @pre None
     * @return The text in the message
     * @post text is unchanged
     */
    public String getText() {
        return text;
    }

    /**
     * @pre None
     * @return The class Fileserialized that is sent in the message.
     * @post file is unchanged
     */
    public FileSerialized getFile() {
        return file;
    }

    /**
     * @pre None
     * @return The messageType of the message
     * @post MessageType is unchanged
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * @pre None
     * @return The ArrayList<User> in the message
     * @post onlineuser is unchanged
     */
    public ArrayList<User> getOnlineUsers() {
        return onlineUsers;
    }

    /**
     * @pre None
     * @return The text of the message converted
     * @post text is unchanged
     */
    public ChatHistory getChatHistory() {
        return chatHistory;
    }

    /**
     * @pre None
     * @return The text of the message converted
     * @post text is unchanged
     */
    @Override
    public String toString() {
        return this.text;
    }

    /**
     * A builder following the builder-pattern for creating Message objects.
     */
    public static class Builder {
        private User sender;
        private User receiver;
        private FileSerialized file;
        private String text;
        private MessageType messageType = MessageType.REGULAR_MESSAGE; // default value
        private ArrayList<User> onlineUsers;
        private ChatHistory chatHistory;

        /**
         * Sets the sender of the message.
         * @pre assumes sender != null
         * @param sender, the User instance serving as sender.
         * @return the builder object containing the given sender param.
         * @post this.sender = sender
         */
        public Builder sender(User sender) {
            this.sender = sender;
            return this;
        }

        /**
         * Sets the receiver of the message.
         * @pre assumes receiver != null
         * @param receiver, the User instance serving as receiver.
         * @return the builder object containing the given receiver param.
         * @post this.receiver = receiver
         */
        public Builder receiver(User receiver) {
            this.receiver = receiver;
            return this;
        }

        /**
         * Sets the file of the message.
         * @pre assumes file != null
         * @param file the instance of FileSerialized to add to the message
         * @return the builder object containing the given file param.
         * @post this.file = file
         */
        public Builder file(FileSerialized file) {
            this.file = file;
            return this;
        }

        /**
         * Sets the text of the message.
         * @pre assumes text != null
         * @param text, the text to add to the message.
         * @return the builder object containing the given file param.
         * @post this.text = text
         */
        public Builder text(String text) {
            this.text = text;
            return this;
        }

        /**
         * Sets the MessageType of the message.
         * @pre assumes MessageType != null
         * @param messageType the enum MessageType to add to the message
         * @return the builder object containing the given MessageType param.
         * @post this.MessageType = MessageType
         */
        public Builder messageType(MessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        /**
         * Sets the ArrayList<User> onlineUsers of the message.
         * @pre assumes onlineUsers != null
         * @param onlineUsers the Arraylist<user> to add to the message.
         * @return the builder object containing the given onlineUsers param.
         * @post this.onlineUsers = onlineUsers
         */
        public Builder onlineUsers(ArrayList<User> onlineUsers) {
            this.onlineUsers = onlineUsers;
            return this;
        }

        /**
         * Sets the class ChatHistory of the message.
         * @pre assumes chatHistory  != null
         * @param chatHistory the instance of chatHistory to add to the message.
         * @return the builder object containing the given chatHistory param.
         * @post this.chatHistory = chatHistory
         */
        public Builder chatHistory(ChatHistory chatHistory) {
            this.chatHistory = chatHistory;
            return this;
        }

        /**
         * @pre None
         * @return a Message class with the given params
         * @post the builder instances is discarded
         */
        public Message build() {
            return new Message(sender, receiver, file, text, messageType, onlineUsers, chatHistory);
        }
    }
}
