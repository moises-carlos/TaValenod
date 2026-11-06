package com.ursia.tavalendo.modules.usuario.model;

/**
 * Enumeração dos perfis de acesso (RBAC) atribuídos aos usuários do sistema.
 * Utilizada para controle de permissões nas rotas da API via {@code @PreAuthorize}.
 */
public enum Role {
    ADMIN,
    GERENTE,
    OPERADOR
}
