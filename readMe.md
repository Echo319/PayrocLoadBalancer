# LoadBalancer

## Technical specification: 

It’s 1999. You, a software engineer working at a rapidly growing scale-up.
The company has outgrown its start-up era, single server setup. Things are starting to fail
rapidly. You are tasked with designing and building a software-based load balancer to allow
multiple machines to handle the load.

Your task is to implement a basic, software-based load-balancer, operating at layer 4. It must
have the following capabilities:

- It can accept traffic from many clients
- It can balance traffic across multiple backend services
- It can remove a service from operation if it goes offline
- You may add other requirements as necessary / you feel appropriate.
- You may choose any language you are comfortable with. 
- You should not use any cloud-services in the completion of this exercise.

## Install

To build

    mvn clean install

To Run

    java -jar \LoadBalancer\Target\LoadBalancer-1.0-SNAPSHOT.jar

To test

In 3 terminals 

    java -jar \LoadBalancer\Target\TestServer-1.0-SNAPSHOT.jar 8081
    java -jar \LoadBalancer\Target\TestServer-1.0-SNAPSHOT.jar 8082
    java -jar \LoadBalancer\Target\TestServer-1.0-SNAPSHOT.jar 8083

In as many terminals as you like 


    java -jar \LoadBalancer\Target\TestClient-1.0-SNAPSHOT.jar X Y

Where X is number of message and Y is the time between messages in ms. 

X Defaults to 5 

Y Defaults to 200


To test more servers you will need to edit LoadBalancer/src/main/java/com/rdavies/Main.java and add the servers you like.
### Notes 

The code is pretty simple we open a port then spin up a thread to deal with the connection.

So on wake up open a socket and wait for message. Once accepted spin up a worker 

- Choose a server to pass the message to
- Create socket get input and output streams for the client and our chosen server
- Wait for the input and output streams to complete 
- Close the socket

Implement a timeout or number of attempts to ensure threads die.

As this is a basic load-balancer using RoundRobin to go through our collection of servers is enough.

We also need a health check running that can set a service to offline making it unable to be selected. I think a seperate thread that will, on an interval, check the livelyness of each server is correct

Replace system out with an actually logger?  


### Testing notes

Im not sure unit testing here makes sense as there is no outputs only if the threads complete.  

So lets go with some integration tests and try and push it.

Small test client that sends X number of "ping" to our Loadbalancer we can do a scenario where this is in the thousands 

Test server that responds pong to the Loadbalancer 

Add in an id to represent which message the client send and a server id to show which server sent it back

We spin up our 3 servers and a client or two and send as many messages as we can.