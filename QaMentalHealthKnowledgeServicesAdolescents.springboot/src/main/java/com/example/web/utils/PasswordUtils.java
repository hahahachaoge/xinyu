package com.example.web.utils;

import java.nio.charset.StandardCharsets;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.web.exception.CustomException;

public final class PasswordUtils {
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();
    private PasswordUtils() {}

    public static String encode(String password) {
        if (password == null || password.length() < 6
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new CustomException("密码至少 6 个字符，且 UTF-8 编码不超过 72 字节");
        }
        return ENCODER.encode(password);
    }

    public static boolean matches(String password, String stored) {
        if (password == null || password.isBlank() || stored == null) return false;
        if (stored.startsWith("$2")) return ENCODER.matches(password, stored);
        return Extension.md5Encrypt(password).equals(stored);
    }

    public static boolean needsUpgrade(String stored) {
        return stored != null && !stored.startsWith("$2");
    }

    public static String upgrade(String password) {
        return ENCODER.encode(password);
    }
}
