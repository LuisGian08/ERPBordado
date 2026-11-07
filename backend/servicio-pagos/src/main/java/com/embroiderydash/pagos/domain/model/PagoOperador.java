package com.embroiderydash.pagos.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name="pagos_operadores")
public class PagoOperador {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=100) private String operador;
    @Column(name="semana_inicio",nullable=false) private LocalDate semanaInicio;
    @Column(name="semana_fin",nullable=false) private LocalDate semanaFin;
    @Column(name="dias_asistencia",nullable=false,columnDefinition="TEXT") private String diasAsistencia;
    @Enumerated(EnumType.STRING) @Column(name="tipo_pago",nullable=false,length=20) private TipoPagoOperador tipoPago;
    @Column(name="tarifa_diaria",precision=12,scale=2) private BigDecimal tarifaDiaria;
    @Column(name="horas_regulares",precision=8,scale=2) private BigDecimal horasRegulares;
    @Column(name="tarifa_hora",precision=12,scale=2) private BigDecimal tarifaHora;
    @Column(name="monto_base",nullable=false,precision=12,scale=2) private BigDecimal montoBase;
    @Column(name="horas_extras",nullable=false,precision=8,scale=2) private BigDecimal horasExtras;
    @Column(name="tarifa_hora_extra",nullable=false,precision=12,scale=2) private BigDecimal tarifaHoraExtra;
    @Column(name="monto_extras",nullable=false,precision=12,scale=2) private BigDecimal montoExtras;
    @Column(name="monto_total",nullable=false,precision=12,scale=2) private BigDecimal montoTotal;
    @Column(name="fecha_pago",nullable=false) private LocalDate fechaPago;
    @Column(nullable=false,length=20) private String estado="PAGADO";
    @Column(length=500) private String notas;
    @Column(name="registrado_por",nullable=false,length=100) private String registradoPor;
    @Column(name="creado_en",nullable=false,updatable=false) private OffsetDateTime creadoEn;

    protected PagoOperador() { }
    public PagoOperador(String operador,LocalDate semanaInicio,String diasAsistencia,TipoPagoOperador tipoPago,
            BigDecimal tarifaDiaria,BigDecimal horasRegulares,BigDecimal tarifaHora,BigDecimal montoBase,
            BigDecimal horasExtras,BigDecimal tarifaHoraExtra,BigDecimal montoExtras,BigDecimal montoTotal,
            LocalDate fechaPago,String notas,String registradoPor) {
        this.operador=operador;this.semanaInicio=semanaInicio;this.semanaFin=semanaInicio.plusDays(6);
        this.diasAsistencia=diasAsistencia;this.tipoPago=tipoPago;this.tarifaDiaria=tarifaDiaria;
        this.horasRegulares=horasRegulares;this.tarifaHora=tarifaHora;this.montoBase=montoBase;
        this.horasExtras=horasExtras;this.tarifaHoraExtra=tarifaHoraExtra;this.montoExtras=montoExtras;
        this.montoTotal=montoTotal;this.fechaPago=fechaPago;this.notas=notas;this.registradoPor=registradoPor;
    }
    @PrePersist void beforeInsert(){creadoEn=OffsetDateTime.now();}
    public Long getId(){return id;} public String getOperador(){return operador;} public LocalDate getSemanaInicio(){return semanaInicio;}
    public LocalDate getSemanaFin(){return semanaFin;} public String getDiasAsistencia(){return diasAsistencia;} public TipoPagoOperador getTipoPago(){return tipoPago;}
    public BigDecimal getTarifaDiaria(){return tarifaDiaria;} public BigDecimal getHorasRegulares(){return horasRegulares;} public BigDecimal getTarifaHora(){return tarifaHora;}
    public BigDecimal getMontoBase(){return montoBase;} public BigDecimal getHorasExtras(){return horasExtras;} public BigDecimal getTarifaHoraExtra(){return tarifaHoraExtra;}
    public BigDecimal getMontoExtras(){return montoExtras;} public BigDecimal getMontoTotal(){return montoTotal;} public LocalDate getFechaPago(){return fechaPago;}
    public String getEstado(){return estado;} public String getNotas(){return notas;} public String getRegistradoPor(){return registradoPor;} public OffsetDateTime getCreadoEn(){return creadoEn;}
}
