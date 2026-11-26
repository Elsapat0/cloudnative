package com.pedidos360.productos.kafka;

import com.pedidos360.productos.dto.AnaliticaEventoDTO;
import com.pedidos360.productos.dto.PedidoEventoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProductoKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductoKafkaConsumer.class);
    private final ProductoKafkaProducer producer;

    public ProductoKafkaConsumer(ProductoKafkaProducer producer) {
        this.producer = producer;
    }

    @KafkaListener(topics = "pedidos-eventos", groupId = "productos-group")
    public void consumirEventoPedido(PedidoEventoDTO evento) {
        log.info("ms-productos recibió evento de pedido #{} con estado: {}", evento.getPedidoId(), evento.getEstado());

        producer.publicarEventoAnalitica(new AnaliticaEventoDTO(
                "ms-productos",
                "INVENTARIO_VERIFICADO",
                "Stock verificado para pedido #" + evento.getPedidoId(),
                evento.getTotal(),
                LocalDateTime.now()
        ));
    }
}