package com.embroiderydash.inventario.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "hilos")
public class Hilo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String marca;

    @Column(name = "tipo_material", nullable = false, length = 50)
    private String tipoMaterial;

    @Column(name = "codigo_color", nullable = false, length = 50)
    private String codigoColor;

    @Column(name = "imagen_ruta", length = 500)
    private String imagenRuta;

    @Column(name = "imagen_content_type", length = 50)
    private String imagenContentType;

    @Column(name = "nombre_color", nullable = false, length = 100)
    private String nombreColor;

    @Column(nullable = false, length = 50, unique = true)
    private String referencia;

    @Column(name = "stock_actual_conos", nullable = false)
    private Integer stockActualConos;

    @Column(name = "metros_por_cono", nullable = false)
    private Integer metrosPorCono;

    @Column(name = "stock_minimo_conos", nullable = false)
    private Integer stockMinimoConos;

    @Column(name = "precio_cono", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCono;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;

    public Hilo() {
    }

    @PrePersist
    protected void onCreate() {
        this.creadoEn = OffsetDateTime.now();
        this.actualizadoEn = this.creadoEn;
        if (this.activo == null) {
            this.activo = true;
        }
        if (this.stockMinimoConos == null) {
            this.stockMinimoConos = 5;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = OffsetDateTime.now();
    }

    public boolean tieneAlertaStockMinimo() {
        return this.stockActualConos != null && this.stockMinimoConos != null && this.stockActualConos <= this.stockMinimoConos;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getTipoMaterial() {
        return tipoMaterial;
    }

    public void setTipoMaterial(String tipoMaterial) {
        this.tipoMaterial = tipoMaterial;
    }

    public String getCodigoColor() {
        return codigoColor;
    }

    public void setCodigoColor(String codigoColor) {
        this.codigoColor = codigoColor;
    }

    public String getImagenRuta() { return imagenRuta; }
    public void setImagenRuta(String imagenRuta) { this.imagenRuta = imagenRuta; }
    public String getImagenContentType() { return imagenContentType; }
    public void setImagenContentType(String imagenContentType) { this.imagenContentType = imagenContentType; }

    public String getNombreColor() {
        return nombreColor;
    }

    public void setNombreColor(String nombreColor) {
        this.nombreColor = nombreColor;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Integer getStockActualConos() {
        return stockActualConos;
    }

    public void setStockActualConos(Integer stockActualConos) {
        this.stockActualConos = stockActualConos;
    }

    public Integer getMetrosPorCono() {
        return metrosPorCono;
    }

    public void setMetrosPorCono(Integer metrosPorCono) {
        this.metrosPorCono = metrosPorCono;
    }

    public Integer getStockMinimoConos() {
        return stockMinimoConos;
    }

    public void setStockMinimoConos(Integer stockMinimoConos) {
        this.stockMinimoConos = stockMinimoConos;
    }

    public BigDecimal getPrecioCono() {
        return precioCono;
    }

    public void setPrecioCono(BigDecimal precioCono) {
        this.precioCono = precioCono;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public OffsetDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(OffsetDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public OffsetDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(OffsetDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
