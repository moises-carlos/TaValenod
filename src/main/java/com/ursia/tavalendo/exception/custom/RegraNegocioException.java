package com.ursia.tavalendo.exception.custom;

/**
 * Exceção personalizada lançada quando uma regra de negócio do domínio é violada
 * (ex: tentativa de cadastrar e-mail duplicado ou operação inválida).
 * Resulta em resposta HTTP 400 (Bad Request).
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String message) {
        super(message);
    }

    public RegraNegocioException(String message, Throwable cause) {
        super(message, cause);
    }
}
