package com.pedidos360.productos.config;

import com.pedidos360.productos.model.Producto;
import com.pedidos360.productos.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ProductoRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                Producto p1 = new Producto(null, "Teclado Mecánico RGB", "Teclado gamer switches blue", new BigDecimal("49990.00"), 15, "Periféricos");
                Producto p2 = new Producto(null, "Mouse Inalámbrico", "Mouse óptico 16000 DPI", new BigDecimal("29990.00"), 30, "Periféricos");

                repository.saveAll(List.of(p1, p2));
                System.out.println(">>> ¡Base de datos de Supabase inicializada con productos demo!");
            }
        };
    }
}
