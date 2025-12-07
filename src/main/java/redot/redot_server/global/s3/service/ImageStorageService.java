package redot.redot_server.global.s3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import redot.redot_server.global.s3.exception.ImageErrorCode;
import redot.redot_server.global.s3.exception.ImageUploadException;
import redot.redot_server.global.s3.util.ImageDirectory;
import redot.redot_server.global.s3.util.ImagePathGenerator;
import redot.redot_server.global.s3.util.S3Manager;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private final S3Manager s3Manager;

    public String upload(ImageDirectory directory, Long ownerId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageUploadException(ImageErrorCode.IMAGE_FILE_REQUIRED);
        }

        String path = ImagePathGenerator.generate(directory, ownerId, file.getOriginalFilename());
        return s3Manager.uploadFile(file, path);
    }

    public void delete(String imageUrl) {
        s3Manager.deleteFile(imageUrl);
    }
}
