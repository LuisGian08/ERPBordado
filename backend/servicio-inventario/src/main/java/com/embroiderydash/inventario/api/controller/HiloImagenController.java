package com.embroiderydash.inventario.api.controller;

import com.embroiderydash.inventario.application.service.HiloImagenService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/inventario/hilos")
public class HiloImagenController {
    private final HiloImagenService imagenes;
    public HiloImagenController(HiloImagenService imagenes) { this.imagenes = imagenes; }

    @PostMapping(value = "/{id}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> guardar(@PathVariable Long id, @RequestPart("archivo") MultipartFile archivo) throws IOException {
        imagenes.guardar(id, archivo.getBytes());
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/imagen", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> leer(@PathVariable Long id) { return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imagenes.leer(id)); }
}
