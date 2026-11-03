package com.universidad.tests;

import static org.assertj.core.api.Assertions.assertThat;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MigracionesTest {
    @Autowired private Flyway flyway;

    @Test
    void todasLasMigracionesDeDatabaseMigrationsEstanAplicadas() {
        assertThat(flyway.info().pending()).isEmpty();
    }
}
