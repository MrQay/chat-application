package client;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import utils.*;

/**
 * The class provides the user with the ability to send messages and attach files to the message.
 * The ChatControl class has the responsibility of handling user input, adding attachments,
 * and it relies on the ClientModel and User classes to handle message delivery and receiver information, respectively.
 *
 * @author David
 */
public class ChatControl implements KeyListener {

    private final ClientModel clientModel;
    private final ClientNetwork clientNetwork;
    private User receiver;
    private final JTextField textField;
    private File selectedFile = null;
    private static final Font BUTTON_FONT = new Font("Dialog", Font.PLAIN, 15);
    private Color standardColor;
    private JPanel controlPanel;
    private JButton pictureButton;


    /**
     * Constructs a ChatControl object with the specified clientmodel, receiver user, and mainframe.
     *  @pre Model, receiver, and mainframe != null.
     *  @param clientModel, the applications model
     *  @post A ChatControl object is created with the specified model, receiver, and mainframe.
     *         Creates JPanel controlPanel that holds the "Send" and "Add File" buttons, as well as a JTextField.
     *         Adds the controlPanel to the mainframes left side.
     * @throws NullPointerException if model, receiver, or mainframe is null
     */
    public ChatControl(ClientModel clientModel, ClientNetwork clientNetwork) {
        this.clientModel = Objects.requireNonNull(clientModel);
        this.clientNetwork = Objects.requireNonNull(clientNetwork);

        this.receiver = new User(clientModel.getReceiver());

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        //SendButton
        JButton sendButton = new JButton("Send");
        sendButton.setFont(BUTTON_FONT);

        sendButton.addActionListener(x -> {
            sendMessage();
        });

        pictureButton = new JButton("Attach Image");
        standardColor = pictureButton.getBackground();
        pictureButton.setFont(BUTTON_FONT);
        pictureButton.addActionListener(x -> attachFile());
        //Textfield
        textField = new JTextField(30);
        textField.addKeyListener(this);
        //Add to panel
        controlPanel.add(textField, BorderLayout.LINE_START);
        controlPanel.add(sendButton, BorderLayout.LINE_END);
        controlPanel.add(pictureButton, BorderLayout.LINE_END);

    }


    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * Check if the enter-key is pressed.
     * @param e, the keyevent parameter
     * @pre e != null
     * @post send_message is called if e.getKeycode = KeyEvent.VK_ENTER
     *       send_message calls method Createmessage() in model
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {      //Enter = send_message();
            sendMessage();
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {}

    private void sendMessage() {
        pictureButton.setBackground(standardColor);
        FileSerialized file = null;
        String text = "";
        if(selectedFile != null) {
            try {
                file = new FileSerialized(this.selectedFile.getAbsolutePath());
            }catch(IOException e) {
                System.out.println("Failed to serialize file");
            }catch (NullPointerException e) {
                System.out.println("No file at location: " + this.selectedFile.getAbsolutePath());
            }
        }
        if(!textField.getText().isEmpty()) {
            text = textField.getText();
        }
        if( !textField.getText().isEmpty() || selectedFile != null )
            clientNetwork.createMessage(receiver, text, file);

        textField.setText("");
        if(selectedFile != null) {
            selectedFile = null;

        }
    }

    private void attachFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg", "gif"));

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            pictureButton.setBackground(new Color(127, 185, 109));
        }
    }


    /**
     * @pre None
     * @return the panel containing the buttons controlling the chat.
     * @post controlPanel is unchanged.
     */
    public JComponent getPanel(){
        return this.controlPanel;
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
     */
    public void setReceiver(String rec){
        this.receiver = new User(rec);
    }
}
