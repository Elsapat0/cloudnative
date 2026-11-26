
package com.pedidos360.kafkaadmin.controller;

import com.pedidos360.kafkaadmin.service.KafkaAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/kafka/admin")
public class KafkaAdminController {

    private final KafkaAdminService kafkaAdminService;

    public KafkaAdminController(KafkaAdminService kafkaAdminService) {
        this.kafkaAdminService = kafkaAdminService;
    }

   @PostMapping("/topicos")
public ResponseEntity<?> crearTopico(@RequestBody Map<String, Object> req) {
    try {
        Object nombreObj = req.get("nombre");
        if (!(nombreObj instanceof String nombre) || nombre.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El campo 'nombre' es obligatorio"));
        }
        int particiones = ((Number) req.getOrDefault("particiones", 2)).intValue();
        short replicacion = ((Number) req.getOrDefault("replicacion", 1)).shortValue();
        String retentionMs = String.valueOf(req.getOrDefault("retentionMs", "604800000"));
        String cleanupPolicy = String.valueOf(req.getOrDefault("cleanupPolicy", "delete"));

        kafkaAdminService.crearTopico(nombre.trim(), particiones, replicacion, retentionMs, cleanupPolicy);
        return ResponseEntity.ok(Map.of("mensaje", "Tópico creado: " + nombre.trim()));
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}

    @PutMapping("/topicos/{nombre}/config")
    public ResponseEntity<?> actualizarConfig(@PathVariable String nombre, @RequestBody Map<String, String> req) {
        try {
            kafkaAdminService.actualizarConfiguracionTopico(nombre, req.get("retentionMs"), req.get("cleanupPolicy"));
            return ResponseEntity.ok(Map.of("mensaje", "Configuración actualizada para: " + nombre));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/topicos")
    public ResponseEntity<?> listarTopicos() {
        try {
            return ResponseEntity.ok(kafkaAdminService.listarTopicos());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/topicos/{nombre}")
    public ResponseEntity<?> eliminarTopico(@PathVariable String nombre) {
        try {
            kafkaAdminService.eliminarTopico(nombre);
            return ResponseEntity.ok(Map.of("mensaje", "Tópico eliminado: " + nombre));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/consumer-groups")
    public ResponseEntity<?> listarConsumerGroups() {
        try {
            return ResponseEntity.ok(kafkaAdminService.listarConsumerGroups());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}