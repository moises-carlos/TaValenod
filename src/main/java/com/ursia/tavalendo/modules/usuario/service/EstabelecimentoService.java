package com.ursia.tavalendo.modules.usuario.service;

import com.ursia.tavalendo.core.base.ServiceGenericoAbstrato;
import com.ursia.tavalendo.exception.custom.RegraNegocioException;
import com.ursia.tavalendo.modules.usuario.dto.EstabelecimentoRequestDTO;
import com.ursia.tavalendo.modules.usuario.dto.EstabelecimentoResponseDTO;
import com.ursia.tavalendo.modules.usuario.model.Estabelecimento;
import com.ursia.tavalendo.modules.usuario.repository.EstabelecimentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável pelas regras de negócio do domínio de {@link Estabelecimento}.
 * Trata validações como a unicidade do CNPJ no sistema.
 */
@Service
public class EstabelecimentoService extends ServiceGenericoAbstrato<Estabelecimento, Long> {

    private final EstabelecimentoRepository estabelecimentoRepository;

    public EstabelecimentoService(EstabelecimentoRepository estabelecimentoRepository) {
        super(estabelecimentoRepository);
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    @Transactional
    public EstabelecimentoResponseDTO cadastrar(EstabelecimentoRequestDTO dto) {
        if (estabelecimentoRepository.existsByCnpj(dto.cnpj())) {
            throw new RegraNegocioException("Já existe um estabelecimento cadastrado com este CNPJ.");
        }

        Estabelecimento estabelecimento = Estabelecimento.builder()
                .nome(dto.nome())
                .cnpj(dto.cnpj())
                .telefone(dto.telefone())
                .endereco(dto.endereco())
                .build();

        return EstabelecimentoResponseDTO.deEntidade(salvar(estabelecimento));
    }
}