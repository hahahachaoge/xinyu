package com.example.web.utils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

/** Resolve uploaded references locally, including URLs returned by the /api proxy. */
public final class LocalDocumentFiles {
    private LocalDocumentFiles() { }

    public static Path resolve(String location, String publicBase) throws IOException {
        return resolve(Path.of("external-resources"), location, publicBase);
    }

    public static Path resolve(Path directory, String location, String publicBase) throws IOException {
        if (location == null || location.isBlank()) throw new IOException("请先上传原文件");
        Path base = directory.toAbsolutePath().normalize();
        try {
            boolean url = location.startsWith("http://") || location.startsWith("https://");
            Path candidate = url ? null : Path.of(location).toAbsolutePath().normalize();
            if (candidate == null || !candidate.startsWith(base)) {
                String relative = url || location.startsWith("/") ? URI.create(location.replace(" ", "%20")).getPath() : location;
                String prefix = publicBase == null ? "" : URI.create(publicBase).getPath();
                prefix = prefix == null ? "" : prefix.replaceAll("/+$", "");
                if (!prefix.isEmpty() && relative.startsWith(prefix + "/")) relative = relative.substring(prefix.length());
                relative = relative.replaceFirst("^/+", "");
                candidate = base.resolve(relative).normalize();
            }
            if (!candidate.startsWith(base) || !Files.isRegularFile(candidate)
                    || !candidate.toRealPath().startsWith(base.toRealPath())) throw new IOException("请重新上传原文件");
            return candidate;
        } catch (IllegalArgumentException error) {
            throw new IOException("文件路径无效，请重新上传原文件");
        }
    }
}
