import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ChatClient {
    public static void main(String[] argv) {
        Mediator mediator = new Mediator();

        Subscriber server;
        Subscriber messageHandler;
        Subscriber messageSwarm;
        Subscriber frontendHandler;

        // server address selector
        ServerAddrForm saf = new ServerAddrForm();
        saf.setVisible(true);
        while (!saf.okPressed()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                // pass
                return;
            }
        }

        try {
            server = new ServerConnector(mediator, saf.getAddr());
        } catch (IOException e) {
            System.out.println("Cannot connect to server");
            return;
        }
        try {
            messageHandler = new MessageHandler(mediator);
        } catch (IOException e) {
            System.out.println("Cannot start MessageHandler");
            return;
        }
        try {
            messageSwarm = new MessageSwarm(mediator);
        } catch (IOException e) {
            System.out.println("Cannot start MessageSwarm");
            return;
        }
        try {
            frontendHandler = new FrontendHandler(mediator);
        } catch (Exception e) {
            System.out.println("Cannot start GUI");
            return;
        }

        mediator.add(server);
        mediator.add(messageHandler);
        mediator.add(messageSwarm);
        mediator.add(frontendHandler);

        server.start();
        messageHandler.start();
        messageSwarm.start();
        frontendHandler.start();

        try {
            server.join();
            System.out.println("SERVER_CONNECTOR SHUTDOWN OK");
            messageHandler.join();
            System.out.println("MESSAGE_HANDLER SHUTDOWN OK");
            frontendHandler.join();
            System.out.println("FRONTEND HANDLER SHUTDOWN OK");
            // not easy to shutdown this
            // messageSwarm.join();
        } catch (InterruptedException e) {
            // pass
        }

        System.out.println("ALL PROCESS SHUTDOWN");
        System.out.println("NO ERROR DETECTED");
    }
}
