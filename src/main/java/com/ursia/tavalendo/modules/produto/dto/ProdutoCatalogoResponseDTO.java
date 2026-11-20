package com.ursia.tavalendo.modules.produto.dto;

import com.ursia.tavalendo.modules.produto.model.Produto;

/**
 * DTO retornado na consulta de código de barras para auto-preenchimento
 * das informações cadastrais do produto na interface móvel/web.
 */
public record ProdutoCatalogoResponseDTO(
        String nome,
        String categoria,
        String codigoBarras,
        String origem
) {
    public static ProdutoCatalogoResponseDTO deEntidade(Produto produto, String origem) {
        return new ProdutoCatalogoResponseDTO(
                produto.getNome(),
                produto.getCategoria(),
                produto.getCodigoBarras(),
                origem
        );
    }
}
