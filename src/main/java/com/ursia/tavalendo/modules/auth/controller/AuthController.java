package com.ursia.tavalendo.modules.auth.controller;

import com.ursia.tavalendo.core.base.RespostaApi;
import com.ursia.tavalendo.modules.auth.dto.LoginRequestDTO;
import com.ursia.tavalendo.modules.auth.dto.LoginResponseDTO;
import com.ursia.tavalendo.modules.auth.dto.RefreshRequestDTO;
import com.ursia.tavalendo.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST responsável pelos endpoints de autenticação (/api/v1/auth).
 * Rotas públicas: login e refresh de token.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<RespostaApi<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO dto
    ) {
        LoginResponseDTO resposta = authService.login(dto);
        return ResponseEntity.ok(RespostaApi.sucesso(resposta, "Login realizado com sucesso."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<RespostaApi<LoginResponseDTO>> refresh(
            @Valid @RequestBody RefreshRequestDTO dto
    ) {
        LoginResponseDTO resposta = authService.refresh(dto);
        return ResponseEntity.ok(RespostaApi.sucesso(resposta, "Token renovado com sucesso."));
    }
}