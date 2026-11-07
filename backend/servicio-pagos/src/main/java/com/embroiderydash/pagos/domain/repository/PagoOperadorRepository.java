package com.embroiderydash.pagos.domain.repository;

import com.embroiderydash.pagos.domain.model.PagoOperador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoOperadorRepository extends JpaRepository<PagoOperador,Long> {
    Page<PagoOperador> findAllByOrderByFechaPagoDesc(Pageable pageable);
    boolean existsByOperadorIgnoreCaseAndSemanaInicio(String operador,java.time.LocalDate semanaInicio);
}
