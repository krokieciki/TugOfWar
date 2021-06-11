package main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Application {
    private ProgressBar progressBar;
    private static String[] argss;
    private String side;
    private boolean stop = false;
    private Label gameOverLabel;

    public static void main(String[] args){
        argss = args;
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        if (argss.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = argss[0];
        int portNumber = Integer.parseInt(argss[1]);

        VBox vBox = new VBox();
        Scene scene = new Scene(vBox, 500, 420);
        Label label = new Label();
        label.setFont(new Font("MV BOLI", 20));

        Label label2 = new Label();
        label2.setText("Tug Of War Game");
        label2.setFont(new Font("MV BOLI", 30));


        progressBar = new ProgressBar();
        progressBar.setPrefSize(300, 30);
        progressBar.setProgress(0.5);
        //progressBar.setForeground(Color.green);
        Button button = new Button("Pull!");
        button.setPrefSize(80, 20);

        vBox.setStyle("-fx-background-color: #9966ff;");
        vBox.getChildren().add(label2);
        vBox.getChildren().add(progressBar);
        vBox.getChildren().add(button);
        vBox.getChildren().add(label);

        vBox.setSpacing(30);
        vBox.setAlignment(Pos.CENTER);

        gameOverLabel = new Label();
        gameOverLabel.setFont(new Font("Arial", 24));
        gameOverLabel.setVisible(false);

        vBox.getChildren().add(gameOverLabel);
        final String left = "Left";
        final String right = "Right";

        try{
            Socket tugOfWarSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(tugOfWarSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(tugOfWarSocket.getInputStream()));

            //pobiera w ktorej gracz jest druzynie i informuje go o tym
            side = in.readLine();
            if (side.equals(left)) {
                label.setText("<---- TEAM LEFT");
            }
            else {
                label.setText("TEAM RIGHT ---->");
            }

            button.setOnAction(actionEvent -> out.println("Pull"));
            //Animation Timer wspiera animacje dla javafx, dzieki niemu widzimy jak pasek sie przesuwa w progrs barze
            AnimationTimer animationTimer = new AnimationTimer() {
                @Override
                public void handle(long l){
                    try {
                        if (!stop){
                            String message = in.readLine();
                            //progress bar ustawiamy na srodek
                            double progress = 0.5;
                            try {
                                progress = Double.parseDouble(message);
                            } catch (NumberFormatException e) {
                                System.out.println(message);
                                boolean win;
                                if(message.equals(Protocol.leftSideWins)){
                                    win = side.equals(left);
                                }else {
                                    win = side.equals(right);
                                }
                                String result = win ? "Congratulation you won :)!" : "You lost :(!";
                                gameOverLabel.setText(result);
                                gameOverLabel.setVisible(true);
                                stop = true;
                            }
                            if (!stop && progress != progressBar.getProgress()) {
                                progressBar.setProgress(progress);
                            }
                        }
                    }catch (IOException e){
                        System.out.println("Problem z komunikacją pomiędzy aplikacją i serwerem!");
                        System.exit(0);
                    }
                }
            };
            animationTimer.start();
            stage.setTitle("TugOfWar");
            stage.setScene(scene);
            stage.show();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
