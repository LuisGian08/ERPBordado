package com.embroiderydash.trabajos.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequestDto(
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 100) String contacto) { }
