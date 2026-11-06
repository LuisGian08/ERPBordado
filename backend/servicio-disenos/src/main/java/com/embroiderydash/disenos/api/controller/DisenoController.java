package com.embroiderydash.disenos.api.controller;

import com.embroiderydash.disenos.api.dto.DisenoRequestDto;
import com.embroiderydash.disenos.api.dto.DisenoResponseDto;
import com.embroiderydash.disenos.application.service.DisenoService;
import com.embroiderydash.disenos.application.service.ArchivoTemporalService;
import com.embroiderydash.disenos.api.dto.ArchivoAnalizadoDto;
import com.embroiderydash.disenos.infrastructure.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.embroiderydash.disenos.api.dto.CarpetaMoverRequest;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/disenos")
@Tag(name = "Diseños de Bordado", description = "Endpoints para la administración del catálogo de diseños, secuencias de hilos y archivos técnicos")
public class DisenoController {

    private final DisenoService disenoService;
    private final ArchivoTemporalService archivos;
    private final com.embroiderydash.disenos.application.service.CarpetaCatalogoService carpetas;

    public DisenoController(DisenoService disenoService, ArchivoTemporalService archivos, com.embroiderydash.disenos.application.service.CarpetaCatalogoService carpetas) {
        this.disenoService = disenoService;
        this.archivos = archivos;
        this.carpetas = carpetas;
    }

    @PostMapping(value = "/archivos/analizar", consumes = "multipart/form-data")
    public ResponseEntity<ArchivoAnalizadoDto> analizarArchivo(@RequestPart("archivo") MultipartFile archivo) throws java.io.IOException {
        return ResponseEntity.ok(archivos.analizar(archivo.getOriginalFilename(), archivo.getBytes()));
    }

    @Operation(summary = "Listar diseños activos", description = "Retorna todos los diseños de bordado activos disponibles para producción.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de diseños obtenida exitosamente",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DisenoResponseDto.class)))),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "403", description = "Permisos insuficientes", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<DisenoResponseDto>> listarTodos() {
        return ResponseEntity.ok(disenoService.listarTodos());
    }

    @Operation(summary = "Obtener diseño por ID", description = "Retorna el detalle completo de un diseño incluyendo su secuencia de colores y archivos técnicos asociados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Diseño encontrado",
            content = @Content(schema = @Schema(implementation = DisenoResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Diseño no encontrado o inactivo",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<DisenoResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(disenoService.obtenerPorId(id));
    }

    @Operation(summary = "Crear nuevo diseño de bordado", description = "Registra un nuevo diseño en el catálogo. Valida la unicidad del nombre.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Diseño creado exitosamente",
            content = @Content(schema = @Schema(implementation = DisenoResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Ya existe un diseño con el mismo nombre",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<DisenoResponseDto> crearDiseno(@Valid @RequestBody DisenoRequestDto request) {
        DisenoResponseDto creado = disenoService.crearDiseno(request);
        if (request.getArchivoTemporalId() != null && !request.getArchivoTemporalId().isBlank()) archivos.confirmar(request.getArchivoTemporalId(), creado.getId(), request);
        else carpetas.actualizarMetadatos(creado.getId(),request.getCarpetaId(),request.getCantidadColores());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PatchMapping("/{id}/carpeta")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void moverCarpeta(@PathVariable Long id, @RequestBody CarpetaMoverRequest request) { carpetas.moverDiseno(id,request.carpetaPadreId()); }

    @GetMapping(value="/{id}/preview", produces=MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> preview(@PathVariable Long id) { return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(archivos.imagen(id,false)); }

    @GetMapping(value="/{id}/miniatura", produces=MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> miniatura(@PathVariable Long id) { return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(archivos.imagen(id,true)); }

    @GetMapping("/{id}/archivo")
    public ResponseEntity<byte[]> archivo(@PathVariable Long id) {
        var file=archivos.archivo(id);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+file.nombre()+"\"").contentType(MediaType.APPLICATION_OCTET_STREAM).body(file.bytes());
    }

    @Operation(summary = "Actualizar diseño existente", description = "Actualiza las propiedades y secuencia de hilos de un diseño de bordado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Diseño actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = DisenoResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Diseño no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "El nuevo nombre ya pertenece a otro diseño",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<DisenoResponseDto> actualizarDiseno(
            @PathVariable Long id,
            @Valid @RequestBody DisenoRequestDto request) {
        DisenoResponseDto actualizado=disenoService.actualizarDiseno(id, request);
        carpetas.actualizarMetadatos(id,request.getCarpetaId(),request.getCantidadColores());
        return ResponseEntity.ok(actualizado);
    }

    @Operation(summary = "Eliminar diseño (baja lógica)", description = "Desactiva un diseño para preservar el historial de trabajos sin romper integridad.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Diseño desactivado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Diseño no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> eliminarDiseno(@PathVariable Long id) {
        disenoService.eliminarDiseno(id);
        return ResponseEntity.noContent().build();
    }
}
