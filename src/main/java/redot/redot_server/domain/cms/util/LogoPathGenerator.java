package redot.redot_server.domain.cms.util;

import java.util.UUID;
import redot.redot_server.domain.cms.exception.SiteSettingErrorCode;
import redot.redot_server.domain.cms.exception.SiteSettingException;

public class LogoPathGenerator {

    public static String generateLogoPath(Long customerId, String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String extension = extractExtension(originalFilename);

        return String.format("customer/%d/logo/%s%s", customerId, uuid, extension);
    }

    private static String extractExtension(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new SiteSettingException(SiteSettingErrorCode.LOGO_FILE_NAME_REQUIRED);
        }

        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1 || lastDot == fileName.length() - 1) {
            throw new SiteSettingException(SiteSettingErrorCode.LOGO_FILE_EXTENSION_REQUIRED);
        }

        String ext = fileName.substring(lastDot);

        if (!ext.matches("^\\.[a-zA-Z0-9]+$")) {
            throw new SiteSettingException(SiteSettingErrorCode.INVALID_FILE_EXTENSION_FORMAT);
        }
        return ext;
    }
}
