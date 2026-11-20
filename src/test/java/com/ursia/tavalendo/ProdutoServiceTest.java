package com.ursia.tavalendo;

import com.ursia.tavalendo.exception.custom.RecursoNaoEncontradoException;
import com.ursia.tavalendo.exception.custom.RegraNegocioException;
import com.ursia.tavalendo.modules.produto.dto.ProdutoCatalogoResponseDTO;
import com.ursia.tavalendo.modules.produto.model.Produto;
import com.ursia.tavalendo.modules.produto.model.SituacaoValidade;
import com.ursia.tavalendo.modules.produto.repository.ProdutoRepository;
import com.ursia.tavalendo.modules.produto.service.CatalogoExternoService;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class ProdutoServiceTest {

    private ProdutoService produtoService;
    private ProdutoRepository produtoRepository;
    private UsuarioAutenticado usuarioAutenticado;
    private CatalogoExternoService catalogoExternoService;
    private Clock clockFixo;

    @BeforeEach
    void setUp() {
        clockFixo = Clock.fixed(
                Instant.parse("2026-11-11T12:00:00Z"),
                ZoneId.of("America/Recife")
        );

        produtoRepository = mock(ProdutoRepository.class);
        usuarioAutenticado = mock(UsuarioAutenticado.class);
        catalogoExternoService = mock(CatalogoExternoService.class);

        produtoService = new ProdutoService(
                produtoRepository,
                mock(EstabelecimentoRepository.class),
                usuarioAutenticado,
                catalogoExternoService,
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

    @Test
    void deveRetornarProdutoDoHistoricoDaLojaQuandoExistir() {
        when(usuarioAutenticado.getEstabelecimentoId()).thenReturn(1L);

        Produto produtoExistente = Produto.builder()
                .nome("Leite Integral 1L")
                .categoria("Laticínios")
                .codigoBarras("7891000100101")
                .build();

        when(produtoRepository.findFirstByEstabelecimentoIdAndCodigoBarrasOrderByAtivoDescCriadoEmDesc(
                eq(1L), eq("7891000100101")
        )).thenReturn(Optional.of(produtoExistente));

        ProdutoCatalogoResponseDTO resultado = produtoService.buscarPorCodigoBarras(" 7891000100101 ");

        assertThat(resultado.nome()).isEqualTo("Leite Integral 1L");
        assertThat(resultado.categoria()).isEqualTo("Laticínios");
        assertThat(resultado.codigoBarras()).isEqualTo("7891000100101");
        assertThat(resultado.origem()).isEqualTo("LOJA");

        verifyNoInteractions(catalogoExternoService);
    }

    @Test
    void deveConsultarCatalogoExternoQuandoNaoHouverNaLoja() {
        when(usuarioAutenticado.getEstabelecimentoId()).thenReturn(1L);
        when(produtoRepository.findFirstByEstabelecimentoIdAndCodigoBarrasOrderByAtivoDescCriadoEmDesc(
                eq(1L), eq("7894900011517")
        )).thenReturn(Optional.empty());

        ProdutoCatalogoResponseDTO externo = new ProdutoCatalogoResponseDTO(
                "Coca-Cola 2L", "Bebidas", "7894900011517", "CATALOGO_EXTERNO");

        when(catalogoExternoService.buscarPorCodigoBarras("7894900011517"))
                .thenReturn(Optional.of(externo));

        ProdutoCatalogoResponseDTO resultado = produtoService.buscarPorCodigoBarras("7894900011517");

        assertThat(resultado.nome()).isEqualTo("Coca-Cola 2L");
        assertThat(resultado.categoria()).isEqualTo("Bebidas");
        assertThat(resultado.origem()).isEqualTo("CATALOGO_EXTERNO");
    }

    @Test
    void deveLancarExcecaoQuandoNaoEncontrarEmNenhumCatalogo() {
        when(usuarioAutenticado.getEstabelecimentoId()).thenReturn(1L);
        when(produtoRepository.findFirstByEstabelecimentoIdAndCodigoBarrasOrderByAtivoDescCriadoEmDesc(
                eq(1L), anyString()
        )).thenReturn(Optional.empty());
        when(catalogoExternoService.buscarPorCodigoBarras(anyString()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> produtoService.buscarPorCodigoBarras("0000000000000"))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("Nenhum produto encontrado");
    }

    @Test
    void deveLancarExcecaoQuandoCodigoBarrasEstiverEmBranco() {
        assertThatThrownBy(() -> produtoService.buscarPorCodigoBarras("   "))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("código de barras deve ser informado");
    }
}
