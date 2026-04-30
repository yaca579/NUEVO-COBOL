package com.example.vidalbet;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Random;

public class MainController {

    @FXML private Label lblSaldo, lblSaldoBono, lblNomUsuari;
    @FXML private VBox contenedorDinamicoPartidos;

    // Variable para el menú desplegable
    private ContextMenu menuPerfil;

    @FXML
    public void initialize() {
        try {
            actualitzarUI();
            inicializarMenuDesplegable(); // Cargamos el menú oculto
            generarPartidoFutbol();
            generarPartidoNBA();
            generarPartidoTenis();
        } catch (Exception e) {
            System.err.println("Error crítico en initialize: " + e.getMessage());
        }
    }

    // --- MENÚ DESPLEGABLE ---
    private void inicializarMenuDesplegable() {
        menuPerfil = new ContextMenu();

        MenuItem itemPerfil = new MenuItem("Mi perfil");
        MenuItem itemIngresar = new MenuItem("Ingresar fondos");
        MenuItem itemRetirar = new MenuItem("Retirar");
        MenuItem itemDesconectar = new MenuItem("Desconexión");

        itemPerfil.setOnAction(e -> navegarDesdeMenu("PerfilView.fxml"));
        itemIngresar.setOnAction(e -> navegarDesdeMenu("DepositView.fxml"));
        itemRetirar.setOnAction(e -> navegarDesdeMenu("RetirarView.fxml"));
        itemDesconectar.setOnAction(e -> navegarDesdeMenu("LoginView.fxml"));

        menuPerfil.getItems().addAll(itemPerfil, itemIngresar, itemRetirar, new SeparatorMenuItem(), itemDesconectar);
    }

    @FXML
    private void handleAbrirMenuPerfil(MouseEvent event) {
        Node source = (Node) event.getSource();
        menuPerfil.show(source, Side.BOTTOM, 0, 5);
    }

    private void navegarDesdeMenu(String fxml) {
        try {
            String ruta = "/com/example/vidalbet/" + fxml;
            FXMLLoader l = new FXMLLoader(getClass().getResource(ruta));
            Scene s = new Scene(l.load());
            Stage st = (Stage) lblNomUsuari.getScene().getWindow();
            st.setScene(s);
        } catch (IOException ex) {
            System.err.println("No se ha encontrado la vista: " + fxml);
        }
    }

    // --- LÓGICA DE APUESTAS ---
    private void handleApuestaDinamica(String seleccion, double cuota, VBox cardPartido) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirmar Aposta - VidalBet");
        dialog.getDialogPane().setStyle("-fx-background-color: #1C2028;");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20, 30, 10, 30));

        Label lblHeader = new Label("Aposta: " + seleccion);
        lblHeader.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

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
                    String ref = registrarAposta(seleccion, cuota, importe);
                    actualitzarUI();
                    resolverApuesta(ref, seleccion, cuota, importe, tipoSaldo, cardPartido);
                } else {
                    mostrarAlerta("Atenció", "Saldo insuficient en: " + tipoSaldo);
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "Siusplau, introdueix un número vàlid.");
            }
        }
    }

    private void resolverApuesta(String ref, String equipo, double cuota, double importe, String tipoUsado, VBox card) {
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

            // El partido desaparece siempre
            FadeTransition ft = new FadeTransition(Duration.millis(500), card);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.setOnFinished(ev -> contenedorDinamicoPartidos.getChildren().remove(card));
            ft.play();

            actualitzarUI();
            mostrarAlerta(gana ? "¡GUANYADA!" : "PERDUDA", gana ? "Has guanyat " + String.format("%.2f", (importe * cuota)) + " €!" : "Aposta perduda.");
        });
        pausa.play();
    }

    // --- GENERACIÓN DE PARTIDOS (DESCOMPRIMIDOS) ---
    private void generarPartidoFutbol() {
        VBox card = crearEstructuraCard("⚽ Copa del Mundo · Final");
        HBox hboxVs = new HBox(60);
        hboxVs.setAlignment(Pos.CENTER);
        hboxVs.getChildren().addAll(crearBoxEquipo("Argentina", "equipo1.png"), crearVsLabel(), crearBoxEquipo("Portugal", "equipo2.png"));

        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(crearBotonCuota("Argentina", 1.80), crearBotonCuota("Empate", 2.00), crearBotonCuota("Portugal", 2.10));

        // NOU: Botó de Més Apostes
        Label lblMesApostes = crearEtiquetaMesApostes();
        lblMesApostes.setOnMouseClicked(e -> abrirDetallePartido("Argentina vs Portugal", "Futbol", e));

        card.getChildren().addAll(hboxVs, botones, lblMesApostes);
        inyectarCard(card);
    }

    private void generarPartidoNBA() {
        VBox card = crearEstructuraCard("🏀 NBA · Regular Season");
        HBox hboxVs = new HBox(60);
        hboxVs.setAlignment(Pos.CENTER);
        hboxVs.getChildren().addAll(crearBoxEquipo("LA Lakers", "nba1.png"), crearVsLabel(), crearBoxEquipo("Boston Celtics", "nba2.png"));

        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(crearBotonCuota("LA Lakers", 2.40), crearBotonCuota("Boston Celtics", 1.70));

        // NOU: Botó de Més Apostes
        Label lblMesApostes = crearEtiquetaMesApostes();
        lblMesApostes.setOnMouseClicked(e -> abrirDetallePartido("LA Lakers vs Boston Celtics", "NBA", e));

        card.getChildren().addAll(hboxVs, botones, lblMesApostes);
        inyectarCard(card);
    }

    private void generarPartidoTenis() {
        VBox card = crearEstructuraCard("🎾 Tenis · Roland Garros");
        HBox hboxVs = new HBox(60);
        hboxVs.setAlignment(Pos.CENTER);
        hboxVs.getChildren().addAll(crearBoxEquipo("Carlos Alcaraz", "tenis1.png"), crearVsLabel(), crearBoxEquipo("Jannik Sinner", "tenis2.png"));

        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(crearBotonCuota("Carlos Alcaraz", 1.20), crearBotonCuota("Jannik Sinner", 3.20));

        // NOU: Botó de Més Apostes
        Label lblMesApostes = crearEtiquetaMesApostes();
        lblMesApostes.setOnMouseClicked(e -> abrirDetallePartido("Carlos Alcaraz vs Jannik Sinner", "Tenis", e));

        card.getChildren().addAll(hboxVs, botones, lblMesApostes);
        inyectarCard(card);
    }

    // --- NOU: MÈTODES PER AL DETALL DEL PARTIT ---
    private Label crearEtiquetaMesApostes() {
        Label lbl = new Label("+ Més apostes >");
        lbl.setStyle("-fx-text-fill: #E5C04A; -fx-font-size: 13px; -fx-cursor: hand; -fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setAlignment(Pos.CENTER_RIGHT); // Alineat a la dreta com demanaves
        return lbl;
    }

    private void abrirDetallePartido(String tituloPartido, String deporte, MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vidalbet/DetallePartidoView.fxml"));
            Scene scene = new Scene(loader.load());

            // Passem les dades al nou controlador
            DetallePartidoController controller = loader.getController();
            controller.setDatosPartido(tituloPartido, deporte);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            System.err.println("Error obrint els detalls del partit.");
            e.printStackTrace();
        }
    }

    // --- MÉTODOS DE APOYO DE INTERFAZ (DESCOMPRIMIDOS) ---
    private VBox crearBoxEquipo(String nombre, String imgName) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        InputStream is = getClass().getResourceAsStream("/com/example/vidalbet/" + imgName);
        if (is != null) {
            ImageView imgView = new ImageView(new Image(is));
            imgView.setFitHeight(80);
            imgView.setFitWidth(80);
            imgView.setPreserveRatio(true);
            box.getChildren().add(imgView);
        } else {
            Label labelError = new Label("🏆");
            labelError.setStyle("-fx-font-size: 40;");
            box.getChildren().add(labelError);
        }
        Label lbl = new Label(nombre);
        lbl.setStyle("-fx-text-fill: white; -fx-font-size: 15; -fx-font-weight: bold;");
        box.getChildren().add(lbl);
        return box;
    }

    private VBox crearEstructuraCard(String t) {
        VBox c = new VBox(20);
        c.setStyle("-fx-background-color: #1C2028; -fx-background-radius: 20; -fx-padding: 30; -fx-border-color: #2D323E; -fx-border-radius: 20;");
        c.setOpacity(0);
        Label l = new Label(t);
        l.setStyle("-fx-text-fill: #7A808C; -fx-font-size: 13;");
        c.getChildren().add(l);
        return c;
    }

    private Label crearVsLabel() {
        Label vs = new Label("VS");
        vs.setStyle("-fx-text-fill: #E5C04A; -fx-font-weight: bold; -fx-font-size: 28;");
        return vs;
    }

    private Button crearBotonCuota(String texto, double cuota) {
        Button b = new Button();
        b.getStyleClass().add("cuota-button");
        b.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(b, Priority.ALWAYS);
        VBox v = new VBox(5);
        v.setAlignment(Pos.CENTER);
        Label lT = new Label(texto);
        lT.setStyle("-fx-text-fill: #7A808C; -fx-font-size: 11;");
        Label lC = new Label(String.format("%.2f", cuota).replace(".", ","));
        lC.setStyle("-fx-text-fill: #D32F2F; -fx-font-size: 20; -fx-font-weight: bold;");
        v.getChildren().addAll(lT, lC);
        b.setGraphic(v);
        b.setOnAction(e -> handleApuestaDinamica(texto, cuota, (VBox) b.getParent().getParent()));
        return b;
    }

    private void inyectarCard(VBox card) {
        Platform.runLater(() -> {
            if (contenedorDinamicoPartidos != null) {
                contenedorDinamicoPartidos.getChildren().add(card);
                FadeTransition ft = new FadeTransition(Duration.millis(800), card);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();
            }
        });
    }

    private String registrarAposta(String eq, double cuo, double imp) {
        String ref = "REF:" + Long.toHexString(Double.doubleToLongBits(Math.random())).substring(0, 5).toUpperCase();
        SessioUsuari.misApuestas.add("● DIRECTE | " + eq + " | Q: " + cuo + " | " + ref);
        return ref;
    }

    public void actualitzarUI() {
        Platform.runLater(() -> {
            if(lblNomUsuari != null) lblNomUsuari.setText(SessioUsuari.nom);
            if(lblSaldo != null) lblSaldo.setText(String.format("%.2f €", SessioUsuari.saldo).replace(".", ","));
            if(lblSaldoBono != null) lblSaldoBono.setText(String.format("%.2f €", SessioUsuari.saldoBono).replace(".", ","));
        });
    }

    private void mostrarAlerta(String t, String m) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION, m);
            a.setTitle(t);
            a.setHeaderText(null);
            a.show();
        });
    }

    // --- NAVEGACIÓN ---
    @FXML private void handleAnarADeposit(MouseEvent e) throws IOException { navegar(e, "DepositView.fxml"); }
    @FXML private void handleBtnMes(ActionEvent e) throws IOException { navegar(e, "DepositView.fxml"); }
    @FXML private void navHistorial(ActionEvent e) throws IOException { navegar(e, "HistorialView.fxml"); }
    @FXML private void navInici(ActionEvent e) { actualitzarUI(); }
    @FXML private void navPerfil(ActionEvent e) { }
    @FXML private void navCasino(ActionEvent e) throws IOException { navegar(e, "CasinoView.fxml"); }

    private void navegar(Object ev, String fxml) throws IOException {
        String ruta = "/com/example/vidalbet/" + fxml;
        FXMLLoader l = new FXMLLoader(getClass().getResource(ruta));
        Scene s = new Scene(l.load());
        Stage st = (Stage) ((Node) (ev instanceof ActionEvent ? ((ActionEvent) ev).getSource() : ((MouseEvent) ev).getSource())).getScene().getWindow();
        st.setScene(s);
    }
}