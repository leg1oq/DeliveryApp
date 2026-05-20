package com.delivery.model;

public abstract class Persoana extends Entity {
    protected String nume;
    protected String telefon;

    public Persoana() {}
    public Persoana(int id, String nume, String telefon) {
        super(id);
        this.nume = nume;
        this.telefon = telefon;
    }

    public String getNume() { return nume; }
    public void setNume(String nume) { this.nume = nume; }
    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public abstract String getRol();
}
