package com.ursia.tavalendo.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;


@Component
public class ProvedorTokenJwt {

    private static final String HMAC_ALGO = "HmacSHA256";

    @Value("${jwt.secret:tavalendo-secret-key-padrao-desenvolvimento-2026}")
    private String chaveSecreta;

    @Value("${jwt.expiration-seconds:3600}")
    private long validadeAccessTokenSegundos;

    @Value("${jwt.refresh-expiration-seconds:604800}")
    private long validadeRefreshTokenSegundos;

    public String gerarAccessToken(String subject, String role) {
        return gerarToken(subject, role, validadeAccessTokenSegundos);
    }

    public String gerarRefreshToken(String subject, String role) {
        return gerarToken(subject, role, validadeRefreshTokenSegundos);
    }

    private String gerarToken(String subject, String role, long validadeSegundos) {
        long exp = Instant.now().getEpochSecond() + validadeSegundos;
        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = String.format("{\"sub\":\"%s\",\"role\":\"%s\",\"exp\":%d}", subject, role, exp);

        String encodedHeader = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String encodedPayload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
        String conteudo = encodedHeader + "." + encodedPayload;
        String assinatura = assinar(conteudo);

        return conteudo + "." + assinatura;
    }

    public boolean validarToken(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                return false;
            }
            String assinaturaEsperada = assinar(partes[0] + "." + partes[1]);
            if (!assinaturaEsperada.equals(partes[2])) {
                return false;
            }
            String payload = new String(Base64.getUrlDecoder().decode(partes[1]), StandardCharsets.UTF_8);
            long exp = extrairExp(payload);
            return Instant.now().getEpochSecond() < exp;
        } catch (Exception e) {
            return false;
        }
    }

    public String extrairSubject(String token) {
        String[] partes = token.split("\\.");
        String payload = new String(Base64.getUrlDecoder().decode(partes[1]), StandardCharsets.UTF_8);
        int subStart = payload.indexOf("\"sub\":\"") + 7;
        int subEnd = payload.indexOf("\"", subStart);
        return payload.substring(subStart, subEnd);
    }

    private long extrairExp(String payload) {
        int expStart = payload.indexOf("\"exp\":") + 6;
        int expEnd = payload.indexOf("}", expStart);
        return Long.parseLong(payload.substring(expStart, expEnd).trim());
    }

    private String assinar(String conteudo) {
        try {
            Mac hmac = Mac.getInstance(HMAC_ALGO);
            SecretKeySpec keySpec = new SecretKeySpec(chaveSecreta.getBytes(StandardCharsets.UTF_8), HMAC_ALGO);
            hmac.init(keySpec);
            return base64UrlEncode(hmac.doFinal(conteudo.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao assinar token JWT", e);
        }
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
