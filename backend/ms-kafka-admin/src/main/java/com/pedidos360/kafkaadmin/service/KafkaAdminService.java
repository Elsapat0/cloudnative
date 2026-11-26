package com.pedidos360.kafkaadmin.service;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class KafkaAdminService {

    private static final Logger log = LoggerFactory.getLogger(KafkaAdminService.class);

    private final AdminClient adminClient;

    public KafkaAdminService(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    public void crearTopico(String nombre, int particiones, short replicacion, String retentionMs, String cleanupPolicy)
            throws ExecutionException, InterruptedException {
        log.info("Creando tópico '{}' con particiones={}, replicacion={}, retentionMs={}, cleanupPolicy={}",
                nombre, particiones, replicacion, retentionMs, cleanupPolicy);
        try {
            NewTopic nuevoTopico = new NewTopic(nombre, particiones, replicacion);
            Map<String, String> configs = new HashMap<>();
            if (retentionMs != null) {
                configs.put("retention.ms", retentionMs);
            }
            if (cleanupPolicy != null) {
                configs.put("cleanup.policy", cleanupPolicy);
            }
            nuevoTopico.configs(configs);

            adminClient.createTopics(Collections.singletonList(nuevoTopico)).all().get();
            log.info("Tópico '{}' creado correctamente", nombre);
        } catch (Exception e) {
            log.error("Error al crear el tópico '{}': {}", nombre, e.getMessage(), e);
            throw e;
        }
    }

    public void actualizarConfiguracionTopico(String nombre, String retentionMs, String cleanupPolicy)
            throws ExecutionException, InterruptedException {
        log.info("Actualizando configuración del tópico '{}' con retentionMs={}, cleanupPolicy={}",
                nombre, retentionMs, cleanupPolicy);
        try {
            ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, nombre);
            List<AlterConfigOp> ops = new ArrayList<>();
            if (retentionMs != null) {
                ops.add(new AlterConfigOp(new ConfigEntry("retention.ms", retentionMs), AlterConfigOp.OpType.SET));
            }
            if (cleanupPolicy != null) {
                ops.add(new AlterConfigOp(new ConfigEntry("cleanup.policy", cleanupPolicy), AlterConfigOp.OpType.SET));
            }
            adminClient.incrementalAlterConfigs(Collections.singletonMap(resource, ops)).all().get();
            log.info("Configuración actualizada correctamente para el tópico '{}'", nombre);
        } catch (Exception e) {
            log.error("Error al actualizar configuración del tópico '{}': {}", nombre, e.getMessage(), e);
            throw e;
        }
    }

    public Map<String, Object> listarTopicos() throws ExecutionException, InterruptedException {
        log.info("Listando tópicos de Kafka");
        try {
            Set<String> nombres = adminClient.listTopics().names().get();
            Map<String, TopicDescription> descripciones = adminClient.describeTopics(nombres).allTopicNames().get();

            Map<String, Object> resultado = new HashMap<>();
            descripciones.forEach((k, v) -> {
                Map<String, Object> info = new HashMap<>();
                info.put("particiones", v.partitions().size());
                info.put("interna", v.isInternal());
                resultado.put(k, info);
            });
            log.info("Se listaron {} tópicos correctamente", resultado.size());
            return resultado;
        } catch (Exception e) {
            log.error("Error al listar tópicos: {}", e.getMessage(), e);
            throw e;
        }
    }

    public void eliminarTopico(String nombre) throws ExecutionException, InterruptedException {
        log.info("Eliminando tópico '{}'", nombre);
        try {
            adminClient.deleteTopics(Collections.singletonList(nombre)).all().get();
            log.info("Tópico '{}' eliminado correctamente", nombre);
        } catch (Exception e) {
            log.error("Error al eliminar el tópico '{}': {}", nombre, e.getMessage(), e);
            throw e;
        }
    }

   public List listarConsumerGroups() throws ExecutionException, InterruptedException {
        log.info("Listando consumer groups de Kafka");
        try {
            Collection<ConsumerGroupListing> groups = adminClient.listConsumerGroups().all().get();
            var resultado = groups.stream().map(g -> Map.of(
                    "groupId", g.groupId(),
                    "estado", g.state().map(Enum::name).orElse("UNKNOWN")
            )).collect(Collectors.toList());
            log.info("Se listaron {} consumer groups correctamente", resultado.size());
            return resultado;
        } catch (Exception e) {
            log.error("Error al listar consumer groups: {}", e.getMessage(), e);
            throw e;
        }
    }