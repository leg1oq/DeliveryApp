package com.delivery.ui;

import com.delivery.dao.ClientDAO;
import com.delivery.dao.ComandaDAO;
import com.delivery.dao.CurierDAO;
import com.delivery.export.ExportService;
import com.delivery.model.*;
import com.delivery.util.Validator;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ComenziTab extends VBox {

    private final ComandaDAO dao = new ComandaDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final CurierDAO curierDAO = new CurierDAO();
    private final ExportService exportService = new ExportService();
    private final TableView<Comanda> table = new TableView<>();
    private final ObservableList<Comanda> data = FXCollections.observableArrayList();
    private final TextField searchField = new TextField();
    private final ComboBox<String> filterStatus = new ComboBox<>();

    public ComenziTab() throws Exception {
        setPadding(new Insets(16));
        setSpacing(12);
        getChildren().addAll(buildToolbar(), table);
        VBox.setVgrow(table, Priority.ALWAYS);
        buildTable();
        loadData();
    }

    private HBox buildToolbar() {
        searchField.setPromptText("Căutare după client, adresă...");
        searchField.setPrefWidth(240);
        searchField.textProperty().addListener((o, ov, nv) -> {
            try { loadData(); } catch (Exception e) { AlertHelper.showError("Eroare", e.getMessage()); }
        });

        filterStatus.setItems(FXCollections.observableArrayList("Toate", "NOU", "IN_PREGATIRE", "IN_LIVRARE", "LIVRAT", "ANULAT"));
        filterStatus.setValue("Toate");
        filterStatus.setOnAction(e -> {
            try { loadData(); } catch (Exception ex) { AlertHelper.showError("Eroare", ex.getMessage()); }
        });

        Button btnAdd = new Button("➕ Adaugă");
        Button btnEdit = new Button("✏️ Editează");
        Button btnDel = new Button("🗑 Șterge");
        Button btnExportCsv = new Button("📤 CSV");
        Button btnExportTxt = new Button("📄 TXT");

        btnAdd.setOnAction(e -> openForm(null));
        btnEdit.setOnAction(e -> {
            Comanda sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { AlertHelper.showError("Atenție", "Selectați o comandă."); return; }
            openForm(sel);
        });
        btnDel.setOnAction(e -> deleteSelected());
        btnExportCsv.setOnAction(e -> exportData(true));
        btnExportTxt.setOnAction(e -> exportData(false));

        HBox box = new HBox(8, searchField, filterStatus, new Separator(), btnAdd, btnEdit, btnDel, new Separator(), btnExportCsv, btnExportTxt);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @SuppressWarnings("unchecked")
    private void buildTable() {
        TableColumn<Comanda, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()));
        colId.setPrefWidth(50);

        TableColumn<Comanda, String> colClient = new TableColumn<>("Client");
        colClient.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getClientNume()));
        colClient.setPrefWidth(160);

        TableColumn<Comanda, String> colCurier = new TableColumn<>("Curier");
        colCurier.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCurierNume()));
        colCurier.setPrefWidth(140);

        TableColumn<Comanda, String> colAdresa = new TableColumn<>("Adresă livrare");
        colAdresa.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAdresaLivrare()));
        colAdresa.setPrefWidth(200);

        TableColumn<Comanda, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStatus().getDbValue()));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "NOU"          -> setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
                    case "IN_PREGATIRE" -> setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                    case "IN_LIVRARE"   -> setStyle("-fx-text-fill: #8e44ad; -fx-font-weight: bold;");
                    case "LIVRAT"       -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    case "ANULAT"       -> setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    default             -> setStyle("");
                }
            }
        });
        colStatus.setPrefWidth(110);

        TableColumn<Comanda, BigDecimal> colTotal = new TableColumn<>("Total (MDL)");
        colTotal.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getTotal()));
        colTotal.setPrefWidth(100);

        TableColumn<Comanda, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getDataComanda() != null ? d.getValue().getDataComanda().toString().replace("T", " ") : ""));
        colData.setPrefWidth(150);

        table.getColumns().addAll(colId, colClient, colCurier, colAdresa, colStatus, colTotal, colData);
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadData() throws Exception {
        String kw = searchField.getText();
        String statusStr = filterStatus.getValue();
        StatusComanda status = "Toate".equals(statusStr) ? null : StatusComanda.fromString(statusStr);
        List<Comanda> list = dao.search(kw == null ? "" : kw, status);
        data.setAll(list);
    }

    private void openForm(Comanda comanda) {
        try {
            List<Client> clienti = clientDAO.findAll();
            List<Curier> curieri = curierDAO.findAll();

            Dialog<Comanda> dialog = new Dialog<>();
            dialog.setTitle(comanda == null ? "Adaugă Comandă" : "Editează Comandă");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            dialog.getDialogPane().setPrefWidth(460);

            GridPane grid = new GridPane();
            grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

            ComboBox<Client> cbClient = new ComboBox<>(FXCollections.observableArrayList(clienti));
            ComboBox<Curier> cbCurier = new ComboBox<>(FXCollections.observableArrayList(curieri));
            TextField tfAdresa = new TextField(comanda != null ? comanda.getAdresaLivrare() : "");
            ComboBox<StatusComanda> cbStatus = new ComboBox<>(FXCollections.observableArrayList(StatusComanda.values()));
            cbStatus.setValue(comanda != null ? comanda.getStatus() : StatusComanda.NOU);
            TextField tfTotal = new TextField(comanda != null ? comanda.getTotal().toPlainString() : "0");
            TextArea taObs = new TextArea(comanda != null && comanda.getObservatii() != null ? comanda.getObservatii() : "");
            taObs.setPrefRowCount(2);

            if (comanda != null) {
                clienti.stream().filter(c -> c.getId() == comanda.getClientId()).findFirst().ifPresent(cbClient::setValue);
                curieri.stream().filter(c -> c.getId() == comanda.getCurierId()).findFirst().ifPresent(cbCurier::setValue);
            }

            grid.addRow(0, new Label("Client*:"), cbClient);
            grid.addRow(1, new Label("Curier*:"), cbCurier);
            grid.addRow(2, new Label("Adresă livrare*:"), tfAdresa);
            grid.addRow(3, new Label("Status*:"), cbStatus);
            grid.addRow(4, new Label("Total (MDL)*:"), tfTotal);
            grid.addRow(5, new Label("Observații:"), taObs);

            dialog.getDialogPane().setContent(grid);
            dialog.setResultConverter(btn -> {
                if (btn != ButtonType.OK) return null;
                try {
                    if (cbClient.getValue() == null) throw new IllegalArgumentException("Selectați un client.");
                    if (cbCurier.getValue() == null) throw new IllegalArgumentException("Selectați un curier.");
                    Validator.validateNotEmpty(tfAdresa.getText(), "Adresa livrare");
                    double totalVal = Double.parseDouble(tfTotal.getText().trim());
                    Validator.validatePositiveNumber(totalVal, "Total");

                    Comanda c = comanda != null ? comanda : new Comanda();
                    c.setClientId(cbClient.getValue().getId());
                    c.setCurierId(cbCurier.getValue().getId());
                    c.setAdresaLivrare(tfAdresa.getText().trim());
                    c.setStatus(cbStatus.getValue());
                    c.setTotal(BigDecimal.valueOf(totalVal));
                    c.setDataComanda(comanda != null ? comanda.getDataComanda() : LocalDateTime.now());
                    c.setObservatii(taObs.getText().trim().isEmpty() ? null : taObs.getText().trim());
                    return c;
                } catch (NumberFormatException ex) {
                    AlertHelper.showError("Validare", "Totalul trebuie să fie un număr valid.");
                    return null;
                } catch (IllegalArgumentException ex) {
                    AlertHelper.showError("Validare", ex.getMessage());
                    return null;
                }
            });

            dialog.showAndWait().ifPresent(c -> {
                try {
                    if (comanda == null) dao.insert(c); else dao.update(c);
                    loadData();
                } catch (Exception ex) {
                    AlertHelper.showError("Eroare BD", ex.getMessage());
                }
            });
        } catch (Exception e) {
            AlertHelper.showError("Eroare", e.getMessage());
        }
    }

    private void deleteSelected() {
        Comanda sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showError("Atenție", "Selectați o comandă."); return; }
        if (!AlertHelper.showConfirm("Confirmare", "Ștergeți comanda #" + sel.getId() + "?")) return;
        try {
            dao.delete(sel.getId());
            loadData();
        } catch (Exception e) {
            AlertHelper.showError("Eroare la ștergere", e.getMessage());
        }
    }

    private void exportData(boolean csv) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Comenzi");
        if (csv) {
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
            fc.setInitialFileName("comenzi.csv");
        } else {
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT", "*.txt"));
            fc.setInitialFileName("comenzi.txt");
        }
        File f = fc.showSaveDialog(getScene().getWindow());
        if (f == null) return;
        try {
            if (csv) exportService.exportToCsv(data, f.getAbsolutePath());
            else exportService.exportToTxt(data, f.getAbsolutePath());
            AlertHelper.showInfo("Export reușit", "Exportat în:\n" + f.getAbsolutePath());
        } catch (Exception e) {
            AlertHelper.showError("Eroare export", e.getMessage());
        }
    }
}
