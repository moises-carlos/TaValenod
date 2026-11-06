package com.ursia.tavalendo.modules.usuario.repository;

import com.ursia.tavalendo.modules.usuario.model.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório Spring Data JPA para operações de persistência e consulta da entidade {@link Estabelecimento}.
 */
@Repository
public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Long> {

    boolean existsByCnpj(String cnpj);
}
