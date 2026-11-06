package com.embroiderydash.pagos.api.controller;

import com.embroiderydash.pagos.api.dto.PagoRequestDto;
import com.embroiderydash.pagos.api.dto.PagoResponseDto;
import com.embroiderydash.pagos.api.dto.ResumenPagoTrabajoDto;
import com.embroiderydash.pagos.application.service.PagoCreationResult;
import com.embroiderydash.pagos.application.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
public class PagoController {
    private final PagoService service;
    public PagoController(PagoService service) { this.service=service; }
    @PostMapping
    public ResponseEntity<PagoResponseDto> crear(@RequestHeader(value="Idempotency-Key",required=false) String key,
            @Valid @RequestBody PagoRequestDto request, Authentication authentication) {
        PagoCreationResult result=service.registrar(request,key,authentication.getName());
        return ResponseEntity.status(result.creado()?HttpStatus.CREATED:HttpStatus.OK).body(result.pago());
    }
    @GetMapping public Page<PagoResponseDto> ingresos(@PageableDefault(size=100, sort="creadoEn", direction=org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) { return service.ingresos(pageable); }
    @GetMapping("/trabajo/{trabajoId}") public List<PagoResponseDto> historial(@PathVariable Long trabajoId) { return service.historial(trabajoId); }
    @GetMapping("/trabajo/{trabajoId}/resumen") public ResumenPagoTrabajoDto resumen(@PathVariable Long trabajoId) { return service.resumen(trabajoId); }
    @PostMapping("/{id}/anular") public PagoResponseDto anular(@PathVariable Long id) { return service.anular(id); }
}
