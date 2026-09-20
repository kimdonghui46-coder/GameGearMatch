package com.gamegearmatch.backend.image.service;

import com.gamegearmatch.backend.image.dto.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStorageService {
    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private final S3Client s3Client;
    @Value("${aws.s3.bucket}") private String bucket;

    public ImageUploadResponse upload(MultipartFile file) {
        if (bucket == null || bucket.isBlank()) throw new IllegalStateException("S3_BUCKET 환경변수가 설정되지 않았습니다.");
        if (file.isEmpty() || !ALLOWED_TYPES.contains(file.getContentType())) throw new IllegalArgumentException("JPG, PNG, WEBP 이미지만 업로드할 수 있습니다.");
        String extension = switch (file.getContentType()) { case "image/jpeg" -> "jpg"; case "image/png" -> "png"; case "image/webp" -> "webp"; default -> throw new IllegalArgumentException("지원하지 않는 이미지입니다."); };
        String fileName = UUID.randomUUID() + "." + extension;
        try {
            s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key("products/" + fileName).contentType(file.getContentType()).build(), RequestBody.fromBytes(file.getBytes()));
        } catch (IOException exception) { throw new IllegalStateException("이미지를 읽지 못했습니다.", exception); }
        return new ImageUploadResponse("/api/images/" + fileName);
    }

    public StoredImage get(String fileName) {
        if (!fileName.matches("[0-9a-fA-F-]+\\.(jpg|png|webp)")) throw new IllegalArgumentException("올바르지 않은 이미지 경로입니다.");
        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(GetObjectRequest.builder().bucket(bucket).key("products/" + fileName).build());
        return new StoredImage(response.response().contentType(), response.asByteArray());
    }

    public record StoredImage(String contentType, byte[] bytes) {}
}
