package com.embroiderydash.trabajos.api.dto;

import com.embroiderydash.trabajos.domain.model.Cliente;
import java.time.OffsetDateTime;

public record ClienteDto(Long id, String nombre, String contacto, OffsetDateTime creadoEn) {
    public static ClienteDto from(Cliente cliente) {
        return new ClienteDto(cliente.getId(), cliente.getNombre(), cliente.getContacto(), cliente.getCreadoEn());
    }
}
