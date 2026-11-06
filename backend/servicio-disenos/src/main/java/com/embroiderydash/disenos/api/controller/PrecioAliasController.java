package com.embroiderydash.disenos.api.controller;

import com.embroiderydash.disenos.application.service.PrecioDisenoService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/precios")
public class PrecioAliasController {
    private final PrecioDisenoService service;
    public PrecioAliasController(PrecioDisenoService service) { this.service=service; }
    @GetMapping("/{disenoId}") public Map<String,Object> obtener(@PathVariable Long disenoId) { return service.obtener(disenoId); }
}
