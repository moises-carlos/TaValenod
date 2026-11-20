package com.ursia.tavalendo.modules.auth.service;

import com.ursia.tavalendo.exception.custom.RegraNegocioException;
import com.ursia.tavalendo.modules.auth.dto.LoginRequestDTO;
import com.ursia.tavalendo.modules.auth.dto.LoginResponseDTO;
import com.ursia.tavalendo.modules.auth.dto.RefreshRequestDTO;
import com.ursia.tavalendo.modules.usuario.model.Usuario;
import com.ursia.tavalendo.modules.usuario.repository.UsuarioRepository;
import com.ursia.tavalendo.security.jwt.ProvedorTokenJwt;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável pelo fluxo de autenticação.
 * Realiza login via AuthenticationManager do Spring Security,
 * gera access e refresh tokens JWT e valida renovação de token.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final ProvedorTokenJwt provedorTokenJwt;

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO dto) {
        String email = dto.email().trim().toLowerCase();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, dto.senha())
            );
        } catch (AuthenticationException e) {
            throw new RegraNegocioException("E-mail ou senha inválidos.");
        }


        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));

        String accessToken = provedorTokenJwt.gerarAccessToken(
                usuario.getEmail(),
                usuario.getRole().name()
        );
        String refreshToken = provedorTokenJwt.gerarRefreshToken(
                usuario.getEmail(),
                usuario.getRole().name()
        );

        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getEstabelecimento() != null ? usuario.getEstabelecimento().getId() : null
        );
    }

    @Transactional(readOnly = true)
    public LoginResponseDTO refresh(RefreshRequestDTO dto) {
        if (!provedorTokenJwt.validarToken(dto.refreshToken())) {
            throw new RegraNegocioException("Refresh token inválido ou expirado.");
        }

        String email = provedorTokenJwt.extrairSubject(dto.refreshToken());

        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado."));

        String novoAccessToken = provedorTokenJwt.gerarAccessToken(
                usuario.getEmail(),
                usuario.getRole().name()
        );
        String novoRefreshToken = provedorTokenJwt.gerarRefreshToken(
                usuario.getEmail(),
                usuario.getRole().name()
        );

        return new LoginResponseDTO(
                novoAccessToken,
                novoRefreshToken,
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getEstabelecimento() != null ? usuario.getEstabelecimento().getId() : null
        );
    }
}