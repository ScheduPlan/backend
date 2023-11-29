package de.hofuniversity.assemblyplanner.util;

import io.jsonwebtoken.security.KeyBuilderSupplier;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecretKeyBuilder;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

public class KeyUtil {
    public static Key toKey(String secret) {
        byte[] data = decodeUtf8ToBytes(secret);
        return Keys.hmacShaKeyFor(data);
    }

    public static byte[] decodeUtf8ToBytes(String base64) {
        return Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
    }

    public static String encodeToUtf8String(byte[] data) {
        return new String(Base64.getEncoder().encode(data), StandardCharsets.UTF_8);
    }

    public static Key randomKey(KeyBuilderSupplier<SecretKey, SecretKeyBuilder> alg) {
        return alg.key().build();
    }
}
