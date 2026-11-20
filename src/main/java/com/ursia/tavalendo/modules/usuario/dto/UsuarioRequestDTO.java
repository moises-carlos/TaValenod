package com.ursia.tavalendo.modules.usuario.dto;

import com.ursia.tavalendo.modules.usuario.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada (Request) para cadastro e atualização de usuários.
 * Contém validações de Bean Validation para garantir integridade dos dados enviados pelo cliente.
 */
public record UsuarioRequestDTO(
        @NotBlank(message = "O nome é obrigatório.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Formato de e-mail inválido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        String senha
) {
}
