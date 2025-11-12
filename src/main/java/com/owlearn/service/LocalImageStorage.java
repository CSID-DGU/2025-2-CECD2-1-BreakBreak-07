package com.owlearn.service;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class LocalImageStorage {

    public String saveUserCharacterImage(Long childId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일입니다.");
        }

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (ext == null) ext = "png";

        // 파일명: userId-uuid.ext
        String filename = childId + "-" + UUID.randomUUID() + "." + ext.toLowerCase();

        String imageRoot = "/home/ubuntu/static/";
        Path dir = Paths.get(imageRoot, "characters");
        Files.createDirectories(dir);

        Path target = dir.resolve(filename).normalize().toAbsolutePath();
        try {
            file.transferTo(target);
        } catch (IOException e) {
            throw new IOException("이미지 저장 실패: " + e.getMessage(), e);
        }

        // 접근 가능한 URL 생성
        return "/images/characters/" + filename;
    }
}
