package com.example.kzero1111;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Hello extends Application {
    private Pane pane;
    private Rectangle player;
    private List<Rectangle> obstacles, bullets;
    private boolean jumping = false;
    private long startTime;
    private AnimationTimer timer;
    private int score = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        pane = new Pane();
        pane.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));

        player = new Rectangle(20, 20, Color.DODGERBLUE);
        player.setLayoutX(100);
        player.setLayoutY(100);

        obstacles = new ArrayList<>();
        bullets = new ArrayList<>();

        Button playButton = createButton("Start Game", (pane.getWidth() /2), (pane.getHeight()  / 2), this::startGame);

        pane.getChildren().addAll(player, playButton);

        Scene scene = new Scene(pane, 1000, 500);
        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));
        scene.setOnKeyReleased(event -> handleKeyRelease(event.getCode()));

        primaryStage.setScene(scene);
        primaryStage.setTitle("Моя игра");
        primaryStage.show();
    }

    private Button createButton(String text, double x, double y, Runnable action) {
        Button button = new Button(text);
        button.setStyle("-fx-font-size: 16;");
        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setOnAction(event -> action.run());
        return button;
    }

    private void startGame() {
        score = 0;
        startTime = System.nanoTime();
        pane.getChildren().clear();
        pane.getChildren().add(player);
        obstacles.clear();
        bullets.clear();
        jumping = false;

        Label timerLabel = createLabel("Time: 0s | Score: 0", 800, 500, 14, Color.BLACK);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                long elapsedTime = (now - startTime) / 1_000_000_000;

                handlePlayerMovement();
                handleBulletMovement();
                spawnObstacle();

                for (Iterator<Rectangle> iterator = bullets.iterator(); iterator.hasNext(); ) {
                    Rectangle bullet = iterator.next();
                    for (Rectangle obstacle : new ArrayList<>(obstacles)) {
                        if (bullet.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                            iterator.remove();
                            obstacles.remove(obstacle);
                            pane.getChildren().removeAll(bullet, obstacle);
                            incrementScore();
                        }
                    }

                    if (bullet.getLayoutX() > pane.getWidth()) {
                        iterator.remove();
                        pane.getChildren().remove(bullet);
                    }
                }

                updateTimerLabel(timerLabel, elapsedTime);
                if (checkCollisions()) {
                    stopGame(timerLabel, elapsedTime);
                    return;
                }
            }
        };
        timer.start();
    }

    private boolean checkCollisions() {
        for (Rectangle obstacle : new ArrayList<>(obstacles)) {
            if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                return true;
            }
        }

        for (Rectangle bullet : new ArrayList<>(bullets)) {
            if (player.getBoundsInParent().intersects(bullet.getBoundsInParent())) {
                return true;
            }
        }

        return false;
    }

    private void handlePlayerMovement() {
        if (player.getLayoutY() < 0) {
            player.setLayoutY(0);
        } else if (player.getLayoutY() > pane.getHeight() - player.getHeight()) {
            player.setLayoutY(pane.getHeight() - player.getHeight());
            jumping = false;
        } else {
            player.setLayoutY(jumping ? player.getLayoutY() - 2 : player.getLayoutY() + 2);
        }
    }

    private void handleBulletMovement() {
        bullets.forEach(bullet -> bullet.setLayoutX(bullet.getLayoutX() + 5));
    }

    private void spawnObstacle() {
        if (Math.random() < 0.007) {
            Rectangle obstacle = new Rectangle(20, 20, Color.RED);
            obstacle.setLayoutX(800);
            obstacle.setLayoutY(player.getLayoutY() + (pane.getHeight() - obstacle.getHeight()) * Math.random());
            obstacles.add(obstacle);
            pane.getChildren().add(obstacle);
        }

        obstacles.forEach(obstacle -> obstacle.setLayoutX(obstacle.getLayoutX() - 2));
    }

    private void handleKeyPress(KeyCode code) {
        if (code == KeyCode.W) {
            jumping = true;
        } else if (code == KeyCode.E) {
            shootBullet();
        }
    }

    private void handleKeyRelease(KeyCode code) {
        if (code == KeyCode.W) {
            jumping = false;
        }
    }

    private void shootBullet() {
        Rectangle bullet = new Rectangle(10, 5, Color.BLUE);
        bullet.setLayoutX(player.getLayoutX() + player.getWidth());
        bullet.setLayoutY(player.getLayoutY() + player.getHeight() / 2);
        bullets.add(bullet);
        pane.getChildren().add(bullet);
    }

    private void stopGame(Label timerLabel, long elapsedTime) {
        timer.stop();

        Label gameOverLabel = createLabel("Game Over\nTime: " + getFormattedTime(elapsedTime) + " | Score: " + score, (pane.getWidth() - 200) / 2, (pane.getHeight() - 60) / 2, 24, Color.RED);
        Button playAgainButton = createButton("Play Again", (pane.getWidth() - 100) / 4, (pane.getHeight() - 30) / 2 + 50, this::startGame);

        pane.getChildren().addAll(gameOverLabel, playAgainButton);
    }

    private Label createLabel(String text, double x, double y, int fontSize, Color color) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: " + fontSize + ";");
        label.setTextFill(color);
        label.setLayoutX(x);
        label.setLayoutY(y);
        return label;
    }

    private void updateTimerLabel(Label timerLabel, long elapsedTime) {
        timerLabel.setText("Time: " + getFormattedTime(elapsedTime) + " | Score: " + score);
    }

    private String getFormattedTime(long seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    private void incrementScore() {

        score++;
    }
}
