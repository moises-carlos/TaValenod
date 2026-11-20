package com.ursia.tavalendo.modules.produto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

/**
 * DTO de entrada (Request) para cadastro e atualização de produtos.
 * Contém anotações do Bean Validation para validar campos obrigatórios e quantidades positivas.
 */
public record ProdutoRequestDTO(
        @NotBlank(message = "O nome do produto é obrigatório.")
        String nome,

        String categoria,

        String codigoBarras,

        String lote,

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantidade,

        @NotNull(message = "A data de validade é obrigatória.")
        LocalDate dataValidade
) {
}
