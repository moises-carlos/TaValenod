package com.ursia.tavalendo.config;

import com.ursia.tavalendo.modules.usuario.model.Role;
import com.ursia.tavalendo.modules.usuario.model.Usuario;
import com.ursia.tavalendo.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seed executado na inicialização da aplicação.
 * Cria o usuário ADMIN do sistema caso ainda não exista,
 * usando as credenciais definidas em application.properties.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeedAdminSistema implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.senha}")
    private String adminSenha;

    @Value("${admin.nome}")
    private String adminNome;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioRepository.existsByEmail(adminEmail)) {
            log.info("ADMIN do sistema já existe, seed ignorado.");
            return;
        }

        Usuario admin = Usuario.builder()
                .nome(adminNome)
                .email(adminEmail)
                .senha(passwordEncoder.encode(adminSenha))
                .role(Role.ADMIN)
                .build();

        usuarioRepository.save(admin);
        log.info("ADMIN do sistema criado com sucesso: {}", adminEmail);
    }
}