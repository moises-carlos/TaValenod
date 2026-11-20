package com.ursia.tavalendo;

import com.ursia.tavalendo.modules.produto.model.SituacaoValidade;
import com.ursia.tavalendo.modules.produto.repository.ProdutoRepository;
import com.ursia.tavalendo.modules.produto.service.ProdutoService;
import com.ursia.tavalendo.modules.usuario.repository.EstabelecimentoRepository;
import com.ursia.tavalendo.security.UsuarioAutenticado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class ProdutoServiceTest {

    private ProdutoService produtoService;
    private Clock clockFixo;

    @BeforeEach
    void setUp() {
        clockFixo = Clock.fixed(
                Instant.parse("2026-11-11T12:00:00Z"),
                ZoneId.of("America/Recife")
        );

        produtoService = new ProdutoService(
                mock(ProdutoRepository.class),
                mock(EstabelecimentoRepository.class),
                mock(UsuarioAutenticado.class),
                clockFixo
        );

        setField(produtoService, "limiteCriticoDias", 7L);
        setField(produtoService, "limiteAtencaoDias", 30L);
    }

    @ParameterizedTest
    @CsvSource({
            "-1, VENCIDO",
            "0, CRITICO",
            "7, CRITICO",
            "8, ATENCAO",
            "30, ATENCAO",
            "31, OK",
            "100, OK"
    })
    void deveClassificarSituacaoCorretamente(int diasAFrente, SituacaoValidade esperado) {
        LocalDate hoje = LocalDate.now(clockFixo);
        LocalDate dataValidade = hoje.plusDays(diasAFrente);

        SituacaoValidade resultado = produtoService.calcularSituacaoValidade(dataValidade);

        assertThat(resultado).isEqualTo(esperado);
    }
}