package com.ursia.tavalendo.modules.produto.dto;

import com.ursia.tavalendo.modules.produto.model.MotivoBaixa;
import jakarta.validation.constraints.NotNull;

public record BaixaProdutoRequestDTO(
        @NotNull(message = "O motivo da baixa é obrigatório.")
        MotivoBaixa motivo
) {
}