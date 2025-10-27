package redot.redot_server.global.s3.util;

import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import redot.redot_server.global.s3.exception.S3ErrorCode;
import redot.redot_server.global.s3.exception.S3StorageException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Manager {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * 지정된 경로로 파일 업로드
     * @param file 업로드할 파일
     * @param targetPath 최종 저장될 S3 key (ex: customer/1/logo/image.png)
     * @return 업로드된 파일의 전체 경로 ("/" 포함)
     */
    public String uploadFile(MultipartFile file, String targetPath) {
        try (InputStream input = file.getInputStream()) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(targetPath)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(input, file.getSize()));
            log.info("✅ S3 업로드 성공: {}", targetPath);
            return "/" + targetPath;

        } catch (IOException | S3Exception e) {
            throw new S3StorageException(S3ErrorCode.FILE_UPLOAD_FAILED, e);
        }
    }

    /**
     * 지정된 경로의 파일 삭제
     * @param filePath 삭제할 파일 경로 (ex: /customer/1/logo/image.png)
     */
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;

        String key = filePath.startsWith("/") ? filePath.substring(1) : filePath;
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            log.info("🗑️ S3 삭제 성공: {}", key);
        } catch (NoSuchKeyException e) {
            log.warn("⚠️ S3 파일이 이미 존재하지 않음: {}", key);
        } catch (S3Exception e) {
            log.error("❌ S3 파일 삭제 실패 ({}): {}", key, e.awsErrorDetails().errorMessage());
            throw new S3StorageException(S3ErrorCode.FILE_DELETE_FAILED, e);
        } catch (Exception e) {
            log.error("❌ S3 파일 삭제 중 알 수 없는 오류: {}", key, e);
            throw new S3StorageException(S3ErrorCode.FILE_DELETE_UNKNOWN_ERROR, e);
        }
    }

    /**
     * S3에 동일 경로의 파일이 이미 존재하는지 확인
     * @param key 확인할 S3 key (ex: customer/1/logo/image.png)
     * @return 존재하면 true, 존재하지 않으면 false
     */
    public boolean exists(String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }
}
