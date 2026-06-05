package com.rdavies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {

    public static void main(String[] args) throws RuntimeException {
        if (args.length < 1) {
            System.out.println("Add a port number");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started and listening on port " + port);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    // Read whatever the client sent
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String line = in.readLine();
                    System.out.println(line);

                    // Respond back with a distinct signature identifying this specific backend instance
                    OutputStream out = clientSocket.getOutputStream();
                    String response = "pong from port " + port + "\n";

                    out.write(response.getBytes());
                    out.flush();
                } catch (IOException e) {
                    System.err.println("Error handling request on port " + port + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + port + ": " + e.getMessage());
        }
    }
}