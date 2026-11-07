package com.embroiderydash.inventario.domain.exception;

public class StockInsuficienteException extends RuntimeException {

    private final Long hiloId;
    private final int stockDisponible;
    private final int cantidadSolicitada;

    public StockInsuficienteException(Long hiloId, int stockDisponible, int cantidadSolicitada) {
        super(String.format("Stock insuficiente para el hilo ID %d: disponible = %d conos, solicitado = %d conos",
                hiloId, stockDisponible, cantidadSolicitada));
        this.hiloId = hiloId;
        this.stockDisponible = stockDisponible;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public Long getHiloId() {
        return hiloId;
    }

    public int getStockDisponible() {
        return stockDisponible;
    }

    public int getCantidadSolicitada() {
        return cantidadSolicitada;
    }
}
