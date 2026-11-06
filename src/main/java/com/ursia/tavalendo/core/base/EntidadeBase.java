package com.ursia.tavalendo.core.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Classe base abstrata para todas as entidades JPA do sistema.
 * Fornece chave primária genérica, campos de auditoria automática (criadoEm, atualizadoEm)
 * e controle de exclusão lógica (soft delete) por meio do campo {@code ativo}.
 *
 * @param <ID> Tipo do identificador único da entidade (ex: Long, UUID).
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class EntidadeBase<ID extends Serializable> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID id;

    @CreatedDate
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    @LastModifiedDate
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm = LocalDateTime.now();

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;
}
