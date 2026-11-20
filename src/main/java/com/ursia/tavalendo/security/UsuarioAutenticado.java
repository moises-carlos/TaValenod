package com.ursia.tavalendo.security;

import com.ursia.tavalendo.exception.custom.RecursoNaoEncontradoException;
import com.ursia.tavalendo.exception.custom.RegraNegocioException;
import com.ursia.tavalendo.modules.usuario.model.Usuario;
import com.ursia.tavalendo.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Componente que resolve o usuário autenticado a partir do SecurityContext.
 * Usado pelos services para garantir isolamento por estabelecimento,
 * evitando que o cliente informe o estabelecimentoId diretamente.
 */
@Component
@RequiredArgsConstructor
public class UsuarioAutenticado {

    private final UsuarioRepository usuarioRepository;

    public Usuario get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário autenticado não encontrado: " + email));
    }

    public Long getEstabelecimentoId() {
        Usuario usuario = get();
        if (usuario.getEstabelecimento() == null) {
            throw new RegraNegocioException(
                    "Usuário não está vinculado a nenhum estabelecimento.");
        }
        return usuario.getEstabelecimento().getId();
    }
}