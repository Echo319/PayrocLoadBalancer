package com.rdavies;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Server> servers = new ArrayList<>();
        // For testing assume we have 3 servers on localhost
        servers.add(new Server("127.0.0.1", 8081));
        servers.add(new Server("127.0.0.1", 8082));
        servers.add(new Server("127.0.0.1", 8083));

        LoadBalancer balancer = new LoadBalancer(servers);
        balancer.run();

    }

}
