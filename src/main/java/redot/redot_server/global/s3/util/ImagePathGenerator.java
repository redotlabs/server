package redot.redot_server.global.s3.util;

import java.util.UUID;
import org.springframework.util.StringUtils;
import redot.redot_server.global.s3.exception.ImageErrorCode;
import redot.redot_server.global.s3.exception.ImageUploadException;

public final class ImagePathGenerator {

    private ImagePathGenerator() {
    }

    public static String generate(ImageDirectory directory, Long ownerId, String originalFilename) {
        if (ownerId == null) {
            throw new ImageUploadException(ImageErrorCode.INVALID_IMAGE_OWNER);
        }

        String extension = extractExtension(originalFilename);
        String basePath = directory.resolve(ownerId);
        return String.format("%s/%s%s", basePath, UUID.randomUUID(), extension);
    }

    private static String extractExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            throw new ImageUploadException(ImageErrorCode.INVALID_IMAGE_FILE_NAME);
        }

        int lastDot = originalFilename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == originalFilename.length() - 1) {
            throw new ImageUploadException(ImageErrorCode.INVALID_IMAGE_EXTENSION);
        }

        return originalFilename.substring(lastDot);
    }
}
