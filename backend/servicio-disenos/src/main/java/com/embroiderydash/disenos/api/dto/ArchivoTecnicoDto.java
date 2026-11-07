package com.embroiderydash.disenos.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.OffsetDateTime;

@Schema(description = "Información del archivo técnico de bordado (DST, PES)")
public class ArchivoTecnicoDto {

    private Long id;

    @Schema(description = "Nombre original del archivo", example = "logotipo_corporativo.dst")
    private String nombreArchivo;

    @Schema(description = "Formato o extensión del archivo", example = "DST")
    private String formato;

    @Schema(description = "Ruta de almacenamiento interno", example = "disenos/1/logotipo_corporativo.dst")
    private String rutaAlmacenamiento;

    @Schema(description = "Tamaño del archivo en bytes", example = "45120")
    private Long tamanoBytes;

    @Schema(description = "Hash MD5 para verificación de integridad", example = "d41d8cd98f00b204e9800998ecf8427e")
    private String checksumMd5;

    @Schema(description = "Fecha y hora en que se subió el archivo")
    private OffsetDateTime subidoEn;

    public ArchivoTecnicoDto() {
    }

    public ArchivoTecnicoDto(Long id, String nombreArchivo, String formato, String rutaAlmacenamiento, Long tamanoBytes, String checksumMd5, OffsetDateTime subidoEn) {
        this.id = id;
        this.nombreArchivo = nombreArchivo;
        this.formato = formato;
        this.rutaAlmacenamiento = rutaAlmacenamiento;
        this.tamanoBytes = tamanoBytes;
        this.checksumMd5 = checksumMd5;
        this.subidoEn = subidoEn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setFormato(String formato) {
        this.formato = formato;
    }

    @JsonIgnore
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
