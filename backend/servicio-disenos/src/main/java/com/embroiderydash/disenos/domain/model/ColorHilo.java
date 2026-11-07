package com.embroiderydash.disenos.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "diseno_colores", uniqueConstraints = {
    @UniqueConstraint(name = "uk_diseno_orden", columnNames = {"diseno_id", "orden_secuencia"})
})
public class ColorHilo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diseno_id", nullable = false)
    @JsonIgnore
    private Diseno diseno;

    @Column(name = "orden_secuencia", nullable = false)
    private Integer ordenSecuencia;

    @Column(name = "codigo_color", nullable = false, length = 50)
    private String codigoColor;

    @Column(name = "nombre_color", nullable = false, length = 100)
    private String nombreColor;

    @Column(name = "referencia_hilo", length = 50)
    private String referenciaHilo;

    public ColorHilo() {
    }

    public ColorHilo(Integer ordenSecuencia, String codigoColor, String nombreColor, String referenciaHilo) {
        this.ordenSecuencia = ordenSecuencia;
        this.codigoColor = codigoColor;
        this.nombreColor = nombreColor;
        this.referenciaHilo = referenciaHilo;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Diseno getDiseno() {
        return diseno;
    }

    public void setDiseno(Diseno diseno) {
        this.diseno = diseno;
    }

    public Integer getOrdenSecuencia() {
        return ordenSecuencia;
    }

    public void setOrdenSecuencia(Integer ordenSecuencia) {
        this.ordenSecuencia = ordenSecuencia;
    }

    public String getCodigoColor() {
        return codigoColor;
    }

    public void setCodigoColor(String codigoColor) {
        this.codigoColor = codigoColor;
    }

    public String getNombreColor() {
        return nombreColor;
    }

    public void setNombreColor(String nombreColor) {
        this.nombreColor = nombreColor;
    }

    public String getReferenciaHilo() {
        return referenciaHilo;
    }

    public void setReferenciaHilo(String referenciaHilo) {
        this.referenciaHilo = referenciaHilo;
    }
}
