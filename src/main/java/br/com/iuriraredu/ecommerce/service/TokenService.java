package br.com.iuriraredu.ecommerce.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class TokenService {

    // 2 hours, written out instead of the magic number 7200000 — so the reader doesn't have to
    // do the math in their head (or trust that whoever wrote it did it right) to know how long it is.
    private static final long TOKEN_EXPIRATION_MILLIS = 2 * 60 * 60 * 1000L;

    // No default value on purpose: if the property isn't configured, the application
    // should fail to start rather than fall back to a predictable secret sitting in the source code.
    @Value("${api.security.token.secret}")
    private String secret;

    // Takes the Spring Security UserDetails contract instead of our own User entity — this
    // keeps TokenService decoupled from the persistence model entirely, and matches what
    // Authentication#getPrincipal() actually hands back after a successful login.
    // Generates the JWT token with a 2-hour validity
    public String generateToken(final UserDetails userDetails) {
        try {
            final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.builder()
                    .issuer("ecommerce-api")
                    .subject(userDetails.getUsername())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MILLIS))
                    .signWith(key)
                    .compact();
        } catch (final Exception e) {
            throw new RuntimeException("Error while generating JWT token", e);
        }
    }

    // Validates the token and returns the user's login (subject)
    public String validateToken(final String token) {
        try {
            final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (final Exception e) {
            return "";
        }
    }
}
