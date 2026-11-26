package com.pedidos360.productos.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.queue:productos.queue}")
    private String queueName;

    @Value("${app.rabbitmq.dlq:productos.dlq}")
    private String dlqName;

    @Value("${app.rabbitmq.exchange:productos.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.dlx:productos.dlx}")
    private String dlxName;

    @Value("${app.rabbitmq.routing-key:productos.routingkey}")
    private String routingKey;

    @Value("${app.rabbitmq.dlq-routing-key:productos.dlq.routingkey}")
    private String dlqRoutingKey;

    @Bean
    public Queue mainQueue() {
        return QueueBuilder.durable(queueName)
                .withArgument("x-dead-letter-exchange", dlxName)
                .withArgument("x-dead-letter-routing-key", dlqRoutingKey)
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(dlqName).build();
    }

    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlxName);
    }

    @Bean
    public Binding mainBinding() {
        return BindingBuilder.bind(mainQueue()).to(mainExchange()).with(routingKey);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(dlqRoutingKey);
    }
}