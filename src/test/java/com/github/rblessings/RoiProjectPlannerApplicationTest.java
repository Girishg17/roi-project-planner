package com.github.rblessings;

import com.github.rblessings.configuration.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class RoiProjectPlannerApplicationTest {

    @Test
    void contextLoads() {
    }
}
