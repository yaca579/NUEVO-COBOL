package com.example.vidalbet;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class RetirarController {

    @FXML private TextField txtImportRetirar;
    @FXML private Label lblSaldoDisponible;

    @FXML
    public void initialize() {
        // Mostramos el saldo actual para que el usuario sepa cuánto puede quitar
        lblSaldoDisponible.setText("Saldo disponible: " + String.format("%.2f", SessioUsuari.saldo) + " €");
    }

    @FXML
    private void handleRetirarDinero(ActionEvent event) {
        try {
            double cantidad = Double.parseDouble(txtImportRetirar.getText().replace(",", "."));

            if (cantidad <= 0) {
                mostrarAlerta("Error", "Introdueix una quantitat vàlida.");
                return;
            }

            if (SessioUsuari.saldo >= cantidad) {
                SessioUsuari.saldo -= cantidad;
                mostrarAlerta("Èxit", "Has retirat " + cantidad + " € correctament.");
                handleVolverInici(event); // Volvemos al menú principal
            } else {
                mostrarAlerta("Saldo Insuficient", "No tens prou diners per retirar aquesta quantitat.");
            }
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Format de número incorrecte.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVolverInici(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vidalbet/MainView.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}