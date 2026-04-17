package com.techmarket.peopleuacademic.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

public class TechMarketIaArchitectureTest {

    private final JavaClasses classes =
            new ClassFileImporter().importPackages("com.techmarket.peopleuacademic");

    @Test
    void controllers_shouldResideInApiAdminPackage() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("Controller")
                        .should()
                        .resideInAPackage("..api.admin..");

        rule.check(classes);
    }

    @Test
    void configurationClasses_shouldResideInBootstrapPackage() {
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("Config")
                        .should()
                        .resideInAPackage("..globalparameters.bootstrap..");

        rule.check(classes);
    }
}
