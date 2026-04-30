package com.example.vidalbet;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

public class DetallePartidoController {

    @FXML private Label lblTituloPartido;
    @FXML private Label lblSaldo;
    @FXML private VBox contenedorMercados;

    private String equipoPartido = "Partit Seleccionat";

    @FXML
    public void initialize() {
        actualitzarSaldo();
    }

    public void setDatosPartido(String titulo, String deporte) {
        this.equipoPartido = titulo;
        String emoji = "⚽";
        if (deporte.equals("NBA")) emoji = "🏀";
        else if (deporte.equals("Tenis")) emoji = "🎾";

        lblTituloPartido.setText(emoji + " " + titulo.toUpperCase());
        generarMercadosDinamicos(deporte);
    }

    private void actualitzarSaldo() {
        if (lblSaldo != null) {
            lblSaldo.setText(String.format("Saldo: %.2f €", SessioUsuari.saldo).replace(".", ","));
        }
    }

    // --- GENERAR MERCADOS SEGÚN EL DEPORTE ---
    private void generarMercadosDinamicos(String deporte) {
        contenedorMercados.getChildren().clear();

        if (deporte.equals("Futbol")) {
            contenedorMercados.getChildren().add(crearBloqueMercado("RESULTAT FINAL (1X2)", new String[]{"1 (Local)", "X (Empat)", "2 (Visitant)"}, new String[]{"1.80", "3.10", "4.50"}));
            contenedorMercados.getChildren().add(crearBloqueMercado("DOBLE OPORTUNITAT", new String[]{"1X", "12", "X2"}, new String[]{"1.20", "1.35", "1.85"}));
            contenedorMercados.getChildren().add(crearBloqueMercado("TOTAL GOLS (2.5)", new String[]{"Més de 2.5", "Menys de 2.5"}, new String[]{"1.90", "1.90"}));
        } else if (deporte.equals("NBA")) {
            contenedorMercados.getChildren().add(crearBloqueMercado("GUANYADOR DEL PARTIT", new String[]{"Local", "Visitant"}, new String[]{"1.65", "2.20"}));
            contenedorMercados.getChildren().add(crearBloqueMercado("HÀNDICAP ASIÀTIC", new String[]{"Local -4.5", "Visitant +4.5"}, new String[]{"1.90", "1.90"}));
            contenedorMercados.getChildren().add(crearBloqueMercado("TOTAL PUNTS (215.5)", new String[]{"Més de 215.5", "Menys de 215.5"}, new String[]{"1.85", "1.95"}));
        } else if (deporte.equals("Tenis")) {
            contenedorMercados.getChildren().add(crearBloqueMercado("GUANYADOR DEL PARTIT", new String[]{"Jugador 1", "Jugador 2"}, new String[]{"1.40", "2.85"}));
            contenedorMercados.getChildren().add(crearBloqueMercado("GUANYADOR 1R SET", new String[]{"Jugador 1", "Jugador 2"}, new String[]{"1.55", "2.35"}));
            contenedorMercados.getChildren().add(crearBloqueMercado("TOTAL SETS", new String[]{"2 Sets", "3 Sets"}, new String[]{"1.50", "2.50"}));
        }
    }

    // --- CREADOR VISUAL DE BOTONES DE CUOTA ---
    private VBox crearBloqueMercado(String titulo, String[] opciones, String[] cuotas) {
        VBox bloque = new VBox(15);
        bloque.getStyleClass().add("dialog-pane");
        bloque.setStyle("-fx-padding: 25;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-text-fill: #7A808C; -fx-font-size: 14px; -fx-font-weight: bold;");

        HBox hboxOpciones = new HBox(15);
        hboxOpciones.setAlignment(Pos.CENTER);

        for (int i = 0; i < opciones.length; i++) {
            Button btn = new Button();
            btn.getStyleClass().add("cuota-button");
            btn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(btn, Priority.ALWAYS);

            VBox contenidoBtn = new VBox(5);
            contenidoBtn.setAlignment(Pos.CENTER);

            Label lblOpcion = new Label(opciones[i]);
            lblOpcion.setStyle("-fx-text-fill: #7A808C; -fx-font-size: 12px;");

            Label lblCuota = new Label(cuotas[i].replace(".", ","));
            lblCuota.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 22px; -fx-font-weight: bold;");

            contenidoBtn.getChildren().addAll(lblOpcion, lblCuota);
            btn.setGraphic(contenidoBtn);

            // Añadir funcionalidad de apuesta
            String seleccionFinal = opciones[i];
            double cuotaFinal = Double.parseDouble(cuotas[i]);
            btn.setOnAction(e -> abrirDialogoApuesta(seleccionFinal, cuotaFinal));

            hboxOpciones.getChildren().add(btn);
        }

        bloque.getChildren().addAll(lblTitulo, hboxOpciones);
        return bloque;
    }

    // --- NAVEGACIÓN ---
    @FXML
    private void navInici(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    // --- LÓGICA DE APUESTAS ---
    private void abrirDialogoApuesta(String seleccion, double cuota) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirmar Aposta - VidalBet");
        dialog.getDialogPane().setStyle("-fx-background-color: #1C2028;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20, 30, 10, 30));

        Label lblHeader = new Label("Aposta: " + seleccion + " (" + equipoPartido + ")");
        lblHeader.setStyle("-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);

        TextField txtImport = new TextField("10");
        txtImport.setStyle("-fx-background-color: #12151B; -fx-text-fill: #E5C04A; -fx-font-weight: bold; -fx-font-size: 14; -fx-border-color: #2D323E; -fx-border-radius: 5; -fx-padding: 8;");
        txtImport.setPrefWidth(100);

        Label lblPremioSugerido = new Label(String.format("%.2f €", 10 * cuota));
        lblPremioSugerido.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 16;");

        CheckBox chkUsarBono = new CheckBox(" Utilitzar Saldo Bono");
        chkUsarBono.setStyle("-fx-text-fill: #7A808C; -fx-font-size: 14; -fx-cursor: hand;");

        Label lblTxtImport = new Label("Import:");
        lblTxtImport.setStyle("-fx-text-fill: #7A808C; -fx-font-size: 14;");
        Label lblTxtGuany = new Label("Guany estimat:");
        lblTxtGuany.setStyle("-fx-text-fill: #7A808C; -fx-font-size: 14;");

        grid.add(lblTxtImport, 0, 0);
        grid.add(txtImport, 1, 0);
        grid.add(chkUsarBono, 0, 1, 2, 1);
        grid.add(lblTxtGuany, 0, 2);
        grid.add(lblPremioSugerido, 1, 2);

        content.getChildren().addAll(lblHeader, grid);
        dialog.getDialogPane().setContent(content);

        ButtonType btnAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAceptar, btnCancelar);

        Node okButton = dialog.getDialogPane().lookupButton(btnAceptar);
        okButton.setStyle("-fx-background-color: #E5C04A; -fx-text-fill: #12151B; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15; -fx-background-radius: 5;");
        Node cancelButton = dialog.getDialogPane().lookupButton(btnCancelar);
        cancelButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #7A808C; -fx-cursor: hand; -fx-border-color: #2D323E; -fx-border-radius: 5; -fx-padding: 7 15;");

        txtImport.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                lblPremioSugerido.setText(String.format("%.2f €", Double.parseDouble(newVal.replace(",", ".")) * cuota));
            } catch (Exception e) {
                lblPremioSugerido.setText("0,00 €");
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == btnAceptar) {
            try {
                double importe = Double.parseDouble(txtImport.getText().replace(",", "."));
                boolean usarBono = chkUsarBono.isSelected();
                String tipoSaldo = usarBono ? "Saldo Bono" : "Saldo Real";

                boolean saldoOk = false;
                if (!usarBono && SessioUsuari.saldo >= importe) {
                    SessioUsuari.saldo -= importe;
                    saldoOk = true;
                } else if (usarBono && SessioUsuari.saldoBono >= importe) {
                    SessioUsuari.saldoBono -= importe;
                    saldoOk = true;
                }

                if (saldoOk) {
                    String ref = "REF:" + Long.toHexString(Double.doubleToLongBits(Math.random())).substring(0, 5).toUpperCase();
                    SessioUsuari.misApuestas.add("● DIRECTE | " + equipoPartido + " (" + seleccion + ") | Q: " + cuota + " | " + ref);
                    actualitzarSaldo();

                    resolverApuesta(ref, cuota, importe, tipoSaldo);
                } else {
                    mostrarAlerta("Atenció", "Saldo insuficient en: " + tipoSaldo);
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Siusplau, introdueix un número vàlid.");
            }
        }
    }

    private void resolverApuesta(String ref, double cuota, double importe, String tipoUsado) {
        PauseTransition pausa = new PauseTransition(Duration.seconds(4));
        pausa.setOnFinished(e -> {
            boolean gana = new Random().nextDouble() <= (1.0 / cuota);

            for (int i = 0; i < SessioUsuari.misApuestas.size(); i++) {
                if (SessioUsuari.misApuestas.get(i).contains(ref)) {
                    String estado = gana ? "✅ GUANYADA" : "❌ PERDUDA";
                    SessioUsuari.misApuestas.set(i, SessioUsuari.misApuestas.get(i).replace("● DIRECTE", estado));
                    break;
                }
            }

            if (gana) {
                double premio = importe * cuota;
                if (tipoUsado.equals("Saldo Real")) SessioUsuari.saldo += premio;
                else SessioUsuari.saldoBono += premio;
            }

            actualitzarSaldo();
            mostrarAlerta(gana ? "¡GUANYADA!" : "PERDUDA", gana ? "Has guanyat " + String.format("%.2f", (importe * cuota)) + " €!" : "Aposta perduda.");
        });
        pausa.play();
    }

    private void mostrarAlerta(String t, String m) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION, m);
            a.setTitle(t);
            a.setHeaderText(null);
            a.show();
        });
    }
}