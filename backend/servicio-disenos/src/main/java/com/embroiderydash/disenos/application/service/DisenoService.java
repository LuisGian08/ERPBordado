package com.embroiderydash.disenos.application.service;

import com.embroiderydash.disenos.api.dto.ArchivoTecnicoDto;
import com.embroiderydash.disenos.api.dto.ColorHiloDto;
import com.embroiderydash.disenos.api.dto.DisenoRequestDto;
import com.embroiderydash.disenos.api.dto.DisenoResponseDto;
import com.embroiderydash.disenos.domain.exception.DisenoDuplicadoException;
import com.embroiderydash.disenos.domain.exception.DisenoNotFoundException;
import com.embroiderydash.disenos.domain.model.ArchivoTecnico;
import com.embroiderydash.disenos.domain.model.ColorHilo;
import com.embroiderydash.disenos.domain.model.Diseno;
import com.embroiderydash.disenos.domain.repository.DisenoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DisenoService {

    private final DisenoRepository disenoRepository;

    public DisenoService(DisenoRepository disenoRepository) {
        this.disenoRepository = disenoRepository;
    }

    @Transactional(readOnly = true)
    public List<DisenoResponseDto> listarTodos() {
        return disenoRepository.findAllActivos().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DisenoResponseDto obtenerPorId(Long id) {
        Diseno diseno = disenoRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new DisenoNotFoundException(id));
        return mapToResponseDto(diseno);
    }

    @Transactional
    public DisenoResponseDto crearDiseno(DisenoRequestDto request) {
        // Regla de negocio: unicidad del nombre insensible a mayúsculas
        if (disenoRepository.existsByNombreIgnoreCase(request.getNombre().trim())) {
            throw new DisenoDuplicadoException(request.getNombre().trim());
        }

        Diseno diseno = new Diseno();
        diseno.setNombre(request.getNombre().trim());
        diseno.setDescripcion(request.getDescripcion());
        diseno.setCantidadPuntadas(request.getCantidadPuntadas());
        diseno.setAnchoMm(request.getAnchoMm());
        diseno.setAltoMm(request.getAltoMm());
        diseno.setFormatosAdmitidos(request.getFormatosAdmitidos() != null ? request.getFormatosAdmitidos() : "DST,PES");
        diseno.setObservacionesTecnicas(request.getObservacionesTecnicas());
        diseno.setActivo(true);

        if (request.getColores() != null && !request.getColores().isEmpty()) {
            for (ColorHiloDto cDto : request.getColores()) {
                ColorHilo color = new ColorHilo(
                        cDto.getOrdenSecuencia(),
                        cDto.getCodigoColor(),
                        cDto.getNombreColor(),
                        cDto.getReferenciaHilo()
                );
                diseno.addColor(color);
            }
        }

        Diseno guardado = disenoRepository.save(diseno);
        return mapToResponseDto(guardado);
    }

    @Transactional
    public DisenoResponseDto actualizarDiseno(Long id, DisenoRequestDto request) {
        Diseno diseno = disenoRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new DisenoNotFoundException(id));

        // Regla de unicidad evitando colisión con otros registros
        if (disenoRepository.existsByNombreIgnoreCaseAndIdNot(request.getNombre().trim(), id)) {
            throw new DisenoDuplicadoException(request.getNombre().trim());
        }

        diseno.setNombre(request.getNombre().trim());
        diseno.setDescripcion(request.getDescripcion());
        diseno.setCantidadPuntadas(request.getCantidadPuntadas());
        diseno.setAnchoMm(request.getAnchoMm());
        diseno.setAltoMm(request.getAltoMm());
        if (request.getFormatosAdmitidos() != null) {
            diseno.setFormatosAdmitidos(request.getFormatosAdmitidos());
        }
        diseno.setObservacionesTecnicas(request.getObservacionesTecnicas());

        // Reemplazo de secuencia de colores
        diseno.clearColores();
        if (request.getColores() != null) {
            for (ColorHiloDto cDto : request.getColores()) {
                ColorHilo color = new ColorHilo(
                        cDto.getOrdenSecuencia(),
                        cDto.getCodigoColor(),
                        cDto.getNombreColor(),
                        cDto.getReferenciaHilo()
                );
                diseno.addColor(color);
            }
        }

        Diseno actualizado = disenoRepository.save(diseno);
        return mapToResponseDto(actualizado);
    }

    @Transactional
    public void eliminarDiseno(Long id) {
        Diseno diseno = disenoRepository.findById(id)
                .filter(Diseno::getActivo)
                .orElseThrow(() -> new DisenoNotFoundException(id));
        // Baja lógica para mantener integridad referencial histórica con trabajos
        diseno.setActivo(false);
        disenoRepository.save(diseno);
    }

    private DisenoResponseDto mapToResponseDto(Diseno entity) {
        DisenoResponseDto dto = new DisenoResponseDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setCantidadPuntadas(entity.getCantidadPuntadas());
        dto.setAnchoMm(entity.getAnchoMm());
        dto.setAltoMm(entity.getAltoMm());
        dto.setFormatosAdmitidos(entity.getFormatosAdmitidos());
        dto.setObservacionesTecnicas(entity.getObservacionesTecnicas());
        dto.setActivo(entity.getActivo());
        dto.setCreadoEn(entity.getCreadoEn());
        dto.setActualizadoEn(entity.getActualizadoEn());

        if (entity.getColores() != null) {
            dto.setColores(entity.getColores().stream()
                    .map(c -> {
                        ColorHiloDto cDto = new ColorHiloDto(
                                c.getOrdenSecuencia(),
                                c.getCodigoColor(),
                                c.getNombreColor(),
                                c.getReferenciaHilo()
                        );
                        cDto.setId(c.getId());
                        return cDto;
                    })
                    .collect(Collectors.toList()));
        }

        if (entity.getArchivos() != null) {
            dto.setArchivos(entity.getArchivos().stream()
                    .map(a -> new ArchivoTecnicoDto(
                            a.getId(),
                            a.getNombreArchivo(),
                            a.getFormato(),
                            a.getRutaAlmacenamiento(),
                            a.getTamanoBytes(),
                            a.getChecksumMd5(),
                            a.getSubidoEn()
                    ))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
