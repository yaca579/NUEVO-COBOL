package com.example.vidalbet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML private TextField txtUsuari;
    @FXML private PasswordField txtPassword;

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = txtUsuari.getText().trim();
        String pass = txtPassword.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            mostrarAlerta("Camps Incomplets", "Si us plau, introdueix el teu usuari i contrasenya.");
            return;
        }

        // --- ADMIN LOGIN ---
        if (user.equalsIgnoreCase("admin") && pass.equals("admin")) {
            SessioUsuari.nom = "Administrador";
            SessioUsuari.saldo = 0.00; // Un poco de saldo para el admin
            navegar(event, "MainView.fxml", "VidalBet - Inici (Admin)");
            return;
        }

        // --- USUARI LOGIN ---
        if (user.equals(SessioUsuari.usuari) && pass.equals(SessioUsuari.password) && !SessioUsuari.usuari.isEmpty()) {
            navegar(event, "MainView.fxml", "VidalBet - Inici");
        } else {
            mostrarAlerta("Error d'accés", "Usuari o contrasenya incorrectes.");
        }
    }

    @FXML
    private void handleAnarARegistre(ActionEvent event) {
        navegar(event, "RegisterView.fxml", "VidalBet - Registre");
    }

    private void navegar(ActionEvent event, String fxml, String titol) {
        try {
            // SOLUCIÓN AL ERROR: Forzamos la ruta absoluta desde resources
            String rutaCompleta = "/com/example/vidalbet/" + fxml;
            URL resource = getClass().getResource(rutaCompleta);

            if (resource == null) {
                throw new IOException("No s'ha trobat el fitxer FXML en la ruta: " + rutaCompleta);
            }

            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            // Si el error es aquí, mira la consola: dirá exactamente qué ID o Método falla en MainView.fxml
            Scene scene = new Scene(fxmlLoader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(titol);
            stage.setScene(scene);
            stage.centerOnScreen(); // Mejora visual
            stage.show();

        } catch (Exception e) {
            System.err.println("❌ ERROR CRÍTIC en carregar " + fxml);
            System.err.println("Causa: " + e.getCause()); // Esto te dirá el error real del FXML
            e.printStackTrace();
            mostrarAlerta("Error de Sistema", "No s'ha pogut carregar la vista: " + fxml);
        }
    }

    private void mostrarAlerta(String titol, String missatge) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titol);
        alert.setHeaderText(null);
        alert.setContentText(missatge);

        // SOLUCIÓN PREVENTIVA: Ruta absoluta también para el CSS
        URL css = getClass().getResource("/com/example/vidalbet/style.css");
        if (css != null) {
            alert.getDialogPane().getStylesheets().add(css.toExternalForm());
            alert.getDialogPane().getStyleClass().add("dialog-pane");
        }

        alert.showAndWait();
    }
}