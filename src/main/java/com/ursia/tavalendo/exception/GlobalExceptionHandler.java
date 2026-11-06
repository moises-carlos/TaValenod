package com.ursia.tavalendo.exception;

import com.ursia.tavalendo.core.base.RespostaApi;
import com.ursia.tavalendo.exception.custom.RecursoNaoEncontradoException;
import com.ursia.tavalendo.exception.custom.RegraNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Interceptador global de exceções da API (@RestControllerAdvice).
 * Captura exceções de negócio, recursos não encontrados, erros de validação (Bean Validation)
 * e falhas inesperadas, convertendo-os em respostas padronizadas {@link RespostaApi}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<RespostaApi<Void>> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(RespostaApi.erro(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<RespostaApi<Void>> tratarRegraNegocio(RegraNegocioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RespostaApi.erro(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespostaApi<Map<String, String>>> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            erros.put(erro.getField(), erro.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(RespostaApi.erro(erros, "Erro de validação nos campos informados.", HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespostaApi<Void>> tratarErroGenerico(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RespostaApi.erro("Ocorreu um erro interno no servidor.", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
