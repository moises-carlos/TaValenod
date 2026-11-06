package com.ursia.tavalendo.modules.produto.service;

import com.ursia.tavalendo.core.base.ServiceGenericoAbstrato;
import com.ursia.tavalendo.core.utils.DataUtils;
import com.ursia.tavalendo.exception.custom.RecursoNaoEncontradoException;
import com.ursia.tavalendo.modules.produto.dto.DashboardResponseDTO;
import com.ursia.tavalendo.modules.produto.dto.ProdutoRequestDTO;
import com.ursia.tavalendo.modules.produto.dto.ProdutoResponseDTO;
import com.ursia.tavalendo.modules.produto.model.Produto;
import com.ursia.tavalendo.modules.produto.model.SituacaoValidade;
import com.ursia.tavalendo.modules.produto.repository.ProdutoRepository;
import com.ursia.tavalendo.modules.usuario.model.Estabelecimento;
import com.ursia.tavalendo.modules.usuario.repository.EstabelecimentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Serviço responsável pelas regras de negócio do domínio de {@link Produto}.
 * Realiza o cálculo automático da {@link SituacaoValidade} (OK, ATENCAO, CRITICO, VENCIDO)
 * com base na data de validade e agrega os indicadores para o dashboard.
 */
@Service
public class ProdutoService extends ServiceGenericoAbstrato<Produto, Long> {

    private static final long LIMITE_CRITICO_DIAS = 7;
    private static final long LIMITE_ATENCAO_DIAS = 30;

    private final ProdutoRepository produtoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    public ProdutoService(
            ProdutoRepository produtoRepository,
            EstabelecimentoRepository estabelecimentoRepository
    ) {
        super(produtoRepository);
        this.produtoRepository = produtoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    @Transactional
    public ProdutoResponseDTO cadastrar(ProdutoRequestDTO dto) {
        Estabelecimento estabelecimento = null;
        if (dto.estabelecimentoId() != null) {
            estabelecimento = estabelecimentoRepository.findById(dto.estabelecimentoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Estabelecimento não encontrado para o ID: " + dto.estabelecimentoId()));
        }

        Produto produto = Produto.builder()
                .nome(dto.nome())
                .codigoBarras(dto.codigoBarras())
                .lote(dto.lote())
                .quantidade(dto.quantidade())
                .dataValidade(dto.dataValidade())
                .situacaoValidade(calcularSituacaoValidade(dto.dataValidade()))
                .estabelecimento(estabelecimento)
                .build();

        return ProdutoResponseDTO.deEntidade(salvar(produto));
    }

    public SituacaoValidade calcularSituacaoValidade(LocalDate dataValidade) {
        long diasRestantes = DataUtils.diasParaVencimento(dataValidade);

        if (diasRestantes < 0) {
            return SituacaoValidade.VENCIDO;
        }
        if (diasRestantes <= LIMITE_CRITICO_DIAS) {
            return SituacaoValidade.CRITICO;
        }
        if (diasRestantes <= LIMITE_ATENCAO_DIAS) {
            return SituacaoValidade.ATENCAO;
        }
        return SituacaoValidade.OK;
    }

    @Transactional(readOnly = true)
    public DashboardResponseDTO obterResumoDashboard() {
        long total = produtoRepository.countByAtivoTrue();
        long ok = produtoRepository.countBySituacaoValidadeAndAtivoTrue(SituacaoValidade.OK);
        long atencao = produtoRepository.countBySituacaoValidadeAndAtivoTrue(SituacaoValidade.ATENCAO);
        long critico = produtoRepository.countBySituacaoValidadeAndAtivoTrue(SituacaoValidade.CRITICO);
        long vencidos = produtoRepository.countBySituacaoValidadeAndAtivoTrue(SituacaoValidade.VENCIDO);

        return new DashboardResponseDTO(total, ok, atencao, critico, vencidos);
    }
}
