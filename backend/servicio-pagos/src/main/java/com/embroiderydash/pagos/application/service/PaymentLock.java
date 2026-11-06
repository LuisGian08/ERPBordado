package com.embroiderydash.pagos.application.service;

public interface PaymentLock {
    void lockWork(Long trabajoId);
    void lockIdempotencyKey(String key);
}
