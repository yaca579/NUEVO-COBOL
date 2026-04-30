package com.example.vidalbet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class PaymentController {

    @FXML private TextField txtTargeta;
    @FXML private TextField txtTitular;
    @FXML private TextField txtVenciment;
    @FXML private TextField txtCVV;

    @FXML
    public void initialize() {
        // Carreguem les dades si ja n'havia posat per no haver d'escriure de nou
        txtTargeta.setText(SessioUsuari.numeroTargeta);
        txtTitular.setText(SessioUsuari.titular);
        txtVenciment.setText(SessioUsuari.venciment);
        txtCVV.setText(SessioUsuari.cvv);
    }

    @FXML
    private void handleGuardar(ActionEvent event) throws IOException {
        if (txtTargeta.getText().isEmpty() || txtCVV.getText().isEmpty()) {
            mostrarAlerta("Dades Incompletes", "Per favor, omple els camps de la targeta.");
            return;
        }

        // GUARDEM A LA SESSIÓ (Això permet que el RegisterController sàpiga que ja hi ha targeta)
        SessioUsuari.numeroTargeta = txtTargeta.getText();
        SessioUsuari.titular = txtTitular.getText();
        SessioUsuari.venciment = txtVenciment.getText();
        SessioUsuari.cvv = txtCVV.getText();

        // CANVI CRÍTIC: Tornem a RegisterView en lloc de MainView
        anarAView(event, "RegisterView.fxml", "VidalBet - Registre");
    }

    @FXML
    private void handleTornar(ActionEvent event) throws IOException {
        // Si l'usuari cancel·la, també torna al registre
        anarAView(event, "RegisterView.fxml", "VidalBet - Registre");
    }

    private void anarAView(ActionEvent event, String fxml, String titol) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(titol);
        stage.setScene(scene);
    }

    private void mostrarAlerta(String titol, String msj) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titol);
        alert.setHeaderText(null);
        alert.setContentText(msj);

        // Intentem carregar l'estil; si falla, l'alerta surt normal
        try {
            alert.getDialogPane().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("dialog-pane");
        } catch (Exception e) {
            System.out.println("No s'ha pogut carregar l'estil de l'alerta.");
        }

        alert.showAndWait();
    }
}