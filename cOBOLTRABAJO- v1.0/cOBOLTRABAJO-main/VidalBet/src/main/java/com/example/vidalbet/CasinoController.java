package com.example.vidalbet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class CasinoController {

    @FXML private Label lblSaldoCasino;

    @FXML
    public void initialize() {
        actualitzarSaldo();
    }

    private void actualitzarSaldo() {
        lblSaldoCasino.setText(String.format("Saldo: %.2f €", SessioUsuari.saldo).replace(".", ","));
    }

    // --- TORNAR A L'INICI ---
    @FXML
    private void navInici(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    // --- NAVEGAR A LES SLOTS (En fer clic a la meitat esquerra) ---
    @FXML
    private void obrirSlots(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SlotsView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    // --- NAVEGAR A LA RULETA (En fer clic a la meitat dreta) ---
    @FXML
    private void obrirRuleta(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RuletaView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}