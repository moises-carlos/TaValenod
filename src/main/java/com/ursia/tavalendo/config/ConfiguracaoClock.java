package com.ursia.tavalendo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Disponibiliza um bean Clock fixado no fuso America/Recife.
 * Evita divergências de data quando a JVM roda em UTC (containers Alpine).
 */
@Configuration
public class ConfiguracaoClock {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("America/Recife"));
    }
}