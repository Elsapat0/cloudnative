package com.pedidos360.clientes.kafka;

import com.pedidos360.clientes.dto.AnaliticaEventoDTO;
import com.pedidos360.clientes.dto.PedidoEventoDTO;
import com.pedidos360.clientes.repository.ClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ClienteKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(ClienteKafkaConsumer.class);
    private final ClienteRepository clienteRepository;
    private final ClienteKafkaProducer producer;

    public ClienteKafkaConsumer(ClienteRepository clienteRepository, ClienteKafkaProducer producer) {
        this.clienteRepository = clienteRepository;
        this.producer = producer;
    }

    @KafkaListener(topics = "pedidos-eventos", groupId = "clientes-group")
    public void consumirEventoPedido(PedidoEventoDTO evento) {
        if (evento == null || evento.getClienteId() == null) {
            throw new IllegalArgumentException("Payload inválido en pedidos-eventos para ms-clientes");
        }

        boolean existe = clienteRepository.existsById(evento.getClienteId());
        log.info("ms-clientes procesó pedido #{} para cliente {} (Existe en BD: {}) - Estado: {}",
                evento.getPedidoId(), evento.getClienteId(), existe, evento.getEstado());

        producer.publicarEventoAnalitica(new AnaliticaEventoDTO(
                "ms-clientes",
                "ACTIVIDAD_CLIENTE_PEDIDO",
                "Cliente #" + evento.getClienteId() + " asociado a pedido #" + evento.getPedidoId(),
                evento.getTotal(),
                LocalDateTime.now()
        ));
    }
}