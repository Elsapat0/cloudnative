package com.pedidos360.pedidos.dto;

import java.time.LocalDateTime;

public class AnaliticaEventoDTO {
    private String origen;
    private String tipoEvento;
    private String detalle;
    private Double monto;
    private LocalDateTime timestamp;

    public AnaliticaEventoDTO() {}

    public AnaliticaEventoDTO(String origen, String tipoEvento, String detalle, Double monto, LocalDateTime timestamp) {
        this.origen = origen;
        this.tipoEvento = tipoEvento;
        this.detalle = detalle;
        this.monto = monto;
        this.timestamp = timestamp;
    }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "AnaliticaEventoDTO{" +
                "origen='" + origen + '\'' +
                ", tipoEvento='" + tipoEvento + '\'' +
                ", detalle='" + detalle + '\'' +
                ", monto=" + monto +
                ", timestamp=" + timestamp +
                '}';
    }
}