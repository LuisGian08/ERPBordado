package com.embroiderydash.trabajos.domain.repository;

import com.embroiderydash.trabajos.domain.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findTop50ByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);
    List<Cliente> findTop50ByOrderByNombreAsc();
}
