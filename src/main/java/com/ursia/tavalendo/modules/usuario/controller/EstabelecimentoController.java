package com.ursia.tavalendo.modules.usuario.controller;

import com.ursia.tavalendo.core.base.ControllerGenerico;
import com.ursia.tavalendo.core.base.RespostaApi;
import com.ursia.tavalendo.modules.usuario.dto.EstabelecimentoRequestDTO;
import com.ursia.tavalendo.modules.usuario.dto.EstabelecimentoResponseDTO;
import com.ursia.tavalendo.modules.usuario.model.Estabelecimento;
import com.ursia.tavalendo.modules.usuario.service.EstabelecimentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST responsável pelos endpoints de gerenciamento de estabelecimentos ({@code /api/v1/estabelecimentos}).
 */
@RestController
@RequestMapping("/api/v1/estabelecimentos")
public class EstabelecimentoController extends ControllerGenerico<Estabelecimento, Long, EstabelecimentoResponseDTO> {

    private final EstabelecimentoService estabelecimentoService;

    public EstabelecimentoController(EstabelecimentoService estabelecimentoService) {
        super(estabelecimentoService);
        this.estabelecimentoService = estabelecimentoService;
    }

    @Override
    protected EstabelecimentoResponseDTO paraResponseDTO(Estabelecimento entidade) {
        return EstabelecimentoResponseDTO.deEntidade(entidade);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespostaApi<EstabelecimentoResponseDTO>> cadastrar(@Valid @RequestBody EstabelecimentoRequestDTO dto) {
        EstabelecimentoResponseDTO resposta = estabelecimentoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespostaApi.criado(resposta, "Estabelecimento cadastrado com sucesso."));
    }
}