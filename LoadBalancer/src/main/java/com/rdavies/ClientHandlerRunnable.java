package com.rdavies;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class ClientHandlerRunnable implements Runnable {
    private final Socket clientSocket;
    private final Server server;
    public static final int CONNECTION_TIMEOUT_MS = 2000;

    public ClientHandlerRunnable(Socket clientSocket, Server server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    public void run() {
        if (server == null) {
            try {
                OutputStream os = clientSocket.getOutputStream();
                os.write("Failed to connect".getBytes());
                os.flush();
                clientSocket.close();
            } catch (IOException ignored) {}
            return;
        }

        // Connect socket to our server
        try (Socket serverSocket = new Socket(server.getHost(), server.getPort())) {
            // Adding timeout so any stuck connections will eventually close
            serverSocket.setSoTimeout(CONNECTION_TIMEOUT_MS);

            System.out.println("Connected to server on port: " + server.getPort());
            // Spin up bidirectional tunnel
            Thread clientToBackend = new Thread(new TunnelRunnable(clientSocket.getInputStream(), serverSocket.getOutputStream()));
            Thread backendToClient = new Thread(new TunnelRunnable(serverSocket.getInputStream(), clientSocket.getOutputStream()));

            clientToBackend.start();
            backendToClient.start();

            // Wait for message to finish
            clientToBackend.join();
            backendToClient.join();

        } catch (IOException | InterruptedException e) {
            // Connection clean up handled in finally block
        } finally { // close client socket
            try {
                clientSocket.close();
            } catch (IOException _) {
            }
        }
    }
}