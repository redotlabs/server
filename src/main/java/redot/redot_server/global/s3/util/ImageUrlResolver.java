package redot.redot_server.global.s3.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * CDN 주소와 S3 경로 간 변환을 담당한다.
 */
@Component
public class ImageUrlResolver {

    private final String cdnBaseUrl;
    private final boolean hasCdnBaseUrl;

    public ImageUrlResolver(@Value("${image.cdn-base-url:}") String cdnBaseUrl) {
        if (StringUtils.hasText(cdnBaseUrl)) {
            String trimmed = cdnBaseUrl.trim();
            this.cdnBaseUrl = trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
            this.hasCdnBaseUrl = true;
        } else {
            this.cdnBaseUrl = "";
            this.hasCdnBaseUrl = false;
        }
    }

    /**
     * 저장된 경로(또는 외부 URL)를 CDN이 붙은 공개 URL로 변환한다.
     */
    public String toPublicUrl(String pathOrUrl) {
        if (!StringUtils.hasText(pathOrUrl)) {
            return null;
        }

        String value = pathOrUrl.trim();
        if (isExternalUrl(value) || !hasCdnBaseUrl) {
            return normalizePath(value);
        }
        String normalizedPath = normalizePath(value);
        if (isExternalUrl(normalizedPath)) {
            return normalizedPath;
        }
        return cdnBaseUrl + normalizedPath;
    }

    /**
     * CDN URL 혹은 경로를 S3에 저장되는 내부 경로(/app/1/logo.png 형태)로 정규화한다.
     */
    public String toStoredPath(String pathOrUrl) {
        if (!StringUtils.hasText(pathOrUrl)) {
            return null;
        }
        String value = stripCdnBase(pathOrUrl.trim());
        if (isExternalUrl(value)) {
            return value;
        }
        return normalizePath(value);
    }

    /**
     * S3 API에서 사용하는 key(app/1/logo.png)로 변환한다.
     */
    public String toS3Key(String pathOrUrl) {
        String storedPath = toStoredPath(pathOrUrl);
        if (!StringUtils.hasText(storedPath) || isExternalUrl(storedPath)) {
            return null;
        }
        return storedPath.startsWith("/") ? storedPath.substring(1) : storedPath;
    }

    private String stripCdnBase(String value) {
        if (hasCdnBaseUrl && value.startsWith(cdnBaseUrl)) {
            return value.substring(cdnBaseUrl.length());
        }
        return value;
    }

    private boolean isExternalUrl(String value) {
        return value.startsWith("http://") || value.startsWith("https://");
    }

    private String normalizePath(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        if (isExternalUrl(value)) {
            return value;
        }
        return value.startsWith("/") ? value : "/" + value;
    }
}
