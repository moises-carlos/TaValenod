package com.ursia.tavalendo.exception;

import com.ursia.tavalendo.core.base.RespostaApi;
import com.ursia.tavalendo.exception.custom.RecursoNaoEncontradoException;
import com.ursia.tavalendo.exception.custom.RegraNegocioException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Interceptador global de exceções da API.
 * Trata erros de negócio, validação, autenticação, autorização e falhas inesperadas,
 * convertendo-os em respostas padronizadas RespostaApi com status HTTP corretos.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<RespostaApi<Void>> tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(RespostaApi.erro(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<RespostaApi<Void>> tratarRegraNegocio(RegraNegocioException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(RespostaApi.erro(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaApi<Map<String, String>>> tratarValidacao(
            MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            erros.put(erro.getField(), erro.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RespostaApi.erro(erros, "Erro de validação nos campos informados.",
                        HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespostaApi<Void>> tratarMensagemIlegivel(
            HttpMessageNotReadableException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RespostaApi.erro("Requisição malformada. Verifique o corpo enviado.",
                        HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RespostaApi<Void>> tratarRotaNaoEncontrada(
            NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(RespostaApi.erro("Rota não encontrada.", HttpStatus.NOT_FOUND));
    }

    // deixa o Spring Security tratar — vira 401 pelo AuthenticationEntryPoint
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RespostaApi<Void>> tratarAutenticacao(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(RespostaApi.erro("Não autenticado.", HttpStatus.UNAUTHORIZED));
    }

    // deixa o Spring Security tratar — vira 403 pelo AccessDeniedHandler
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RespostaApi<Void>> tratarAcessoNegado(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(RespostaApi.erro("Acesso negado.", HttpStatus.FORBIDDEN));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaApi<Void>> tratarErroGenerico(Exception ex) {
        log.error("Erro interno não tratado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RespostaApi.erro("Ocorreu um erro interno no servidor.",
                        HttpStatus.INTERNAL_SERVER_ERROR));
    }
}