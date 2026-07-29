package org.cocoproject.homepage.metrics;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.HexFormat;
import java.util.regex.Pattern;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class VisitorIdentityService {
    static final String COOKIE_NAME = "coco_vid";
    private static final Duration COOKIE_LIFETIME = Duration.ofDays(365);
    private static final Pattern VALID_IDENTIFIER = Pattern.compile("[A-Za-z0-9_-]{20,128}");

    private final SecureRandom secureRandom = new SecureRandom();

    public String visitorHash(HttpServletRequest request, HttpServletResponse response) {
        String identifier = findValidIdentifier(request);
        if (identifier == null) {
            identifier = newIdentifier();
            ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, identifier)
                    .httpOnly(true)
                    .secure(request.isSecure())
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(COOKIE_LIFETIME)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        return sha256(identifier);
    }

    private String findValidIdentifier(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())
                    && cookie.getValue() != null
                    && VALID_IDENTIFIER.matcher(cookie.getValue()).matches()) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String newIdentifier() {
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
