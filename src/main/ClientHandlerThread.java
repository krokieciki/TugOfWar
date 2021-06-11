package main;

import java.io.IOException;
import java.util.Random;

public class ClientHandlerThread extends Thread {
    private final Server server;
    private double timer = 0;
    static boolean isGoingToShutdown = false;

    public ClientHandlerThread(Server server) {
        this.server = server;
        start();
    }

    //wyjatek dodaje gracza i wybiera strone klientowi
    public void run() {
        Random random = new Random();
        String side = random.nextBoolean() ? "Right" : "Left";

        while (true) {
            server.addNewPlayer(side);
            side = side.equals("Left") ? "Right" : "Left";

        }
    }
}