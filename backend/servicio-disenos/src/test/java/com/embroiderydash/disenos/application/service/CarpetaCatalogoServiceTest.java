package com.embroiderydash.disenos.application.service;

import com.embroiderydash.disenos.api.dto.CarpetaMoverRequest;
import com.embroiderydash.disenos.api.dto.CarpetaRequest;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CarpetaCatalogoServiceTest {
    @Test
    void puedeEntrarAUnaCarpetaRaizConPadreNulo() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.queryForMap(anyString(), any(Object[].class))).thenReturn(folder(3L, null, "Huy Can"));
        when(jdbc.queryForList(anyString(), any(Object[].class))).thenReturn(List.of());

        Map<String,Object> contents = new CarpetaCatalogoService(jdbc).contenido(3L, null);

        assertEquals("Huy Can", ((Map<?,?>) contents.get("carpetaActual")).get("nombre"));
        assertEquals(1, ((List<?>) contents.get("ruta")).size());
        assertTrue(((List<?>) contents.get("disenos")).isEmpty());
    }

    @Test
    void renombraCarpetaRaizConPadreNulo() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.queryForMap(anyString(), any(Object[].class))).thenReturn(folder(1L, null, "Bordados"), folder(1L, null, "Logos"));
        when(jdbc.queryForObject(anyString(), eq(Integer.class), any(), any())).thenReturn(0);
        when(jdbc.update(anyString(), any(), any())).thenReturn(1);

        Map<String,Object> renamed = new CarpetaCatalogoService(jdbc).renombrar(1L, new CarpetaRequest("Logos", null));

        assertEquals("Logos", renamed.get("nombre"));
        verify(jdbc).update("update carpetas set nombre=? where id=?", "Logos", 1L);
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void mueveCarpetaAUnaCarpetaHermana() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        when(jdbc.queryForMap(anyString(), any(Object[].class))).thenAnswer(invocation -> {
            Object[] args = invocation.getArgument(1);
            long id = ((Number) args[0]).longValue();
            return id == 10 ? folder(10L, null, "Bordados") : folder(20L, null, "Archivo");
        });
        when(jdbc.query(anyString(), any(RowMapper.class), any())).thenReturn(List.of());
        when(jdbc.queryForObject(anyString(), eq(Integer.class), any(), any())).thenReturn(0);
        when(jdbc.update(anyString(), any(), any())).thenReturn(1);

        Map<String,Object> moved = new CarpetaCatalogoService(jdbc).mover(10L, new CarpetaMoverRequest(20L));

        assertEquals("Bordados", moved.get("nombre"));
        verify(jdbc).update("update carpetas set carpeta_padre_id=? where id=?", 20L, 10L);
    }

    private static Map<String,Object> folder(Long id, Long parent, String name) {
        Map<String,Object> result = new HashMap<>();
        result.put("id", id);
        result.put("nombre", name);
        result.put("carpetaPadreId", parent);
        return result;
    }
}
