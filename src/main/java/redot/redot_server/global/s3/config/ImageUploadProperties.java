package redot.redot_server.global.s3.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "image.upload")
public class ImageUploadProperties {

    private long maxSizeBytes = 5_242_880L; // 5MB default

    private List<String> allowedContentTypes = new ArrayList<>(List.of(
            "image/*"
    ));
}
