package com.ursia.tavalendo;

import com.ursia.tavalendo.modules.produto.repository.ProdutoRepository;
import com.ursia.tavalendo.modules.produto.specification.ProdutoSpecification;
import com.ursia.tavalendo.modules.usuario.model.Estabelecimento;
import com.ursia.tavalendo.modules.usuario.repository.EstabelecimentoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
class IsolamentoEstabelecimentoTest {

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    void naoDeveRetornarProdutosDeOutroEstabelecimento() {
        Estabelecimento lojaA = estabelecimentoRepository.save(
                Estabelecimento.builder().nome("Loja A").cnpj("11111111111111").build());
        Estabelecimento lojaB = estabelecimentoRepository.save(
                Estabelecimento.builder().nome("Loja B").cnpj("22222222222222").build());

        produtoRepository.save(com.ursia.tavalendo.modules.produto.model.Produto.builder()
                .nome("Produto da Loja A")
                .quantidade(10)
                .dataValidade(LocalDate.now().plusDays(10))
                .situacaoValidade(com.ursia.tavalendo.modules.produto.model.SituacaoValidade.ATENCAO)
                .estabelecimento(lojaA)
                .build());

        produtoRepository.save(com.ursia.tavalendo.modules.produto.model.Produto.builder()
                .nome("Produto da Loja B")
                .quantidade(5)
                .dataValidade(LocalDate.now().plusDays(10))
                .situacaoValidade(com.ursia.tavalendo.modules.produto.model.SituacaoValidade.ATENCAO)
                .estabelecimento(lojaB)
                .build());

        var resultado = produtoRepository.findAll(
                ProdutoSpecification.filtrar(lojaA.getId(), null, null, null, null, null),
                PageRequest.of(0, 10)
        );

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Produto da Loja A");
    }

    @Test
    void deveRetornarHistoricoDeBaixasIsoladoPorEstabelecimento() {
        Estabelecimento lojaA = estabelecimentoRepository.save(
                Estabelecimento.builder().nome("Loja Histórico A").cnpj("33333333333333").build());
        Estabelecimento lojaB = estabelecimentoRepository.save(
                Estabelecimento.builder().nome("Loja Histórico B").cnpj("44444444444444").build());

        var pA = produtoRepository.save(com.ursia.tavalendo.modules.produto.model.Produto.builder()
                .nome("Produto Baixado Loja A")
                .quantidade(2)
                .dataValidade(LocalDate.now().plusDays(2))
                .situacaoValidade(com.ursia.tavalendo.modules.produto.model.SituacaoValidade.CRITICO)
                .estabelecimento(lojaA)
                .motivoBaixa(com.ursia.tavalendo.modules.produto.model.MotivoBaixa.DESCARTADO)
                .dataBaixa(LocalDate.now())
                .build());
        pA.setAtivo(false);
        produtoRepository.save(pA);

        var pB = produtoRepository.save(com.ursia.tavalendo.modules.produto.model.Produto.builder()
                .nome("Produto Baixado Loja B")
                .quantidade(1)
                .dataValidade(LocalDate.now().plusDays(1))
                .situacaoValidade(com.ursia.tavalendo.modules.produto.model.SituacaoValidade.CRITICO)
                .estabelecimento(lojaB)
                .motivoBaixa(com.ursia.tavalendo.modules.produto.model.MotivoBaixa.VENDIDO)
                .dataBaixa(LocalDate.now())
                .build());
        pB.setAtivo(false);
        produtoRepository.save(pB);

        var resultado = produtoRepository.findAll(
                ProdutoSpecification.filtrarHistoricoBaixas(lojaA.getId(), null, null, null),
                PageRequest.of(0, 10)
        );

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Produto Baixado Loja A");
        assertThat(resultado.getContent().get(0).getMotivoBaixa())
                .isEqualTo(com.ursia.tavalendo.modules.produto.model.MotivoBaixa.DESCARTADO);
    }
}