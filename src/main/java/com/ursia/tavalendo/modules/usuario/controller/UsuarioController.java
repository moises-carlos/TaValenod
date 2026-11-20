package com.ursia.tavalendo.modules.usuario.controller;

import com.ursia.tavalendo.core.base.ControllerGenerico;
import com.ursia.tavalendo.core.base.RespostaApi;
import com.ursia.tavalendo.modules.usuario.dto.CriarGerenteRequestDTO;
import com.ursia.tavalendo.modules.usuario.dto.CriarOperadorRequestDTO;
import com.ursia.tavalendo.modules.usuario.dto.UsuarioResponseDTO;
import com.ursia.tavalendo.modules.usuario.model.Role;
import com.ursia.tavalendo.modules.usuario.model.Usuario;
import com.ursia.tavalendo.modules.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST responsável pelos endpoints de gerenciamento de usuários (/api/v1/usuarios).
 * ADMIN do sistema cria GERENTEs. GERENTEs criam OPERADOREs e outros GERENTEs na própria loja.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController extends ControllerGenerico<Usuario, Long, UsuarioResponseDTO> {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        super(usuarioService);
        this.usuarioService = usuarioService;
    }

    @Override
    protected UsuarioResponseDTO paraResponseDTO(Usuario entidade) {
        return UsuarioResponseDTO.deEntidade(entidade);
    }

    @PostMapping("/gerente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespostaApi<UsuarioResponseDTO>> criarGerente(
            @Valid @RequestBody CriarGerenteRequestDTO dto
    ) {
        UsuarioResponseDTO resposta = usuarioService.criarGerente(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespostaApi.criado(resposta, "Gerente criado com sucesso."));
    }

    @PostMapping("/operador")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<RespostaApi<UsuarioResponseDTO>> criarOperador(
            @Valid @RequestBody CriarOperadorRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long estabelecimentoId = usuarioService.buscarEstabelecimentoIdPorEmail(userDetails.getUsername());
        UsuarioResponseDTO resposta = usuarioService.criarUsuarioNaLoja(dto, estabelecimentoId, Role.OPERADOR);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespostaApi.criado(resposta, "Operador criado com sucesso."));
    }

    @PostMapping("/gerente-loja")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<RespostaApi<UsuarioResponseDTO>> criarGerenteLoja(
            @Valid @RequestBody CriarOperadorRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long estabelecimentoId = usuarioService.buscarEstabelecimentoIdPorEmail(userDetails.getUsername());
        UsuarioResponseDTO resposta = usuarioService.criarUsuarioNaLoja(dto, estabelecimentoId, Role.GERENTE);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespostaApi.criado(resposta, "Gerente criado com sucesso."));
    }

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespostaApi<List<UsuarioResponseDTO>>> listarTodos() {
        return super.listarTodos();
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RespostaApi<Void>> deletar(@PathVariable Long id) {
        return super.deletar(id);
    }
}