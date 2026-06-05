package com.rdavies;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class LoadBalancer {

    private static final int BALANCER_PORT = 8080;
    private static List<Server> servers;
    public static int currentIndex = 0;

    public LoadBalancer(List<Server> servers) {
        LoadBalancer.servers = servers;
    }

    public void run() {

        System.out.println("Starting Load Balancer on port " + BALANCER_PORT + "...");

        // Spin up health check thread
        Thread healthChecker = new Thread(new HealthCheckRunnable(servers));
        healthChecker.setDaemon(true);
        healthChecker.start();

        // open socket
        try (ServerSocket serverSocket = new ServerSocket(BALANCER_PORT)) {
            System.out.println("Accepting client traffic on port: " + BALANCER_PORT);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    // Select server
                    Server server = getNextBackend();

                    // Spin up worker thread
                    Thread clientHandler = new Thread(new ClientHandlerRunnable(clientSocket, server));
                    clientHandler.setDaemon(true);
                    clientHandler.start();

                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to open " + BALANCER_PORT + ": " + e.getMessage());
        }
    }

    public static synchronized Server getNextBackend() {
        int poolSize = servers.size();
        for (int i = 0; i < poolSize; i++) {
            Server server = servers.get(currentIndex);
            currentIndex = (currentIndex + 1) % poolSize;

            if (server.isAlive()) {
                return server;
            }
        }
        return null;
    }
}