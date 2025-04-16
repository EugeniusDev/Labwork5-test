package edu.froliak.labwork5;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

/*
  @author eugen
  @project labwork5
  @class ProjArchitectureTests
  @version 1.0.0
  @since 4/16/2025 - 08.45
*/
@SpringBootTest
public class ProjArchitectureTests {

    private JavaClasses applicationClasses;

    @BeforeEach
    void initialize() {
        applicationClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_ARCHIVES)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_JARS)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("edu.froliak.labwork5");
    }

    @Test
    void shouldFollowLayerArchitecture()  {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("Controller").definedBy("..controller..")
                .layer("Service").definedBy("..service..")
                .layer("Repository").definedBy("..repository..")

                .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Service")
                .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
                .check(applicationClasses);
    }

    // Not depending on controllers
    @Test
    void modelsShouldNotDependOnControllers() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .because("Models should not depend on controllers")
                .check(applicationClasses);
    }

    @Test
    void repositoriesShouldNotDependOnControllers() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..service..")
                .because("Repositories should not depend on controllers")
                .check(applicationClasses);
    }

    @Test
    void controllersShouldNotDependOnOtherControllers() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .because("Controllers should not depend on other controllers")
                .check(applicationClasses);
    }

    @Test
    void servicesShouldNotDependOnControllers() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .because("Services should not depend on controllers")
                .check(applicationClasses);
    }

    @Test
    void serviceClassesShouldNotDependOnController() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..controller..")
                .check(applicationClasses);
    }

    // Not depending on services
    @Test
    void repositoriesShouldNotDependOnServices() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..service..")
                .because("Services should not depend on services")
                .check(applicationClasses);
    }

    // Not depending on repository
    @Test
    void modelClassesShouldNotDependOnRepository() {
        noClasses()
                .that().resideInAPackage("..model..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("..repository..")
                .check(applicationClasses);
    }

    // Naming
    @Test
    void repositoryClassesShouldBeNamedXRepository() {
        classes()
                .that().resideInAPackage("..repository..")
                .should()
                .haveSimpleNameEndingWith("Repository")
                .check(applicationClasses);
    }

    @Test
    void  controllerClassesShouldBeNamedXController() {
        classes()
                .that().resideInAPackage("..controller..")
                .should()
                .haveSimpleNameEndingWith("Controller")
                .check(applicationClasses);
    }

    @Test
    void serviceClassesShouldBeNamedXService() {
        classes()
                .that().resideInAPackage("..service..")
                .should()
                .haveSimpleNameEndingWith("Service")
                .check(applicationClasses);
    }

    // Repository annotations
    @Test
    void repositoryClassesShouldBeAnnotatedWithRepository() {
        classes()
                .that().resideInAPackage("..repository..")
                .should()
                .beAnnotatedWith(Repository.class)
                .check(applicationClasses);
    }

    @Test
    void anyRepositoryFieldsShouldNotBeAnnotatedAutowired() {
        noClasses()
                .that().resideInAPackage("..repository..")
                .should()
                .beAnnotatedWith(Autowired.class)
                .check(applicationClasses);
    }

    // Controller annotations
    @Test
    void controllerClassesShouldBeAnnotatedByControllerClass() {
        classes()
                .that().resideInAPackage("..controller..")
                .should()
                .beAnnotatedWith(RestController.class)
                .check(applicationClasses);
    }

    @Test
    void anyControllerFieldsShouldNotBeAnnotatedAutowired() {
        noClasses()
                .that().resideInAPackage("..controller..")
                .should()
                .beAnnotatedWith(Autowired.class)
                .check(applicationClasses);
    }

    // Service annotations
    @Test
    void serviceClassesShouldBeAnnotatedWithService() {
        classes()
                .that().resideInAPackage("..service..")
                .should()
                .beAnnotatedWith(Service.class)
                .check(applicationClasses);
    }

    @Test
    void anyServiceFieldsShouldNotBeAnnotatedAutowired() {
        noClasses()
                .that().resideInAPackage("..service..")
                .should()
                .beAnnotatedWith(Autowired.class)
                .check(applicationClasses);
    }

    @Test
    void repositoryShouldBeInterface() {
        classes()
                .that().resideInAPackage("..repository..")
                .should()
                .beInterfaces()
                .check(applicationClasses);
    }


    @Test
    void modelFieldsShouldBePrivate() {
        fields()
                .that().areDeclaredInClassesThat(
                    resideInAPackage("..model..")
                )
                .should().bePrivate()
                .because("Model fields should be encapsulated to follow OOP")
                .check(applicationClasses);
    }

    @Test
    void noClassesShouldAccessStandardStreams() {
        NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
                .because("Use a dedicated logging framework instead of System.out/err")
                .check(applicationClasses);
    }
}