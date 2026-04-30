package com.example.vidalbet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class DepositController {

    @FXML private Label lblTargetaInfo;
    @FXML private TextField txtImport;

    @FXML
    public void initialize() {
        // Mostramos la tarjeta del usuario de forma elegante
        if (SessioUsuari.numeroTargeta != null && SessioUsuari.numeroTargeta.length() >= 4) {
            String t = SessioUsuari.numeroTargeta;
            lblTargetaInfo.setText("Targeta: **** **** **** " + t.substring(t.length() - 4));
        } else {
            lblTargetaInfo.setText("Targeta: **** **** **** 1234");
        }
    }

    @FXML
    private void handleConfirmarDiposit(ActionEvent event) {
        try {
            String textoImport = txtImport.getText().replace(",", ".");
            if (textoImport.isEmpty()) throw new NumberFormatException();

            double importADepositar = Double.parseDouble(textoImport);

            if (importADepositar > 0) {
                // 1. Aumentamos el saldo real
                SessioUsuari.saldo += importADepositar;

                String mensajeAlerta = String.format("Has dipositat %.2f € correctament.", importADepositar);
                String tituloAlerta = "Dipòsit Realitzat";

                // 2. Lógica del BONO (Solo la primera vez)
                if (!SessioUsuari.primerDepositRealitzat) {
                    double cantidadBono = Math.min(importADepositar, 500.0); // Máximo 500€
                    SessioUsuari.saldoBono += cantidadBono;
                    SessioUsuari.primerDepositRealitzat = true; // Bloqueamos para futuros depósitos

                    tituloAlerta = "¡Benvinguda a VidalBet!";
                    mensajeAlerta += String.format("\n\n🎁 Bono de benvinguda aplicat: %.2f €", cantidadBono);
                }

                // 3. Mostrar Alerta con estilo Dark
                mostrarAlertaInformativa(tituloAlerta, mensajeAlerta);

                // 4. Volver al Main
                cambiarEscena(event, "MainView.fxml", "VidalBet - Principal");
            } else {
                mostrarAlertaError("Import no vàlid", "L'import ha de ser superior a 0 €.");
            }
        } catch (NumberFormatException e) {
            txtImport.setStyle("-fx-border-color: #ff4444; -fx-background-color: #1c2028; -fx-text-fill: white;");
            mostrarAlertaError("Error de format", "Per favor, introdueix una quantitat numèrica vàlida.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTornar(ActionEvent event) throws IOException {
        cambiarEscena(event, "MainView.fxml", "VidalBet - Principal");
    }

    // --- MÉTODOS AUXILIARES ---

    private void mostrarAlertaInformativa(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        estilizarAlerta(alert);
        alert.showAndWait();
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        estilizarAlerta(alert);
        alert.showAndWait();
    }

    private void estilizarAlerta(Alert alert) {
        alert.getDialogPane().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog-pane");
    }

    private void cambiarEscena(ActionEvent event, String fxml, String titol) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(titol);
        stage.setScene(scene);
    }
}