package com.vigil;

import com.mongodb.client.MongoClient;
import com.vigil.repository.ScanRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class BackendApplicationTests {

    // Mock MongoDB beans so this test doesn't require a running database.
    // This verifies that all Spring wiring is correct without real I/O.
    @MockitoBean
    MongoClient mongoClient;

    @MockitoBean
    ScanRepository scanRepository;

    @Test
    void contextLoads() {
        // Verifies the Spring application context loads successfully
        // with all components correctly wired together.
    }

}
