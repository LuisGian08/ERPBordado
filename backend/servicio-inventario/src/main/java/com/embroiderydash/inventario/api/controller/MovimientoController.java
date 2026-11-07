package com.embroiderydash.inventario.api.controller;

import com.embroiderydash.inventario.api.dto.MovimientoRequestDto;
import com.embroiderydash.inventario.api.dto.MovimientoResponseDto;
import com.embroiderydash.inventario.application.service.MovimientoInventarioService;
import com.embroiderydash.inventario.infrastructure.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario/movimientos")
@Tag(name = "Movimientos de Inventario", description = "Endpoints para registro de entradas, salidas y consulta histórica de trazabilidad de conos")
public class MovimientoController {

    private final MovimientoInventarioService movimientoService;

    public MovimientoController(MovimientoInventarioService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @Operation(summary = "Registrar movimiento de almacén", description = "Registra una entrada, salida o ajuste físico de conos con bloqueo pesimista y prevención de stock negativo.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Movimiento registrado exitosamente",
            content = @Content(schema = @Schema(implementation = MovimientoResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Hilo no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Stock insuficiente para realizar la salida",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<MovimientoResponseDto> registrarMovimiento(
            @Valid @RequestBody MovimientoRequestDto request,
            Authentication authentication) {
        String operador = (authentication != null && authentication.getName() != null)
                ? authentication.getName()
                : "sistema";

        MovimientoResponseDto resultado = movimientoService.registrarMovimiento(request, operador);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(resultado.getId())
                .toUri();
        return ResponseEntity.created(location).body(resultado);
    }

    @Operation(summary = "Listar movimientos de un hilo específico", description = "Retorna el historial cronológico descendente de cambios de stock de un cono de hilo.")
    @GetMapping("/hilo/{hiloId}")
    public ResponseEntity<List<MovimientoResponseDto>> listarPorHilo(@PathVariable Long hiloId) {
        return ResponseEntity.ok(movimientoService.listarMovimientosPorHilo(hiloId));
    }

    @Operation(summary = "Listar todos los movimientos paginados", description = "Retorna el historial global de operaciones de almacén con soporte de paginación.")
    @GetMapping
    public ResponseEntity<Page<MovimientoResponseDto>> listarTodos(
            @PageableDefault(size = 20, sort = "fechaMovimiento") Pageable pageable) {
        return ResponseEntity.ok(movimientoService.listarTodos(pageable));
    }

    @Operation(summary = "Listar gastos por compra de hilos", description = "Retorna las entradas de hilo con el precio unitario y gasto total guardados al registrar la compra.")
    @GetMapping("/gastos")
    public ResponseEntity<Page<MovimientoResponseDto>> listarGastos(
            @PageableDefault(size = 100, sort = "fechaMovimiento") Pageable pageable) {
        return ResponseEntity.ok(movimientoService.listarGastos(pageable));
    }
}
