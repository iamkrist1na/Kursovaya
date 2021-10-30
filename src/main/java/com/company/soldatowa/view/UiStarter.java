package com.company.soldatowa.view;

import com.company.soldatowa.config.application.ApplicationConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;
import java.util.logging.Logger;

public class UiStarter extends Application {

    private double xOffset;
    private double yOffset;

    public static void main(String[] args) {
        launch(args);
        Logger.getGlobal().info("APPLICATION STOPPED...");
    }

    public static void restart(Node node) throws Exception {
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
        Logger.getGlobal().info("RESTARTING...");
        new UiStarter().start(new Stage());
    }

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(Objects.requireNonNull(UiStarter.class.getResource("/fxml/main.fxml")));


        Scene scene = new Scene(root, ApplicationConfig.windowWidth, ApplicationConfig.windowHeight);

        stage.setFullScreen(false);
        stage.setResizable(false);
        stage.setTitle("Kursovaya");
        stage.setScene(scene);
        stage.setIconified(false);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.UNDECORATED);

        scene.setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        scene.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

        stage.show();

        Logger.getGlobal().info("APPLICATION STARTED...");
    }

}
