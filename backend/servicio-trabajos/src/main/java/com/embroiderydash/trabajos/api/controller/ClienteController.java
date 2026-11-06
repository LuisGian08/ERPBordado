package com.embroiderydash.trabajos.api.controller;

import com.embroiderydash.trabajos.api.dto.ClienteDto;
import com.embroiderydash.trabajos.api.dto.ClienteRequestDto;
import com.embroiderydash.trabajos.domain.model.Cliente;
import com.embroiderydash.trabajos.domain.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {
    private final ClienteRepository repository;
    public ClienteController(ClienteRepository repository) { this.repository = repository; }

    @GetMapping
    public List<ClienteDto> listar(@RequestParam(required = false) String q) {
        List<Cliente> clientes = q == null || q.isBlank()
                ? repository.findTop50ByOrderByNombreAsc()
                : repository.findTop50ByNombreContainingIgnoreCaseOrderByNombreAsc(q.trim());
        return clientes.stream().map(ClienteDto::from).toList();
    }

    @PostMapping
    public ResponseEntity<ClienteDto> crear(@Valid @RequestBody ClienteRequestDto request) {
        Cliente cliente = new Cliente();
        cliente.setNombre(request.nombre().trim());
        cliente.setContacto(request.contacto() == null || request.contacto().isBlank() ? null : request.contacto().trim());
        Cliente guardado = repository.save(cliente);
        return ResponseEntity.created(URI.create("/api/v1/clientes/" + guardado.getId())).body(ClienteDto.from(guardado));
    }
}
