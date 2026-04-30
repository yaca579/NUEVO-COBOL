package com.example.vidalbet;

import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.Random;

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

        // Llamamos al motor COBOL en un hilo separado
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("slots.exe");
                Process p = pb.start();

                // 1. ENVIAMOS LA APUESTA A COBOL
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
                writer.write(String.format("%04d\n", apuesta));
                writer.flush();
                writer.close();

                // 2. LEEMOS LA RESPUESTA DE COBOL
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String resultadoCobol = reader.readLine();

                if (resultadoCobol != null) {
                    Platform.runLater(() -> procesarRespuestaCobol(resultadoCobol));
                }

            } catch (Exception e) {
                Platform.runLater(() -> mostrarAlerta("Error Sistema", "No se pudo conectar con el Mainframe COBOL."));
                e.printStackTrace();
            }
        }).start();
    }

    private void procesarRespuestaCobol(String resultadoCobol) {
        String[] partes = resultadoCobol.split(",");

        if (partes.length == 4) {
            String val1 = partes[0].trim();
            String val2 = partes[1].trim();
            String val3 = partes[2].trim();
            int premio = Integer.parseInt(partes[3].trim());

            // Lanzamos la animación visual de la ruleta antes de mostrar el resultado
            animarRuletaYMostrarResultado(val1, val2, val3, premio);
        }
    }

    private void animarRuletaYMostrarResultado(String val1, String val2, String val3, int premio) {
        // Usamos Emojis puros para evitar que JavaFX los desalinee
        String[] emojisAleatorios = {"💎", "🍒", "🍋", "🔔", "🍉", "🍇"};
        Random rand = new Random();

        // Creamos la animación rápida
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            slot1.setText(emojisAleatorios[rand.nextInt(emojisAleatorios.length)]);
            slot2.setText(emojisAleatorios[rand.nextInt(emojisAleatorios.length)]);
            slot3.setText(emojisAleatorios[rand.nextInt(emojisAleatorios.length)]);
        }));

        // Gira 15 veces (1.5 segundos de tensión)
        timeline.setCycleCount(15);

        // Cuando acaba la animación, plantamos el resultado real
        timeline.setOnFinished(e -> {
            slot1.setText(traducirSimbolo(val1));
            slot2.setText(traducirSimbolo(val2));
            slot3.setText(traducirSimbolo(val3));

            if (premio > 0) {
                SessioUsuari.saldo += premio; // Sumamos el premio
                if (val1.equals("7") && val2.equals("7") && val3.equals("7")) {
                    mostrarAlerta("🔥 JACKPOT! 🔥", "¡Pleno de Diamantes! El Mainframe te otorga: " + premio + "€");
                } else {
                    mostrarAlerta("🍒 PREMIO!", "Has ganado " + premio + "€!");
                }
            } else {
                mostrarAlerta("😢 Lástima", "No has ganado nada esta vez.");
            }
            actualizarSaldo();
        });

        timeline.play();
    }

    private String traducirSimbolo(String letra) {
        switch (letra) {
            case "7": return "💎"; // Cambiado de 7️⃣ a 💎 para alineamiento perfecto
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
        a.setTitle(t);
        a.setHeaderText(null);
        try {
            a.getDialogPane().getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            a.getDialogPane().getStyleClass().add("dialog-pane");
        } catch (Exception ignored) {}
        a.show();
    }
}