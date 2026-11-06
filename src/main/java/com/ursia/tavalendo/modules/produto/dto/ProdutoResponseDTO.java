package com.ursia.tavalendo.modules.produto.dto;

import com.ursia.tavalendo.core.utils.DataUtils;
import com.ursia.tavalendo.modules.produto.model.Produto;
import com.ursia.tavalendo.modules.produto.model.SituacaoValidade;

import java.time.LocalDate;

/**
 * DTO de saída (Response) que representa os dados de um {@link Produto} retornados para o aplicativo mobile,
 * incluindo a quantidade calculada de dias restantes até o vencimento e sua {@link SituacaoValidade}.
 */
public record ProdutoResponseDTO(
        Long id,
        String nome,
        String codigoBarras,
        String lote,
        Integer quantidade,
        LocalDate dataValidade,
        long diasRestantes,
        SituacaoValidade situacaoValidade,
        Long estabelecimentoId
) {
    public static ProdutoResponseDTO deEntidade(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getCodigoBarras(),
                produto.getLote(),
                produto.getQuantidade(),
                produto.getDataValidade(),
                DataUtils.diasParaVencimento(produto.getDataValidade()),
                produto.getSituacaoValidade(),
                produto.getEstabelecimento() != null ? produto.getEstabelecimento().getId() : null
        );
    }
}
