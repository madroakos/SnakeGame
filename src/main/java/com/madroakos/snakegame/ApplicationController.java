package com.madroakos.snakegame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Random;

public class ApplicationController {
    @FXML
    private GridPane gameBoard;
    @FXML
    private Button startButton;
    @FXML
    private Label pointLabel;
    private final Deque<Position> snakePosition = new ArrayDeque<>();
    private final Position fruitPosition = new Position(14, 4);
    private double cellSize;
    private static KeyCode lastKeyDown = KeyCode.RIGHT;
    private static KeyCode lastRegisteredKey = KeyCode.RIGHT;
    private final Random random = new Random();
    private boolean haveFailed = false;
    private int point;

    public ApplicationController() {
        fillStartingPosition();
    }

    @FXML
    private void onStartButtonClick() {
        cellSize = gameBoard.getWidth() / gameBoard.getColumnCount();
        startButton.setVisible(false);
        paintStartingPositions();
        startGame();
    }

    private void startGame() {
        point = 0;
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(0.3), event -> {
                    if (!haveFailed) {
                        if (!snakePosition.isEmpty()) {
                            Position temp = null;
                            switch (lastKeyDown) {
                                case RIGHT:
                                    temp = new Position(snakePosition.peekLast().getX() + 1, Objects.requireNonNull(snakePosition.peekLast()).getY());
                                    lastRegisteredKey = KeyCode.RIGHT;
                                    break;
                                case LEFT:
                                    temp = new Position(snakePosition.peekLast().getX() - 1, Objects.requireNonNull(snakePosition.peekLast()).getY());
                                    lastRegisteredKey = KeyCode.LEFT;
                                    break;
                                case UP:
                                    temp = new Position(snakePosition.peekLast().getX(), Objects.requireNonNull(snakePosition.peekLast()).getY() - 1);
                                    lastRegisteredKey = KeyCode.UP;
                                    break;
                                case DOWN:
                                    temp = new Position(snakePosition.peekLast().getX(), Objects.requireNonNull(snakePosition.peekLast()).getY() + 1);
                                    lastRegisteredKey = KeyCode.DOWN;
                                    break;
                                default:
                                    break;
                            }
                            if (temp != null && inBoundary(temp.getX(), temp.getY())) {
                                for (Position p :snakePosition) {
                                    if (p.getX() == temp.getX() && p.getY() == temp.getY()) {
                                        haveFailed = true;
                                        break;
                                    }
                                }
                                    snakePosition.add(temp);
                                    if (Objects.requireNonNull(snakePosition.peekLast()).getX() == fruitPosition.getX() && (snakePosition.peekLast() != null ? snakePosition.peekLast().getY() : 0) == fruitPosition.getY()) {
                                        clearCell(fruitPosition.getX(), fruitPosition.getY());
                                        fruitPosition.setPosition(generatePosition(), generatePosition());
                                        putDownFruit(fruitPosition.getX(), fruitPosition.getY());
                                        point++;
                                        pointLabel.setText(String.valueOf(point));
                                        fillWithSnake(Objects.requireNonNull(snakePosition.peekLast()).getX(), Objects.requireNonNull(snakePosition.peekLast()).getY());
                                    } else {
                                        clearCell(Objects.requireNonNull(snakePosition.peekFirst()).getX(), Objects.requireNonNull(snakePosition.peekFirst()).getY());
                                        snakePosition.removeFirst();

                                        fillWithSnake(Objects.requireNonNull(snakePosition.peekLast()).getX(), Objects.requireNonNull(snakePosition.peekLast()).getY());
                                    }
                                } else {
                                haveFailed = true;
                            }

                        }
                    } else {
                        timeline.stop();
                        System.out.println("ENDED-----------------------------------------------");
                    }
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private boolean inBoundary(int x, int y) {
        return (-1 < x && x < gameBoard.getColumnCount()) && (-1 < y && y < gameBoard.getRowCount());
    }


    private void paintStartingPositions() {
        for (Position p : snakePosition) {
            fillWithSnake(p.getX(), p.getY());
        }
        putDownFruit(fruitPosition.getX(), fruitPosition.getY());
    }

    private void fillStartingPosition() {
        snakePosition.add(new Position(2, 9));
        snakePosition.add(new Position(3, 9));
        snakePosition.add(new Position(4, 9));
    }

    private int generatePosition() {
        return random.nextInt(0, gameBoard.getColumnCount());
    }

    public static void setLastKeyDown(KeyCode keyCodeToBeSet) {
        switch (keyCodeToBeSet) {
            case RIGHT:
                if (lastRegisteredKey != KeyCode.LEFT) {
                    lastKeyDown = keyCodeToBeSet;
                }
                break;
            case LEFT:
                if (lastRegisteredKey != KeyCode.RIGHT) {
                    lastKeyDown = keyCodeToBeSet;
                }
                break;
            case UP:
                if (lastRegisteredKey != KeyCode.DOWN) {
                    lastKeyDown = keyCodeToBeSet;
                }
                break;
            case DOWN:
                if (lastRegisteredKey != KeyCode.UP) {
                    lastKeyDown = keyCodeToBeSet;
                }
                break;
        }
    }

    private void fillWithSnake(int x, int y) {
        Rectangle snakeGraphic = new Rectangle(cellSize, cellSize, Color.GREEN);
        gameBoard.add(snakeGraphic, x, y);
    }

    private void putDownFruit(int x, int y) {
        Circle fruit = new Circle(100, 100, cellSize/2);
        fruit.setFill(Color.WHITE);
        gameBoard.add(fruit, x, y);
    }

    private void clearCell(int x, int y) {
        gameBoard.getChildren().removeIf(node ->
                GridPane.getRowIndex(node) == y && GridPane.getColumnIndex(node) == x);
    }
}
