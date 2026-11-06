package com.ursia.tavalendo.core.base;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Envelope genérico imutável para padronizar todas as respostas HTTP da API REST.
 * Facilita o tratamento de respostas e interceptação de erros no aplicativo mobile (Expo).
 *
 * @param <T>       Tipo do payload de dados retornado.
 * @param dados     Conteúdo principal da resposta (ou detalhes de validação em caso de erro).
 * @param mensagem  Mensagem descritiva sobre o resultado da operação.
 * @param status    Código numérico do status HTTP.
 * @param timestamp Data e hora exata em que a resposta foi gerada.
 */
public record RespostaApi<T>(
        T dados,
        String mensagem,
        int status,
        LocalDateTime timestamp
) {

    public static <T> RespostaApi<T> sucesso(T dados, String mensagem) {
        return new RespostaApi<>(dados, mensagem, HttpStatus.OK.value(), LocalDateTime.now());
    }

    public static <T> RespostaApi<T> criado(T dados, String mensagem) {
        return new RespostaApi<>(dados, mensagem, HttpStatus.CREATED.value(), LocalDateTime.now());
    }

    public static <T> RespostaApi<T> erro(String mensagem, HttpStatus status) {
        return new RespostaApi<>(null, mensagem, status.value(), LocalDateTime.now());
    }

    public static <T> RespostaApi<T> erro(T dados, String mensagem, HttpStatus status) {
        return new RespostaApi<>(dados, mensagem, status.value(), LocalDateTime.now());
    }
}
