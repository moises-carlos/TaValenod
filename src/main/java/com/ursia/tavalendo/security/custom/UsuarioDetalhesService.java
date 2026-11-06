package com.ursia.tavalendo.security.custom;

import com.ursia.tavalendo.modules.usuario.model.Usuario;
import com.ursia.tavalendo.modules.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementação customizada de {@link UserDetailsService} do Spring Security.
 * Carrega os dados de credenciais e permissões (Roles) de um usuário ativo a partir do banco de dados.
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetalhesService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));

        return new User(
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getAtivo(),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()))
        );
    }
}
