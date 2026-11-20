package com.ursia.tavalendo.modules.produto.specification;

import com.ursia.tavalendo.modules.produto.model.MotivoBaixa;
import com.ursia.tavalendo.modules.produto.model.Produto;
import com.ursia.tavalendo.modules.produto.model.SituacaoValidade;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * Specifications para filtragem dinâmica de produtos.
 * Combina estabelecimento, nome, categoria, situação e período de vencimento
 * de forma independente, permitindo qualquer combinação de filtros.
 */
public final class ProdutoSpecification {

    private ProdutoSpecification() {
    }

    public static Specification<Produto> filtrar(
            Long estabelecimentoId,
            String nome,
            String categoria,
            SituacaoValidade situacao,
            LocalDate inicio,
            LocalDate fim
    ) {
        return (root, query, cb) -> {
            var predicados = cb.conjunction();

            predicados = cb.and(predicados, cb.isTrue(root.get("ativo")));
            predicados = cb.and(predicados,
                    cb.equal(root.get("estabelecimento").get("id"), estabelecimentoId));

            if (nome != null && !nome.isBlank()) {
                predicados = cb.and(predicados,
                        cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
            }
            if (categoria != null && !categoria.isBlank()) {
                predicados = cb.and(predicados,
                        cb.equal(cb.lower(root.get("categoria")), categoria.toLowerCase()));
            }
            if (situacao != null) {
                predicados = cb.and(predicados,
                        cb.equal(root.get("situacaoValidade"), situacao));
            }
            if (inicio != null) {
                predicados = cb.and(predicados,
                        cb.greaterThanOrEqualTo(root.get("dataValidade"), inicio));
            }
            if (fim != null) {
                predicados = cb.and(predicados,
                        cb.lessThanOrEqualTo(root.get("dataValidade"), fim));
            }

            return predicados;
        };
    }

    public static Specification<Produto> filtrarHistoricoBaixas(
            Long estabelecimentoId,
            MotivoBaixa motivo,
            LocalDate inicioBaixa,
            LocalDate fimBaixa
    ) {
        return (root, query, cb) -> {
            var predicados = cb.conjunction();

            predicados = cb.and(predicados, cb.isFalse(root.get("ativo")));
            predicados = cb.and(predicados,
                    cb.equal(root.get("estabelecimento").get("id"), estabelecimentoId));

            if (motivo != null) {
                predicados = cb.and(predicados,
                        cb.equal(root.get("motivoBaixa"), motivo));
            }
            if (inicioBaixa != null) {
                predicados = cb.and(predicados,
                        cb.greaterThanOrEqualTo(root.get("dataBaixa"), inicioBaixa));
            }
            if (fimBaixa != null) {
                predicados = cb.and(predicados,
                        cb.lessThanOrEqualTo(root.get("dataBaixa"), fimBaixa));
            }

            return predicados;
        };
    }
}