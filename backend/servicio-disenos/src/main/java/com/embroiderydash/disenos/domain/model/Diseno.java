package com.embroiderydash.disenos.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "disenos")
public class Diseno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "cantidad_puntadas", nullable = false)
    private Integer cantidadPuntadas;

    @Column(name = "ancho_mm", precision = 8, scale = 2)
    private BigDecimal anchoMm;

    @Column(name = "alto_mm", precision = 8, scale = 2)
    private BigDecimal altoMm;

    @Column(name = "formatos_admitidos", nullable = false, length = 100)
    private String formatosAdmitidos;

    @Column(name = "observaciones_tecnicas", columnDefinition = "TEXT")
    private String observacionesTecnicas;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private OffsetDateTime actualizadoEn;

    @OneToMany(mappedBy = "diseno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ColorHilo> colores = new ArrayList<>();

    @OneToMany(mappedBy = "diseno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ArchivoTecnico> archivos = new ArrayList<>();

    public Diseno() {
    }

    @PrePersist
    protected void onCreate() {
        this.creadoEn = OffsetDateTime.now();
        this.actualizadoEn = this.creadoEn;
        if (this.activo == null) {
            this.activo = true;
        }
        if (this.formatosAdmitidos == null || this.formatosAdmitidos.isBlank()) {
            this.formatosAdmitidos = "DST,PES";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.actualizadoEn = OffsetDateTime.now();
    }

    public void addColor(ColorHilo color) {
        colores.add(color);
        color.setDiseno(this);
    }

    public void clearColores() {
        colores.forEach(c -> c.setDiseno(null));
        colores.clear();
    }

    public void addArchivo(ArchivoTecnico archivo) {
        archivos.add(archivo);
        archivo.setDiseno(this);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidadPuntadas() {
        return cantidadPuntadas;
    }

    public void setCantidadPuntadas(Integer cantidadPuntadas) {
        this.cantidadPuntadas = cantidadPuntadas;
    }

    public BigDecimal getAnchoMm() {
        return anchoMm;
    }

    public void setAnchoMm(BigDecimal anchoMm) {
        this.anchoMm = anchoMm;
    }

    public BigDecimal getAltoMm() {
        return altoMm;
    }

    public void setAltoMm(BigDecimal altoMm) {
        this.altoMm = altoMm;
    }

    public String getFormatosAdmitidos() {
        return formatosAdmitidos;
    }

    public void setFormatosAdmitidos(String formatosAdmitidos) {
        this.formatosAdmitidos = formatosAdmitidos;
    }

    public String getObservacionesTecnicas() {
        return observacionesTecnicas;
    }

    public void setObservacionesTecnicas(String observacionesTecnicas) {
        this.observacionesTecnicas = observacionesTecnicas;
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

    public List<ColorHilo> getColores() {
        return colores;
    }

    public void setColores(List<ColorHilo> colores) {
        this.colores = colores;
    }

    public List<ArchivoTecnico> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<ArchivoTecnico> archivos) {
        this.archivos = archivos;
    }
}
