package com.techmarket.techmarket.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

public class TechMarketIaArchitectureTest {

    private final JavaClasses classes =
            new ClassFileImporter().importPackages("com.techmarket.techmarket");

    @Test
    void controllers_shouldResideInApiPackage() {
        // IA expone varias superficies de API (api.admin para back-office, api.mobile para apps de
        // embajador, auth.api para login/registro). Todas son capas de entrada y deben vivir bajo
        // un
        // paquete ..api.. para mantener la separacion contra dominio/aplicacion.
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("Controller")
                        .should()
                        .resideInAPackage("..api..");

        rule.check(classes);
    }

    @Test
    void configurationClasses_shouldResideInBootstrapPackage() {
        boolean hasConfigClasses =
                classes.stream()
                        .anyMatch(javaClass -> javaClass.getSimpleName().endsWith("Config"));
        if (!hasConfigClasses) {
            return;
        }

        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("Config")
                        .should()
                        .resideInAPackage("..config..");

        rule.check(classes);
    }
}
