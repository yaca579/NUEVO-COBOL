package com.example.vidalbet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

public class RegisterController {

    @FXML private TextField txtNom;
    @FXML private TextField txtCognoms;
    @FXML private DatePicker dpNaixement;
    @FXML private TextField txtPais;
    @FXML private TextField txtComunitat;
    @FXML private TextField txtCP;

    // Nous camps de Login
    @FXML private TextField txtNouUsuari;
    @FXML private PasswordField txtNouPassword;

    @FXML
    public void initialize() {
        txtNom.setText(SessioUsuari.nom);
        txtCognoms.setText(SessioUsuari.cognoms);
        txtPais.setText(SessioUsuari.pais);
        txtComunitat.setText(SessioUsuari.comunitat);
        txtCP.setText(SessioUsuari.cp);
        txtNouUsuari.setText(SessioUsuari.usuari);
        txtNouPassword.setText(SessioUsuari.password);
    }

    @FXML
    private void handleRegistrar(ActionEvent event) throws IOException {
        guardarEnSessio();

        if (txtNom.getText().isEmpty() || txtNouUsuari.getText().isEmpty() || txtNouPassword.getText().isEmpty() || dpNaixement.getValue() == null) {
            mostrarAlerta("Camps Incomplets", "Si us plau, omple totes les dades personals i d'accés.");
            return;
        }

        int edat = Period.between(dpNaixement.getValue(), LocalDate.now()).getYears();
        if (edat < 18) {
            mostrarAlerta("Edat No Permesa", "Has de ser major de 18 anys.");
            return;
        }

        if (!SessioUsuari.teTargeta()) {
            mostrarAlerta("Mètode de Pagament", "És obligatori configurar una targeta abans de registrar-se.");
            return;
        }

        // ÈXIT! Mostrem missatge i tornem al login
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registre Completat");
        alert.setHeaderText(null);
        alert.setContentText("Compte creat correctament per a " + SessioUsuari.nom + "! Ara pots iniciar sessió.");
        alert.showAndWait();

        cambiarEscena(event, "LoginView.fxml", "VidalBet - Inici de Sessió");
    }

    @FXML
    private void handleAnarAPagament(ActionEvent event) throws IOException {
        guardarEnSessio();
        cambiarEscena(event, "PaymentView.fxml", "VidalBet - Pagament");
    }

    @FXML
    private void handleTornarLogin(ActionEvent event) throws IOException {
        cambiarEscena(event, "LoginView.fxml", "VidalBet - Inici de Sessió");
    }

    private void guardarEnSessio() {
        SessioUsuari.nom = txtNom.getText();
        SessioUsuari.cognoms = txtCognoms.getText();
        SessioUsuari.pais = txtPais.getText();
        SessioUsuari.comunitat = txtComunitat.getText();
        SessioUsuari.cp = txtCP.getText();
        SessioUsuari.usuari = txtNouUsuari.getText();
        SessioUsuari.password = txtNouPassword.getText();
    }

    private void cambiarEscena(ActionEvent event, String fxml, String titol) throws IOException {
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
        alert.showAndWait();
    }
}