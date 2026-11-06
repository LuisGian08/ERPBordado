package com.embroiderydash.disenos.application.service;

import com.embroiderydash.disenos.api.dto.CarpetaMoverRequest;
import com.embroiderydash.disenos.api.dto.CarpetaRequest;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;

@Service
public class CarpetaCatalogoService {
    private final JdbcTemplate jdbc;
    public CarpetaCatalogoService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Map<String,Object> contenido(Long folderId, String query) {
        Map<String,Object> current = folderId == null ? null : folder(folderId);
        List<Map<String,Object>> path = new ArrayList<>();
        Map<String,Object> cursor = current;
        while (cursor != null) {
            path.add(cursor);
            Object parent = value(cursor,"carpetaPadreId");
            cursor = parent == null ? null : folder(((Number) parent).longValue());
        }
        Collections.reverse(path);
        List<Map<String,Object>> folders = folderId == null
                ? jdbc.queryForList("select f.id, f.nombre, (select count(*) from disenos d where d.carpeta_id=f.id and d.activo=true) as \"cantidadDisenos\", (select count(*) from carpetas c where c.carpeta_padre_id=f.id) as \"cantidadSubcarpetas\" from carpetas f where f.carpeta_padre_id is null order by lower(f.nombre)")
                : jdbc.queryForList("select f.id, f.nombre, (select count(*) from disenos d where d.carpeta_id=f.id and d.activo=true) as \"cantidadDisenos\", (select count(*) from carpetas c where c.carpeta_padre_id=f.id) as \"cantidadSubcarpetas\" from carpetas f where f.carpeta_padre_id=? order by lower(f.nombre)", folderId);
        String search = query == null || query.isBlank() ? null : "%" + query.trim().toLowerCase(Locale.ROOT) + "%";
        String sql = "select d.id,d.nombre,d.cantidad_puntadas as \"puntadas\",d.ancho_mm as \"anchoMm\",d.alto_mm as \"altoMm\",d.cantidad_colores as \"cantidadColores\",(exists(select 1 from diseno_archivos a where a.diseno_id=d.id)) as \"tieneArchivo\",p.precio_unitario as \"precioUnitario\",coalesce(p.moneda,'PEN') as \"moneda\",d.preview_ruta as \"previewRuta\",d.miniatura_ruta as \"miniaturaRuta\" from disenos d left join diseno_precios p on p.diseno_id=d.id where d.activo=true and " + (folderId == null ? "d.carpeta_id is null" : "d.carpeta_id=?") + (search == null ? "" : " and lower(d.nombre) like ?") + " order by lower(d.nombre)";
        List<Object> args = new ArrayList<>(); if (folderId != null) args.add(folderId); if (search != null) args.add(search);
        List<Map<String,Object>> designs = jdbc.queryForList(sql, args.toArray());
        designs.forEach(d -> {
            d.put("tieneMiniatura", miniaturaExiste(d.get("miniaturaRuta")));
            d.remove("previewRuta");
            d.remove("miniaturaRuta");
        });
        return Map.of("carpetaActual", current == null ? Map.of() : current, "ruta", path, "subcarpetas", folders, "disenos", designs);
    }

    @Transactional
    public Map<String,Object> crear(CarpetaRequest request, String actor) {
        String name = cleanName(request.nombre());
        Long parent = request.carpetaPadreId(); validateParent(parent);
        ensureUnique(name,parent,null);
        jdbc.update("insert into carpetas(nombre,carpeta_padre_id,creado_por) values (?,?,?)",name,parent,actor);
        return jdbc.queryForMap("select id,nombre,carpeta_padre_id as \"carpetaPadreId\" from carpetas where lower(nombre)=lower(?) and carpeta_padre_id is not distinct from ?",name,parent);
    }

    @Transactional
    public Map<String,Object> renombrar(Long id, CarpetaRequest request) {
        Map<String,Object> current=folder(id); String name=cleanName(request.nombre()); Long parent=num(value(current,"carpetaPadreId"));
        ensureUnique(name,parent,id); jdbc.update("update carpetas set nombre=? where id=?",name,id); return folder(id);
    }

    @Transactional
    public Map<String,Object> mover(Long id, CarpetaMoverRequest request) {
        Map<String,Object> current=folder(id); Long parent=request.carpetaPadreId(); validateParent(parent);
        if (Objects.equals(id,parent)) throw bad("Una carpeta no puede moverse dentro de sí misma");
        if (parent!=null && descendants(id).contains(parent)) throw bad("Una carpeta no puede moverse dentro de sus descendientes");
        if (depth(parent)+subtreeDepth(id)>10) throw bad("La profundidad máxima de carpetas es 10");
        ensureUnique((String)current.get("nombre"),parent,id);
        jdbc.update("update carpetas set carpeta_padre_id=? where id=?",parent,id); return folder(id);
    }

    @Transactional
    public void eliminar(Long id) {
        folder(id);
        Integer children=jdbc.queryForObject("select count(*) from carpetas where carpeta_padre_id=?",Integer.class,id);
        Integer designs=jdbc.queryForObject("select count(*) from disenos where carpeta_id=? and activo=true",Integer.class,id);
        if ((children!=null && children>0)||(designs!=null && designs>0)) throw new ResponseStatusException(HttpStatus.CONFLICT,"La carpeta no está vacía: contiene diseños o subcarpetas");
        jdbc.update("delete from carpetas where id=?",id);
    }

    @Transactional
    public void moverDiseno(Long designId, Long folderId) {
        Integer exists=jdbc.queryForObject("select count(*) from disenos where id=? and activo=true",Integer.class,designId);
        if(exists==null||exists==0) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Diseño no encontrado");
        if(folderId!=null) folder(folderId);
        jdbc.update("update disenos set carpeta_id=? where id=?",folderId,designId);
    }

    @Transactional
    public void actualizarMetadatos(Long designId,Long folderId,Integer colors) {
        Integer exists=jdbc.queryForObject("select count(*) from disenos where id=? and activo=true",Integer.class,designId);
        if(exists==null||exists==0) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Diseño no encontrado");
        if(folderId!=null) folder(folderId);
        if(colors!=null && colors<0) throw bad("La cantidad de colores no puede ser negativa");
        if(folderId!=null || colors!=null) jdbc.update("update disenos set carpeta_id=coalesce(?,carpeta_id),cantidad_colores=coalesce(?,cantidad_colores) where id=?",folderId,colors,designId);
    }

    private Map<String,Object> folder(Long id) {
        try { return jdbc.queryForMap("select id,nombre,carpeta_padre_id as \"carpetaPadreId\" from carpetas where id=?",id); }
        catch (org.springframework.dao.EmptyResultDataAccessException ex) { throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Carpeta no encontrada"); }
    }
    private void validateParent(Long parent) { if(parent!=null) { folder(parent); if(depth(parent)>=10) throw bad("La profundidad máxima de carpetas es 10"); } }
    private void ensureUnique(String name,Long parent,Long self) {
        Integer count=parent==null?jdbc.queryForObject("select count(*) from carpetas where carpeta_padre_id is null and lower(nombre)=lower(?) and id is distinct from ?",Integer.class,name,self):jdbc.queryForObject("select count(*) from carpetas where carpeta_padre_id=? and lower(nombre)=lower(?) and id is distinct from ?",Integer.class,parent,name,self);
        if(count!=null&&count>0) throw new ResponseStatusException(HttpStatus.CONFLICT,"Ya existe una carpeta con ese nombre en este nivel");
    }
    private Set<Long> descendants(Long id) { Set<Long> found=new HashSet<>(); List<Long> todo=new ArrayList<>(List.of(id)); while(!todo.isEmpty()){Long parent=todo.remove(todo.size()-1); List<Long> children=jdbc.query("select id from carpetas where carpeta_padre_id=?",(rs,n)->rs.getLong(1),parent); for(Long child:children)if(found.add(child))todo.add(child);} return found; }
    private int depth(Long id) { int depth=0; while(id!=null){Object parent=value(folder(id),"carpetaPadreId"); id=num(parent); if(id!=null)depth++;} return depth; }
    private int subtreeDepth(Long id) { int max=0; for(Long child:descendants(id))max=Math.max(max,depth(child)-depth(id)); return max; }
    private static Long num(Object value) { return value instanceof Number n?n.longValue():null; }
    private static boolean miniaturaExiste(Object value) {
        if (value == null || value.toString().isBlank()) return false;
        try { Path path = Path.of(value.toString()); return Files.isRegularFile(path) && Files.isReadable(path); }
        catch (InvalidPathException ex) { return false; }
    }
    private static Object value(Map<String,Object> map,String key) {
        // Stream.findFirst() throws when the matched value is null; root folders have a null parent.
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(key)) return entry.getValue();
        }
        return null;
    }
    private static String cleanName(String value) { if(value==null||value.isBlank()||value.trim().length()>120)throw bad("El nombre de carpeta debe tener entre 1 y 120 caracteres"); return value.trim(); }
    private static ResponseStatusException bad(String text) { return new ResponseStatusException(HttpStatus.BAD_REQUEST,text); }
}
