package com.ursia.tavalendo.modules.produto.model;

/**
 * Enumeração que classifica a situação de validade de um {@link Produto}
 * de acordo com a proximidade da sua data de vencimento.
 */
public enum SituacaoValidade {
    OK,
    ATENCAO,
    CRITICO,
    VENCIDO
}
