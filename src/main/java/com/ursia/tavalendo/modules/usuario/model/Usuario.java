package com.ursia.tavalendo.modules.usuario.model;

import com.ursia.tavalendo.core.base.EntidadeBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade JPA que representa um usuário do sistema Tavalendo.
 * Armazena credenciais de autenticação, perfil de acesso ({@link Role})
 * e vínculo com o {@link Estabelecimento} onde atua.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario extends EntidadeBase<Long> {

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private Role role = Role.OPERADOR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estabelecimento_id")
    private Estabelecimento estabelecimento;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;
}
