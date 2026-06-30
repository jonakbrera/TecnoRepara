package com.tecnorepara.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/Login.fxml")
        );

        Scene scene = new Scene(root);

        stage.setTitle("TecnoRepara - Login");
        stage.setScene(scene);

        stage.setWidth(900);
        stage.setHeight(560);
        stage.centerOnScreen();

        stage.getIcons().add(
                new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/imagenes/logo.png")
                )
        );

        stage.show();
    
    }
    public static void main(String[] args) {
        launch();
    }
}