package com.ursia.tavalendo.modules.produto.controller;

import com.ursia.tavalendo.core.base.ControllerGenerico;
import com.ursia.tavalendo.core.base.RespostaApi;
import com.ursia.tavalendo.modules.produto.dto.DashboardResponseDTO;
import com.ursia.tavalendo.modules.produto.dto.ProdutoRequestDTO;
import com.ursia.tavalendo.modules.produto.dto.ProdutoResponseDTO;
import com.ursia.tavalendo.modules.produto.model.Produto;
import com.ursia.tavalendo.modules.produto.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST responsável pelos endpoints de produtos e controle de validade ({@code /api/v1/produtos}).
 * Expõe operações de cadastro, listagem, busca por ID, exclusão lógica e resumo do dashboard para o app mobile.
 */
@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoController extends ControllerGenerico<Produto, Long, ProdutoResponseDTO> {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        super(produtoService);
        this.produtoService = produtoService;
    }

    @Override
    protected ProdutoResponseDTO paraResponseDTO(Produto entidade) {
        return ProdutoResponseDTO.deEntidade(entidade);
    }

    @PostMapping
    public ResponseEntity<RespostaApi<ProdutoResponseDTO>> cadastrar(@Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO resposta = produtoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespostaApi.criado(resposta, "Produto cadastrado com sucesso."));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<RespostaApi<DashboardResponseDTO>> obterDashboard() {
        DashboardResponseDTO dashboard = produtoService.obterResumoDashboard();
        return ResponseEntity.ok(RespostaApi.sucesso(dashboard, "Resumo do dashboard carregado com sucesso."));
    }
}
