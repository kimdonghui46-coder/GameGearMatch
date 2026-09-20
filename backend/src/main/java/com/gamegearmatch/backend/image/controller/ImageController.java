package com.gamegearmatch.backend.image.controller;

import com.gamegearmatch.backend.image.dto.ImageUploadResponse;
import com.gamegearmatch.backend.image.service.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageStorageService imageStorageService;

    @PostMapping(value = "/api/admin/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponse> upload(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(imageStorageService.upload(file));
    }

    @GetMapping("/api/images/{fileName:.+}")
    public ResponseEntity<byte[]> get(@PathVariable String fileName) {
        ImageStorageService.StoredImage image = imageStorageService.get(fileName);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.contentType()))
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic()).body(image.bytes());
    }
}
