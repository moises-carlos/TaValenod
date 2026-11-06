package com.ursia.tavalendo.modules.usuario.model;

import com.ursia.tavalendo.core.base.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA que representa um estabelecimento comercial (ex: mercado, farmácia ou loja)
 * ao qual usuários e produtos estão vinculados no controle de validade.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "estabelecimentos")
public class Estabelecimento extends EntidadeBase<Long> {

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(unique = true, length = 18)
    private String cnpj;

    @Column(length = 20)
    private String telefone;

    @Column(length = 255)
    private String endereco;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;
}
