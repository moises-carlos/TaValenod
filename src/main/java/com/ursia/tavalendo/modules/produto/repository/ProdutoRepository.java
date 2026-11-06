package com.ursia.tavalendo.modules.produto.repository;

import com.ursia.tavalendo.modules.produto.model.Produto;
import com.ursia.tavalendo.modules.produto.model.SituacaoValidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositório Spring Data JPA para persistência e consultas personalizadas da entidade {@link Produto}.
 * Fornece contagens de produtos ativos por {@link SituacaoValidade} para o dashboard e buscas por período de vencimento.
 */
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    long countByAtivoTrue();

    long countBySituacaoValidadeAndAtivoTrue(SituacaoValidade situacaoValidade);

    List<Produto> findByDataValidadeBetweenAndAtivoTrueOrderByDataValidadeAsc(LocalDate inicio, LocalDate fim);

    List<Produto> findBySituacaoValidadeAndAtivoTrueOrderByDataValidadeAsc(SituacaoValidade situacaoValidade);
}
