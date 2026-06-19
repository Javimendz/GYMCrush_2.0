package com.backend.service;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper; 

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.backend.dto.ExerciseApiRequestDto;
import com.backend.util.TraductorEjercicio;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseProxyServiceImp implements IExerciseProxyService {

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${rapidapi.host}")
    private String apiHost;

    @Value("${rapidapi.url}")
    private String apiUrl;

    @Value("${pexels.api.key}")
    private String pexelsApiKey;

    private final RestTemplate restTemplate;
    // Instanciamos el mapeador de JSON a DTO (inyectado por Spring)
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<ExerciseApiRequestDto> buscarPorNombre(String nombre) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", apiKey);
        headers.set("X-RapidAPI-Host", apiHost);
        headers.set("User-Agent", "Mozilla/5.0");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        String nombreIngles = TraductorEjercicio.traducirParaBusqueda(nombre);
        String url = apiUrl + "/name/" + nombreIngles.toLowerCase().trim();

        try {
            log.info("Buscando en WorkoutX API: {}", nombreIngles);

            ResponseEntity<ExerciseApiRequestDto[]> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, ExerciseApiRequestDto[].class);

            if (response.getBody() != null) {
                List<ExerciseApiRequestDto> lista = Arrays.asList(response.getBody());

                lista.forEach(ejercicio -> {
                    ejercicio.setTarget(TraductorEjercicio.traducir(ejercicio.getTarget()));
                    ejercicio.setEquipment(TraductorEjercicio.traducir(ejercicio.getEquipment()));
                    ejercicio.setBodyPart(TraductorEjercicio.traducir(ejercicio.getBodyPart()));
                    ejercicio.setName(ejercicio.getName().toUpperCase());

                    // Orquestación
                    String mp4VideoUrl = buscarVideoEnPexels(ejercicio.getName(), ejercicio.getTarget());
                    ejercicio.setGifUrl(mp4VideoUrl);

                    if (mp4VideoUrl != null) {
                        log.info(" Vídeo encontrado para: {}", ejercicio.getName());
                    } else {
                        log.warn(" Pexels no encontró vídeo para: {}", ejercicio.getName());
                    }
                });

                return lista;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error crítico: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private String buscarVideoEnPexels(String nombreEjercicio, String musculoObjetivo) {
        try {

            // Simplificamos la búsqueda para aumentar el éxito
            String query = nombreEjercicio.toLowerCase() + " " + musculoObjetivo.toLowerCase() + " workout";
            query += " fitness exercise";

            String pexelsUrl = "https://api.pexels.com/videos/search?query=" + query + "&per_page=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", pexelsApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            //  Pedimos la respuesta como String para evitar el "Type definition error"
            ResponseEntity<String> response = restTemplate.exchange(
                    pexelsUrl, HttpMethod.GET, entity, String.class);

            if (response.getBody() != null) {
                // Parseamos manualmente el JSON
                JsonNode root = objectMapper.readTree(response.getBody());
                if (root.has("videos") && root.get("videos").size() > 0) {
                    JsonNode videoFiles = root.get("videos").get(0).get("video_files");
                    for (JsonNode file : videoFiles) {
                        String quality = file.get("quality").asText();
                        // Preferimos HD o SD 
                        if (quality.equalsIgnoreCase("hd") || quality.equalsIgnoreCase("sd")) {
                            return file.get("link").asText();
                        }
                    }
                    return videoFiles.get(0).get("link").asText();
                }
            }
        } catch (Exception e) {
            log.error("Error en búsqueda Pexels: {}", e.getMessage());
        }
        return null;
    }
}