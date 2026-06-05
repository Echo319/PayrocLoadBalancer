package com.rdavies;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class HealthCheckRunnable implements Runnable {

    private static final int HEALTH_CHECK_INTERVAL_MS = 5000;
    private static final int TIMEOUT_MS = 2000;
    List<Server> servers;

    public HealthCheckRunnable(List<Server> servers){
        this.servers = servers;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(HEALTH_CHECK_INTERVAL_MS);
            } catch (InterruptedException e) {
                break;
            }
            System.out.println("HealthChecker running");
            for (Server server : servers) {
                try (Socket socket = new Socket(server.getHost(), server.getPort())) {
                    // time out
                    socket.setSoTimeout(TIMEOUT_MS);

                    if (!server.isAlive()) {
                        System.out.println("Server " + server.getHost() + ":" + server.getPort() + " recovered! Re-adding to pool.");
                        server.setAlive(true);
                    }
                } catch (IOException e) {
                    if (server.isAlive()) {
                        System.err.println("Server " + server.getHost() + ":" + server.getPort() + " failed check. Removing from pool.");
                        server.setAlive(false);
                    }
                }
            }
        }
    }
}