package com.ursia.tavalendo.core.utils;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Classe utilitária para cálculos de datas e prazos de validade.
 * Recebe um Clock injetável para garantir o fuso correto e facilitar testes.
 */
public final class DataUtils {

    private DataUtils() {
    }

    public static long diasParaVencimento(LocalDate dataValidade, Clock clock) {
        if (dataValidade == null) {
            throw new IllegalArgumentException("A data de validade não pode ser nula.");
        }
        return ChronoUnit.DAYS.between(LocalDate.now(clock), dataValidade);
    }

    public static boolean estaVencido(LocalDate dataValidade, Clock clock) {
        return diasParaVencimento(dataValidade, clock) < 0;
    }
}