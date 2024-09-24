package client;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Objects;
import javax.swing.*;
import utils.*;

/**
 * The class is responsible for managing the user interface and interactions
 * between the client model and the chat application.
 *
 * @author Ebrahim
 */
public class ClientControl implements Observer {
    private final ClientModel clientModel;
    private final JPanel onlinePanel;
    private ButtonGroup onlineGroup;
    private JScrollPane scrollPane;
    private ArrayList<User> clientList;
    private static final Color BUTTON_COLOR_UNMARKED = new Color(236, 236, 236);
    private static final Color BUTTON_COLOR_MARKED = new Color(126, 126, 126, 255);

    private JButton markedButton;

    /**
     * Constructor: creates a control panel with buttons for starting new chats for each user online.
     * @pre Mainframe && model != null
     * @param clientModel, the model to be controlled
     * @post a new ClientControl object is created with the given model and mainframe
     *       A Jpanel Onlinepanel is created.
     *       The method set_scrollpane is created which add the Onlinepanel to Scrollpane
     *       A ButtonGroup is created.
     *       The ArrayList<User> clientList is updated with the current users online via the getUserList method.
     *       The method set_user_control_buttons is called which adds buttons to the OnlinePanel
     */
    public ClientControl(ClientModel clientModel) {
        this.clientModel = Objects.requireNonNull(clientModel);

        //OnlinePanel + Onlinegroup
        onlinePanel = new JPanel();              //Panel för att lägga till en knapp för alla som är online.
        onlinePanel.setPreferredSize(new Dimension(260,500));
        onlinePanel.setDoubleBuffered(true);
        onlinePanel.setBackground(new Color(255,200,87));


        setScrollPane(onlinePanel);

        onlineGroup = new ButtonGroup();//Grupp för alla de knapparna

        clientList = this.clientModel.getUserList();
        setUserControlButtons(clientList);
    }

    private void setUserControlButtons(ArrayList<User> clientUsers) {

        this.clientList = clientUsers;

        onlineGroup = new ButtonGroup();
        onlinePanel.removeAll();

        for (User user : clientUsers) {                //Skapar en knapp för alla användare som är online
            if (!Objects.equals(user.getName(), clientModel.getUser().getName())) {

                JButton button = new JButton(user.getName());       //Texten på knappen är densamma som användarens Username
                button.setPreferredSize(new Dimension(200, 45));
                button.setBackground(BUTTON_COLOR_UNMARKED);
                button.setFocusPainted(false);

                onlineGroup.add(button);

                button.addActionListener(x -> {

                    if (markedButton != null) {
                        markedButton.setBackground(BUTTON_COLOR_UNMARKED); // unmark previously marked button
                    }

                    button.setBackground(BUTTON_COLOR_MARKED);
                    markedButton = button;
                    Enumeration<AbstractButton> buttons = onlineGroup.getElements();          //Övriga knappar blir vita
                    while (buttons.hasMoreElements()) {
                        AbstractButton otherButton = buttons.nextElement();
                        if (otherButton != button) {
                            otherButton.setBackground(BUTTON_COLOR_UNMARKED);
                        }
                    }

                    if(!clientModel.getReceiver().equals(button.getText()) && button.isDisplayable()){
                        clientModel.setReceiver(button.getText());
                        System.out.println("New reeiver set in ClientControl");
                    }

                });
                if (markedButton != null && markedButton.getText().equals(button.getText())) {
                    markedButton.setBackground(BUTTON_COLOR_MARKED);
                    onlinePanel.add(markedButton);
                } else {
                    onlinePanel.add(button);

                }
            }
        }
        onlinePanel.setPreferredSize(new Dimension(260,50*onlineGroup.getButtonCount()));
        controlChatViewRemoval();
    }
    private void setScrollPane(JPanel panel){
        scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().addMouseWheelListener(e -> {
            JViewport viewport = (JViewport) e.getSource();
            int unitsToScroll = e.getUnitsToScroll();
            Point p = viewport.getViewPosition();

            if (unitsToScroll < 0 && p.y == 0) {
                return;
            }
            p.translate(0, unitsToScroll * 12);
            viewport.setViewPosition(p);
        });
    }

    private void setControlPanel(ArrayList<User> temp){

        this.scrollPane.removeAll();
        setUserControlButtons(temp);
        setScrollPane(onlinePanel);

    }
    private void controlChatViewRemoval(){
        if (markedButton != null) {
            String markedUserName = markedButton.getText();
            boolean isMarkedUserOnline = false;
            for (User user : this.clientList) {
                if (user.getName().equals(markedUserName)) {
                    isMarkedUserOnline = true;
                    break;
                }
            }
            if (!isMarkedUserOnline) {
                markedButton = null;
                clientModel.setReceiver("");

            }
        }
    }
    /**
     * @pre None
     * @post The method set_controlpanel is called which runs the metod set_user_control_buttons which adds buttons to the OnlinePanel.
     *       Clearpanel is called in mainframe which clears the leftpanel
     *       The updated Scrollpane and Onlinepanel is added to the mainframes left side.
     */
    @Override
    public synchronized void update() {
        setControlPanel(clientModel.getUserList());
    }

    /**
     * @pre None
     * @return the panel containing the buttons for every user online on the server.
     * @post scrollPanel is unchanged.
     */
    public JComponent getPanel(){
        return this.scrollPane;
    }

}





