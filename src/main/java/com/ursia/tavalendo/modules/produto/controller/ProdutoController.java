package com.ursia.tavalendo.modules.produto.controller;

import com.ursia.tavalendo.core.base.ControllerGenerico;
import com.ursia.tavalendo.core.base.RespostaApi;
import com.ursia.tavalendo.modules.produto.dto.BaixaProdutoRequestDTO;
import com.ursia.tavalendo.modules.produto.dto.DashboardResponseDTO;
import com.ursia.tavalendo.modules.produto.dto.ProdutoCatalogoResponseDTO;
import com.ursia.tavalendo.modules.produto.dto.ProdutoRequestDTO;
import com.ursia.tavalendo.modules.produto.dto.ProdutoResponseDTO;
import com.ursia.tavalendo.modules.produto.model.MotivoBaixa;
import com.ursia.tavalendo.modules.produto.model.Produto;
import com.ursia.tavalendo.modules.produto.model.SituacaoValidade;
import com.ursia.tavalendo.modules.produto.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller REST responsável pelos endpoints de produtos e controle de validade ({@code /api/v1/produtos}).
 * Expõe cadastro, edição, busca paginada, alertas, baixa e dashboard, isolados por estabelecimento.
 */
@RestController
@RequestMapping("/api/v1/produtos")
public class ProdutoController extends ControllerGenerico<Produto, Long, ProdutoResponseDTO> {

    private final ProdutoService produtoService;
    private final Clock clock;

    public ProdutoController(ProdutoService produtoService, Clock clock) {
        super(produtoService);
        this.produtoService = produtoService;
        this.clock = clock;
    }

    @Override
    protected ProdutoResponseDTO paraResponseDTO(Produto entidade) {
        return ProdutoResponseDTO.deEntidade(entidade, this.clock);
    }

    @Override
    @GetMapping
    public ResponseEntity<RespostaApi<List<ProdutoResponseDTO>>> listarTodos() {
        List<ProdutoResponseDTO> produtos = produtoService.listarDaLoja();
        return ResponseEntity.ok(RespostaApi.sucesso(produtos, "Produtos listados com sucesso."));
    }

    @PostMapping
    public ResponseEntity<RespostaApi<ProdutoResponseDTO>> cadastrar(
            @Valid @RequestBody ProdutoRequestDTO dto
    ) {
        ProdutoResponseDTO resposta = produtoService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespostaApi.criado(resposta, "Produto cadastrado com sucesso."));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RespostaApi<ProdutoResponseDTO>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO dto
    ) {
        ProdutoResponseDTO resposta = produtoService.atualizar(id, dto);
        return ResponseEntity.ok(RespostaApi.sucesso(resposta, "Produto atualizado com sucesso."));
    }

    @GetMapping("/buscar")
    public ResponseEntity<RespostaApi<Page<ProdutoResponseDTO>>> buscar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) SituacaoValidade situacao,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @PageableDefault(size = 20, sort = "dataValidade") Pageable pageable
    ) {
        Page<ProdutoResponseDTO> resultado = produtoService.buscar(nome, categoria, situacao, inicio, fim, pageable);
        return ResponseEntity.ok(RespostaApi.sucesso(resultado, "Busca realizada com sucesso."));
    }

    @GetMapping("/alertas")
    public ResponseEntity<RespostaApi<List<ProdutoResponseDTO>>> listarAlertas() {
        List<ProdutoResponseDTO> alertas = produtoService.listarAlertas();
        return ResponseEntity.ok(RespostaApi.sucesso(alertas, "Alertas carregados com sucesso."));
    }

    @PostMapping("/{id}/baixa")
    public ResponseEntity<RespostaApi<Void>> darBaixa(
            @PathVariable Long id,
            @Valid @RequestBody BaixaProdutoRequestDTO dto
    ) {
        produtoService.darBaixa(id, dto);
        return ResponseEntity.ok(RespostaApi.sucesso(null, "Produto baixado com sucesso."));
    }

    @GetMapping("/historico")
    public ResponseEntity<RespostaApi<Page<ProdutoResponseDTO>>> listarHistorico(
            @RequestParam(required = false) MotivoBaixa motivo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @PageableDefault(size = 20, sort = "dataBaixa", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ProdutoResponseDTO> resultado = produtoService.buscarHistoricoBaixas(motivo, inicio, fim, pageable);
        return ResponseEntity.ok(RespostaApi.sucesso(resultado, "Histórico de baixas carregado com sucesso."));
    }

    @GetMapping("/codigo/{codigoBarras}")
    public ResponseEntity<RespostaApi<ProdutoCatalogoResponseDTO>> buscarPorCodigoBarras(
            @PathVariable String codigoBarras
    ) {
        ProdutoCatalogoResponseDTO resposta = produtoService.buscarPorCodigoBarras(codigoBarras);
        return ResponseEntity.ok(RespostaApi.sucesso(resposta, "Produto localizado no catálogo com sucesso."));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<RespostaApi<DashboardResponseDTO>> obterDashboard() {
        DashboardResponseDTO dashboard = produtoService.obterResumoDashboard();
        return ResponseEntity.ok(RespostaApi.sucesso(dashboard, "Dashboard carregado com sucesso."));
    }
}