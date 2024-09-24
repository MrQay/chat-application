package client;

import utils.Message;
import utils.User;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Objects;

/**
 * Represents the Login Control of the client application. This class is shown when the client application starts
 * Listens to key events and handles user input related to the login process.
 * It checks via the server if the user is already online, if the server is offline or if the user has entered a valid username.
 * It displays appropriate error messages when needed.
 *
 * @author David
 */
public class LoginControl implements KeyListener {
    private ClientModel clientModel;
    private ClientNetwork clientNetwork;
    private JLabel Username;
    private JTextField textField1;
    private JButton loginButton;
    private JLabel logolabel;
    private JPanel mainpanel;


    /**
     * Constructs a new LoginControl object.
     * @pre clientModel != null, assumes username != null.
     * @param clientModel, the clientModel to be controlled by login
     * @param username, the username to be added if application started from console
     * @post A LoginControl object is created with the specified clientModel and username
     *       A keylistener is added to the textField.
     *       An actionlistner is added to the loginButton
     */
    public LoginControl(ClientModel clientModel, ClientNetwork clientNetwork, String username){
        this.clientModel = Objects.requireNonNull(clientModel);
        this.clientNetwork = Objects.requireNonNull(clientNetwork);
        Objects.requireNonNull(username);
        textField1.addKeyListener(this);
        textField1.setText(username);
        loginButton.addActionListener(x -> {
            loginButtonFunc();
        });
    }
    /**
     * @pre None
     * @return the mainpanel containing the login components, Label, textfield, button.
     * @post mainpanel is unchanged
     */
    public JPanel getPanel(){
        return this.mainpanel;
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * @pre e != null
     * @param e, the event parameter.
     * @post When Enter-key is pressed the login_button_func is called
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {      //Enter = send_message();
            loginButtonFunc();
        }
    }

    /**
     * Validates user input and updates the application status.
     * @pre None
     * @post Calls the method CheckUser in model with entered text from textField
     *       If textfiled = empty , an error message frame is displayed
     *       If server connection couldn't be made an error message frame is displayed
     *       If entered username is allready connected to server an error message frame is displayed
     */
    public void loginButtonFunc(){
        clientNetwork.connectToServer(new User(textField1.getText())); //TODO Lite konstigt att skapa en ny användare här men orkar inte krångla
        clientModel.checkUser(textField1.getText());
        if(Objects.equals(textField1.getText(), "")){
            String errorMessage = "Please type a username";
            JOptionPane.showMessageDialog(null, errorMessage, "No Username", JOptionPane.ERROR_MESSAGE);

        }else{

            if (clientModel.getServerStatus() == Message.VALIDATE.NETWORK_ERROR){
                String errorMessage = "Server Offline";
                JOptionPane.showMessageDialog(null, errorMessage, "Server Offline", JOptionPane.ERROR_MESSAGE);
            }else if(clientModel.getServerStatus() == Message.VALIDATE.DENIED) {
                String errorMessage = "User Already Online: LoginControl";
                JOptionPane.showMessageDialog(null, errorMessage, "User already online:  LoginControl", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }

}




