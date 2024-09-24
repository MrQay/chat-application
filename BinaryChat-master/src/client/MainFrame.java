package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Objects;
import utils.*;


/**
 *  The class is responsible for creating and managing the graphical user interface (GUI) of the client application.
 *  It provides methods to set up different components of the GUI, such as the left and right panels, and to add,
 *  remove or clear different panels. The class also implements the Observer and WindowListener interfaces,
 *  which enable it to receive updates from the ClientModel and to listen for window events.
 *
 * @author David
 */

public class MainFrame implements Observer {
    private final JFrame window;
    private final ClientModel clientModel;
    private final ClientNetwork clientNetwork;
    private final JPanel leftPanel;
    private final JPanel rightPanel;
    private final JPanel top_panel;

    private ChatControl chatControl;
    private ChatView chatView;
    private ClientView clientview;
    private ClientControl clientControl;
    private boolean loginComplete = false;
    private static final Color Background = new Color(236, 236, 236);

    /**
     * @pre model != null, assumes username != null
     * @param clientModel, the model to be passed on to the views and controllers.
     * @param username, the username to be set incase of starting application from console.
     * @post Creates the mainframe with the specified model and username
     *      Creates a new Jframe,
     *      Creates a rightpanel, leftpanel and top_panel
     *      Creates an instance of LoginControl and adds it to the leftpanel
     */
    public MainFrame(ClientModel clientModel, ClientNetwork clientNetwork, String username){

        this.clientModel = Objects.requireNonNull(clientModel);
        this.clientNetwork = Objects.requireNonNull(clientNetwork);

        this.window = new JFrame("Binary Chat");
        this.window.setPreferredSize(new Dimension(280, 500));
        this.window.setLayout(new BorderLayout());
        this.window.setResizable(false);


        chatControl = new ChatControl(this.clientModel, this.clientNetwork);
        chatView = new ChatView(this.clientModel);

        this.rightPanel = new JPanel(new BorderLayout());
        this.rightPanel.setSize(new Dimension(560,500));

        this.top_panel = new JPanel();

        //leftpanel + loginpanel
        this.leftPanel = new JPanel(new BorderLayout());
        LoginControl loginControl = new LoginControl(this.clientModel, this.clientNetwork, username);
        this.leftPanel.add(loginControl.getPanel(),BorderLayout.CENTER);

        this.window.add(leftPanel,BorderLayout.CENTER);

        this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.window.setLocationByPlatform(true);
        this.window.setVisible(true);
        this.window.pack();

        if(!username.equals(""))
            loginControl.loginButtonFunc();
    }

    /**
     * @pre Ensures that the method sets up the view with the latest information from the model when the user logs in.
     * @post       the showclient runs only once, after
     *             the loginComplete flag is set to true
     *             Creates a chatwindow if a receiver is chosen
     *             the window is packed
     */
    @Override
    public synchronized void update() {
        SwingUtilities.invokeLater(new Runnable() {   //Använder vi för att köra följande operationer på EventDisplay Thread senare.
                                                    // Det gör vi för att förhindrta att användargränssnittet låser sig,
                                                    // vilket det kan göra då vi kör många updates i uppstart av en klient
            public void run() {
                if (!loginComplete && clientModel.loggedIn()) {
                    loginComplete = true;
                    window.getContentPane().removeAll();
                    leftPanel.removeAll();
                    showClient();
                } else if (clientModel.loggedIn()) {
                    leftPanel.removeAll();
                    setChat();
                }

                if (clientControl != null) {
                    leftPanel.add(clientControl.getPanel(), BorderLayout.CENTER);
                    leftPanel.revalidate();
                    leftPanel.repaint();
                }

                window.pack();
            }
        });
    }

    private void showClient(){
        System.out.println("Showclient runned");
        clientControl = new ClientControl(this.clientModel);
        clientview = new ClientView(this.clientModel);

        this.window.setPreferredSize(new Dimension(840, 500));

        this.window.add(rightPanel,BorderLayout.CENTER);
        this.rightPanel.setSize(new Dimension(560,500));
        this.rightPanel.setBackground(Background);

        this.window.add(leftPanel,BorderLayout.WEST);
        this.rightPanel.add(chatView.getPanel(), BorderLayout.PAGE_START);
        this.rightPanel.add(chatControl.getPanel(), BorderLayout.CENTER);

        this.window.setVisible(true);

        this.top_panel.add(clientview.getPanel());
        this.window.add(top_panel,BorderLayout.PAGE_START);
        this.clientModel.addObserver(clientControl);
        this.leftPanel.add(clientControl.getPanel(),BorderLayout.CENTER);
        this.clientModel.addObserver(chatView);


        this.leftPanel.setPreferredSize(new Dimension(280, 500));

        this.window.invalidate();
        this.window.repaint();
        this.window.revalidate();


    }
    private void setChat() {

    if (clientModel.getReceiver().equals("")) {
            rightPanel.setVisible(false);
        }else {
            rightPanel.setVisible(true);
            if (!chatControl.getUser().equals(clientModel.getReceiver()) && !chatView.getUser().equals(clientModel.getReceiver())) {
                rightPanel.setVisible(true);
                chatControl.setReceiver(clientModel.getReceiver());
                chatView.setReceiver(clientModel.getReceiver());
            }
        }
    }

}


