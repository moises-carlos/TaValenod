package com.ursia.tavalendo.modules.usuario.service;

import com.ursia.tavalendo.core.base.ServiceGenericoAbstrato;
import com.ursia.tavalendo.exception.custom.RecursoNaoEncontradoException;
import com.ursia.tavalendo.exception.custom.RegraNegocioException;
import com.ursia.tavalendo.modules.usuario.dto.UsuarioRequestDTO;
import com.ursia.tavalendo.modules.usuario.dto.UsuarioResponseDTO;
import com.ursia.tavalendo.modules.usuario.model.Estabelecimento;
import com.ursia.tavalendo.modules.usuario.model.Role;
import com.ursia.tavalendo.modules.usuario.model.Usuario;
import com.ursia.tavalendo.modules.usuario.repository.EstabelecimentoRepository;
import com.ursia.tavalendo.modules.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável pelas regras de negócio do domínio de {@link Usuario}.
 * Gerencia o cadastro de novos usuários, validação de unicidade de e-mail,
 * criptografia de senha e associação com o estabelecimento.
 */
@Service
public class UsuarioService extends ServiceGenericoAbstrato<Usuario, Long> {

    private final UsuarioRepository usuarioRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            EstabelecimentoRepository estabelecimentoRepository,
            PasswordEncoder passwordEncoder
    ) {
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }

        Estabelecimento estabelecimento = null;
        if (dto.estabelecimentoId() != null) {
            estabelecimento = estabelecimentoRepository.findById(dto.estabelecimentoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Estabelecimento não encontrado para o ID: " + dto.estabelecimentoId()));
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .senha(passwordEncoder.encode(dto.senha()))
                .role(dto.role() != null ? dto.role() : Role.OPERADOR)
                .estabelecimento(estabelecimento)
                .build();

        return UsuarioResponseDTO.deEntidade(salvar(usuario));
    }
}
