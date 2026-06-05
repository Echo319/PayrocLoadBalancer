package com.rdavies;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TunnelRunnable implements Runnable {
    private final InputStream input;
    private final OutputStream output;

    public TunnelRunnable(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    public void run() {
        byte[] buffer = new byte[4096];
        int bytesRead;
        try {
            // Continuously stream raw network bytes back and forth
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                output.flush();
            }
        } catch (IOException e) {
            // Connection reset, broken pipe, or closed socket - clean exit
        }
    }
}
