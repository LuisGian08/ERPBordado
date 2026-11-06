package com.embroiderydash.inventario.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "Datos para el registro o actualización de un hilo en el catálogo")
public class HiloRequestDto {

    @Schema(description = "Marca comercial del hilo", example = "Madeira")
    @Size(max = 100, message = "La marca no puede exceder 100 caracteres")
    private String marca;

    @Schema(description = "Tipo de material y grosor", example = "Poliéster 40wt")
    @Size(max = 50, message = "El tipo de material no puede exceder 50 caracteres")
    private String tipoMaterial;

    @Schema(description = "Código de color numérico", example = "00042")
    @NotBlank(message = "El código de color es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9#._-]{1,50}$", message = "El código de color contiene caracteres no permitidos")
    private String codigoColor;

    @Schema(description = "Nombre descriptivo del color", example = "Azul Marino")
    @NotBlank(message = "El nombre del color es obligatorio")
    @Size(max = 100, message = "El nombre del color no puede exceder 100 caracteres")
    private String nombreColor;

    @Schema(description = "Código de referencia único del fabricante o taller", example = "POLY-1042")
    @Size(max = 50, message = "La referencia no puede exceder 50 caracteres")
    private String referencia;

    @Schema(description = "Existencia inicial en conos", example = "10")
    @NotNull(message = "El stock inicial de conos es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stockActualConos;

    @Schema(description = "Metros lineales contenidos por cono", example = "5000")
    @Positive(message = "Los metros por cono deben ser mayores a 0")
    private Integer metrosPorCono;

    @Schema(description = "Nivel de stock mínimo que dispara alerta", example = "5")
    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimoConos;

    @Schema(description = "Precio de compra o reposición por cono", example = "24.50")
    @NotNull(message = "El precio por cono es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo")
    private BigDecimal precioCono;

    public HiloRequestDto() {
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
}
