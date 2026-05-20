package com.delivery.ui;

import com.delivery.dao.ComandaDAO;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.util.List;

public class RapoarteTab extends VBox {

    private final ComandaDAO dao = new ComandaDAO();

    public RapoarteTab() {
        setPadding(new Insets(16));
        setSpacing(16);

        Label title = new Label("📊 Rapoarte");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TabPane reportTabs = new TabPane();
        reportTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab t1 = new Tab("📦 Comenzi pe Status");
        t1.setContent(buildStatusReport());

        Tab t2 = new Tab("👥 Top Clienți");
        t2.setContent(buildTopClientiReport());

        Tab t3 = new Tab("🚀 Performanță Curieri");
        t3.setContent(buildCurieriReport());

        reportTabs.getTabs().addAll(t1, t2, t3);
        VBox.setVgrow(reportTabs, Priority.ALWAYS);

        getChildren().addAll(title, reportTabs);
    }

    @SuppressWarnings("unchecked")
    private VBox buildStatusReport() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(12));

        Label desc = new Label("Numărul și valoarea totală a comenzilor grupate pe status.");
        desc.setStyle("-fx-text-fill: #666;");

        TableView<Object[]> table = new TableView<>();
        TableColumn<Object[], String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty((String) d.getValue()[0]));
        colStatus.setPrefWidth(160);

        TableColumn<Object[], Long> colNr = new TableColumn<>("Nr. Comenzi");
        colNr.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>((Long) d.getValue()[1]));
        colNr.setPrefWidth(120);

        TableColumn<Object[], BigDecimal> colTotal = new TableColumn<>("Total (MDL)");
        colTotal.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>((BigDecimal) d.getValue()[2]));
        colTotal.setPrefWidth(160);

        table.getColumns().addAll(colStatus, colNr, colTotal);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnLoad = new Button("🔄 Actualizează");
        btnLoad.setOnAction(e -> {
            try {
                List<Object[]> data = dao.reportByStatus();
                table.setItems(FXCollections.observableArrayList(data));
            } catch (Exception ex) {
                AlertHelper.showError("Eroare", ex.getMessage());
            }
        });
        btnLoad.fire();

        box.getChildren().addAll(desc, btnLoad, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return box;
    }

    @SuppressWarnings("unchecked")
    private VBox buildTopClientiReport() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(12));

        Label desc = new Label("Top 10 clienți după valoarea totală a comenzilor livrate.");
        desc.setStyle("-fx-text-fill: #666;");

        TableView<Object[]> table = new TableView<>();

        TableColumn<Object[], String> colNume = new TableColumn<>("Client");
        colNume.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty((String) d.getValue()[0]));
        colNume.setPrefWidth(180);

        TableColumn<Object[], Long> colNr = new TableColumn<>("Nr. Comenzi");
        colNr.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>((Long) d.getValue()[1]));
        colNr.setPrefWidth(120);

        TableColumn<Object[], BigDecimal> colTotal = new TableColumn<>("Total Cheltuit (MDL)");
        colTotal.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>((BigDecimal) d.getValue()[2]));
        colTotal.setPrefWidth(180);

        table.getColumns().addAll(colNume, colNr, colTotal);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnLoad = new Button("🔄 Actualizează");
        btnLoad.setOnAction(e -> {
            try {
                List<Object[]> data = dao.reportTopClienti();
                table.setItems(FXCollections.observableArrayList(data));
            } catch (Exception ex) {
                AlertHelper.showError("Eroare", ex.getMessage());
            }
        });
        btnLoad.fire();

        box.getChildren().addAll(desc, btnLoad, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return box;
    }

    @SuppressWarnings("unchecked")
    private VBox buildCurieriReport() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(12));

        Label desc = new Label("Performanța fiecărui curier: număr de livrări și valoare totală livrată.");
        desc.setStyle("-fx-text-fill: #666;");

        TableView<Object[]> table = new TableView<>();

        TableColumn<Object[], String> colNume = new TableColumn<>("Curier");
        colNume.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty((String) d.getValue()[0]));
        colNume.setPrefWidth(160);

        TableColumn<Object[], String> colVehicul = new TableColumn<>("Vehicul");
        colVehicul.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty((String) d.getValue()[1]));
        colVehicul.setPrefWidth(180);

        TableColumn<Object[], Long> colNr = new TableColumn<>("Nr. Livrări");
        colNr.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>((Long) d.getValue()[2]));
        colNr.setPrefWidth(100);

        TableColumn<Object[], BigDecimal> colTotal = new TableColumn<>("Valoare Livrat (MDL)");
        colTotal.setCellValueFactory(d -> new javafx.beans.property.SimpleObjectProperty<>((BigDecimal) d.getValue()[3]));
        colTotal.setPrefWidth(160);

        table.getColumns().addAll(colNume, colVehicul, colNr, colTotal);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button btnLoad = new Button("🔄 Actualizează");
        btnLoad.setOnAction(e -> {
            try {
                List<Object[]> data = dao.reportCurieri();
                table.setItems(FXCollections.observableArrayList(data));
            } catch (Exception ex) {
                AlertHelper.showError("Eroare", ex.getMessage());
            }
        });
        btnLoad.fire();

        box.getChildren().addAll(desc, btnLoad, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        return box;
    }
}
