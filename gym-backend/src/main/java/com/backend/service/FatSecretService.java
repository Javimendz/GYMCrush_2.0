package com.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import com.backend.dto.FatSecretRecipeDto;
import com.backend.dto.FatSecretResponseDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class FatSecretService {

    @Value("${fatsecret.client.id}")
    private String clientId;

    @Value("${fatsecret.client.secret}")
    private String clientSecret;

    @Value("${fatsecret.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper; // Jackson

    public String obtenerTokenAutenticacion() {
        String url = "https://oauth.fatsecret.com/connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("scope", "basic");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            if (root.has("access_token")) {
                return root.get("access_token").asText();
            }
            throw new RuntimeException("Respuesta de FatSecret no contiene token");

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener token con Jackson: " + e.getMessage());
        }
    }

    /**
     * Busca alimentos/recetas por nombre usando el método recipes.search de
     * FatSecret
     */
    public String buscarAlimentosPorNombre(String query) {
        String token = obtenerTokenAutenticacion();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        String url = apiUrl + "?method=recipes.search&format=json" +
                "&search_expression=" + query +
                "&max_results=20"; // Limitamos a 20 resultados para el móvil

        HttpEntity<?> request = new HttpEntity<>(headers);

        try {
            log.info("Llamando a FatSecret para buscar: {}", query);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            String body = response.getBody();

            // Validamos errores en el JSON
            JsonNode root = objectMapper.readTree(body);
            if (root.has("error")) {
                log.error("Error API FatSecret: {}", root.get("error").get("message").asText());
                return "{\"recipes\": {\"recipe\": []}}";
            }

            return body;
        } catch (Exception e) {
            log.error("Error en la búsqueda de alimentos: {}", e.getMessage());
            return "{\"recipes\": {\"recipe\": []}}";
        }
    }

    public String buscarRecetasPorMacros(int calorias, int proteinas, int carbohidratos, int grasas) {
        String token = obtenerTokenAutenticacion();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        int calPorPlato = calorias / 3;

        String[] categorias = { "chicken", "fish", "salad", "rice", "pasta", "vegan" };
        String categoria = categorias[new java.util.Random().nextInt(categorias.length)];

        String url = apiUrl + "?method=recipes.search&format=json" +
                "&search_expression=" + categoria +
                "&calories.from=" + (calPorPlato - 200) +
                "&calories.to=" + (calPorPlato + 200) +
                "&max_results=10";

        HttpEntity<?> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            String body = response.getBody();

            JsonNode root = objectMapper.readTree(body);
            if (root.has("error")) {
                log.error("FatSecret error: {}", root.get("error").get("message").asText());
                return "{\"recipes\": {\"recipe\": []}}";
            }

            return body;
        } catch (Exception e) {
            log.error("Error buscando recetas: {}", e.getMessage());
            return "{\"recipes\": {\"recipe\": []}}";
        }
    }

    public List<FatSecretRecipeDto> buscarMuchasRecetas(int kcal) {

        List<FatSecretRecipeDto> todas = new java.util.ArrayList<>();

        String[] categorias = { "chicken", "fish", "rice", "pasta", "salad", "vegan" };

        for (String cat : categorias) {
            String json = buscarAlimentosPorNombre(cat);

            try {
                FatSecretResponseDto fs = objectMapper.readValue(json, FatSecretResponseDto.class);
                if (fs.getRecipes() != null) {
                    todas.addAll(fs.getRecipes().getRecipe());
                }
            } catch (Exception ignored) {
            }
        }

        return todas;
    }
}