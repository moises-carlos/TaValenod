package com.ursia.tavalendo.modules.usuario.dto;

import com.ursia.tavalendo.modules.usuario.model.Estabelecimento;

import java.time.LocalDateTime;

public record EstabelecimentoResponseDTO(
        Long id,
        String nome,
        String cnpj,
        String telefone,
        String endereco,
        boolean ativo,
        LocalDateTime criadoEm
) {
    public static EstabelecimentoResponseDTO deEntidade(Estabelecimento estabelecimento) {
        return new EstabelecimentoResponseDTO(
                estabelecimento.getId(),
                estabelecimento.getNome(),
                estabelecimento.getCnpj(),
                estabelecimento.getTelefone(),
                estabelecimento.getEndereco(),
                estabelecimento.getAtivo(),
                estabelecimento.getCriadoEm()
        );
    }
}
