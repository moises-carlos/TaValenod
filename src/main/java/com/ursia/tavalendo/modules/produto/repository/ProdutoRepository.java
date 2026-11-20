package com.ursia.tavalendo.modules.produto.repository;

import com.ursia.tavalendo.modules.produto.model.Produto;
import com.ursia.tavalendo.modules.produto.model.SituacaoValidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositório Spring Data JPA para persistência e consultas personalizadas da entidade Produto.
 * JpaSpecificationExecutor habilita buscas dinâmicas e paginadas via ProdutoSpecification.
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long>,
        JpaSpecificationExecutor<Produto> {

    Optional<Produto> findFirstByEstabelecimentoIdAndCodigoBarrasOrderByAtivoDescCriadoEmDesc(
            Long estabelecimentoId, String codigoBarras);

    long countByEstabelecimentoIdAndAtivoTrue(Long estabelecimentoId);

    long countByEstabelecimentoIdAndSituacaoValidadeAndAtivoTrue(
            Long estabelecimentoId, SituacaoValidade situacaoValidade);

    List<Produto> findByEstabelecimentoIdAndAtivoTrueOrderByDataValidadeAsc(Long estabelecimentoId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Produto p
               SET p.situacaoValidade = :situacao
             WHERE p.ativo = true
               AND p.situacaoValidade <> :situacao
               AND (:inicio IS NULL OR p.dataValidade >= :inicio)
               AND (:fim    IS NULL OR p.dataValidade <= :fim)
            """)
    int atualizarSituacaoPorFaixa(
            @Param("situacao") SituacaoValidade situacao,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );
}