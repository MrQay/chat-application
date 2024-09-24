package server;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

/**
 * Creates a queue for incoming socket connections, listening on a specified port, and provides a method to
 * retrieve the next available socket connection from the queue. Implements the AutoCloseable interface,
 * allowing it to be used in a try-with-resources statement to ensure proper closing of the underlying ServerSocket
 * and all the sockets in the queue.
 *
 * @author Lukas
 */

public class SocketQueue implements AutoCloseable{
    private final ServerSocket serverSocket;
    private final BlockingQueue<Socket> socketQueue;

    /**
     * @pre assumes port != null
     * @param port, the port number to be opened in the socket.
     * @post Creates new serverSocket with given port
     *       Creates new LinkedBlockingQueue
     *       Creates and start new thread accepting incoming connections to the port.
     *       Adds the incoming connections to the queue
     * @throws IOException if a socket cannot be opened.
     */
    public SocketQueue(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        socketQueue = new LinkedBlockingQueue<>();

        new Thread(() -> {
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    socketQueue.put(socket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * @pre None
     * @return the head-socket of the queue
     * @post removes the socket returned from the queue
     * @throws InterruptedException if the thread is interrupted
     */
    public Socket getNextSocket() throws InterruptedException {
        return socketQueue.take();
    }

    /**
     * Closes the ServerSocket and all the Socket objects that are currently in the queue.
     * @pre None
     * @post Tries to close the serversocket
     *       Tries to close all sockets in socketQueue
     * @throws IOException if socket fails to close
     */
    @Override
    public void close() throws IOException {
        serverSocket.close();
        for (Socket socket : socketQueue) {
            socket.close();
        }
    }
}