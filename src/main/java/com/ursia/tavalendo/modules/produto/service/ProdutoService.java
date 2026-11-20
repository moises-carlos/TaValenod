package com.ursia.tavalendo.modules.produto.service;

import com.ursia.tavalendo.core.base.ServiceGenericoAbstrato;
import com.ursia.tavalendo.core.utils.DataUtils;
import com.ursia.tavalendo.exception.custom.RecursoNaoEncontradoException;
import com.ursia.tavalendo.exception.custom.RegraNegocioException;
import com.ursia.tavalendo.modules.produto.dto.BaixaProdutoRequestDTO;
import com.ursia.tavalendo.modules.produto.dto.DashboardResponseDTO;
import com.ursia.tavalendo.modules.produto.dto.ProdutoCatalogoResponseDTO;
import com.ursia.tavalendo.modules.produto.dto.ProdutoRequestDTO;
import com.ursia.tavalendo.modules.produto.dto.ProdutoResponseDTO;
import com.ursia.tavalendo.modules.produto.model.MotivoBaixa;
import com.ursia.tavalendo.modules.produto.model.Produto;
import com.ursia.tavalendo.modules.produto.model.SituacaoValidade;
import com.ursia.tavalendo.modules.produto.repository.ProdutoRepository;
import com.ursia.tavalendo.modules.produto.specification.ProdutoSpecification;
import com.ursia.tavalendo.modules.usuario.model.Estabelecimento;
import com.ursia.tavalendo.modules.usuario.repository.EstabelecimentoRepository;
import com.ursia.tavalendo.security.UsuarioAutenticado;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pelas regras de negócio do domínio de Produto.
 * Todas as operações são isoladas pelo estabelecimento do usuário autenticado.
 */
@Service
public class ProdutoService extends ServiceGenericoAbstrato<Produto, Long> {

    @Value("${tavalendo.validade.dias-critico}")
    private long limiteCriticoDias;

    @Value("${tavalendo.validade.dias-atencao}")
    private long limiteAtencaoDias;

    private final ProdutoRepository produtoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioAutenticado usuarioAutenticado;
    private final CatalogoExternoService catalogoExternoService;
    private final Clock clock;

    public ProdutoService(
            ProdutoRepository produtoRepository,
            EstabelecimentoRepository estabelecimentoRepository,
            UsuarioAutenticado usuarioAutenticado,
            CatalogoExternoService catalogoExternoService,
            Clock clock
    ) {
        super(produtoRepository);
        this.produtoRepository = produtoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.usuarioAutenticado = usuarioAutenticado;
        this.catalogoExternoService = catalogoExternoService;
        this.clock = clock;
    }

    @Transactional
    public ProdutoResponseDTO cadastrar(ProdutoRequestDTO dto) {
        Long estabelecimentoId = usuarioAutenticado.getEstabelecimentoId();

        Estabelecimento estabelecimento = estabelecimentoRepository
                .findById(estabelecimentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estabelecimento não encontrado para o ID: " + estabelecimentoId));

        Produto produto = Produto.builder()
                .nome(dto.nome())
                .categoria(dto.categoria())
                .codigoBarras(dto.codigoBarras())
                .lote(dto.lote())
                .quantidade(dto.quantidade())
                .dataValidade(dto.dataValidade())
                .estabelecimento(estabelecimento)
                .build();

        return ProdutoResponseDTO.deEntidade(salvar(produto), clock);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarDaLoja() {
        Long estabelecimentoId = usuarioAutenticado.getEstabelecimentoId();
        return produtoRepository
                .findByEstabelecimentoIdAndAtivoTrueOrderByDataValidadeAsc(estabelecimentoId)
                .stream()
                .map(p -> ProdutoResponseDTO.deEntidade(p, clock))
                .toList();
    }

    @Transactional(readOnly = true)
    public DashboardResponseDTO obterResumoDashboard() {
        Long estabelecimentoId = usuarioAutenticado.getEstabelecimentoId();

        long total = produtoRepository.countByEstabelecimentoIdAndAtivoTrue(estabelecimentoId);
        long ok = produtoRepository.countByEstabelecimentoIdAndSituacaoValidadeAndAtivoTrue(estabelecimentoId, SituacaoValidade.OK);
        long atencao = produtoRepository.countByEstabelecimentoIdAndSituacaoValidadeAndAtivoTrue(estabelecimentoId, SituacaoValidade.ATENCAO);
        long critico = produtoRepository.countByEstabelecimentoIdAndSituacaoValidadeAndAtivoTrue(estabelecimentoId, SituacaoValidade.CRITICO);
        long vencidos = produtoRepository.countByEstabelecimentoIdAndSituacaoValidadeAndAtivoTrue(estabelecimentoId, SituacaoValidade.VENCIDO);

        return new DashboardResponseDTO(total, ok, atencao, critico, vencidos);
    }

    @Override
    protected void antesDeSalvar(Produto produto) {
        if (produto.getDataValidade() != null) {
            produto.setSituacaoValidade(calcularSituacaoValidade(produto.getDataValidade()));
        }
    }

    public SituacaoValidade calcularSituacaoValidade(LocalDate dataValidade) {
        long diasRestantes = DataUtils.diasParaVencimento(dataValidade, clock);

        if (diasRestantes < 0) return SituacaoValidade.VENCIDO;
        if (diasRestantes <= limiteCriticoDias) return SituacaoValidade.CRITICO;
        if (diasRestantes <= limiteAtencaoDias) return SituacaoValidade.ATENCAO;
        return SituacaoValidade.OK;
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Long estabelecimentoId = usuarioAutenticado.getEstabelecimentoId();

        Produto produto = produtoRepository.findById(id)
                .filter(Produto::getAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produto não encontrado para o ID: " + id));

        if (!produto.getEstabelecimento().getId().equals(estabelecimentoId)) {
            throw new RecursoNaoEncontradoException("Produto não encontrado para o ID: " + id);
        }

        produto.setNome(dto.nome());
        produto.setCodigoBarras(dto.codigoBarras());
        produto.setLote(dto.lote());
        produto.setCategoria(dto.categoria());
        produto.setQuantidade(dto.quantidade());
        produto.setDataValidade(dto.dataValidade());
        produto.setSituacaoValidade(calcularSituacaoValidade(dto.dataValidade()));

        return ProdutoResponseDTO.deEntidade(produtoRepository.save(produto), clock);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> buscar(
            String nome, String categoria, SituacaoValidade situacao,
            LocalDate inicio, LocalDate fim, Pageable pageable
    ) {
        Long estabelecimentoId = usuarioAutenticado.getEstabelecimentoId();

        return produtoRepository.findAll(
                ProdutoSpecification.filtrar(estabelecimentoId, nome, categoria, situacao, inicio, fim),
                pageable
        ).map(p -> ProdutoResponseDTO.deEntidade(p, clock));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarAlertas() {
        Long estabelecimentoId = usuarioAutenticado.getEstabelecimentoId();

        return produtoRepository.findAll(
                        ProdutoSpecification.filtrar(estabelecimentoId, null, null, null, null, null)
                ).stream()
                .filter(p -> p.getSituacaoValidade() != SituacaoValidade.OK)
                .sorted((a, b) -> a.getDataValidade().compareTo(b.getDataValidade()))
                .map(p -> ProdutoResponseDTO.deEntidade(p, clock))
                .toList();
    }

    @Transactional
    public void darBaixa(Long id, BaixaProdutoRequestDTO dto) {
        Long estabelecimentoId = usuarioAutenticado.getEstabelecimentoId();

        Produto produto = produtoRepository.findById(id)
                .filter(Produto::getAtivo)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produto não encontrado para o ID: " + id));

        if (!produto.getEstabelecimento().getId().equals(estabelecimentoId)) {
            throw new RecursoNaoEncontradoException("Produto não encontrado para o ID: " + id);
        }

        produto.setMotivoBaixa(dto.motivo());
        produto.setDataBaixa(LocalDate.now(clock));
        produto.setAtivo(false);

        produtoRepository.save(produto);
    }

    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> buscarHistoricoBaixas(
            MotivoBaixa motivo,
            LocalDate inicioBaixa,
            LocalDate fimBaixa,
            Pageable pageable
    ) {
        Long estabelecimentoId = usuarioAutenticado.getEstabelecimentoId();

        return produtoRepository.findAll(
                ProdutoSpecification.filtrarHistoricoBaixas(estabelecimentoId, motivo, inicioBaixa, fimBaixa),
                pageable
        ).map(p -> ProdutoResponseDTO.deEntidade(p, clock));
    }

    @Transactional(readOnly = true)
    public ProdutoCatalogoResponseDTO buscarPorCodigoBarras(String codigoBarras) {
        if (codigoBarras == null || codigoBarras.isBlank()) {
            throw new RegraNegocioException("O código de barras deve ser informado.");
        }
        String codigoLimpo = codigoBarras.trim();
        Long estabelecimentoId = usuarioAutenticado.getEstabelecimentoId();

        // 1. Prioridade: histórico cadastrado no próprio estabelecimento
        Optional<Produto> produtoLocal = produtoRepository
                .findFirstByEstabelecimentoIdAndCodigoBarrasOrderByAtivoDescCriadoEmDesc(
                        estabelecimentoId, codigoLimpo);

        if (produtoLocal.isPresent()) {
            Produto p = produtoLocal.get();
            return ProdutoCatalogoResponseDTO.deEntidade(p, "LOJA");
        }

        // 2. Fallback: catálogo aberto externo (Open Food Facts)
        return catalogoExternoService.buscarPorCodigoBarras(codigoLimpo)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhum produto encontrado para o código de barras informado: " + codigoLimpo));
    }
}