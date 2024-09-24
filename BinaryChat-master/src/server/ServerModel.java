package server;

import utils.*;
/**
 * Represents the server's model.
 * Responsible for managing the uptime of the server
 * Responsible for notifying its observers of any changes to the uptime.
 *
 * @author David
 */
public class ServerModel implements Observable {
    private final ObservableSupport obs = new ObservableSupport();
    private int uptime;

    /**
     * @pre None
     * @post Initiates int uptime at 0 seconds
     *       Creates a connectionManager
     *       Creates and starts a new thread that runs the connectionManager
     */
    public ServerModel() {
        uptime = 0;

        ConnectionManager connectionManager = new ConnectionManager();
        Thread managerThread = new Thread(connectionManager);
        managerThread.start();
    }
    /**
     * @pre None
     * @return Uptime >= 0
     * @post uptime is unchanged.
     */
    public int getUptime() {
        return uptime;
    }

    /**
     * @pre None
     * @post runs incrementUptime.
     *       Calls update on all observers in obs
     */
    public void updateUptime() {
        uptime++;
        this.obs.update();
    }

    /**
     * @pre obs != null
     * @param obs, the observer to be added
     * @post Adds the given observer obs to the observable set
     * @throws NullPointerException if obs = null
     */
    @Override
    public void addObserver(Observer obs) {
        if (obs == null){
            throw new NullPointerException("Obs cannot be null: Clear");
        }
        this.obs.addObserver(obs);
    }

    /**
     * @pre obs != null
     * @param obs, the observer to be removed.
     * @post removes the obserer obs from the observable set
     * @throws NullPointerException if obs = null
     */
    @Override
    public void removeObserver(Observer obs) {
        if (obs == null){
            throw new NullPointerException("Obs cannot be null: Clear");
        }
        this.obs.removeObserver(obs);
    }
}