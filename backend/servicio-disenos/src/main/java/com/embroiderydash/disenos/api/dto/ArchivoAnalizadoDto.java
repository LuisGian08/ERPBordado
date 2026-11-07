package com.embroiderydash.disenos.api.dto;

import java.math.BigDecimal;

public record ArchivoAnalizadoDto(String archivoTemporalId, String nombreSugerido, String formato,
                                  int puntadas, BigDecimal anchoMm, BigDecimal altoMm,
                                  int cantidadColores, String previewPngBase64) { }
