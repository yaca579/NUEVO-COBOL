package com.example.vidalbet;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;

public class SlotsController {

    @FXML private Label lblSaldoSlots, slot1, slot2, slot3;
    @FXML private TextField txtApuesta;
    @FXML private Button btnPalanca;

    @FXML
    public void initialize() {
        actualizarSaldo();
    }

    @FXML
    private void tirarDeLaPalanca(javafx.event.ActionEvent event) {
        int apuesta;
        try {
            apuesta = Integer.parseInt(txtApuesta.getText());
            if (apuesta <= 0 || apuesta > SessioUsuari.saldo) throw new Exception();
        } catch (Exception e) {
            mostrarAlerta("Error", "Importe no válido o saldo insuficiente.");
            return;
        }

        // Restamos la apuesta del saldo visual temporalmente
        SessioUsuari.saldo -= apuesta;
        actualizarSaldo();
        animarPalanca();

        // Llamamos al motor COBOL en un hilo separado para no congelar la pantalla
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("./slots"); // Si estás en Windows usa "slots.exe"
                Process p = pb.start();

                // 1. ENVIAMOS LA APUESTA A COBOL
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                writer.write(String.format("%04d\n", apuesta)); // Envía "0005" si apuestas 5
                writer.flush();
                writer.close();

                // 2. LEEMOS LA RESPUESTA DE COBOL (Ej: "7,7,7,00050")
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String resultadoCobol = reader.readLine();

                if (resultadoCobol != null) {
                    Platform.runLater(() -> procesarRespuestaCobol(resultadoCobol));
                }

            } catch (Exception e) {
                Platform.runLater(() -> mostrarAlerta("Error Sistema", "No se pudo conectar con el Mainframe COBOL. Compílalo primero!"));
                e.printStackTrace();
            }
        }).start();
    }

    private void procesarRespuestaCobol(String resultadoCobol) {
        // resultadoCobol es algo como: "C,C,C,00015"
        String[] partes = resultadoCobol.split(",");

        if (partes.length == 4) {
            // Traducimos las letras de COBOL a Emojis Visuales
            slot1.setText(traducirSimbolo(partes[0]));
            slot2.setText(traducirSimbolo(partes[1]));
            slot3.setText(traducirSimbolo(partes[2]));

            // COBOL ya ha calculado cuánto ganamos
            int premio = Integer.parseInt(partes[3]);

            if (premio > 0) {
                SessioUsuari.saldo += premio; // Sumamos el premio al saldo
                if (partes[0].equals("7") && partes[1].equals("7")) {
                    mostrarAlerta("🔥 JACKPOT! 🔥", "El Mainframe te ha otorgado: " + premio + "€!");
                } else {
                    mostrarAlerta("🍒 PREMIO!", "Has ganado " + premio + "€!");
                }
            }
            actualizarSaldo();
        }
    }

    private String traducirSimbolo(String letra) {
        switch (letra) {
            case "7": return "7️⃣";
            case "C": return "🍒";
            case "L": return "🍋";
            default: return "❓";
        }
    }

    private void animarPalanca() {
        RotateTransition rt = new RotateTransition(Duration.millis(200), btnPalanca);
        rt.setByAngle(45);
        rt.setCycleCount(2);
        rt.setAutoReverse(true);
        rt.play();
    }

    private void actualizarSaldo() {
        lblSaldoSlots.setText(String.format("Saldo: %.2f €", SessioUsuari.saldo));
    }

    @FXML
    private void volverAlCasino(javafx.event.ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CasinoView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    private void mostrarAlerta(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, m);
        a.setTitle(t); a.setHeaderText(null);
        try {
            a.getDialogPane().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            a.getDialogPane().getStyleClass().add("dialog-pane");
        } catch (Exception ignored) {}
        a.show();
    }
}