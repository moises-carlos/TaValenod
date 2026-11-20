package com.ursia.tavalendo.modules.auth.dto;

import com.ursia.tavalendo.modules.usuario.model.Role;

/**
 * DTO de saída do login com os tokens e dados básicos do usuário autenticado.
 */
public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        String nome,
        String email,
        Role role,
        Long estabelecimentoId
) {
}