package com.tu.votingapp.security;

import com.tu.votingapp.entities.RoleEntity;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProviderImpl implements TokenProvider {

    private final String secret;

    public TokenProviderImpl(@Value("${app.tokenSecret}") String secret) {
        this.secret = secret;
    }

    @Override
    public String createToken(Long userId, Collection<RoleEntity> roles) {
        try {
            // Construct payload: userId|role1,role2|timestamp
            String rolesCsv = roles.stream()
                    .map(RoleEntity::getName)
                    .collect(Collectors.joining(","));
            long timestamp = System.currentTimeMillis();
            String payload = userId + "|" + rolesCsv + "|" + timestamp;

            // HMAC‑SHA256 signature
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(keySpec);
            byte[] sigBytes = hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            // Base64‑URL encode parts
            String payloadB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
            String sigB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(sigBytes);

            return payloadB64 + "." + sigB64;
        } catch (Exception e) {
            log.error("Error creating token", e);
            throw new IllegalStateException("Token generation failed", e);
        }
    }
}