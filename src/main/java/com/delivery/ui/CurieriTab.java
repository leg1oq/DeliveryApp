package com.delivery.ui;

import com.delivery.dao.CurierDAO;
import com.delivery.export.ExportService;
import com.delivery.model.Curier;
import com.delivery.util.Validator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class CurieriTab extends VBox {

    private final CurierDAO dao = new CurierDAO();
    private final ExportService exportService = new ExportService();
    private final TableView<Curier> table = new TableView<>();
    private final ObservableList<Curier> data = FXCollections.observableArrayList();
    private final TextField searchField = new TextField();
    private final ComboBox<String> filterDisponibil = new ComboBox<>();

    public CurieriTab() throws Exception {
        setPadding(new Insets(16));
        setSpacing(12);
        getChildren().addAll(buildToolbar(), table);
        VBox.setVgrow(table, Priority.ALWAYS);
        buildTable();
        loadData();
    }

    private HBox buildToolbar() {
        searchField.setPromptText("Căutare după nume, vehicul...");
        searchField.setPrefWidth(240);
        searchField.textProperty().addListener((o, ov, nv) -> {
            try { loadData(); } catch (Exception e) { AlertHelper.showError("Eroare", e.getMessage()); }
        });

        filterDisponibil.setItems(FXCollections.observableArrayList("Toți", "Disponibili", "Indisponibili"));
        filterDisponibil.setValue("Toți");
        filterDisponibil.setOnAction(e -> {
            try { loadData(); } catch (Exception ex) { AlertHelper.showError("Eroare", ex.getMessage()); }
        });

        Button btnAdd = new Button("➕ Adaugă");
        Button btnEdit = new Button("✏️ Editează");
        Button btnDel = new Button("🗑 Șterge");
        Button btnExport = new Button("📤 Export CSV");

        btnAdd.setOnAction(e -> openForm(null));
        btnEdit.setOnAction(e -> {
            Curier sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { AlertHelper.showError("Atenție", "Selectați un curier."); return; }
            openForm(sel);
        });
        btnDel.setOnAction(e -> deleteSelected());
        btnExport.setOnAction(e -> exportData());

        HBox box = new HBox(8, searchField, filterDisponibil, new Separator(), btnAdd, btnEdit, btnDel, new Separator(), btnExport);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @SuppressWarnings("unchecked")
    private void buildTable() {
        TableColumn<Curier, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()));
        colId.setPrefWidth(50);

        TableColumn<Curier, String> colNume = new TableColumn<>("Nume");
        colNume.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNume()));
        colNume.setPrefWidth(160);

        TableColumn<Curier, String> colTelefon = new TableColumn<>("Telefon");
        colTelefon.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTelefon()));
        colTelefon.setPrefWidth(120);

        TableColumn<Curier, String> colVehicul = new TableColumn<>("Vehicul");
        colVehicul.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getVehicul()));
        colVehicul.setPrefWidth(200);

        TableColumn<Curier, Boolean> colDisp = new TableColumn<>("Disponibil");
        colDisp.setCellValueFactory(d -> new SimpleBooleanProperty(d.getValue().isDisponibil()));
        colDisp.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "✓ DA" : "✗ NU");
                    setStyle(item
                        ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                        : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });
        colDisp.setPrefWidth(100);

        table.getColumns().addAll(colId, colNume, colTelefon, colVehicul, colDisp);
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadData() throws Exception {
        String kw = searchField.getText();
        String filter = filterDisponibil.getValue();
        Boolean disponibil = "Disponibili".equals(filter) ? Boolean.TRUE
                           : "Indisponibili".equals(filter) ? Boolean.FALSE
                           : null;
        List<Curier> list = dao.search(kw == null ? "" : kw, disponibil);
        data.setAll(list);
    }

    private void openForm(Curier curier) {
        Dialog<Curier> dialog = new Dialog<>();
        dialog.setTitle(curier == null ? "Adaugă Curier" : "Editează Curier");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField tfNume = new TextField(curier != null ? curier.getNume() : "");
        TextField tfTelefon = new TextField(curier != null ? curier.getTelefon() : "");
        TextField tfVehicul = new TextField(curier != null ? curier.getVehicul() : "");
        CheckBox cbDisp = new CheckBox();
        cbDisp.setSelected(curier == null || curier.isDisponibil());

        grid.addRow(0, new Label("Nume*:"), tfNume);
        grid.addRow(1, new Label("Telefon*:"), tfTelefon);
        grid.addRow(2, new Label("Vehicul*:"), tfVehicul);
        grid.addRow(3, new Label("Disponibil:"), cbDisp);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            try {
                Validator.validateNotEmpty(tfNume.getText(), "Nume");
                Validator.validatePhone(tfTelefon.getText());
                Validator.validateNotEmpty(tfVehicul.getText(), "Vehicul");
                Curier c = curier != null ? curier : new Curier();
                c.setNume(tfNume.getText().trim());
                c.setTelefon(tfTelefon.getText().trim());
                c.setVehicul(tfVehicul.getText().trim());
                c.setDisponibil(cbDisp.isSelected());
                return c;
            } catch (IllegalArgumentException ex) {
                AlertHelper.showError("Validare", ex.getMessage());
                return null;
            }
        });

        dialog.showAndWait().ifPresent(c -> {
            try {
                if (curier == null) dao.insert(c); else dao.update(c);
                loadData();
            } catch (Exception ex) {
                AlertHelper.showError("Eroare BD", ex.getMessage());
            }
        });
    }

    private void deleteSelected() {
        Curier sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showError("Atenție", "Selectați un curier."); return; }
        if (!AlertHelper.showConfirm("Confirmare", "Ștergeți curierul \"" + sel.getNume() + "\"?")) return;
        try {
            dao.delete(sel.getId());
            loadData();
        } catch (Exception e) {
            AlertHelper.showError("Eroare la ștergere", e.getMessage());
        }
    }

    private void exportData() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Curieri CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fc.setInitialFileName("curieri.csv");
        File f = fc.showSaveDialog(getScene().getWindow());
        if (f == null) return;
        try {
            exportService.exportCurieriToCsv(data, f.getAbsolutePath());
            AlertHelper.showInfo("Export reușit", "Exportat în:\n" + f.getAbsolutePath());
        } catch (Exception e) {
            AlertHelper.showError("Eroare export", e.getMessage());
        }
    }
}
