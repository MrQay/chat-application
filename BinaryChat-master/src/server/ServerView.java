package server;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.*;
import java.util.Objects;

import utils.Observer;

/**
 * Responsible for creating and updating the GUI for the server's uptime.
 *
 * @author Baseer
 */
public class ServerView implements Observer {
    private final ServerModel model;
    private final JLabel uptimeLabel;

    /**
     * @pre assumens model != null
     * @param model, the model to be displayed by the view.
     * @post Creates a JFrame
     *       Creates an Uptimelabel
     *       Runs method update that get the uptime in seconds from model and add it to Uptimelabel
     *       Adds uptimelabel to Jframe.
     */
    public ServerView(ServerModel model) {
        this.model = Objects.requireNonNull(model);

        JFrame frame = new JFrame("Server Uptime");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        uptimeLabel = new JLabel();
        update();
        frame.setLocationRelativeTo(null);
        int yPos = (frame.getHeight() + 80);
        frame.setLocation(frame.getX(), yPos);

        uptimeLabel.setHorizontalAlignment(JLabel.CENTER);
        uptimeLabel.setVerticalAlignment(JLabel.CENTER);
        frame.getContentPane().add(uptimeLabel, BorderLayout.CENTER);
        frame.setSize(300,150);
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);
        frame.pack();

    }

    /**
     * Formats a duration in seconds as a string representation of hours, minutes, and seconds.
     * @pre assumes seconds != null
     * @param seconds, the number of seconds to be formatted.
     * @return a string representation of the duration in the format "H hour(s) M minute(s) S second(s)",
     *         where H is the number of hours, M is the number of minutes, and S is the number of seconds.
     *         The "hour(s)", "minute(s)", and "second(s)" parts are omitted if their values are 0.
     * @post seconds is unchanged
     */
    public static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int remainingSeconds = seconds % 60;

        StringBuilder sb = new StringBuilder();

        if (hours > 0) {
            sb.append(hours).append(" hour");
            if (hours > 1) {
                sb.append("s");
            }
            sb.append(" ");
        }

        if (minutes > 0) {
            sb.append(minutes).append(" minute");
            if (minutes > 1) {
                sb.append("s");
            }
            sb.append(" ");
        }

        if (remainingSeconds > 0 || sb.length() == 0) {
            sb.append(remainingSeconds).append(" second");
            if (remainingSeconds != 1) {
                sb.append("s");
            }
        }

        return sb.toString();
    }

    /**
     * @pre None
     * @post updates the uptimeLabel with the latest info. +1 second
     */
    @Override
    public void update() {
        uptimeLabel.setText(formatTime(model.getUptime()));
    }
}