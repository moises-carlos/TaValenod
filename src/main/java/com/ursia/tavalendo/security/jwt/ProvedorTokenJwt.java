package com.ursia.tavalendo.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

/**
 * Componente responsável pela geração e validação de tokens JWT,
 * utilizando a biblioteca jjwt (assinatura HMAC-SHA256).
 */
@Component
public class ProvedorTokenJwt {

    private final SecretKey chaveSecreta;
    private final long validadeAccessTokenSegundos;
    private final long validadeRefreshTokenSegundos;
    private final Clock clock;

    public ProvedorTokenJwt(
            @Value("${security.jwt.secret}") String segredo,
            @Value("${security.jwt.access-expiration-seconds:3600}") long validadeAccessTokenSegundos,
            @Value("${security.jwt.refresh-expiration-seconds:604800}") long validadeRefreshTokenSegundos,
            Clock clock
    ) {
        this.chaveSecreta = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.validadeAccessTokenSegundos = validadeAccessTokenSegundos;
        this.validadeRefreshTokenSegundos = validadeRefreshTokenSegundos;
        this.clock = clock;
    }

    public String gerarAccessToken(String subject, String role) {
        return gerarToken(subject, role, validadeAccessTokenSegundos);
    }

    public String gerarRefreshToken(String subject, String role) {
        return gerarToken(subject, role, validadeRefreshTokenSegundos);
    }

    private String gerarToken(String subject, String role, long validadeSegundos) {
        Instant agora = Instant.now(clock);
        Instant expiracao = agora.plusSeconds(validadeSegundos);

        return Jwts.builder()
                .subject(subject)
                .claim("role", role)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiracao))
                .signWith(chaveSecreta)
                .compact();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(chaveSecreta)
                    .clock(() -> Date.from(Instant.now(clock)))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extrairSubject(String token) {
        return extrairClaims(token).getSubject();
    }

    public String extrairRole(String token) {
        return extrairClaims(token).get("role", String.class);
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(chaveSecreta)
                .clock(() -> Date.from(Instant.now(clock)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}