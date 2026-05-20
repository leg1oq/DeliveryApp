package com.delivery;

import com.delivery.ui.AlertHelper;
import com.delivery.ui.ClientiTab;
import com.delivery.ui.ComenziTab;
import com.delivery.ui.CurieriTab;
import com.delivery.ui.RapoarteTab;
import com.delivery.util.DatabaseConnection;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseConnection.getInstance();
        } catch (Exception e) {
            AlertHelper.showError("Eroare inițializare BD",
                "Nu s-a putut inițializa baza de date SQLite!\n\n" + e.getMessage());
        }

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        try {
            Tab tabClienti = new Tab("👥 Clienți");
            tabClienti.setContent(new ClientiTab());

            Tab tabCurieri = new Tab("🚴 Curieri");
            tabCurieri.setContent(new CurieriTab());

            Tab tabComenzi = new Tab("📦 Comenzi");
            tabComenzi.setContent(new ComenziTab());

            Tab tabRapoarte = new Tab("📊 Rapoarte");
            tabRapoarte.setContent(new RapoarteTab());

            tabPane.getTabs().addAll(tabClienti, tabCurieri, tabComenzi, tabRapoarte);
        } catch (Exception e) {
            AlertHelper.showError("Eroare inițializare", e.getMessage());
        }

        VBox root = new VBox();
        Label header = new Label("🍕 Sistem de Livrare — Gestionare Comenzi  [SQLite]");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 12 16 8 16; -fx-text-fill: #2c3e50;");
        Separator sep = new Separator();
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        root.getChildren().addAll(header, sep, tabPane);
        root.setStyle("-fx-background-color: #f8f9fa;");

        Scene scene = new Scene(root, 1100, 680);
        primaryStage.setTitle("Sistem Livrare — SQLite");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(580);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
