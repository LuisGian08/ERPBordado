package com.embroiderydash.disenos.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CarpetaRequest(@NotBlank @Size(max = 120) String nombre, Long carpetaPadreId) { }
