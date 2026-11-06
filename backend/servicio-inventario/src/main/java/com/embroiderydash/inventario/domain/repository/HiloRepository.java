package com.embroiderydash.inventario.domain.repository;

import com.embroiderydash.inventario.domain.model.Hilo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HiloRepository extends JpaRepository<Hilo, Long> {

    @Query("SELECT h FROM Hilo h WHERE h.activo = true ORDER BY h.marca, h.nombreColor")
    List<Hilo> findAllActivos();

    @Query("SELECT h FROM Hilo h WHERE h.id = :id AND h.activo = true")
    Optional<Hilo> findActivoById(@Param("id") Long id);

    /**
     * Bloqueo Pesimista (SELECT ... FOR UPDATE) para prevenir lecturas y modificaciones concurrentes
     * sobre el stock de conos de hilo, garantizando la consistencia y evitando stock negativo.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM Hilo h WHERE h.id = :id AND h.activo = true")
    Optional<Hilo> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT h FROM Hilo h WHERE LOWER(h.referencia) = LOWER(:referencia) AND h.activo = true")
    Optional<Hilo> findByReferenciaIgnoreCase(@Param("referencia") String referencia);

    @Query("SELECT COUNT(h) > 0 FROM Hilo h WHERE LOWER(h.referencia) = LOWER(:referencia)")
    boolean existsByReferenciaIgnoreCase(@Param("referencia") String referencia);

    @Query("SELECT COUNT(h) > 0 FROM Hilo h WHERE LOWER(h.referencia) = LOWER(:referencia) AND h.id <> :id")
    boolean existsByReferenciaIgnoreCaseAndIdNot(@Param("referencia") String referencia, @Param("id") Long id);

    @Query("SELECT h FROM Hilo h WHERE h.activo = true AND h.stockActualConos <= h.stockMinimoConos ORDER BY h.stockActualConos ASC")
    List<Hilo> findHilosBajoStockMinimo();
}
