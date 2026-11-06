package com.ursia.tavalendo.security.custom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração que disponibiliza o bean de criptografia de senhas ({@link BCryptPasswordEncoder})
 * para hash seguro e verificação de credenciais de usuários.
 */
@Configuration
public class ConfiguracaoCodificadorSenha {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
