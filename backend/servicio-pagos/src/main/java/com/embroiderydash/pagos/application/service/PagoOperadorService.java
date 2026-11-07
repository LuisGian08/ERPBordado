package com.embroiderydash.pagos.application.service;

import com.embroiderydash.pagos.api.dto.PagoOperadorRequestDto;
import com.embroiderydash.pagos.api.dto.PagoOperadorResponseDto;
import com.embroiderydash.pagos.domain.model.PagoOperador;
import com.embroiderydash.pagos.domain.model.TipoPagoOperador;
import com.embroiderydash.pagos.domain.repository.PagoOperadorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.util.List;

@Service
public class PagoOperadorService {
    private final PagoOperadorRepository repository;
    public PagoOperadorService(PagoOperadorRepository repository){this.repository=repository;}

    @Transactional
    public PagoOperadorResponseDto registrar(PagoOperadorRequestDto request,String registradoPor){
        if(request.semanaInicio().getDayOfWeek()!=DayOfWeek.MONDAY)throw bad("La semana debe comenzar un lunes");
        if(repository.existsByOperadorIgnoreCaseAndSemanaInicio(request.operador().trim(),request.semanaInicio()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Ya existe un pago para este operador en esa semana");
        List<java.time.LocalDate> days=request.diasAsistencia().stream().distinct().sorted().toList();
        if(days.size()!=request.diasAsistencia().size())throw bad("No repitas días en la asistencia");
        if(days.stream().anyMatch(day->day.isBefore(request.semanaInicio())||day.isAfter(request.semanaInicio().plusDays(6))))throw bad("La asistencia debe estar entre el lunes y el domingo seleccionados");
        BigDecimal base;
        BigDecimal daily=null,hours=null,hourlyRate=null;
        if(request.tipoPago()==TipoPagoOperador.DIA_COMPLETO){
            daily=positive(request.tarifaDiaria(),"Indica el pago por día completo");
            base=daily.multiply(BigDecimal.valueOf(days.size()));
        }else{
            hours=positive(request.horasRegulares(),"Indica las horas regulares trabajadas");
            hourlyRate=positive(request.tarifaHora(),"Indica el pago por hora");
            base=hours.multiply(hourlyRate);
        }
        BigDecimal overtime=request.horasExtras()==null?BigDecimal.ZERO:request.horasExtras();
        if(overtime.signum()<0)throw bad("Las horas extras no pueden ser negativas");
        BigDecimal overtimeRate=request.tarifaHoraExtra()==null?BigDecimal.ZERO:request.tarifaHoraExtra();
        if(overtime.signum()>0&&overtimeRate.signum()<=0)throw bad("Indica el pago por cada hora extra");
        if(overtimeRate.signum()<0)throw bad("El pago por hora extra no puede ser negativo");
        base=base.setScale(2,RoundingMode.HALF_UP);BigDecimal extras=overtime.multiply(overtimeRate).setScale(2,RoundingMode.HALF_UP);
        BigDecimal total=base.add(extras).setScale(2,RoundingMode.HALF_UP);
        PagoOperador saved=repository.save(new PagoOperador(request.operador().trim(),request.semanaInicio(),String.join(",",days.stream().map(java.time.LocalDate::toString).toList()),request.tipoPago(),daily,hours,hourlyRate,base,overtime,overtimeRate,extras,total,request.fechaPago(),request.notas(),registradoPor));
        return PagoOperadorResponseDto.from(saved);
    }

    @Transactional(readOnly=true)
    public Page<PagoOperadorResponseDto> listar(Pageable pageable){return repository.findAllByOrderByFechaPagoDesc(pageable).map(PagoOperadorResponseDto::from);}
    private static BigDecimal positive(BigDecimal value,String message){if(value==null||value.signum()<=0)throw bad(message);return value;}
    private static ResponseStatusException bad(String message){return new ResponseStatusException(HttpStatus.BAD_REQUEST,message);}
}
