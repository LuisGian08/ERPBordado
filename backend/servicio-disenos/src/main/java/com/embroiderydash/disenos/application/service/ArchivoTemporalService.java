package com.embroiderydash.disenos.application.service;

import com.embroiderydash.disenos.api.dto.ArchivoAnalizadoDto;
import com.embroiderydash.disenos.api.dto.DisenoRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.Duration;
import java.net.http.HttpClient;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import java.util.HexFormat;
import java.security.MessageDigest;
import java.util.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ArchivoTemporalService {
    private final Path temporaryDirectory;
    private final RestClient previewClient;
    private final String internalToken;
    private final ObjectMapper mapper;
    private final Path storageRoot;
    private final JdbcTemplate jdbc;
    private final int maxFileMb;

    public ArchivoTemporalService(@Value("${app.storage.upload-dir:/var/data/embroidery/disenos}") String storage,
                                  @Value("${app.clients.preview-url:http://localhost:8101}") String previewUrl,
                                  @Value("${PREVIEW_INTERNAL_TOKEN:}") String internalToken,
                                  ObjectMapper mapper, JdbcTemplate jdbc,
                                  @Value("${MAX_TECHNICAL_FILE_MB:25}") int maxFileMb) {
        this.storageRoot = Path.of(storage).toAbsolutePath().normalize();
        this.temporaryDirectory = Path.of(storage).toAbsolutePath().normalize().resolve("uploads-tmp");
        this.internalToken = internalToken;
        this.mapper = mapper;
        this.jdbc = jdbc;
        this.maxFileMb = Math.min(25,Math.max(1,maxFileMb));
        // The preview service (Uvicorn) expects HTTP/1.1; Java's default HTTP/2
        // negotiation uses h2c Upgrade on cleartext connections and can leave
        // multipart requests unread by Starlette's form parser.
        var httpClient=HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        var factory = new org.springframework.http.client.JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(20));
        this.previewClient = RestClient.builder().baseUrl(previewUrl).requestFactory(factory).build();
    }

    public ArchivoAnalizadoDto analizar(String originalName, byte[] bytes) {
        if (bytes.length == 0 || bytes.length > (long) maxFileMb * 1024 * 1024) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,"El archivo excede el tamaño permitido");
        String supplied=originalName==null?"bordado":originalName.replace('\\','/');
        String safeName=supplied.substring(supplied.lastIndexOf('/')+1).replaceAll("[\\p{Cntrl}\"]","_");
        if(safeName.isBlank()) safeName="bordado";
        String extension = extension(safeName);
        if (!extension.equals("DST") && !extension.equals("PES")) throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"Solo se aceptan archivos DST o PES");
        String id = UUID.randomUUID().toString();
        try {
            Files.createDirectories(temporaryDirectory);
            Path file = temporaryDirectory.resolve(id + "." + extension.toLowerCase(Locale.ROOT));
            Files.write(file, bytes, StandardOpenOption.CREATE_NEW);
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("archivo", new ByteArrayResource(bytes) {
                @Override public String getFilename() { return "upload." + extension.toLowerCase(Locale.ROOT); }
            }).contentType(MediaType.APPLICATION_OCTET_STREAM);
            JsonNode result = previewClient.post().uri("/api/v1/preview/analizar")
                    .header("X-Internal-Token", internalToken)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(builder.build()).retrieve().body(JsonNode.class);
            if (result == null) throw new IllegalStateException("El servicio de vista previa no devolvió datos");
            JsonNode stored = result.deepCopy();
            ((com.fasterxml.jackson.databind.node.ObjectNode) stored).put("nombreOriginal", safeName);
            ((com.fasterxml.jackson.databind.node.ObjectNode) stored).put("archivo", file.getFileName().toString());
            ((com.fasterxml.jackson.databind.node.ObjectNode) stored).put("creadoEpoch", Instant.now().getEpochSecond());
            mapper.writeValue(temporaryDirectory.resolve(id + ".json").toFile(), stored);
            return new ArchivoAnalizadoDto(id, safeName.replaceFirst("(?i)\\.[^.]+$", ""), extension,
                    result.path("puntadas").asInt(), decimal(result, "anchoMm"), decimal(result, "altoMm"),
                    result.path("cantidadColores").asInt(), result.path("previewPngBase64").asText());
        } catch (Exception ex) {
            try { Files.deleteIfExists(temporaryDirectory.resolve(id + "." + extension.toLowerCase(Locale.ROOT))); } catch (IOException ignored) { }
            if (ex instanceof org.springframework.web.client.HttpStatusCodeException response) {
                int status=response.getStatusCode().value();
                if(status==413||status==415||status==422) throw new ResponseStatusException(HttpStatus.valueOf(status),"El archivo de bordado no pudo validarse");
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"El servicio de análisis de bordados no está disponible",ex);
        }
    }

    @org.springframework.transaction.annotation.Transactional
    public void confirmar(String temporaryId,Long designId,DisenoRequestDto request) {
        if(temporaryId==null||!temporaryId.matches("[0-9a-fA-F-]{36}")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Archivo temporal inválido");
        Path metadata=temporaryDirectory.resolve(temporaryId+".json").normalize();
        try {
            if(!metadata.startsWith(temporaryDirectory)||!Files.exists(metadata)) throw new ResponseStatusException(HttpStatus.GONE,"El archivo temporal expiró; vuelve a analizarlo");
            JsonNode data=mapper.readTree(metadata.toFile());
            if(Instant.now().getEpochSecond()-data.path("creadoEpoch").asLong()>3600) throw new ResponseStatusException(HttpStatus.GONE,"El archivo temporal expiró; vuelve a analizarlo");
            Path source=temporaryDirectory.resolve(data.path("archivo").asText()).normalize();
            if(!source.startsWith(temporaryDirectory)||!Files.exists(source)) throw new ResponseStatusException(HttpStatus.GONE,"El archivo temporal ya no existe");
            String extension=extension(source.getFileName().toString()).toLowerCase(Locale.ROOT);
            Path files=storageRoot.resolve("files"); Path previews=storageRoot.resolve("previews");
            Files.createDirectories(files); Files.createDirectories(previews);
            Path destination=files.resolve(UUID.randomUUID()+"."+extension).normalize();
            Files.move(source,destination,StandardCopyOption.REPLACE_EXISTING);
            byte[] preview=Base64.getDecoder().decode(data.path("previewPngBase64").asText());
            byte[] thumbnail=Base64.getDecoder().decode(data.path("miniaturaPngBase64").asText());
            Path previewPath=previews.resolve(designId+"-preview.png"); Path thumbPath=previews.resolve(designId+"-thumb.png");
            Files.write(previewPath,preview); Files.write(thumbPath,thumbnail);
            byte[] bytes=Files.readAllBytes(destination);
            String md5=HexFormat.of().formatHex(MessageDigest.getInstance("MD5").digest(bytes));
            jdbc.update("insert into diseno_archivos(diseno_id,nombre_archivo,formato,ruta_almacenamiento,tamano_bytes,checksum_md5) values(?,?,?,?,?,?)",designId,data.path("nombreOriginal").asText(),extension.toUpperCase(Locale.ROOT),destination.toString(),bytes.length,md5);
            jdbc.update("update disenos set carpeta_id=?,cantidad_colores=?,preview_ruta=?,miniatura_ruta=? where id=?",request.getCarpetaId(),request.getCantidadColores()!=null?request.getCantidadColores():data.path("cantidadColores").asInt(),previewPath.toString(),thumbPath.toString(),designId);
            Files.deleteIfExists(metadata);
        } catch(ResponseStatusException ex) { throw ex; }
        catch(Exception ex) { throw new IllegalStateException("No se pudo guardar el archivo del diseño",ex); }
    }

    @org.springframework.transaction.annotation.Transactional(readOnly=true)
    public byte[] imagen(Long designId,boolean thumbnail) {
        String column=thumbnail?"miniatura_ruta":"preview_ruta";
        String path=jdbc.query("select "+column+" from disenos where id=? and activo=true",rs->rs.next()?rs.getString(1):null,designId);
        if(path==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"La vista previa no está disponible");
        Path image=Path.of(path).toAbsolutePath().normalize();
        try { if(!image.startsWith(storageRoot)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"La vista previa no está disponible"); return Files.readAllBytes(image); }
        catch(IOException ex) { throw new ResponseStatusException(HttpStatus.NOT_FOUND,"La vista previa no está disponible"); }
    }

    @org.springframework.transaction.annotation.Transactional(readOnly=true)
    public Download archivo(Long designId) {
        MapFile file=jdbc.query("select nombre_archivo,ruta_almacenamiento from diseno_archivos where diseno_id=?",rs->rs.next()?new MapFile(rs.getString(1),rs.getString(2)):null,designId);
        if(file==null) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El archivo técnico no está disponible");
        Path path=Path.of(file.path()).toAbsolutePath().normalize();
        try { if(!path.startsWith(storageRoot)) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El archivo técnico no está disponible"); return new Download(file.name(),Files.readAllBytes(path)); }
        catch(IOException ex) { throw new ResponseStatusException(HttpStatus.NOT_FOUND,"El archivo técnico no está disponible"); }
    }
    private record MapFile(String name,String path) { }
    public record Download(String nombre,byte[] bytes) { }

    @Scheduled(fixedDelay = 300000)
    public void limpiarCaducados() {
        if (!Files.isDirectory(temporaryDirectory)) return;
        try (var files = Files.list(temporaryDirectory)) {
            files.filter(p -> p.getFileName().toString().endsWith(".json")).forEach(meta -> {
                try {
                    JsonNode data = mapper.readTree(meta.toFile());
                    if (Instant.now().getEpochSecond() - data.path("creadoEpoch").asLong() > 3600) {
                        Files.deleteIfExists(temporaryDirectory.resolve(data.path("archivo").asText()));
                        Files.deleteIfExists(meta);
                    }
                } catch (IOException ignored) { }
            });
        } catch (IOException ignored) { }
    }

    private static String extension(String name) { int dot = name.lastIndexOf('.'); return dot < 0 ? "" : name.substring(dot + 1).toUpperCase(Locale.ROOT); }
    private static BigDecimal decimal(JsonNode node, String key) { return node.path(key).isNumber() ? node.path(key).decimalValue() : BigDecimal.ZERO; }
}
