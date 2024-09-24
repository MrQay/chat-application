package client;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import utils.*;

/**
 * The ChatView class is responsible for displaying the chat history of a particular user.
 * Uses the Observer pattern to receive updates from the ClientModel when there are changes to the chat history.
 * Implements the getHistory method to fetch the chat history of the user and displays it on the chat history panel.
 *
 * @author Natan
 */
public class ChatView implements Observer {
    private final ClientModel model;

    private final JTextPane chatHistoryArea;
    private JScrollPane chatHistoryScrollPane;
    private User receiver;

    /**
     * Creates a new ChatView instance with the given model, receiver and mainframe.
     * @pre receiver, mainframe && model != null
     * @param model, the applications model.
     * @post A ChatView object is created with the specified model, receiver, and mainframe.
     *       A JTextPane chatHistoryArea is created
     *       A JScrollpane chathistoryScrollpane is created that holds ChatHistoryArea
     *       The method getHistory is called which fills the Textpane with messages, if any.
     *       The chathistoryScrollpane is added to the mainframes right side.
     * @throws NullPointerException if model, receiver, or mainframe is null
     */
    public ChatView(ClientModel model) {
        this.model = Objects.requireNonNull(model);
        this.receiver = new User(model.getReceiver());
        //ChathistPanel
        chatHistoryArea = new JTextPane();
        chatHistoryArea.setEditable(false);

        chatHistoryArea.setContentType("text/html");

        chatHistoryScrollPane = new JScrollPane(chatHistoryArea);
        chatHistoryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        chatHistoryScrollPane.setPreferredSize(new Dimension(560, 380));
        getHistory();
    }

    /**
     * Updates the chat history displayed in the chat view.
     * @pre none
     * @post The getHistory method is called, which fills/updates the chatHistoryArea Textpane with messages if any.
     */
    @Override
    public synchronized void update() {
        getHistory();
    }
    private void getHistory() {
        try {
            chatHistoryArea.setText("");

            ArrayList<Message> chatHistory = model.getHistory(receiver);

            StyledDocument doc = chatHistoryArea.getStyledDocument();

            if(chatHistory.size() > 0) {
                for (Message msg : chatHistory) {
                    SimpleAttributeSet attributes = new SimpleAttributeSet();
                    if (msg.getSender().getName().equals(model.getUser().getName())) {
                        StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_RIGHT);
                    } else {
                        StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_LEFT);

                    }
                    int offset = doc.getLength();
                    doc.insertString(offset, msg.getText() + "\n", messageTextAttributes());
                    doc.setParagraphAttributes(offset, msg.getText().length() + 1, attributes, false);
                    doc.insertString(doc.getLength(), msg.getTime() + " " + msg.getSender() + ": " + "\n", infoTextAttributes());
                    doc.setParagraphAttributes(offset, msg.getTime().length() + msg.getSender().getName().length(), attributes, false);

                    if (msg.getFile() != null) {

                        ImageIcon imageIcon = new ImageIcon(msg.getFile().getData());

                        if (imageIcon.getIconWidth() > 150 || imageIcon.getIconHeight() > 150) {
                            imageIcon.setImage(imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_REPLICATE));  //Scala bilden till
                        }

                        chatHistoryArea.setCaretPosition(chatHistoryArea.getDocument().getLength());
                        doc.insertString(doc.getLength(), "               " + "\n", infoTextAttributes());
                        doc.insertString(doc.getLength(), " ", infoTextAttributes());
                        chatHistoryArea.insertIcon(imageIcon);
                        doc.insertString(doc.getLength(), "\n", infoTextAttributes());
                    }
                }
                chatHistoryArea.setCaretPosition(chatHistoryArea.getDocument().getLength());
                chatHistoryArea.setStyledDocument(doc);
            }

        } catch (NullPointerException e) {
            System.out.println(e.toString());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private static SimpleAttributeSet infoTextAttributes() {
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontSize(attrs, 6);
        return attrs;
    }

    private static SimpleAttributeSet messageTextAttributes() {
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontSize(attrs, 14);
        StyleConstants.setBold(attrs, true);
        return attrs;
    }

    /**
     * @pre None
     * @return the chatHistory scrollpanel containing the chat History between current client user and receiver
     * @post chatHistoryScrollPane is unchanged.
     */
    public JComponent getPanel(){
        return this.chatHistoryScrollPane;
    }

    /**
     * @pre None
     * @return the username of the user currently being the receiver in the chat
     * @post the User receiver is unchanged.
     */
    public String getUser(){
        return this.receiver.getName();
    }

    /**
     * @pre assumumes rec != null
     * @param rec, the new username to be set to the new User receiver.
     * @post Creates a new user from the rec param and sets it to this.receiver
     *       Runs the getHistory() method, to get and set the history between the new receiver and the current client user.
     */
    public void setReceiver(String rec){
        this.receiver = new User(rec);
        getHistory();
    }
}


