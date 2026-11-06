package com.ursia.tavalendo.modules.usuario.repository;

import com.ursia.tavalendo.modules.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório Spring Data JPA para operações de persistência e consultas customizadas da entidade {@link Usuario},
 * incluindo busca de usuários ativos por e-mail para autenticação.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailAndAtivoTrue(String email);

    boolean existsByEmail(String email);
}
