package com.embroiderydash.disenos.application.service;

import com.embroiderydash.disenos.api.dto.PrecioRequest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

@Service
public class PrecioDisenoService {
    private final JdbcTemplate jdbc;
    public PrecioDisenoService(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    public Map<String,Object> obtener(Long id) {
        ensureDesign(id);
        try { return jdbc.queryForMap("select diseno_id as \"disenoId\",precio_unitario as \"precioUnitario\",moneda,actualizado_en as \"actualizadoEn\",actualizado_por as \"actualizadoPor\" from diseno_precios where diseno_id=?",id); }
        catch(org.springframework.dao.EmptyResultDataAccessException ex) { return Map.of("disenoId",id,"precioUnitario",java.math.BigDecimal.ZERO,"moneda","PEN","sinPrecio",true); }
    }
    @Transactional
    public Map<String,Object> actualizar(Long id, PrecioRequest req, String actor) {
        ensureDesign(id);
        var old=jdbc.query("select precio_unitario from diseno_precios where diseno_id=?", rs->rs.next()?rs.getBigDecimal(1):null,id);
        jdbc.update("insert into diseno_precios(diseno_id,precio_unitario,moneda,actualizado_en,actualizado_por) values(?,?,'PEN',CURRENT_TIMESTAMP,?) on conflict(diseno_id) do update set precio_unitario=excluded.precio_unitario,moneda='PEN',actualizado_en=CURRENT_TIMESTAMP,actualizado_por=excluded.actualizado_por",id,req.precioUnitario(),actor);
        jdbc.update("insert into diseno_precios_historial(diseno_id,precio_anterior,precio_nuevo,motivo,cambiado_por) values(?,?,?,?,?)",id,old,req.precioUnitario(),req.motivo(),actor);
        return obtener(id);
    }
    public List<Map<String,Object>> historial(Long id) {
        ensureDesign(id);
        return jdbc.queryForList("select id,diseno_id as \"disenoId\",precio_anterior as \"precioAnterior\",precio_nuevo as \"precioNuevo\",motivo,cambiado_por as \"cambiadoPor\",cambiado_en as \"cambiadoEn\" from diseno_precios_historial where diseno_id=? order by cambiado_en desc",id);
    }
    private void ensureDesign(Long id) { Integer n=jdbc.queryForObject("select count(*) from disenos where id=? and activo=true",Integer.class,id); if(n==null||n==0)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Diseño no encontrado"); }
}
