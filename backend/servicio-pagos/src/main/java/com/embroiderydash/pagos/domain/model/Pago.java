package com.embroiderydash.pagos.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trabajo_id")
    private Long trabajoId;

    @Column(name = "idempotency_key", nullable = false, length = 100, unique = true)
    private String idempotencyKey;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 30)
    private MetodoPago metodoPago;

    @Column(name = "referencia_transaccion", length = 100)
    private String referenciaTransaccion;

    @Column(name = "estado_resultado", nullable = false, length = 30)
    private String estadoResultado = "REGISTRADO";

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(nullable = false, length = 100)
    private String operador;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    public Pago() {
    }

    @PrePersist
    protected void onCreate() {
        this.creadoEn = OffsetDateTime.now();
        if (this.estadoResultado == null) {
            this.estadoResultado = "REGISTRADO";
        }
    }

    public Pago(Long trabajoId, String idempotencyKey, BigDecimal monto, MetodoPago metodoPago, String referenciaTransaccion, String notas, String operador) {
        this.trabajoId = trabajoId;
        this.idempotencyKey = idempotencyKey;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.referenciaTransaccion = referenciaTransaccion;
        this.notas = notas;
        this.operador = operador;
        this.estadoResultado = "REGISTRADO";
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTrabajoId() {
        return trabajoId;
    }

    public void setTrabajoId(Long trabajoId) {
        this.trabajoId = trabajoId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferenciaTransaccion() {
        return referenciaTransaccion;
    }

    public void setReferenciaTransaccion(String referenciaTransaccion) {
        this.referenciaTransaccion = referenciaTransaccion;
    }

    public String getEstadoResultado() {
        return estadoResultado;
    }

    public void setEstadoResultado(String estadoResultado) {
        this.estadoResultado = estadoResultado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(OffsetDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
}
