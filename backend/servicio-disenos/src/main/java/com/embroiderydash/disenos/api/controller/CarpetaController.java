package com.embroiderydash.disenos.api.controller;

import com.embroiderydash.disenos.api.dto.CarpetaMoverRequest;
import com.embroiderydash.disenos.api.dto.CarpetaRequest;
import com.embroiderydash.disenos.application.service.CarpetaCatalogoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/carpetas")
public class CarpetaController {
    private final CarpetaCatalogoService carpetas;
    public CarpetaController(CarpetaCatalogoService carpetas) { this.carpetas = carpetas; }

    @GetMapping("/contenido")
    public Map<String,Object> contenido(@RequestParam(required=false) Long carpetaId, @RequestParam(required=false) String q) { return carpetas.contenido(carpetaId,q); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> crear(@Valid @RequestBody CarpetaRequest body, Authentication auth) { return carpetas.crear(body,auth.getName()); }
    @PutMapping("/{id}")
    public Map<String,Object> renombrar(@PathVariable Long id,@Valid @RequestBody CarpetaRequest body) { return carpetas.renombrar(id,body); }
    @PatchMapping("/{id}/mover")
    public Map<String,Object> mover(@PathVariable Long id,@RequestBody CarpetaMoverRequest body) { return carpetas.mover(id,body); }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) { carpetas.eliminar(id); }
    @PatchMapping("/disenos/{id}/carpeta")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void moverDiseno(@PathVariable Long id,@RequestBody CarpetaMoverRequest body) { carpetas.moverDiseno(id,body.carpetaPadreId()); }
}
