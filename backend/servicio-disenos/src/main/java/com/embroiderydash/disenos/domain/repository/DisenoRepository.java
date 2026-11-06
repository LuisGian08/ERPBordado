package com.embroiderydash.disenos.domain.repository;

import com.embroiderydash.disenos.domain.model.Diseno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisenoRepository extends JpaRepository<Diseno, Long> {

    @Query("SELECT d FROM Diseno d WHERE d.activo = true")
    List<Diseno> findAllActivos();

    @Query("SELECT d FROM Diseno d WHERE d.activo = true")
    Page<Diseno> findAllActivos(Pageable pageable);

    @Query("SELECT d FROM Diseno d LEFT JOIN FETCH d.colores LEFT JOIN FETCH d.archivos WHERE d.id = :id AND d.activo = true")
    Optional<Diseno> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT COUNT(d) > 0 FROM Diseno d WHERE LOWER(d.nombre) = LOWER(:nombre)")
    boolean existsByNombreIgnoreCase(@Param("nombre") String nombre);

    @Query("SELECT COUNT(d) > 0 FROM Diseno d WHERE LOWER(d.nombre) = LOWER(:nombre) AND d.id <> :id")
    boolean existsByNombreIgnoreCaseAndIdNot(@Param("nombre") String nombre, @Param("id") Long id);
}
