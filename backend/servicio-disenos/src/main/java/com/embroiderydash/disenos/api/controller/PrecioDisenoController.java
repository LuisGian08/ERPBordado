package com.embroiderydash.disenos.api.controller;

import com.embroiderydash.disenos.api.dto.PrecioRequest;
import com.embroiderydash.disenos.application.service.PrecioDisenoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/disenos/{id}/precio")
public class PrecioDisenoController {
    private final PrecioDisenoService precios;
    public PrecioDisenoController(PrecioDisenoService precios) { this.precios=precios; }
    @GetMapping public Map<String,Object> obtener(@PathVariable Long id) { return precios.obtener(id); }
    @PutMapping @PreAuthorize("hasAnyRole('ADMIN', 'ADMIN_EMPRESA')")
    public Map<String,Object> actualizar(@PathVariable Long id,@Valid @RequestBody PrecioRequest body,Authentication auth) { return precios.actualizar(id,body,auth.getName()); }
    @GetMapping("/historial") public List<Map<String,Object>> historial(@PathVariable Long id) { return precios.historial(id); }
}
