package com.embroiderydash.pagos.infrastructure;

import com.embroiderydash.pagos.application.service.PaymentLock;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component @Profile("test")
public class NoOpPaymentLock implements PaymentLock {
    @Override public void lockWork(Long id) { }
    @Override public void lockIdempotencyKey(String key) { }
}
