package com.delivery.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Comanda extends Entity {
    private int clientId;
    private int curierId;
    private String adresaLivrare;
    private StatusComanda status;
    private BigDecimal total;
    private LocalDateTime dataComanda;
    private String observatii;

    private String clientNume;
    private String curierNume;

    public Comanda() {}
    public Comanda(int id, int clientId, int curierId, String adresaLivrare,
                   StatusComanda status, BigDecimal total, LocalDateTime dataComanda, String observatii) {
        super(id);
        this.clientId = clientId;
        this.curierId = curierId;
        this.adresaLivrare = adresaLivrare;
        this.status = status;
        this.total = total;
        this.dataComanda = dataComanda;
        this.observatii = observatii;
    }

    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    public int getCurierId() { return curierId; }
    public void setCurierId(int curierId) { this.curierId = curierId; }
    public String getAdresaLivrare() { return adresaLivrare; }
    public void setAdresaLivrare(String adresaLivrare) { this.adresaLivrare = adresaLivrare; }
    public StatusComanda getStatus() { return status; }
    public void setStatus(StatusComanda status) { this.status = status; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public LocalDateTime getDataComanda() { return dataComanda; }
    public void setDataComanda(LocalDateTime dataComanda) { this.dataComanda = dataComanda; }
    public String getObservatii() { return observatii; }
    public void setObservatii(String observatii) { this.observatii = observatii; }
    public String getClientNume() { return clientNume; }
    public void setClientNume(String clientNume) { this.clientNume = clientNume; }
    public String getCurierNume() { return curierNume; }
    public void setCurierNume(String curierNume) { this.curierNume = curierNume; }

    @Override
    public String toDisplayString() {
        return "[" + id + "] " + (clientNume != null ? clientNume : "Client #" + clientId)
               + " → " + adresaLivrare + " [" + status + "]";
    }
}
