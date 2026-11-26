package com.pedidos360.productos.service;

import com.pedidos360.productos.dto.AnaliticaEventoDTO;
import com.pedidos360.productos.kafka.ProductoKafkaProducer;
import com.pedidos360.productos.model.Producto;
import com.pedidos360.productos.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository repository;
    private final ProductoKafkaProducer kafkaProducer;

    public ProductoService(ProductoRepository repository, ProductoKafkaProducer kafkaProducer) {
        this.repository = repository;
        this.kafkaProducer = kafkaProducer;
    }

    public List<Producto> findAll() {
        return repository.findAll();
    }

    public Optional<Producto> findById(Long id) {
        return repository.findById(id);
    }

    public List<Producto> findByCategoria(String categoria) {
        return repository.findByCategoria(categoria);
    }

    public Producto save(Producto producto) {
        Producto guardado = repository.save(producto);
        Double precio = guardado.getPrecio() != null ? guardado.getPrecio().doubleValue() : 0.0;

        kafkaProducer.publicarEventoAnalitica(new AnaliticaEventoDTO(
                "ms-productos",
                "PRODUCTO_CREADO",
                "Producto creado: " + guardado.getNombre() + " (Stock: " + guardado.getStock() + ")",
                precio,
                LocalDateTime.now()
        ));

        return guardado;
    }

    public Optional<Producto> update(Long id, Producto producto) {
        return repository.findById(id).map(existing -> {
            existing.setNombre(producto.getNombre());
            existing.setDescripcion(producto.getDescripcion());
            existing.setPrecio(producto.getPrecio());
            existing.setStock(producto.getStock());
            existing.setCategoria(producto.getCategoria());
            Producto actualizado = repository.save(existing);

            Double precio = actualizado.getPrecio() != null ? actualizado.getPrecio().doubleValue() : 0.0;

            kafkaProducer.publicarEventoAnalitica(new AnaliticaEventoDTO(
                    "ms-productos",
                    "PRODUCTO_ACTUALIZADO",
                    "Producto actualizado: " + actualizado.getNombre() + " (Stock: " + actualizado.getStock() + ")",
                    precio,
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