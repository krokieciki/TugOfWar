package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ConnectException;

public class PushLine extends Thread {
    private final Server server;
    private final BufferedReader bufferedReader;
    private boolean finish = false;
    private final String side;
    private double timerUserInteraction;
    private boolean userFinished = false;

    //konstruktor w ktorym uruchamiamy watek
    public PushLine(Server server, BufferedReader bufferedReader, String side) {
        this.server = server;
        this.bufferedReader = bufferedReader;
        this.side = side;
        timerUserInteraction = System.currentTimeMillis();
        start();
    }

    //funkcja run nadaje wartosci przyciskowi pull dla lewej i prawej strony, decyduje kiedy koniec jest
    public void run() {
        String input;
        while (!finish) {
                try {
                    input = bufferedReader.readLine();
                    if (input.equals("Pull") && !server.isFinished()) {
                        timerUserInteraction = System.currentTimeMillis();
                        if (side.equals("Left"))
                            server.pullLine(-0.1);
                        else
                            server.pullLine(0.1);
                        if (server.getLinePosition() <= 0 || server.getLinePosition() >= 1)
                            server.setFinished(true);
                    }
                } catch (ConnectException e) {
                    userFinished = true;

                    return;
                } catch (IOException e){

                }
        }
    }

    public double getTimerUserInteraction() {
        return timerUserInteraction;
    }

    public boolean isUserFinished() {
        return userFinished;
    }
}
