package redot.redot_server.global.s3.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageMimeDetector {

    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] GIF87A = "GIF87a".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] GIF89A = "GIF89a".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] BMP = {0x42, 0x4D};
    private static final byte[] HEIC = {'f', 't', 'y', 'p', 'h', 'e', 'i', 'c'};
    private static final byte[] HEIF = {'f', 't', 'y', 'p', 'h', 'e', 'i', 'f'};

    public Optional<String> detect(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] header = inputStream.readNBytes(12);
            if (matches(header, JPEG)) {
                return Optional.of("image/jpeg");
            }
            if (matches(header, PNG)) {
                return Optional.of("image/png");
            }
            if (matches(header, GIF87A) || matches(header, GIF89A)) {
                return Optional.of("image/gif");
            }
            if (matches(header, BMP)) {
                return Optional.of("image/bmp");
            }
            if (isWebp(header)) {
                return Optional.of("image/webp");
            }
            if (isHeic(header, inputStream)) {
                return Optional.of("image/heic");
            }
            if (isSvg(file)) {
                return Optional.of("image/svg+xml");
            }
            return Optional.empty();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private boolean matches(byte[] header, byte[] signature) {
        if (header.length < signature.length) {
            return false;
        }
        for (int i = 0; i < signature.length; i++) {
            if (header[i] != signature[i]) {
                return false;
            }
        }
        return true;
    }

    private boolean isWebp(byte[] header) {
        if (header.length < 12) {
            return false;
        }
        return header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
    }

    private boolean isHeic(byte[] header, InputStream inputStream) throws IOException {
        if (header.length < 12) {
            return false;
        }
        byte[] type = new byte[8];
        System.arraycopy(header, 4, type, 0, 8);
        if (matches(type, HEIC) || matches(type, HEIF)) {
            return true;
        }
        byte[] next = inputStream.readNBytes(12);
        return next.length >= 12 && (matches(next, HEIC) || matches(next, HEIF));
    }

    private boolean isSvg(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] snippetBytes = inputStream.readNBytes(256);
            String snippet = new String(snippetBytes, StandardCharsets.UTF_8).toLowerCase();
            return snippet.contains("<svg");
        } catch (IOException e) {
            return false;
        }
    }
}
