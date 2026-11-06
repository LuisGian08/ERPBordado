package com.embroiderydash.disenos.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "Datos para la creación o actualización de un diseño de bordado")
public class DisenoRequestDto {

    @Schema(description = "Nombre único del diseño", example = "Logotipo Escolar Los Álamos")
    @NotBlank(message = "El nombre del diseño es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String nombre;

    @Schema(description = "Descripción o notas del diseño", example = "Escudo bordado para camisa de gala")
    private String descripcion;

    @Schema(description = "Cantidad total de puntadas del diseño", example = "8500")
    @NotNull(message = "La cantidad de puntadas es obligatoria")
    @Positive(message = "La cantidad de puntadas debe ser un número entero positivo mayor a cero")
    private Integer cantidadPuntadas;

    @Schema(description = "Ancho en milímetros", example = "80.50")
    @DecimalMin(value = "0.0", inclusive = false, message = "El ancho debe ser mayor a 0")
    private BigDecimal anchoMm;

    @Schema(description = "Alto en milímetros", example = "75.00")
    @DecimalMin(value = "0.0", inclusive = false, message = "El alto debe ser mayor a 0")
    private BigDecimal altoMm;

    @Schema(description = "Formatos soportados separados por coma", example = "DST,PES")
    private String formatosAdmitidos;

    @Schema(description = "Observaciones técnicas de producción (tensión, tipo de entretela, etc.)")
    private String observacionesTecnicas;

    @Schema(description = "Secuencia ordenada de colores de hilo utilizados")
    @Valid
    private List<ColorHiloDto> colores = new ArrayList<>();

    private Long carpetaId;
    private String archivoTemporalId;
    private Integer cantidadColores;

    public DisenoRequestDto() {
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

    public List<ColorHiloDto> getColores() {
        return colores;
    }

    public void setColores(List<ColorHiloDto> colores) {
        this.colores = colores;
    }

    public Long getCarpetaId() { return carpetaId; }
    public void setCarpetaId(Long carpetaId) { this.carpetaId = carpetaId; }
    public String getArchivoTemporalId() { return archivoTemporalId; }
    public void setArchivoTemporalId(String archivoTemporalId) { this.archivoTemporalId = archivoTemporalId; }
    public Integer getCantidadColores() { return cantidadColores; }
    public void setCantidadColores(Integer cantidadColores) { this.cantidadColores = cantidadColores; }
}
