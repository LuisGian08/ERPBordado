package com.embroiderydash.disenos.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "diseno_archivos")
public class ArchivoTecnico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diseno_id", nullable = false)
    @JsonIgnore
    private Diseno diseno;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(nullable = false, length = 10)
    private String formato;

    @Column(name = "ruta_almacenamiento", nullable = false, length = 500)
    private String rutaAlmacenamiento;

    @Column(name = "tamano_bytes", nullable = false)
    private Long tamanoBytes;

    @Column(name = "checksum_md5", length = 64)
    private String checksumMd5;

    @Column(name = "subido_en", nullable = false)
    private OffsetDateTime subidoEn;

    public ArchivoTecnico() {
    }

    @PrePersist
    protected void onCreate() {
        this.subidoEn = OffsetDateTime.now();
    }

    public ArchivoTecnico(String nombreArchivo, String formato, String rutaAlmacenamiento, Long tamanoBytes, String checksumMd5) {
        this.nombreArchivo = nombreArchivo;
        this.formato = formato;
        this.rutaAlmacenamiento = rutaAlmacenamiento;
        this.tamanoBytes = tamanoBytes;
        this.checksumMd5 = checksumMd5;
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

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormatos(String formato) {
        this.formato = formato;
    }

    public String getRutaAlmacenamiento() {
        return rutaAlmacenamiento;
    }

    public void setRutaAlmacenamiento(String rutaAlmacenamiento) {
        this.rutaAlmacenamiento = rutaAlmacenamiento;
    }

    public Long getTamanoBytes() {
        return tamanoBytes;
    }

    public void setTamanoBytes(Long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public String getChecksumMd5() {
        return checksumMd5;
    }

    public void setChecksumMd5(String checksumMd5) {
        this.checksumMd5 = checksumMd5;
    }

    public OffsetDateTime getSubidoEn() {
        return subidoEn;
    }

    public void setSubidoEn(OffsetDateTime subidoEn) {
        this.subidoEn = subidoEn;
    }
}
