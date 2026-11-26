package com.pedidos360.pedidos.kafka;

import com.pedidos360.pedidos.dto.AnaliticaEventoDTO;
import com.pedidos360.pedidos.dto.PedidoEventoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PedidoKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(PedidoKafkaProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PedidoKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarEventoPedido(PedidoEventoDTO evento) {
        if (evento == null) {
            log.warn("Se intentó publicar un PedidoEventoDTO nulo en [pedidos-eventos]");
            return;
        }
        String key = evento.getPedidoId() != null ? String.valueOf(evento.getPedidoId()) : "sin-id";
        kafkaTemplate.send("pedidos-eventos", key, evento)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error al publicar en [pedidos-eventos] con key={}: {}", key, ex.getMessage(), ex);
                    } else {
                        log.info("Evento publicado en [pedidos-eventos] con key={}: {}", key, evento);
                    }
                });
    }

    public void publicarEventoAnalitica(AnaliticaEventoDTO evento) {
        if (evento == null) {
            log.warn("Se intentó publicar un AnaliticaEventoDTO nulo en [analitica-eventos]");
            return;
        }
        String key = (evento.getOrigen() != null && !evento.getOrigen().isBlank())
                ? evento.getOrigen()
                : "ms-pedidos";
        kafkaTemplate.send("analitica-eventos", key, evento)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error al publicar en [analitica-eventos] con key={}: {}", key, ex.getMessage(), ex);
                    } else {
                        log.info("Evento publicado en [analitica-eventos] con key={}: {}", key, evento);
                    }
                });
    }
}