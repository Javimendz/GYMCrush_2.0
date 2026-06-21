package com.backend.service;

import com.backend.dto.wger.WgerInfoResponseDto;
import com.backend.dto.wger.WgerPageResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class WgerService {

    private final WebClient.Builder webClientBuilder;
    private static final String BASE_URL = "https://wger.de/api/v2";

    public WgerPageResponseDto getEjercicios(int page, int size, String language, Integer muscleId, Integer categoryId) {
        int offset = (page - 1) * size;
        log.info("Consultando Wger API: Página={}, Offset={}", page, offset);

        WebClient client = webClientBuilder.baseUrl(BASE_URL).build();

        WgerInfoResponseDto response = client.get()
            .uri(uriBuilder -> {
                var builder = uriBuilder.path("/exerciseinfo/")
                    .queryParam("language", language)
                    .queryParam("status", 2)
                    .queryParam("limit", size)
                    .queryParam("offset", offset);
                if (muscleId != null) builder.queryParam("muscles", muscleId);
                if (categoryId != null) builder.queryParam("category", categoryId);
                return builder.build();
            })
            .retrieve()
            .bodyToMono(WgerInfoResponseDto.class)
            .block();

        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            log.warn("Wger API no devolvió ejercicios para la página {}", page);
            return WgerPageResponseDto.empty();
        }

        int totalPages = (int) Math.ceil((double) response.getCount() / size);
        log.info("Recibidos {} ejercicios. Total páginas: {}", response.getResults().size(), totalPages);

        return WgerPageResponseDto.builder()
            .ejercicios(response.getResults())
            .totalElementos(response.getCount())
            .totalPaginas(totalPages)
            .paginaActual(page)
            .tamañoPagina(size)
            .hayAnterior(response.getPrevious() != null)
            .haySiguiente(response.getNext() != null)
            .build();
    }
}