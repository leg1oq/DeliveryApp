package com.delivery.model;

import java.time.LocalDate;

public class Client extends Persoana {
    private String email;
    private String adresa;
    private LocalDate dataInregistrare;

    public Client() {}
    public Client(int id, String nume, String email, String telefon, String adresa, LocalDate dataInregistrare) {
        super(id, nume, telefon);
        this.email = email;
        this.adresa = adresa;
        this.dataInregistrare = dataInregistrare;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAdresa() { return adresa; }
    public void setAdresa(String adresa) { this.adresa = adresa; }
    public LocalDate getDataInregistrare() { return dataInregistrare; }
    public void setDataInregistrare(LocalDate dataInregistrare) { this.dataInregistrare = dataInregistrare; }

    @Override
    public String getRol() { return "Client"; }

    @Override
    public String toDisplayString() {
        return "[" + id + "] " + nume + " (" + email + ")";
    }
}
