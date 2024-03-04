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
import java.util.*;

public class ApplicationController {
    @FXML
    private GridPane gameBoard;
    @FXML
    private Button startButton;
    @FXML
    private Label pointLabel;
    private final Deque<Position> snakePosition = new ArrayDeque<>();
    private final Position fruitPosition = new Position(5, 5);
    private double cellSize;
    private static KeyCode lastKeyDown = KeyCode.RIGHT;
    private static KeyCode lastRegisteredKey = KeyCode.RIGHT;
    private final Random random = new Random();
    private boolean haveFailed = false;
    private int point;
    private final Set<Position> allPositions = new HashSet<>();

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
                                for (Position p : snakePosition) {
                                    if (p.getX() == temp.getX() && p.getY() == temp.getY()) {
                                        haveFailed = true;
                                        break;
                                    }
                                }
                                    snakePosition.add(temp);
                                    if (Objects.requireNonNull(snakePosition.peekLast()).getX() == fruitPosition.getX() && (snakePosition.peekLast() != null ? snakePosition.peekLast().getY() : 0) == fruitPosition.getY()) {
                                        clearCell(fruitPosition.getX(), fruitPosition.getY());
                                        putDownFruit();
                                        point++;
                                        pointLabel.setText(String.valueOf(point));
                                    } else {
                                        clearCell(Objects.requireNonNull(snakePosition.peekFirst()).getX(), Objects.requireNonNull(snakePosition.peekFirst()).getY());
                                        snakePosition.removeFirst();
                                    }
                                fillWithSnake(Objects.requireNonNull(snakePosition.peekLast()).getX(), Objects.requireNonNull(snakePosition.peekLast()).getY());
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
        for (int i = 0; i < gameBoard.getRowCount(); i++) {
            for (int j = 0; j < gameBoard.getColumnCount(); j++) {
                allPositions.add(new Position(i, j));
            }
        }
        putDownFruit();
    }

    private void fillStartingPosition() {
        snakePosition.add(new Position(2, 6));
        snakePosition.add(new Position(3, 6));
        snakePosition.add(new Position(4, 6));
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

    private void putDownFruit() {
        Circle fruit = new Circle(100, 100, cellSize/2);
        fruit.setFill(Color.WHITE);

        System.out.println("SNAKEPOSITIONS:---------------------------");
        for (Position p : snakePosition) {
            System.out.println(p.getX() + " " + p.getY());
        }

        System.out.println("REMOVED POSITIONS FROM ALL");
        List<Position> availablePositionsList = new ArrayList<>(allPositions);
        for (Position p : snakePosition) {
            System.out.println(p.getX() + " " + p.getY());
            availablePositionsList.remove(p);
        }

        System.out.println("AVAILABLE POSITIONS:---------------------------");
        for (Position p : availablePositionsList) {
            System.out.println(p.getX() + " " + p.getY());
        }

        if (!availablePositionsList.isEmpty()) {
            Position current = availablePositionsList.get(random.nextInt(availablePositionsList.size()));
            fruitPosition.setPosition(current.getX(), current.getY());
            gameBoard.add(fruit, current.getX(), current.getY());
        }
    }


    private void clearCell(int x, int y) {
        gameBoard.getChildren().removeIf(node -> {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer colIndex = GridPane.getColumnIndex(node);
            return rowIndex != null && colIndex != null && rowIndex == y && colIndex == x;
        });
    }

}
