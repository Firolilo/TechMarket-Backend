package com.techmarket.techmarket.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sirve las imagenes subidas (guardadas en {@code app.uploads.dir}) bajo la ruta publica {@code
 * /uploads/**}. Asi un {@code <img src="http://host:8082/uploads/...">} resuelve al archivo real en
 * disco. La ruta esta permitida sin autenticacion en {@code SecurityConfig}.
 */
@Configuration
public class UploadsResourceConfig implements WebMvcConfigurer {

    private final String uploadsDir;

    public UploadsResourceConfig(@Value("${app.uploads.dir:uploads}") String uploadsDir) {
        this.uploadsDir = uploadsDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path base = Path.of(uploadsDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(base);
        } catch (IOException ignored) {
            // Si no se puede crear al iniciar, se intentara al primer upload.
        }
        String location = base.toUri().toString();
        if (!location.endsWith("/")) {
            location = location + "/";
        }
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}
