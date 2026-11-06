package com.ursia.tavalendo.core.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Classe utilitária com métodos auxiliares para cálculos de datas e prazos de validade.
 * Utilizada para determinar a quantidade de dias restantes até o vencimento e verificar se um item venceu.
 */
public final class DataUtils {

    private DataUtils() {
    }

    public static long diasParaVencimento(LocalDate dataValidade) {
        if (dataValidade == null) {
            throw new IllegalArgumentException("A data de validade não pode ser nula.");
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), dataValidade);
    }

    public static boolean estaVencido(LocalDate dataValidade) {
        return diasParaVencimento(dataValidade) < 0;
    }
}
