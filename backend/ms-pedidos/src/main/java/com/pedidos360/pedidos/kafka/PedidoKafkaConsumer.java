package com.pedidos360.pedidos.kafka;

import com.pedidos360.pedidos.dto.AnaliticaEventoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(PedidoKafkaConsumer.class);

    @KafkaListener(topics = "analitica-eventos", groupId = "pedidos-analitica-group")
    public void consumirAnalitica(AnaliticaEventoDTO evento) {
        if (evento == null || evento.getTipoEvento() == null || evento.getTipoEvento().isBlank()) {
            log.warn("Payload inválido recibido en analitica-eventos: {}", evento);
            return;
        }
        log.info("Procesando analítica en ms-pedidos: origen={}, tipo={}, monto={}",
                evento.getOrigen(), evento.getTipoEvento(), evento.getMonto());
    }
}