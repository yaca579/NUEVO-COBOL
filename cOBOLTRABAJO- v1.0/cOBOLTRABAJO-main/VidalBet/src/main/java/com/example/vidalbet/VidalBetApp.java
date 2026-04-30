package com.example.vidalbet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class VidalBetApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Carreguem la vista de Login primer
        FXMLLoader fxmlLoader = new FXMLLoader(VidalBetApp.class.getResource("LoginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("VidalBet - Benvingut");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}