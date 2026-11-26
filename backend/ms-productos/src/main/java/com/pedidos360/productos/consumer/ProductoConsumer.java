package com.pedidos360.productos.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ProductoConsumer {

    @RabbitListener(queues = "${app.rabbitmq.queue:productos.queue}")
    public void recibirMensaje(String mensaje) {
        System.out.println(">>> [RabbitMQ] Mensaje recibido en ms-productos: " + mensaje);

        // Simulación de procesamiento de mensaje
        if (mensaje.contains("error")) {
            throw new RuntimeException("Error al procesar mensaje. Redirigiendo a productos.dlq...");
        }
    }
}
