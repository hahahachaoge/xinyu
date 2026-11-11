package com.example.web.controller;

import com.example.web.common.FileResultDto;
import com.example.web.exception.CustomException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/File")
public class FileController {
    @Value("${server.ip}") private String serverIp;
    private static final Set<String> ALLOWED = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp", "ico",
            "mp4", "webm", "mov", "pdf", "txt", "md", "doc", "docx", "xls", "xlsx", "ppt", "pptx");

    @PostMapping("/batchUpload")
    public ArrayList<FileResultDto> batchUpload(@RequestParam("file") MultipartFile[] files, HttpServletRequest request) {
        if (files == null || files.length == 0 || files.length > 10) throw new CustomException("每次请选择 1–10 个文件");
        for (MultipartFile file : files) validate(file);
        ArrayList<FileResultDto> results = new ArrayList<>();
        Path base = Path.of("external-resources").toAbsolutePath().normalize();
        for (MultipartFile file : files) {
            String name = file.getOriginalFilename();
            String folder = UUID.randomUUID().toString();
            Path directory = base.resolve(folder);
            try {
                Files.createDirectories(directory);
                try (InputStream input = file.getInputStream()) { Files.copy(input, directory.resolve(name)); }
                String url = serverIp.replaceAll("/+$", "") + "/" + folder + "/" + UriUtils.encodePathSegment(name, StandardCharsets.UTF_8);
                results.add(new FileResultDto(url, name));
            } catch (IOException error) { throw new CustomException("文件保存失败，请稍后重试"); }
        }
        return results;
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new CustomException("文件不能为空");
        String name = file.getOriginalFilename();
        if (name == null || name.isBlank() || name.length() > 180
                || name.matches("(?s).*[\\\\/:*?\"<>|\\p{Cntrl}].*")) throw new CustomException("文件名无效，请重命名后上传");
        int dot = name.lastIndexOf('.');
        if (dot < 1 || !ALLOWED.contains(name.substring(dot + 1).toLowerCase(Locale.ROOT)))
            throw new CustomException("支持图片、视频、PDF、TXT、Markdown 和 Office 文档");
    }
}
