package com.ursia.tavalendo.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para renovação do access token via refresh token.
 */
public record RefreshRequestDTO(

        @NotBlank(message = "O refresh token é obrigatório.")
        String refreshToken
) {
}