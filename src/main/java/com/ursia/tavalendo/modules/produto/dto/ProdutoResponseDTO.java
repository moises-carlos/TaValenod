package com.ursia.tavalendo.modules.produto.dto;

import com.ursia.tavalendo.core.utils.DataUtils;
import com.ursia.tavalendo.modules.produto.model.MotivoBaixa;
import com.ursia.tavalendo.modules.produto.model.Produto;
import com.ursia.tavalendo.modules.produto.model.SituacaoValidade;

import java.time.Clock;
import java.time.LocalDate;

/**
 * DTO de saída (Response) que representa os dados de um {@link Produto} retornados para o aplicativo mobile,
 * incluindo a quantidade calculada de dias restantes até o vencimento, sua {@link SituacaoValidade}
 * e os dados de baixa e status quando aplicável.
 */
public record ProdutoResponseDTO(
        Long id,
        String nome,
        String categoria,
        String codigoBarras,
        String lote,
        Integer quantidade,
        LocalDate dataValidade,
        long diasRestantes,
        SituacaoValidade situacaoValidade,
        MotivoBaixa motivoBaixa,
        LocalDate dataBaixa,
        Boolean ativo,
        Long estabelecimentoId
) {
    public static ProdutoResponseDTO deEntidade(Produto produto, Clock clock) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getCategoria(),
                produto.getCodigoBarras(),
                produto.getLote(),
                produto.getQuantidade(),
                produto.getDataValidade(),
                DataUtils.diasParaVencimento(produto.getDataValidade(), clock),
                produto.getSituacaoValidade(),
                produto.getMotivoBaixa(),
                produto.getDataBaixa(),
                produto.getAtivo(),
                produto.getEstabelecimento() != null ? produto.getEstabelecimento().getId() : null
        );
    }
}
