package com.ursia.tavalendo.security.jwt;

import com.ursia.tavalendo.security.custom.UsuarioDetalhesService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro HTTP executado uma vez por requisição ({@link OncePerRequestFilter}).
 * Intercepta o cabeçalho {@code Authorization: Bearer <token>}, valida o JWT
 * e autentica o usuário no contexto de segurança do Spring Security.
 */
@Component
@RequiredArgsConstructor
public class FiltroAutenticacaoJwt extends OncePerRequestFilter {

    private final ProvedorTokenJwt provedorTokenJwt;
    private final UsuarioDetalhesService usuarioDetalhesService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = extrairTokenDoHeader(request);

        if (StringUtils.hasText(token) && provedorTokenJwt.validarToken(token)) {
            String email = provedorTokenJwt.extrairSubject(token);
            UserDetails userDetails = usuarioDetalhesService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extrairTokenDoHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
