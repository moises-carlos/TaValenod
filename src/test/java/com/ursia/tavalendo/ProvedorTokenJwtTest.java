package com.ursia.tavalendo;

import com.ursia.tavalendo.security.jwt.ProvedorTokenJwt;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class ProvedorTokenJwtTest {

    private final Clock clockFixo = Clock.fixed(
            Instant.parse("2026-11-11T12:00:00Z"), ZoneOffset.UTC);

    private final ProvedorTokenJwt provedor = new ProvedorTokenJwt(
            "segredo-de-teste-com-pelo-menos-32-bytes-1234",
            3600,
            604800,
            clockFixo
    );

    @Test
    void deveGerarEValidarTokenCorretamente() {
        String token = provedor.gerarAccessToken("usuario@teste.com", "ADMIN");

        assertThat(provedor.validarToken(token)).isTrue();
        assertThat(provedor.extrairSubject(token)).isEqualTo("usuario@teste.com");
        assertThat(provedor.extrairRole(token)).isEqualTo("ADMIN");
    }

    @Test
    void deveInvalidarTokenExpirado() {
        // token com validade de -1 segundo já nasce expirado
        String token = provedor.gerarAccessToken("usuario@teste.com", "ADMIN");

        Clock clockFuturo = Clock.offset(clockFixo, java.time.Duration.ofSeconds(3700));
        ProvedorTokenJwt provedorComClockFuturo = new ProvedorTokenJwt(
                "segredo-de-teste-com-pelo-menos-32-bytes-1234",
                3600,
                604800,
                clockFuturo
        );

        assertThat(provedorComClockFuturo.validarToken(token)).isFalse();
    }

    @Test
    void deveRejeitarTokenComAssinaturaInvalida() {
        String tokenAdulterado = provedor.gerarAccessToken("usuario@teste.com", "ADMIN") + "adulterado";

        assertThat(provedor.validarToken(tokenAdulterado)).isFalse();
    }
}