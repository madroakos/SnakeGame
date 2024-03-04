package com.madroakos.snakegame;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("mainPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 600);
        stage.setTitle("SnakeGame");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                    System.out.println("Up arrow key pressed");
                    ApplicationController.setLastKeyDown(KeyCode.UP);
                    break;
                case DOWN:
                    System.out.println("Down arrow key pressed");
                    ApplicationController.setLastKeyDown(KeyCode.DOWN);
                    break;
                case LEFT:
                    System.out.println("Left arrow key pressed");
                    ApplicationController.setLastKeyDown(KeyCode.LEFT);
                    break;
                case RIGHT:
                    System.out.println("Right arrow key pressed");
                    ApplicationController.setLastKeyDown(KeyCode.RIGHT);
                    break;
                default:
                    break;
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}