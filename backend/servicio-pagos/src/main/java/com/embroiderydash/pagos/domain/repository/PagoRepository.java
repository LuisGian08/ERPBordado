package com.embroiderydash.pagos.domain.repository;

import com.embroiderydash.pagos.domain.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByTrabajoIdOrderByCreadoEnDesc(Long trabajoId);

    Page<Pago> findByTrabajoIdIsNotNullAndEstadoResultadoOrderByCreadoEnDesc(String estadoResultado, Pageable pageable);

    Optional<Pago> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);
    boolean existsByTrabajoIdAndEstadoResultado(Long trabajoId, String estadoResultado);

    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM Pago p WHERE p.trabajoId = :trabajoId AND p.estadoResultado = 'REGISTRADO'")
    BigDecimal calcularTotalPagadoPorTrabajo(@Param("trabajoId") Long trabajoId);

}
