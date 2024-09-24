package client;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import utils.*;

/**
 * The class is responsible for displaying the current users name, and the numbers of users online on the server.
 *
 * @author Baseer
 */

public class ClientView implements Observer{
    private final ClientModel clientModel;
    private final JLabel infoLabel;
    private final JPanel infoPanel;


    /**
     * @pre model != null, assumes mainframe != null
     * @param clientModel, the model to be displayed by the view.
     * @post Creates a new ClientView with the specified model and mainframe
     *       adds this to the observer set in model
     *       Creates a new infopanel with a JLabel
     *       Calls set_view that adds info from the model to the Label
     *       Sets the Panel to the top in mainframe
     */
    public ClientView(ClientModel clientModel){
        this.clientModel = Objects.requireNonNull(clientModel);
        this.clientModel.addObserver(this);

        infoPanel = new JPanel();

        this.infoLabel = new JLabel();

        setView();

    }

    /**
     * @pre None
     * @post Calls set_view that updates the info on the Label and adds it to the Infopanel
     *       Adds the updated Infopanel to the top in mainframe
     */
    @Override
    public synchronized void update() {
        setView();
    }

    private void setView(){
        this.infoLabel.setText("Client name: " + clientModel.getUser().getName() + ": Users Online: " + clientModel.getUserList().size() );
        this.infoLabel.setFont(new Font("Info", Font.BOLD, 13));
        infoPanel.add(infoLabel);
    }

    /**
     * @pre None
     * @return the panel showing the current clients name and the number of logged in users on the server.
     * @post infoPanel is unchanged.
     */
    public JComponent getPanel(){
        return this.infoPanel;
    }

}
