package com.ursia.tavalendo.modules.usuario.service;

import com.ursia.tavalendo.core.base.ServiceGenericoAbstrato;
import com.ursia.tavalendo.exception.custom.RecursoNaoEncontradoException;
import com.ursia.tavalendo.exception.custom.RegraNegocioException;
import com.ursia.tavalendo.modules.usuario.dto.CriarGerenteRequestDTO;
import com.ursia.tavalendo.modules.usuario.dto.CriarOperadorRequestDTO;
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
 * Serviço responsável pelas regras de negócio do domínio de Usuario.
 * Gerencia criação de GERENTE (pelo ADMIN do sistema) e
 * criação de OPERADOR/GERENTE (pelo GERENTE do estabelecimento).
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
    public UsuarioResponseDTO criarGerente(CriarGerenteRequestDTO dto) {
        validarEmailUnico(dto.email());

        Estabelecimento estabelecimento = estabelecimentoRepository
                .findById(dto.estabelecimentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estabelecimento não encontrado para o ID: " + dto.estabelecimentoId()));

        Usuario gerente = Usuario.builder()
                .nome(dto.nome())
                .email(normalizar(dto.email()))
                .senha(passwordEncoder.encode(dto.senha()))
                .role(Role.GERENTE)
                .estabelecimento(estabelecimento)
                .build();

        return UsuarioResponseDTO.deEntidade(salvar(gerente));
    }

    @Transactional
    public UsuarioResponseDTO criarUsuarioNaLoja(
            CriarOperadorRequestDTO dto,
            Long estabelecimentoId,
            Role role
    ) {
        validarEmailUnico(dto.email());

        Estabelecimento estabelecimento = estabelecimentoRepository
                .findById(estabelecimentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estabelecimento não encontrado para o ID: " + estabelecimentoId));

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .email(normalizar(dto.email()))
                .senha(passwordEncoder.encode(dto.senha()))
                .role(role)
                .estabelecimento(estabelecimento)
                .build();

        return UsuarioResponseDTO.deEntidade(salvar(usuario));
    }

    private void validarEmailUnico(String email) {
        if (usuarioRepository.existsByEmailAndAtivoTrue(normalizar(email))) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }
    }

    @Transactional(readOnly = true)
    public Long buscarEstabelecimentoIdPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmailAndAtivoTrue(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado: " + email));

        if (usuario.getEstabelecimento() == null) {
            throw new RegraNegocioException("Usuário não está vinculado a nenhum estabelecimento.");
        }

        return usuario.getEstabelecimento().getId();
    }

    private String normalizar(String email) {
        return email.trim().toLowerCase();
    }
}