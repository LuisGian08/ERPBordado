package com.embroiderydash.inventario.application.service;

import com.embroiderydash.inventario.domain.exception.HiloNotFoundException;
import com.embroiderydash.inventario.domain.model.Hilo;
import com.embroiderydash.inventario.domain.repository.HiloRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Service
public class HiloImagenService {
    private final HiloRepository hilos;
    private final Path directory;

    public HiloImagenService(HiloRepository hilos, @Value("${app.storage.upload-dir:/var/data/embroidery/inventario}") String directory) {
        this.hilos = hilos;
        this.directory = Path.of(directory).toAbsolutePath().normalize();
    }

    @Transactional
    public void guardar(Long id, byte[] bytes) {
        if (bytes.length > 2 * 1024 * 1024) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,"La imagen debe pesar hasta 2 MB");
        if (bytes.length == 0) throw new IllegalArgumentException("La imagen no puede estar vacía");
        Hilo hilo = hilos.findActivoById(id).orElseThrow(() -> new HiloNotFoundException(id));
        try (var imageInput = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            var readers = ImageIO.getImageReaders(imageInput);
            if (!readers.hasNext()) throw new IllegalArgumentException("La imagen debe ser JPG o PNG válida");
            var reader = readers.next();
            String format = reader.getFormatName().toLowerCase();
            if (!format.equals("jpeg") && !format.equals("jpg") && !format.equals("png")) throw new IllegalArgumentException("La imagen debe ser JPG o PNG");
            reader.setInput(imageInput, true, true);
            int sourceWidth=reader.getWidth(0), sourceHeight=reader.getHeight(0);
            if(sourceWidth<=0||sourceHeight<=0||(long)sourceWidth*sourceHeight>40_000_000L) throw new IllegalArgumentException("La imagen excede la resolución permitida");
            BufferedImage decoded = reader.read(0);
            reader.dispose();
            if (decoded == null || decoded.getWidth() <= 0 || decoded.getHeight() <= 0 || (long) decoded.getWidth() * decoded.getHeight() > 40_000_000L) throw new IllegalArgumentException("La imagen excede la resolución permitida");
            double ratio = Math.min(1d, 600d / Math.max(decoded.getWidth(), decoded.getHeight()));
            int width = Math.max(1, (int) Math.round(decoded.getWidth() * ratio));
            int height = Math.max(1, (int) Math.round(decoded.getHeight() * ratio));
            BufferedImage clean = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = clean.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.drawImage(decoded, 0, 0, width, height, null);
            graphics.dispose();
            Files.createDirectories(directory);
            Path destination = directory.resolve(UUID.randomUUID() + ".png").normalize();
            if (!destination.startsWith(directory)) throw new IllegalArgumentException("Ruta de imagen inválida");
            ImageIO.write(clean, "png", destination.toFile());
            hilo.setImagenRuta(destination.toString());
            hilo.setImagenContentType("image/png");
            hilos.save(hilo);
        } catch (IOException ex) {
            throw new IllegalArgumentException("No se pudo procesar la imagen", ex);
        }
    }

    @Transactional(readOnly = true)
    public byte[] leer(Long id) {
        Hilo hilo = hilos.findActivoById(id).orElseThrow(() -> new HiloNotFoundException(id));
        if (hilo.getImagenRuta() == null) throw new HiloNotFoundException(id);
        Path imagePath;
        try { imagePath = Path.of(hilo.getImagenRuta()).toAbsolutePath().normalize(); }
        catch (InvalidPathException ex) { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La imagen del hilo no está disponible"); }
        if (!imagePath.startsWith(directory) || !Files.isRegularFile(imagePath))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La imagen del hilo no está disponible");
        try { return Files.readAllBytes(imagePath); }
        catch (IOException ex) { throw new IllegalStateException("No se pudo leer la imagen del hilo", ex); }
    }
}
