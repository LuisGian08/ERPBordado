package com.embroiderydash.pagos.infrastructure;

import com.embroiderydash.pagos.application.service.PaymentLock;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component @Profile("!test")
public class PgAdvisoryPaymentLock implements PaymentLock {
    private final EntityManager entityManager;
    public PgAdvisoryPaymentLock(EntityManager entityManager) { this.entityManager=entityManager; }
    @Override public void lockWork(Long id) { entityManager.createNativeQuery("SELECT pg_advisory_xact_lock(CAST(:lockId AS bigint))").setParameter("lockId",id).getSingleResult(); }
    @Override public void lockIdempotencyKey(String key) { entityManager.createNativeQuery("SELECT pg_advisory_xact_lock(hashtextextended(CAST(:lockKey AS text), 0))").setParameter("lockKey",key).getSingleResult(); }
}
