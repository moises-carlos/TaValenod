package com.ursia.tavalendo.config;

import com.ursia.tavalendo.modules.produto.model.SituacaoValidade;
import com.ursia.tavalendo.modules.produto.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Job agendado que recalcula diariamente a situação de validade
 * de todos os produtos ativos, mantendo o banco sincronizado
 * com a data atual independente de quando o produto foi cadastrado.
 */
@Slf4j
@Component
public class RecalculoSituacaoJob {

    private final ProdutoRepository produtoRepository;
    private final Clock clock;
    private final long limiteCriticoDias;
    private final long limiteAtencaoDias;

    public RecalculoSituacaoJob(
            ProdutoRepository produtoRepository,
            Clock clock,
            @Value("${tavalendo.validade.dias-critico:7}") long limiteCriticoDias,
            @Value("${tavalendo.validade.dias-atencao:30}") long limiteAtencaoDias
    ) {
        this.produtoRepository = produtoRepository;
        this.clock = clock;
        this.limiteCriticoDias = limiteCriticoDias;
        this.limiteAtencaoDias = limiteAtencaoDias;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void recalcular() {
        log.info("Iniciando recálculo de situação de validade...");
        LocalDate hoje = LocalDate.now(clock);

        int vencidos = produtoRepository.atualizarSituacaoPorFaixa(
                SituacaoValidade.VENCIDO,
                null,
                hoje.minusDays(1)
        );

        int criticos = produtoRepository.atualizarSituacaoPorFaixa(
                SituacaoValidade.CRITICO,
                hoje,
                hoje.plusDays(limiteCriticoDias)
        );

        int atencao = produtoRepository.atualizarSituacaoPorFaixa(
                SituacaoValidade.ATENCAO,
                hoje.plusDays(limiteCriticoDias + 1),
                hoje.plusDays(limiteAtencaoDias)
        );

        int ok = produtoRepository.atualizarSituacaoPorFaixa(
                SituacaoValidade.OK,
                hoje.plusDays(limiteAtencaoDias + 1),
                null
        );

        log.info("Recálculo concluído — vencidos: {}, críticos: {}, atenção: {}, ok: {}",
                vencidos, criticos, atencao, ok);
    }
}