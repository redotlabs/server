package redot.redot_server.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public final class RuntimePostgres {
    private RuntimePostgres() {}

    public static PostgreSQLContainer<?> create() {
        try {
            var image = new ObjectMapper().readTree(Path.of("docs/runtime-images.json").toFile()).get("postgresql");
            // Testcontainers 1.20 parses repository@digest, but not repository:tag@digest.
            String reference = image.get("image").asText();
            String digestReference = reference.replaceFirst(":([^/@]+)@", "@");
            var container = new PostgreSQLContainer<>(DockerImageName.parse(digestReference));
            container.withCreateContainerCmdModifier(cmd -> cmd.withPlatform(image.get("platform").asText()));
            return container;
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot read pinned PostgreSQL runtime image", ex);
        }
    }
}
