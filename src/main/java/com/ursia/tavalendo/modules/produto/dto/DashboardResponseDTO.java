package com.ursia.tavalendo.modules.produto.dto;

/**
 * DTO de saída (Response) que consolida os indicadores numéricos do dashboard de validade,
 * agrupando o total de produtos ativos e as quantidades em cada status (OK, Atenção, Crítico e Vencido).
 */
public record DashboardResponseDTO(
        long totalProdutos,
        long ok,
        long atencao,
        long critico,
        long vencidos
) {
}
