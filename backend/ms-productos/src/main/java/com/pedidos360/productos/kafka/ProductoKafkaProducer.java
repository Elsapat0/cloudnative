package com.pedidos360.productos.kafka;

import com.pedidos360.productos.dto.AnaliticaEventoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductoKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(ProductoKafkaProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProductoKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarEventoAnalitica(AnaliticaEventoDTO evento) {
        kafkaTemplate.send("analitica-eventos", evento.getOrigen(), evento);
        log.info("Evento publicado en [analitica-eventos] desde ms-productos: {}", evento);
    }
}