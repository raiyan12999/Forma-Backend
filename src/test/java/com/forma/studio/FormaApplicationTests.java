package com.forma.studio;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Basic smoke test — verifies the Spring application context loads without errors.
 * Run this with: mvn test
 *
 * For a real database test, you'd need a test database configured.
 * The @TestPropertySource here overrides the datasource to avoid needing MySQL running during tests.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class FormaApplicationTests {

    @Test
    void contextLoads() {
        // If the application context starts without errors, this test passes.
        // This verifies that all beans are correctly configured and wired together.
    }
}
