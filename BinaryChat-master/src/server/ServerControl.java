package server;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents a controller for a server, which is responsible for
 * starting and stopping the server, and updating its uptime.
 *
 * @author Lukas
 */

public class ServerControl implements Runnable {
    private final Timer timer;
    private final ServerModel model;
    public boolean running;

    /**
     * @pre  model != null
     * @param model, the model to be controlled.
     * @post Creates a ServerControl object with given model
     *       Creates a new Timer
     *       sets running = false
     */
    public ServerControl(ServerModel model) {
        this.timer = new Timer();
        this.model = Objects.requireNonNull(model);
        running = false;
    }

    /**
     * @pre None
     * @post Sets running = true
     *       Creates a new thread for this class instance and starts it
     */
    public void start() {
        running = true;
        new Thread(this).start();
    }

    /**
     * Called when the new thread starts running,
     * @pre None
     * @post schedules a new TimerTask, delay 1 sec, period 1 sec. That run updateUptime in model.
     */
    @Override
    public void run() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.updateUptime();
            }
        }, 1000, 1000);
    }

}