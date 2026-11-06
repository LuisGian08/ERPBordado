package com.embroiderydash.disenos.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos de un color de hilo en la secuencia del bordado")
public class ColorHiloDto {

    private Long id;

    @Schema(description = "Número secuencial de cambio de color en la máquina", example = "1")
    @NotNull(message = "El orden de secuencia es obligatorio")
    @Positive(message = "El orden de secuencia debe ser mayor a 0")
    private Integer ordenSecuencia;

    @Schema(description = "Código hexadecimal o referencia del color", example = "#002B49")
    @NotBlank(message = "El código de color es obligatorio")
    @Size(max = 50, message = "El código de color no puede exceder 50 caracteres")
    private String codigoColor;

    @Schema(description = "Nombre descriptivo del color", example = "Azul Marino Corporativo")
    @NotBlank(message = "El nombre del color es obligatorio")
    @Size(max = 100, message = "El nombre del color no puede exceder 100 caracteres")
    private String nombreColor;

    @Schema(description = "Referencia interna o comercial del cono de hilo", example = "POLY-1042")
    @Size(max = 50, message = "La referencia de hilo no puede exceder 50 caracteres")
    private String referenciaHilo;

    public ColorHiloDto() {
    }

    public ColorHiloDto(Integer ordenSecuencia, String codigoColor, String nombreColor, String referenciaHilo) {
        this.ordenSecuencia = ordenSecuencia;
        this.codigoColor = codigoColor;
        this.nombreColor = nombreColor;
        this.referenciaHilo = referenciaHilo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
