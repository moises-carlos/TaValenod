package com.ursia.tavalendo.modules.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para criação de um GERENTE vinculado a um estabelecimento.
 * Uso exclusivo do ADMIN do sistema.
 */
public record CriarGerenteRequestDTO(

        @NotBlank(message = "O nome é obrigatório.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        String senha,

        @NotNull(message = "O estabelecimento é obrigatório.")
        Long estabelecimentoId
) {
}