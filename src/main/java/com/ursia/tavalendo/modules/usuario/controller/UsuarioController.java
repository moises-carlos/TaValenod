package com.ursia.tavalendo.modules.usuario.controller;

import com.ursia.tavalendo.core.base.ControllerGenerico;
import com.ursia.tavalendo.core.base.RespostaApi;
import com.ursia.tavalendo.modules.usuario.dto.UsuarioRequestDTO;
import com.ursia.tavalendo.modules.usuario.dto.UsuarioResponseDTO;
import com.ursia.tavalendo.modules.usuario.model.Usuario;
import com.ursia.tavalendo.modules.usuario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST responsável pelos endpoints de gerenciamento de usuários ({@code /api/v1/usuarios}).
 * Expõe rotas para cadastro, consulta e exclusão lógica com controle de acesso por perfil (RBAC).
 */
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

    @PostMapping("/cadastro")
    public ResponseEntity<RespostaApi<UsuarioResponseDTO>> cadastrar(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO resposta = usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespostaApi.criado(resposta, "Usuário cadastrado com sucesso."));
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
