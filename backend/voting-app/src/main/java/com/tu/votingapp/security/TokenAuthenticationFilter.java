package com.tu.votingapp.security;

import com.tu.votingapp.entities.UserEntity;
import com.tu.votingapp.repositories.interfaces.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // For accessing the secret
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    // No need to inject TokenProvider if validation logic is self-contained here
    // based on the provided implementation, but keep it if you add validation methods there.
    // private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final String tokenSecret; // Inject the secret directly or via TokenProvider
    private final Logger logger = Logger.getLogger(TokenAuthenticationFilter.class.getName());

    // Constructor to inject the secret (or inject TokenProvider and get secret from it)
    public TokenAuthenticationFilter(UserRepository userRepository, @Value("${app.tokenSecret}") String tokenSecret) {
        this.userRepository = userRepository;
        this.tokenSecret = tokenSecret;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Extract token (payload.signature) from header
            String token = extractJwtFromRequest(request);

            // 2. Basic check and split token
            if (StringUtils.hasText(token) && token.contains(".")) {
                String[] parts = token.split("\\.");
                if (parts.length == 2) {
                    String payloadB64 = parts[0];
                    String sigB64 = parts[1];

                    // 3. Decode Payload
                    String payload = new String(Base64.getUrlDecoder().decode(payloadB64), StandardCharsets.UTF_8);

                    // 4. Validate Signature
                    if (validateSignature(payload, sigB64)) {

                        // 5. Parse Payload (userId|roles|timestamp)
                        String[] payloadParts = payload.split("\\|");
                        if (payloadParts.length >= 1) { // Expect at least userId, roles might be empty
                            Long userId = Long.parseLong(payloadParts[0]);
                            // Roles from payload are not used for auth decision here,
                            // we load fresh roles from DB below. Timestamp could be validated for expiry.

                            // Check if user is already authenticated
                            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                                // 6. Load UserEntity from Database using userId from token
                                UserEntity userEntity = userRepository.findById(userId)
                                        .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId + " from token"));

                                // 7. Create UserPrincipal (loads fresh roles/authorities from DB)
                                UserPrincipal userPrincipal = UserPrincipal.fromEntity(userEntity);

                                // 8. Create Authentication object WITH UserPrincipal
                                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        userPrincipal,
                                        null,
                                        userPrincipal.getAuthorities()
                                );

                                // 9. Set optional details
                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                                // 10. Set Authentication in SecurityContextHolder
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                logger.fine(() -> "User " + userPrincipal.getUsername() + " authenticated successfully via custom token.");
                            }
                        } else {
                            logger.warning("Invalid custom token payload format.");
                        }
                    } else {
                        logger.warning("Invalid custom token signature.");
                    }
                } else {
                    logger.warning("Invalid custom token format (expected 2 parts).");
                }
            }
        } catch (UsernameNotFoundException ex) {
            logger.log(Level.WARNING, "User from token not found in DB: {0}", ex.getMessage());
            SecurityContextHolder.clearContext(); // Clear context on error
        } catch (NumberFormatException ex) {
            logger.log(Level.WARNING, "Could not parse userId from token payload.", ex);
            SecurityContextHolder.clearContext(); // Clear context on error
        } catch (Exception ex) {
            // Catch potential Base64 decoding errors, crypto errors etc.
            logger.log(Level.SEVERE, "Could not process custom token or set user authentication", ex);
            SecurityContextHolder.clearContext(); // Clear context on error
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the custom token from the Authorization header.
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Validates the HMAC-SHA256 signature of the custom token.
     */
    private boolean validateSignature(String payload, String receivedSigB64) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(tokenSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(keySpec);
            byte[] calculatedSigBytes = hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String calculatedSigB64 = Base64.getUrlEncoder().withoutPadding().encodeToString(calculatedSigBytes);

            return calculatedSigB64.equals(receivedSigB64);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error validating token signature", e);
            return false;
        }
    }
}
