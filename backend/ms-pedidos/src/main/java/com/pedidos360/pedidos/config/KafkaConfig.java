package com.pedidos360.pedidos.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    @Bean
    public NewTopic topicoPedidos() {
        return TopicBuilder.name("pedidos-eventos")
                .partitions(3)
                .replicas(1)
                .config("retention.ms", "604800000")
                .config("cleanup.policy", "delete")
                .build();
    }

    @Bean
    public NewTopic topicoAnalitica() {
        return TopicBuilder.name("analitica-eventos")
                .partitions(2)
                .replicas(1)
                .config("retention.ms", "2592000000")
                .config("cleanup.policy", "delete")
                .build();
    }

    @Bean
    public CommonErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(2000L, 3));
        handler.setRetryListeners((record, ex, deliveryAttempt) ->
            log.error("Error procesando evento Kafka [Tópico: {}, Partición: {}, Offset: {}] - Intento {}: {}",
                    record.topic(), record.partition(), record.offset(), deliveryAttempt, ex.getMessage())
        );
        return handler;
    }
}