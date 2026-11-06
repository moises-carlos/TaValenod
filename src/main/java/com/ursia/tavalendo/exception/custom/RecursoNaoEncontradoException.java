package com.ursia.tavalendo.exception.custom;

/**
 * Exceção personalizada lançada quando uma entidade ou recurso solicitado
 * não é encontrado no banco de dados ou encontra-se inativo.
 * Resulta em resposta HTTP 404 (Not Found).
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String message) {
        super(message);
    }
}
