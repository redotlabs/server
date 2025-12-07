package redot.redot_server.global.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import org.springframework.util.StringUtils;
import redot.redot_server.global.s3.config.ImageUploadProperties;
import redot.redot_server.global.s3.exception.ImageErrorCode;
import redot.redot_server.global.s3.exception.ImageUploadException;
import redot.redot_server.global.s3.util.ImageDirectory;
import redot.redot_server.global.s3.util.ImageMimeDetector;
import redot.redot_server.global.s3.util.ImagePathGenerator;
import redot.redot_server.global.s3.util.S3Manager;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final S3Manager s3Manager;
    private final ImageUploadProperties properties;
    private final ImageMimeDetector imageMimeDetector;

    public String upload(ImageDirectory directory, Long ownerId, MultipartFile file) {
        validateFile(file);

        String path = ImagePathGenerator.generate(directory, ownerId, file.getOriginalFilename());
        return s3Manager.uploadFile(file, path);
    }

    public void delete(String imageUrl) {
        s3Manager.deleteFile(imageUrl);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageUploadException(ImageErrorCode.IMAGE_FILE_REQUIRED);
        }

        if (file.getSize() > properties.getMaxSizeBytes()) {
            throw new ImageUploadException(ImageErrorCode.IMAGE_TOO_LARGE);
        }

        String contentType = file.getContentType();
        List<String> allowed = properties.getAllowedContentTypes();
        boolean allowedType = StringUtils.hasText(contentType) &&
                allowed.stream().anyMatch(pattern -> matchesContentType(contentType, pattern));
        if (!allowedType) {
            throw new ImageUploadException(ImageErrorCode.UNSUPPORTED_IMAGE_TYPE);
        }

        String detected = imageMimeDetector.detect(file)
                .orElseThrow(() -> new ImageUploadException(ImageErrorCode.UNSUPPORTED_IMAGE_TYPE));

        boolean detectedAllowed = allowed.stream().anyMatch(pattern -> matchesContentType(detected, pattern));
        if (!detectedAllowed) {
            throw new ImageUploadException(ImageErrorCode.UNSUPPORTED_IMAGE_TYPE);
        }
    }

    private boolean matchesContentType(String contentType, String pattern) {
        if (!StringUtils.hasText(pattern)) {
            return false;
        }
        if ("*/*".equals(pattern)) {
            return true;
        }
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 1);
            return contentType.regionMatches(true, 0, prefix, 0, prefix.length());
        }
        return contentType.equalsIgnoreCase(pattern);
    }
}
