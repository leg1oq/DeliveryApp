package com.delivery.export;

import com.delivery.model.Client;
import com.delivery.model.Comanda;
import com.delivery.model.Curier;
import com.delivery.util.Exportable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportService implements Exportable<Comanda> {

    @Override
    public void exportToCsv(List<Comanda> items, String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("ID,Client,Curier,Adresa Livrare,Status,Total,Data Comanda,Observatii");
            bw.newLine();
            for (Comanda c : items) {
                bw.write(String.join(",",
                    String.valueOf(c.getId()),
                    csvEscape(c.getClientNume()),
                    csvEscape(c.getCurierNume()),
                    csvEscape(c.getAdresaLivrare()),
                    c.getStatus().getDbValue(),
                    c.getTotal().toPlainString(),
                    c.getDataComanda().toString(),
                    csvEscape(c.getObservatii() != null ? c.getObservatii() : "")
                ));
                bw.newLine();
            }
        }
    }

    @Override
    public void exportToTxt(List<Comanda> items, String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("=== RAPORT COMENZI ===");
            bw.newLine();
            bw.write(String.format("Total inregistrari: %d%n", items.size()));
            bw.newLine();
            for (Comanda c : items) {
                bw.write("------------------------------------------");
                bw.newLine();
                bw.write(String.format("ID: %d | Status: %s | Total: %.2f MDL%n", c.getId(), c.getStatus(), c.getTotal()));
                bw.write(String.format("Client: %s | Curier: %s%n", c.getClientNume(), c.getCurierNume()));
                bw.write(String.format("Adresa: %s%n", c.getAdresaLivrare()));
                bw.write(String.format("Data: %s%n", c.getDataComanda()));
                if (c.getObservatii() != null && !c.getObservatii().isEmpty()) {
                    bw.write(String.format("Observatii: %s%n", c.getObservatii()));
                }
            }
        }
    }

    public void exportClientiToCsv(List<Client> items, String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("ID,Nume,Email,Telefon,Adresa,Data Inregistrare");
            bw.newLine();
            for (Client c : items) {
                bw.write(String.join(",",
                    String.valueOf(c.getId()),
                    csvEscape(c.getNume()),
                    csvEscape(c.getEmail()),
                    c.getTelefon(),
                    csvEscape(c.getAdresa() != null ? c.getAdresa() : ""),
                    c.getDataInregistrare().toString()
                ));
                bw.newLine();
            }
        }
    }

    public void exportCurieriToCsv(List<Curier> items, String filePath) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("ID,Nume,Telefon,Vehicul,Disponibil");
            bw.newLine();
            for (Curier c : items) {
                bw.write(String.join(",",
                    String.valueOf(c.getId()),
                    csvEscape(c.getNume()),
                    c.getTelefon(),
                    csvEscape(c.getVehicul()),
                    c.isDisponibil() ? "DA" : "NU"
                ));
                bw.newLine();
            }
        }
    }

    private String csvEscape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
