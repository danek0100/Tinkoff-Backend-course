package edu.java.scrapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class PostgreSQLContainerTest {
    /* TODO find issues (drop all test with db after it)
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = IntegrationTest.POSTGRES;

    @Test
    public void containerShouldBeRunning() {
        Assertions.assertTrue(postgresContainer.isRunning(), "PostgreSQL container should be running");
    }
    */
}
