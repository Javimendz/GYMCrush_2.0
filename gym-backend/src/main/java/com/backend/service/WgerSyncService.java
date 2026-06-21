package com.backend.service;

import com.backend.domain.Entrenamiento; // Usamos tu clase real de catálogo
import com.backend.domain.PlanEntrenamiento;
import com.backend.domain.DetallePlan;
import com.backend.dto.wger.WgerExerciseInfoObjectDto; 
import com.backend.dto.wger.WgerInfoResponseDto;       
import com.backend.repository.EntrenamientoRepository;     // Tu repositorio de entrenamientos
import com.backend.repository.PlanEntrenamientoRepository; 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import jakarta.transaction.Transactional;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j

public class WgerSyncService {

    private final EntrenamientoRepository entrenamientoRepository; // Cambiado a tu repositorio real
    private final PlanEntrenamientoRepository planRepository; 
    private final WebClient.Builder webClientBuilder;

    @Transactional
    public void sincronizarEjercicios() {
        log.info("Iniciando sincronización con Wger API en la tabla de Entrenamientos...");

        WebClient webClient = webClientBuilder.baseUrl("https://wger.de/api/v2").build();

        WgerInfoResponseDto response = webClient.get()
                .uri("/exerciseinfo/?language=4&status=2&limit=30") 
                .retrieve()
                .bodyToMono(WgerInfoResponseDto.class)
                .block(); 

                log.info("¿Respuesta de Wger recibida?: {}", response != null);
if (response != null && response.getResults() != null) {
    log.info("Número de ejercicios traídos de Wger: {}", response.getResults().size());
} else {
    log.error("La API de Wger no devolvió resultados. Verificar DTO de respuesta.");
}

        if (response != null && response.getResults() != null) {
            
            // 1. Buscamos o creamos el Plan Maestro Global
           PlanEntrenamiento planMaestro = planRepository.findByNombre("Plan Base Wger")
    .orElseGet(() -> {
        PlanEntrenamiento p = PlanEntrenamiento.builder()
            .nombre("Plan Base Wger")
            .esGlobal(true)
            .ejercicios(new ArrayList<>())
            .build();
        return planRepository.save(p); // Guardar primero
    });

            int ordenEjercicio = 1;
            int diaSemana = 1; 

            for (WgerExerciseInfoObjectDto wgerEx : response.getResults()) {
                log.info("Procesando ejercicio de API: {}", wgerEx.getName());
                if (wgerEx.getName() == null || wgerEx.getName().isBlank()) {
                    continue;
                }

                // 2. Si el ejercicio no existe en tu tabla 'entrenamientos', lo creamos dinámicamente
                Entrenamiento entrenamiento = entrenamientoRepository.findByNombre(wgerEx.getName().trim())
                        .orElseGet(() -> {
                            String urlFinalImagen = (wgerEx.getImages() != null && !wgerEx.getImages().isEmpty()) 
                                    ? wgerEx.getImages().get(0).getImageUrl() 
                                    : "https://wger.de/static/images/illustrations/icons/placeholder.png";

                            String descripcionLimpia = (wgerEx.getDescription() != null) 
                                    ? wgerEx.getDescription().replaceAll("<[^>]*>", "").trim() 
                                    : "";

                            // Ajusta estos builders según los atributos reales de tu entidad Entrenamiento
                            return entrenamientoRepository.save(
                                Entrenamiento.builder()
                                        .nombre(wgerEx.getName().trim())
                                        .descripcion(descripcionLimpia)
                                        .intensidad("MEDIA") // Valor por defecto para tu modelo de negocio
                                        .duracion(10)        // Minutos estimados por defecto
                                        .urlImagen(urlFinalImagen)
                                        .build()
                            );
                        });

                // 3. Vinculamos el Entrenamiento al Plan Maestro mediante DetallePlan
                boolean yaExisteEnPlan = planMaestro.getEjercicios().stream()
                        .anyMatch(d -> d.getEntrenamiento().getNombre().equals(entrenamiento.getNombre()));

                if (!yaExisteEnPlan) {
                    DetallePlan detalle = DetallePlan.builder()
                            .entrenamiento(entrenamiento) // Mapeo directo a tu entidad intermedia
                            .diaSemana(diaSemana)      
                            .orden(ordenEjercicio++)   
                            .series(4)                 
                            .repeticiones(12)
                            .build();

                    // Mantenemos la consistencia bidireccional usando tu método helper
                    planMaestro.addEjercicio(detalle);

                    // Estructuración básica de la agenda (5 ejercicios por día)
                    if (ordenEjercicio > 5) {
                        ordenEjercicio = 1;
                        diaSemana += 2; 
                        if (diaSemana > 5) diaSemana = 1; 
                    }
                }
            }
            
            // 4. Guardamos todo el árbol del plan maestro en cascada
            planRepository.save(planMaestro);
            log.info("Sincronización finalizada. Catálogo e itinerario del 'Plan Base Wger' listos en tu tabla de Entrenamientos.");
        }
    }
}