package br.com.iuriraredu.ecommerce.service;

import br.com.iuriraredu.ecommerce.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

import static java.lang.System.*;
import static java.nio.charset.StandardCharsets.*;

@Service
public class TokenService {
    @Value("${api.security.token.secret:my-secret-key-very-secure-ecommerce-iuri}")
    private String secret;

    // Gera o token JWT com validade de 2 horas
    public String generateToken(User user) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(UTF_8));
            return Jwts.builder()
                    .issuer("ecommerce-api")
                    .subject(user.getUsername())
                    .issuedAt(new Date())
                    .expiration(new Date(currentTimeMillis() + 7200000)) // 2 horas
                    .signWith(key)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Erro enquanto gerava o token JWT", e);
        }
    }

    // Valida o token e retorna o login do usuário (subject)
    public String validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return "";
        }
    }
}
