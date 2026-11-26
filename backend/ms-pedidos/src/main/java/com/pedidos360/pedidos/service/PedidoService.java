package com.pedidos360.pedidos.service;

import com.pedidos360.pedidos.dto.AnaliticaEventoDTO;
import com.pedidos360.pedidos.dto.PedidoEventoDTO;
import com.pedidos360.pedidos.kafka.PedidoKafkaProducer;
import com.pedidos360.pedidos.model.Pedido;
import com.pedidos360.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository repository;
    private final PedidoKafkaProducer kafkaProducer;

    public PedidoService(PedidoRepository repository, PedidoKafkaProducer kafkaProducer) {
        this.repository = repository;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional(readOnly = true)
    public List<Pedido> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Pedido> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Pedido> findByClienteId(Long clienteId) {
        return repository.findByClienteId(clienteId);
    }

    @Transactional
    public Pedido save(Pedido pedido) {
        if (pedido.getFechaCreacion() == null) {
            pedido.setFechaCreacion(LocalDateTime.now());
        }
        if (pedido.getEstado() == null || pedido.getEstado().isBlank()) {
            pedido.setEstado("PENDIENTE");
        }
        calcularTotal(pedido);
        Pedido guardado = repository.save(pedido);

        Double totalDouble = guardado.getTotal() != null ? guardado.getTotal().doubleValue() : 0.0;
        LocalDateTime ahora = LocalDateTime.now();

        kafkaProducer.publicarEventoPedido(new PedidoEventoDTO(
                guardado.getId(),
                guardado.getClienteId(),
                totalDouble,
                guardado.getEstado(),
                guardado.getFechaCreacion()
        ));

        kafkaProducer.publicarEventoAnalitica(new AnaliticaEventoDTO(
                "ms-pedidos",
                "PEDIDO_CREADO",
                "Pedido #" + guardado.getId() + " creado para cliente " + guardado.getClienteId(),
                totalDouble,
                ahora
        ));

        return guardado;
    }

    @Transactional
    public Optional<Pedido> updateEstado(Long id, String estado) {
        return repository.findById(id).map(existing -> {
            existing.setEstado(estado);
            Pedido actualizado = repository.save(existing);

            Double totalDouble = actualizado.getTotal() != null ? actualizado.getTotal().doubleValue() : 0.0;
            LocalDateTime ahora = LocalDateTime.now();

            kafkaProducer.publicarEventoPedido(new PedidoEventoDTO(
                    actualizado.getId(),
                    actualizado.getClienteId(),
                    totalDouble,
                    actualizado.getEstado(),
                    ahora
            ));

            kafkaProducer.publicarEventoAnalitica(new AnaliticaEventoDTO(
                    "ms-pedidos",
                    "PEDIDO_ESTADO_ACTUALIZADO",
                    "Pedido #" + actualizado.getId() + " cambió a estado " + actualizado.getEstado(),
                    totalDouble,
                    ahora
            ));

            return actualizado;
        });
    }

    @Transactional
    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    private void calcularTotal(Pedido pedido) {
        BigDecimal total = Optional.ofNullable(pedido.getDetalles())
                .orElse(List.of())
                .stream()
                .map(d -> {
                    BigDecimal precio = d.getPrecioUnitario() != null ? d.getPrecioUnitario() : BigDecimal.ZERO;
                    int cantidad = d.getCantidad() != null ? d.getCantidad() : 0;
                    return precio.multiply(BigDecimal.valueOf(cantidad));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setTotal(total);
    }
}