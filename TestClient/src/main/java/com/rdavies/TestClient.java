package com.rdavies;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class TestClient {
    private static final String LB_HOST = "127.0.0.1";
    private static final int LB_PORT = 8080;
    private static int NUM_REQUESTS = 5;
    private static int SLEEP_TIME = 200;

    public static void main(String[] args) {

        if (args[0] != null) {
            NUM_REQUESTS = Integer.parseInt(args[0]);
        } else {
            System.out.println("Default to 5 messages");
        }

        if(args[1] != null) {
            SLEEP_TIME = Integer.parseInt(args[1]);
            System.out.println("Sleep time between messages set to " + SLEEP_TIME + "ms");
        } else
        {
            System.out.println("Sleep time between messages defaulted to 200ms");
        }


        System.out.println("Starting load balancer test with " + NUM_REQUESTS + " messages...");

        for (int i = 1; i <= NUM_REQUESTS; i++) {
            final int requestId = i;
            new Thread(() -> {
                try (Socket socket = new Socket(LB_HOST, LB_PORT)) {
                    OutputStream out = socket.getOutputStream();
                    String request = "Ping number " + requestId + "\n";
                    out.write(request.getBytes());
                    out.flush();

                    // Read the response routed back from the load balancer
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String response = in.readLine();
                    System.out.println(response);

                } catch (Exception e) {
                    System.err.println("Request #" + requestId + " failed: " + e.getMessage());
                }
            }).start();

            // Sleep for testing
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ignored) {
            }
        }
    }
}
