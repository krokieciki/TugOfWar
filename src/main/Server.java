package main;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private double linePosition;
    private Protocol[] protocols;
    private ServerSocket serverSocket;
    private int howManyPlayersPlaying;
    private int maxPlayers;
    private boolean finished;

    public static void main(String[] args) throws IOException {

        int portNumber = Integer.parseInt(args[0]);
        int maxPlayers = 2;

        Server server = new Server();
        server.protocols = new Protocol[maxPlayers];
        server.linePosition = 0.5d;
        server.howManyPlayersPlaying = 0;
        server.maxPlayers = maxPlayers;

        try{
            server.serverSocket = new ServerSocket(portNumber);
            ClientHandlerThread clientHandlerThread = new ClientHandlerThread(server);
            while (true) {
               try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                if (server.finished) {
                    for (int i = 0; i < server.howManyPlayersPlaying; i++)
                        server.protocols[i].setFinished(true);
                    break;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //funkcja przekazujaca informacje o stanie liny serverowi
    public synchronized void pullLine(double value){
        linePosition += value;
    }

    //funkcja akceptujaca graczy do servera
    public void addNewPlayer(String side){
        if (getHowManyPlayersPlaying() <= getMaxPlayers()){
            try {
                protocols[howManyPlayersPlaying] = new Protocol(getServerSocket().accept(), this, side);
                howManyPlayersPlaying++;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Za duzo graczy!");
            System.exit(0);
        }
    }

    public double getLinePosition() {
        return linePosition;
    }

    public int getHowManyPlayersPlaying() {
        return howManyPlayersPlaying;
    }

    public synchronized void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public boolean isFinished() {
        return finished;
    }
}
