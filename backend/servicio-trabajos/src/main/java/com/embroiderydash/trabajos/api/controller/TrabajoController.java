package com.embroiderydash.trabajos.api.controller;

import com.embroiderydash.trabajos.api.dto.*;
import com.embroiderydash.trabajos.application.service.CalculadoraCostoService;
import com.embroiderydash.trabajos.application.service.TrabajoService;
import com.embroiderydash.trabajos.infrastructure.error.ErrorResponse;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/trabajos")
@Tag(name = "Órdenes de Trabajo y Costos", description = "Endpoints para la gestión del ciclo de vida de bordados, simulación de costos y cálculo de rentabilidad")
public class TrabajoController {

    private final TrabajoService trabajoService;
    private final CalculadoraCostoService calculadoraCostoService;

    public TrabajoController(TrabajoService trabajoService, CalculadoraCostoService calculadoraCostoService) {
        this.trabajoService = trabajoService;
        this.calculadoraCostoService = calculadoraCostoService;
    }

    @Operation(summary = "Simular y calcular costo estimado", description = "Calcula el desglose del costo de producción según puntadas y prendas sin registrar una orden.")
    @PostMapping("/calcular-costo")
    public ResponseEntity<CostoEstimadoDto> simularCosto(@Valid @RequestBody CalcularCostoRequestDto request) {
        return ResponseEntity.ok(calculadoraCostoService.calcularCosto(request));
    }

    @Operation(summary = "Listar órdenes de trabajo", description = "Retorna el listado paginado de órdenes de trabajo con filtrado opcional por nombre de cliente.")
    @GetMapping
    public ResponseEntity<Page<TrabajoResponseDto>> listarTrabajos(
            @RequestParam(required = false) String cliente,
            @PageableDefault(size = 20, sort = "creadoEn") Pageable pageable) {
        return ResponseEntity.ok(trabajoService.listarTrabajos(cliente, pageable));
    }

    @Operation(summary = "Obtener orden de trabajo por ID", description = "Consulta el detalle, costos y margen estimado de un trabajo específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden encontrada"),
        @ApiResponse(responseCode = "404", description = "Orden de trabajo no encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TrabajoResponseDto> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(trabajoService.obtenerPorId(id));
    }

    @Operation(summary = "Obtener orden de trabajo por código", description = "Permite a otros servicios (como pagos) consultar una orden mediante su código público.")
    @GetMapping("/codigo/{codigoTrabajo}")
    public ResponseEntity<TrabajoResponseDto> obtenerPorCodigo(@PathVariable String codigoTrabajo) {
        return ResponseEntity.ok(trabajoService.obtenerPorCodigo(codigoTrabajo));
    }

    @Operation(summary = "Registrar nueva orden de trabajo", description = "Crea la orden calculando automáticamente el costo estimado y margen comercial inicial.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Orden de trabajo creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Código de orden de trabajo duplicado")
    })
    @PostMapping
    public ResponseEntity<TrabajoResponseDto> crearTrabajo(@Valid @RequestBody TrabajoRequestDto request) {
        TrabajoResponseDto creado = trabajoService.crearTrabajo(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @Operation(summary = "Actualizar estado de producción", description = "Realiza la transición de estado (COTIZADO -> CONFIRMADO -> EN_PROCESO -> TERMINADO -> ENTREGADO).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
        @ApiResponse(responseCode = "422", description = "Transición de estado prohibida")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<TrabajoResponseDto> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarEstadoDto request) {
        return ResponseEntity.ok(trabajoService.actualizarEstado(id, request));
    }

    @Operation(summary = "Liquidar costo real de producción", description = "Registra el costo real medido tras finalizar la producción, diferenciándolo del estimado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Costo real liquidado exitosamente"),
        @ApiResponse(responseCode = "403", description = "Permiso restringido a administradores"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @PostMapping("/{id}/liquidar-costo")
    public ResponseEntity<TrabajoResponseDto> liquidarCostoReal(
            @PathVariable Long id,
            @Valid @RequestBody LiquidarCostoRealDto request) {
        return ResponseEntity.ok(trabajoService.liquidarCostoReal(id, request));
    }
}
