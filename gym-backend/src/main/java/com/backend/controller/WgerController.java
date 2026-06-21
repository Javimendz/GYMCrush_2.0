package com.backend.controller;



import com.backend.dto.wger.WgerPageResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.WgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/api/v1/wger")    // ← obligatorio

@RequiredArgsConstructor  // ← genera el constructor con los campos final
@Tag(name = "Wger", description = "Ejercicios del catálogo externo Wger")
public class WgerController {

    private final WgerService wgerService;
    private final WebClient.Builder webClientBuilder; // ← añade esto
    private static final String BASE_URL = "https://wger.de/api/v2"; // ← añade esto

    @GetMapping("/ejercicios")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    public ResponseEntity<ApiResponseDto<WgerPageResponseDto>> getEjercicios(
            @RequestParam(defaultValue = "1")   int page,
            @RequestParam(defaultValue = "20")  int size,
            @RequestParam(defaultValue = "4")   String language,
            @RequestParam(required = false)     Integer muscleId,
            @RequestParam(required = false)     Integer categoryId) {

        if (page < 1) page = 1;
        if (size < 1 || size > 100) size = 20;

        WgerPageResponseDto result = wgerService.getEjercicios(
            page, size, language, muscleId, categoryId);

        return ResponseEntity.ok(
            ApiResponseDto.success("Ejercicios obtenidos con éxito", result)
        );
    }

   @GetMapping("/musculos")
@PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> getMusculos() {
    WebClient client = webClientBuilder.baseUrl(BASE_URL).build();
    
    Map response = client.get()
        .uri("/muscle/?format=json&limit=100")
        .retrieve()
        .bodyToMono(Map.class)
        .block();

    List<Map<String, Object>> results = response != null 
        ? (List<Map<String, Object>>) response.get("results") 
        : List.of();

    return ResponseEntity.ok(ApiResponseDto.success("Músculos obtenidos", results));
}

@GetMapping("/categorias")
@PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> getCategorias() {
    WebClient client = webClientBuilder.baseUrl(BASE_URL).build();

    Map response = client.get()
        .uri("/exercisecategory/?format=json&limit=100")
        .retrieve()
        .bodyToMono(Map.class)
        .block();

    List<Map<String, Object>> results = response != null 
        ? (List<Map<String, Object>>) response.get("results") 
        : List.of();

    return ResponseEntity.ok(ApiResponseDto.success("Categorías obtenidas", results));
}
}
