package com.ursia.tavalendo.modules.produto.model;

import com.ursia.tavalendo.core.base.EntidadeBase;
import com.ursia.tavalendo.modules.usuario.model.Estabelecimento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entidade JPA que representa um produto cadastrado para monitoramento de validade.
 * Armazena dados de lote, código de barras, quantidade, data de validade,
 * classificação em {@link SituacaoValidade} e vínculo com o {@link Estabelecimento}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "produtos", indexes = {
        @Index(name = "idx_produtos_estabelecimento_codigo", columnList = "estabelecimento_id, codigo_barras")
})
public class Produto extends EntidadeBase<Long> {

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "codigo_barras", length = 60)
    private String codigoBarras;

    @Column(length = 60)
    private String lote;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(length = 80)
    private String categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_baixa", length = 30)
    private MotivoBaixa motivoBaixa;

    @Column(name = "data_baixa")
    private LocalDate dataBaixa;

    @Column(name = "data_validade", nullable = false)
    private LocalDate dataValidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao_validade", nullable = false, length = 30)
    private SituacaoValidade situacaoValidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    private Estabelecimento estabelecimento;
}
