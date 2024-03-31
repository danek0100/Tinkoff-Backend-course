package edu.java;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Property;
import org.jooq.meta.jaxb.Strategy;
import org.jooq.meta.jaxb.Target;


@SuppressWarnings({"UncommentedMain"})
public class JooqCodegen {
    private JooqCodegen() {}

    public static void main(String[] args) throws Exception {
        Database database = new Database()
            .withName("org.jooq.meta.extensions.liquibase.LiquibaseDatabase")
            .withProperties(
                new Property().withKey("rootPath").withValue("migrations"),
                new Property().withKey("scripts").withValue("master.xml")
            );

        Generate options = new Generate()
            .withGeneratedAnnotation(true)
            .withGeneratedAnnotationDate(false)
            .withNullableAnnotation(true)
            .withNullableAnnotationType("org.jetbrains.annotations.Nullable")
            .withNonnullAnnotation(true)
            .withNonnullAnnotationType("org.jetbrains.annotations.NotNull")
            .withJpaAnnotations(false)
            .withValidationAnnotations(true)
            .withSpringAnnotations(true)
            .withConstructorPropertiesAnnotation(true)
            .withConstructorPropertiesAnnotationOnPojos(true)
            .withConstructorPropertiesAnnotationOnRecords(true)
            .withFluentSetters(false)
            .withDaos(false)
            .withPojos(true);


        Target target = new Target()
            .withPackageName("edu.java.jooq.generated")
            .withDirectory("scrapper/src/main/java/");

        Strategy strategy = new Strategy()
            .withName("edu.java.CustomNamingStrategy");

        Configuration configuration = new Configuration()
            .withGenerator(
                new Generator()
                    .withDatabase(database)
                    .withGenerate(options)
                    .withTarget(target)
                    .withStrategy(strategy)
            );

        GenerationTool.generate(configuration);
    }
}
