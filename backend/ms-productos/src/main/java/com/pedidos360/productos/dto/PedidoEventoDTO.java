package com.pedidos360.productos.dto;

import java.time.LocalDateTime;

public class PedidoEventoDTO {
    private Long pedidoId;
    private Long clienteId;
    private Double total;
    private String estado;
    private LocalDateTime fecha;

    public PedidoEventoDTO() {}

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}