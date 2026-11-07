package com.embroiderydash.inventario.api.controller;

import com.embroiderydash.inventario.api.dto.AlertaStockDto;
import com.embroiderydash.inventario.api.dto.HiloRequestDto;
import com.embroiderydash.inventario.api.dto.HiloResponseDto;
import com.embroiderydash.inventario.application.service.HiloService;
import com.embroiderydash.inventario.infrastructure.error.ErrorResponse;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario/hilos")
@Tag(name = "Catálogo de Hilos", description = "Endpoints para la gestión del catálogo de hilos, existencias de conos y alertas de stock crítico")
public class HiloController {

    private final HiloService hiloService;

    public HiloController(HiloService hiloService) {
        this.hiloService = hiloService;
    }

    @Operation(summary = "Listar catálogo de hilos", description = "Retorna todos los hilos activos y sus existencias actuales en almacén.")
    @GetMapping
    public ResponseEntity<List<HiloResponseDto>> listarTodos() {
        return ResponseEntity.ok(hiloService.listarTodos());
    }

    @Operation(summary = "Obtener hilo por ID", description = "Consulta las propiedades y existencias de un hilo específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Hilo encontrado",
            content = @Content(schema = @Schema(implementation = HiloResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Hilo no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<HiloResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(hiloService.obtenerPorId(id));
    }

    @Operation(summary = "Obtener hilo por código de referencia", description = "Permite a otros servicios buscar hilos por código comercial de catálogo.")
    @GetMapping("/referencia/{referencia}")
    public ResponseEntity<HiloResponseDto> obtenerPorReferencia(@PathVariable String referencia) {
        return ResponseEntity.ok(hiloService.obtenerPorReferencia(referencia));
    }

    @Operation(summary = "Listar alertas de stock mínimo", description = "Retorna hilos cuyo stock actual es menor o igual al umbral mínimo configurado.")
    @GetMapping("/alertas")
    public ResponseEntity<List<AlertaStockDto>> obtenerAlertas() {
        return ResponseEntity.ok(hiloService.obtenerAlertasStockMinimo());
    }

    @Operation(summary = "Registrar nuevo hilo en catálogo", description = "Registra un nuevo tipo/color de hilo verificando que la referencia no esté duplicada.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Hilo registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Referencia de hilo duplicada")
    })
    @PostMapping
    public ResponseEntity<HiloResponseDto> crearHilo(@Valid @RequestBody HiloRequestDto request) {
        HiloResponseDto creado = hiloService.crearHilo(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @Operation(summary = "Actualizar información de hilo", description = "Modifica propiedades del hilo. No altera el stock directamente (use movimientos para trazabilidad).")
    @PutMapping("/{id}")
    public ResponseEntity<HiloResponseDto> actualizarHilo(
            @PathVariable Long id,
            @Valid @RequestBody HiloRequestDto request) {
        return ResponseEntity.ok(hiloService.actualizarHilo(id, request));
    }

    @Operation(summary = "Baja lógica de hilo", description = "Desactiva un hilo del catálogo sin borrar su historial de movimientos.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> eliminarHilo(@PathVariable Long id) {
        hiloService.eliminarHilo(id);
        return ResponseEntity.noContent().build();
    }
}
