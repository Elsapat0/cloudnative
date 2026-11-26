package com.pedidos360.clientes.service;

import com.pedidos360.clientes.dto.AnaliticaEventoDTO;
import com.pedidos360.clientes.kafka.ClienteKafkaProducer;
import com.pedidos360.clientes.model.Cliente;
import com.pedidos360.clientes.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final ClienteKafkaProducer kafkaProducer;

    public ClienteService(ClienteRepository repository, ClienteKafkaProducer kafkaProducer) {
        this.repository = repository;
        this.kafkaProducer = kafkaProducer;
    }

    public List<Cliente> findAll() {
        return repository.findAll();
    }

    public Optional<Cliente> findById(Long id) {
        return repository.findById(id);
    }

    public Cliente save(Cliente cliente) {
        Cliente guardado = repository.save(cliente);

        kafkaProducer.publicarEventoAnalitica(new AnaliticaEventoDTO(
                "ms-clientes",
                "CLIENTE_CREADO",
                "Cliente registrado: " + guardado.getNombre() + " " + guardado.getApellido(),
                0.0,
                LocalDateTime.now()
        ));

        return guardado;
    }

    public Optional<Cliente> update(Long id, Cliente cliente) {
        return repository.findById(id).map(existing -> {
            existing.setNombre(cliente.getNombre());
            existing.setApellido(cliente.getApellido());
            existing.setEmail(cliente.getEmail());
            existing.setTelefono(cliente.getTelefono());
            existing.setDireccion(cliente.getDireccion());
            Cliente actualizado = repository.save(existing);

            kafkaProducer.publicarEventoAnalitica(new AnaliticaEventoDTO(
                    "ms-clientes",
                    "CLIENTE_ACTUALIZADO",
                    "Cliente actualizado: " + actualizado.getNombre() + " " + actualizado.getApellido(),
                    0.0,
                    LocalDateTime.now()
            ));

            return actualizado;
        });
    }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}