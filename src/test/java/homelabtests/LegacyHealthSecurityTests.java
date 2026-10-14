package homelabtests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = HealthProbeTests.ProbeApplication.class, properties = {
        "management.endpoint.health.access=read-only", "management.endpoints.web.exposure.include=health"
})
@AutoConfigureMockMvc
class LegacyHealthSecurityTests {
    @Autowired MockMvc mvc;
    @Test void healthIsNotPublicWithoutExplicitHomelabOptIn() throws Exception {
        mvc.perform(get("/actuator/health")).andExpect(status().isForbidden());
    }
}
