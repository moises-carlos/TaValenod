package com.ursia.tavalendo.modules.usuario.dto;

import com.ursia.tavalendo.modules.usuario.model.Role;
import com.ursia.tavalendo.modules.usuario.model.Usuario;

import java.time.LocalDateTime;

/**
 * DTO de saída (Response) com os dados públicos de um {@link Usuario}.
 * Garante o isolamento da entidade de domínio e omite informações sensíveis como a senha.
 */
public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Role role,
        Long estabelecimentoId,
        LocalDateTime criadoEm
) {
    public static UsuarioResponseDTO deEntidade(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getEstabelecimento() != null ? usuario.getEstabelecimento().getId() : null,
                usuario.getCriadoEm()
        );
    }
}
