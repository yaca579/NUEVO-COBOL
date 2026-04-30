package com.example.vidalbet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HistorialController {

    @FXML private ListView<String> lvApuestas;

    @FXML
    public void initialize() {
        // --- APLICAMOS TU CSS DIRECTAMENTE AL LISTVIEW ---
        lvApuestas.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-padding: 10;");

        // --- TRUCO JAVA PARA EVITAR EL FONDO AZUL DE SELECCIÓN ---
        lvApuestas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lvApuestas.getSelectionModel().clearSelection(); // Deselecciona al instante
            }
        });

        // Cargamos las apuestas
        if (SessioUsuari.misApuestas != null && !SessioUsuari.misApuestas.isEmpty()) {
            lvApuestas.getItems().addAll(SessioUsuari.misApuestas);
        } else {
            lvApuestas.getItems().add("BUIDA");
        }

        // CellFactory: Convierte el texto en la tarjeta
        lvApuestas.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

                } else {
                    setGraphic(crearCardApuesta(item));
                    setStyle("-fx-background-color: transparent; -fx-padding: 8 0; -fx-border-color: transparent;");
                }
            }
        });
    }

    private VBox crearCardApuesta(String textoApuesta) {
        String[] partes = textoApuesta.split(" \\| ");
        String estado = partes.length > 0 ? partes[0] : "Desconegut";
        String equipo = partes.length > 1 ? partes[1] : "Desconegut";
        String cuota = partes.length > 2 ? partes[2] : "Q: -";
        String ref = partes.length > 3 ? partes[3] : "REF: -";

        VBox card = new VBox(12);

        // Lógica de colores estilo Winamax
        String colorBordeEstado = "#7A808C";
        String colorFondoEstado = "transparent";

        if (estado.contains("GUANYADA")) {
            colorBordeEstado = "#27ae60";
            colorFondoEstado = "#27ae6033";
        } else if (estado.contains("PERDUDA")) {
            colorBordeEstado = "#e74c3c";
            colorFondoEstado = "#e74c3c33";
        } else if (estado.contains("DIRECTE")) {
            colorBordeEstado = "#f1c40f";
            colorFondoEstado = "#f1c40f33";
        }

        // --- APLICAMOS LOS VALORES DE TU CSS ---
        card.setStyle("-fx-background-color: #1C2028; " +
                "-fx-padding: 20; " +
                "-fx-background-radius: 15; " +
                "-fx-border-color: " + colorBordeEstado + " #2D323E #2D323E #2D323E; " +
                "-fx-border-width: 3 1 1 1; " +
                "-fx-border-radius: 15;");

        // --- CORRECCIÓN: Usamos un Region como muelle para separar los elementos ---
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label lblEstado = new Label(estado);
        lblEstado.setStyle("-fx-text-fill: " + colorBordeEstado + "; -fx-font-weight: bold; -fx-font-size: 12; -fx-background-color: " + colorFondoEstado + "; -fx-padding: 3 8; -fx-background-radius: 4;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // Esto empuja la cuota a la derecha del todo

        Label lblCuota = new Label(cuota.replace("Q: ", ""));
        lblCuota.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-background-color: #12151B; -fx-padding: 5 12; -fx-background-radius: 8; -fx-border-color: #2D323E; -fx-border-radius: 8;");

        topRow.getChildren().addAll(lblEstado, spacer, lblCuota);

        Label lblEquipo = new Label(equipo);
        lblEquipo.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

        HBox bottomRow = new HBox();
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        Label lblRef = new Label("Ref: " + ref.replace("REF:", ""));
        lblRef.setStyle("-fx-text-fill: #7A808C; -fx-font-size: 11;");
        bottomRow.getChildren().add(lblRef);

        card.getChildren().addAll(topRow, lblEquipo, bottomRow);
        return card;
    }

    @FXML
    private void navInici(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }
}