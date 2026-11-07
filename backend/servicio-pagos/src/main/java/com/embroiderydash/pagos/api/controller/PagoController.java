package com.embroiderydash.pagos.api.controller;

import com.embroiderydash.pagos.api.dto.PagoRequestDto;
import com.embroiderydash.pagos.api.dto.PagoResponseDto;
import com.embroiderydash.pagos.api.dto.ResumenPagoTrabajoDto;
import com.embroiderydash.pagos.api.dto.PagoOperadorRequestDto;
import com.embroiderydash.pagos.api.dto.PagoOperadorResponseDto;
import com.embroiderydash.pagos.application.service.PagoCreationResult;
import com.embroiderydash.pagos.application.service.PagoService;
import com.embroiderydash.pagos.application.service.PagoOperadorService;
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
    private final PagoOperadorService operadores;
    public PagoController(PagoService service,PagoOperadorService operadores) { this.service=service;this.operadores=operadores; }
    @PostMapping("/operadores")
    public ResponseEntity<PagoOperadorResponseDto> registrarPagoOperador(@Valid @RequestBody PagoOperadorRequestDto request,Authentication authentication){
        return ResponseEntity.status(HttpStatus.CREATED).body(operadores.registrar(request,authentication.getName()));
    }
    @GetMapping("/operadores")
    public Page<PagoOperadorResponseDto> pagosOperadores(@PageableDefault(size=100,sort="fechaPago",direction=org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable){return operadores.listar(pageable);}
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
