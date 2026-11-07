package com.embroiderydash.trabajos.domain.repository;

import com.embroiderydash.trabajos.domain.model.EstadoTrabajo;
import com.embroiderydash.trabajos.domain.model.TrabajoBordado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrabajoRepository extends JpaRepository<TrabajoBordado, Long> {

    @Query("SELECT t FROM TrabajoBordado t WHERE LOWER(t.codigoTrabajo) = LOWER(:codigoTrabajo)")
    Optional<TrabajoBordado> findByCodigoTrabajoIgnoreCase(@Param("codigoTrabajo") String codigoTrabajo);

    @Query("SELECT COUNT(t) > 0 FROM TrabajoBordado t WHERE LOWER(t.codigoTrabajo) = LOWER(:codigoTrabajo)")
    boolean existsByCodigoTrabajoIgnoreCase(@Param("codigoTrabajo") String codigoTrabajo);

    @Query("SELECT COUNT(t) > 0 FROM TrabajoBordado t WHERE LOWER(t.codigoTrabajo) = LOWER(:codigoTrabajo) AND t.id <> :id")
    boolean existsByCodigoTrabajoIgnoreCaseAndIdNot(@Param("codigoTrabajo") String codigoTrabajo, @Param("id") Long id);

    List<TrabajoBordado> findByEstado(EstadoTrabajo estado);

    Page<TrabajoBordado> findByClienteNombreContainingIgnoreCase(String clienteNombre, Pageable pageable);
}
