package com.embroiderydash.inventario.domain.repository;

import com.embroiderydash.inventario.domain.model.MovimientoInventario;
import com.embroiderydash.inventario.domain.model.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByHiloIdOrderByFechaMovimientoDesc(Long hiloId);

    Page<MovimientoInventario> findAllByOrderByFechaMovimientoDesc(Pageable pageable);
    Page<MovimientoInventario> findByTipoMovimientoOrderByFechaMovimientoDesc(TipoMovimiento tipoMovimiento, Pageable pageable);
}
