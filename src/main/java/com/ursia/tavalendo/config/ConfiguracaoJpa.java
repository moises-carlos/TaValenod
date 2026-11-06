package com.ursia.tavalendo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuração de persistência JPA da aplicação.
 * Habilita a auditoria automática (@EnableJpaAuditing) para preenchimento
 * dos campos de criação e atualização nas entidades que herdam de EntidadeBase.
 */
@Configuration
@EnableJpaAuditing
public class ConfiguracaoJpa {
}
