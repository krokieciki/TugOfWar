package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Protocol extends Thread{
    private PrintWriter printWriter;
    private PushLine pushLine;
    private Server server;
    private String side;
    private boolean finished = false;
    private double timer = 0;


    public static final String leftSideWins = "Finished. Left side wins";
    public static final String rightSideWins = "Finished. Right side wins";

    //konstruktor rozpoczynajacy watek
    public Protocol(Socket clientSocket, Server server, String side) throws IOException{
        this.printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        this.server = server;
        this.side = side;
        printWriter.println(side); //informuje server o druzynie

        pushLine = new PushLine(server, new BufferedReader(new InputStreamReader(clientSocket.getInputStream())), side);
        start();
    }

    @Override
    public void run() {
        while (!finished){
            //jezeli nie bedziemy robili interakcji program wylaczy sie w ciagu 30 sekund
            if (pushLine.isUserFinished() || System.currentTimeMillis() - pushLine.getTimerUserInteraction() >= 100000)
                System.exit(0);
            if (System.currentTimeMillis() - timer > 200) {
                printLinePosition(server.getLinePosition());
                timer = System.currentTimeMillis();
            }
        }
        System.out.println("Endeeee!");
        displayFinish();
    }

    public void printLinePosition(double linePosition){
        if (linePosition > 0 && linePosition < 1)
            printWriter.println(linePosition);
        else if (linePosition < 0)
            printWriter.println(0.0);
        else
            printWriter.println(1.0);
    }
    //funkcja informujaca server o koncu programu oraz clientow ktora strona wygrala
    public void displayFinish(){
        System.out.println("End!");
        if (server.getLinePosition() <= 0) {
            printWriter.println(leftSideWins);
        }
        else
            printWriter.println(rightSideWins);
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
