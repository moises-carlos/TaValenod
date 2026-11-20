package com.ursia.tavalendo.modules.produto.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ursia.tavalendo.modules.produto.dto.ProdutoCatalogoResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Serviço de integração com catálogo aberto externo (Open Food Facts).
 * Usado como fallback quando um código de barras ainda não possui histórico cadastrado na loja.
 */
@Slf4j
@Service
public class CatalogoExternoService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CatalogoExternoService() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    public Optional<ProdutoCatalogoResponseDTO> buscarPorCodigoBarras(String codigoBarras) {
        try {
            String url = "https://world.openfoodfacts.org/api/v2/product/" + codigoBarras + ".json?fields=product_name,categories";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "TaValendo-App/1.0 (contato@tavalendo.com)")
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return Optional.empty();
            }

            JsonNode root = objectMapper.readTree(response.body());
            if (root.path("status").asInt(0) != 1 || !root.hasNonNull("product")) {
                return Optional.empty();
            }

            JsonNode product = root.path("product");
            String nome = product.path("product_name").asText(null);
            if (nome == null || nome.isBlank()) {
                return Optional.empty();
            }

            String categoria = sanitizarCategoria(product.path("categories").asText(null));
            return Optional.of(new ProdutoCatalogoResponseDTO(nome.trim(), categoria, codigoBarras, "CATALOGO_EXTERNO"));
        } catch (Exception e) {
            log.warn("Consulta ao catálogo externo falhou para o código {}: {}", codigoBarras, e.getMessage());
            return Optional.empty();
        }
    }

    private String sanitizarCategoria(String categoriasRaw) {
        if (categoriasRaw == null || categoriasRaw.isBlank()) {
            return null;
        }
        String primeira = categoriasRaw.split(",")[0].trim();
        if (primeira.contains(":")) {
            primeira = primeira.substring(primeira.indexOf(':') + 1).trim();
        }
        if (primeira.isBlank()) {
            return null;
        }
        return primeira.substring(0, 1).toUpperCase() + primeira.substring(1);
    }
}
