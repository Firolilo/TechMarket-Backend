package com.techmarket.techmarket.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Estrategia personalizada para Flyway: corre repair() antes de migrate() para auto-corregir
 * checksums desactualizados cuando una migración fue aplicada con contenido distinto al actual.
 *
 * <p>Útil en entornos dev/staging donde las migraciones se editan después de la primera ejecución.
 * No reinventa migraciones: solo actualiza el flyway_schema_history para reflejar el estado actual
 * de los archivos.
 */
@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy repairAndMigrateStrategy() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
