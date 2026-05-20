package com.delivery.model;

public class Curier extends Persoana {
    private String vehicul;
    private boolean disponibil;

    public Curier() {}
    public Curier(int id, String nume, String telefon, String vehicul, boolean disponibil) {
        super(id, nume, telefon);
        this.vehicul = vehicul;
        this.disponibil = disponibil;
    }

    public String getVehicul() { return vehicul; }
    public void setVehicul(String vehicul) { this.vehicul = vehicul; }
    public boolean isDisponibil() { return disponibil; }
    public void setDisponibil(boolean disponibil) { this.disponibil = disponibil; }

    @Override
    public String getRol() { return "Curier"; }

    @Override
    public String toDisplayString() {
        return "[" + id + "] " + nume + " - " + vehicul + (disponibil ? " ✓" : " ✗");
    }
}
