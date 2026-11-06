package com.embroiderydash.pagos.application.service;

import com.embroiderydash.pagos.domain.model.Pago;
import com.embroiderydash.pagos.domain.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagoPersistenceService {
    private final PagoRepository repository;
    public PagoPersistenceService(PagoRepository repository) { this.repository=repository; }
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public Pago saveAndFlush(Pago pago) { return repository.saveAndFlush(pago); }
}
