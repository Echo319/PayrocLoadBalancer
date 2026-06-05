package com.rdavies;

public class Server {

    private final String host;
    private final int port;
    private boolean alive;

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
        this.alive = true; // Assume up initially
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public synchronized boolean isAlive() {
        return alive;
    }

    public synchronized void setAlive(boolean alive) {
        this.alive = alive;
    }
}