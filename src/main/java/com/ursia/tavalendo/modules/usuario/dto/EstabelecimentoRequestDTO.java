package com.ursia.tavalendo.modules.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada (Request) para cadastro e atualização de estabelecimentos.
 * Garante que os dados obrigatórios, como nome e CNPJ, sejam validados antes do processamento.
 */
public record EstabelecimentoRequestDTO(
        @NotBlank(message = "O nome do estabelecimento é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String nome,

        @NotBlank(message = "O CNPJ é obrigatório.")
        @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter exatamente 14 dígitos numéricos.")
        String cnpj,

        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
        String telefone,

        @Size(max = 255, message = "O endereço deve ter no máximo 255 caracteres.")
        String endereco
) {
}