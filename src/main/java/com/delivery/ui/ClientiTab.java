package com.delivery.ui;

import com.delivery.dao.ClientDAO;
import com.delivery.export.ExportService;
import com.delivery.model.Client;
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
import java.time.LocalDate;
import java.util.List;

public class ClientiTab extends VBox {

    private final ClientDAO dao = new ClientDAO();
    private final ExportService exportService = new ExportService();
    private final TableView<Client> table = new TableView<>();
    private final ObservableList<Client> data = FXCollections.observableArrayList();
    private final TextField searchField = new TextField();

    public ClientiTab() throws Exception {
        setPadding(new Insets(16));
        setSpacing(12);
        buildTable();
        HBox toolbar = buildToolbar();
        loadData("");
        getChildren().addAll(toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private HBox buildToolbar() {
        searchField.setPromptText("Căutare după nume, email, telefon...");
        searchField.setPrefWidth(280);
        searchField.textProperty().addListener((o, ov, nv) -> {
            try { loadData(nv); } catch (Exception e) { AlertHelper.showError("Eroare", e.getMessage()); }
        });

        Button btnAdd = new Button("➕ Adaugă");
        Button btnEdit = new Button("✏️ Editează");
        Button btnDel = new Button("🗑 Șterge");
        Button btnExport = new Button("📤 Export CSV");

        btnAdd.setOnAction(e -> openForm(null));
        btnEdit.setOnAction(e -> {
            Client sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { AlertHelper.showError("Atenție", "Selectați un client."); return; }
            openForm(sel);
        });
        btnDel.setOnAction(e -> deleteSelected());
        btnExport.setOnAction(e -> exportData());

        HBox box = new HBox(8, searchField, new Separator(), btnAdd, btnEdit, btnDel, new Separator(), btnExport);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    @SuppressWarnings("unchecked")
    private void buildTable() {
        TableColumn<Client, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getId()));
        colId.setPrefWidth(50);

        TableColumn<Client, String> colNume = new TableColumn<>("Nume");
        colNume.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNume()));
        colNume.setPrefWidth(160);

        TableColumn<Client, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        colEmail.setPrefWidth(200);

        TableColumn<Client, String> colTelefon = new TableColumn<>("Telefon");
        colTelefon.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTelefon()));
        colTelefon.setPrefWidth(120);

        TableColumn<Client, String> colAdresa = new TableColumn<>("Adresă");
        colAdresa.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAdresa() != null ? d.getValue().getAdresa() : ""));
        colAdresa.setPrefWidth(220);

        TableColumn<Client, LocalDate> colData = new TableColumn<>("Înregistrat");
        colData.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getDataInregistrare()));
        colData.setPrefWidth(110);

        table.getColumns().addAll(colId, colNume, colEmail, colTelefon, colAdresa, colData);
        table.setItems(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadData(String keyword) throws Exception {
        List<Client> list = (keyword == null || keyword.isBlank()) ? dao.findAll() : dao.search(keyword);
        data.setAll(list);
    }

    private void openForm(Client client) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle(client == null ? "Adaugă Client" : "Editează Client");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20));

        TextField tfNume = new TextField(client != null ? client.getNume() : "");
        TextField tfEmail = new TextField(client != null ? client.getEmail() : "");
        TextField tfTelefon = new TextField(client != null ? client.getTelefon() : "");
        TextField tfAdresa = new TextField(client != null && client.getAdresa() != null ? client.getAdresa() : "");
        DatePicker dpData = new DatePicker(client != null ? client.getDataInregistrare() : LocalDate.now());

        grid.addRow(0, new Label("Nume*:"), tfNume);
        grid.addRow(1, new Label("Email*:"), tfEmail);
        grid.addRow(2, new Label("Telefon*:"), tfTelefon);
        grid.addRow(3, new Label("Adresă:"), tfAdresa);
        grid.addRow(4, new Label("Data înreg.*:"), dpData);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn != ButtonType.OK) return null;
            try {
                Validator.validateNotEmpty(tfNume.getText(), "Nume");
                Validator.validateEmail(tfEmail.getText());
                Validator.validatePhone(tfTelefon.getText());
                if (dpData.getValue() == null) throw new IllegalArgumentException("Data înregistrării este obligatorie.");
                Client c = client != null ? client : new Client();
                c.setNume(tfNume.getText().trim());
                c.setEmail(tfEmail.getText().trim());
                c.setTelefon(tfTelefon.getText().trim());
                c.setAdresa(tfAdresa.getText().trim());
                c.setDataInregistrare(dpData.getValue());
                return c;
            } catch (IllegalArgumentException ex) {
                AlertHelper.showError("Validare", ex.getMessage());
                return null;
            }
        });

        dialog.showAndWait().ifPresent(c -> {
            try {
                if (client == null) dao.insert(c); else dao.update(c);
                loadData(searchField.getText());
            } catch (Exception ex) {
                AlertHelper.showError("Eroare BD", ex.getMessage());
            }
        });
    }

    private void deleteSelected() {
        Client sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showError("Atenție", "Selectați un client."); return; }
        if (!AlertHelper.showConfirm("Confirmare", "Ștergeți clientul \"" + sel.getNume() + "\"?\nAtenție: comenzile asociate vor fi șterse!")) return;
        try {
            dao.delete(sel.getId());
            loadData(searchField.getText());
        } catch (Exception e) {
            AlertHelper.showError("Eroare la ștergere", e.getMessage());
        }
    }

    private void exportData() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Export Clienți CSV");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        fc.setInitialFileName("clienti.csv");
        File f = fc.showSaveDialog(getScene().getWindow());
        if (f == null) return;
        try {
            exportService.exportClientiToCsv(data, f.getAbsolutePath());
            AlertHelper.showInfo("Export reușit", "Datele au fost exportate în:\n" + f.getAbsolutePath());
        } catch (Exception e) {
            AlertHelper.showError("Eroare export", e.getMessage());
        }
    }
}
