package com.backend.mapper;

import com.backend.domain.ComidaDiaria;
import com.backend.domain.Nutricion;
import com.backend.dto.ComidaDiariaRequestDto;
import com.backend.dto.ComidaDiariaResponseDto;
import com.backend.dto.NutricionResponseDto;
import com.backend.dto.FatSecretResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper manual para la gestión de Nutrición.
 * Se utiliza @Component en lugar de MapStruct para manejar la lógica compleja 
 * de serialización/deserialización del JSON de FatSecret.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NutricionMapper {

    private final ObjectMapper objectMapper;
    private final ComidaDiariaMapper comidaMapper;

    /**
     * Convierte Entidad a DTO (Para enviar a Android)
     */
    public NutricionResponseDto toResponseDto(Nutricion entity) {
        if (entity == null) return null;

        NutricionResponseDto dto = new NutricionResponseDto();
        dto.setId(entity.getId());
        dto.setCaloriasObjetivo(entity.getCaloriasObjetivo());
        dto.setFechaGeneracion(entity.getFechaGeneracion());
        dto.setNombreDieta(entity.getNombreDieta());
        dto.setTipoDieta(entity.getTipoDieta());

        // Mapear la lista de comidas individuales
        if (entity.getComidas() != null) {
            dto.setComidas(entity.getComidas().stream()
                    .map(comidaMapper::toResponseDto)
                    .collect(Collectors.toList()));
        }

        // Convertir String JSON de la DB al Objeto DTO para Android
        if (entity.getMacronutrientesJson() != null && !entity.getMacronutrientesJson().isBlank()) {
            try {
                FatSecretResponseDto fsDto = objectMapper.readValue(
                        entity.getMacronutrientesJson(),
                        FatSecretResponseDto.class);
                dto.setMacronutrientes(fsDto);
            } catch (Exception e) {
                log.error("Error al deserializar JSON en Nutricion ID {}: {}", entity.getId(), e.getMessage());
                dto.setMacronutrientes(null);
            }
        }
        return dto;
    }

    /**
     * Convierte DTO a Entidad (Para guardar en la DB)
     * ¡Este es el método que le faltaba a tu NutricionServiceImpl!
     */
    public Nutricion toEntity(NutricionResponseDto dto) {
        if (dto == null) return null;

        Nutricion entity = new Nutricion();
        entity.setId(dto.getId());
        entity.setNombreDieta(dto.getNombreDieta());
        entity.setTipoDieta(dto.getTipoDieta());
        entity.setCaloriasObjetivo(dto.getCaloriasObjetivo());
        entity.setFechaGeneracion(dto.getFechaGeneracion());

        // Convertir el Objeto Macronutrientes de nuevo a String JSON para guardar en la DB
        if (dto.getMacronutrientes() != null) {
            try {
                entity.setMacronutrientesJson(objectMapper.writeValueAsString(dto.getMacronutrientes()));
            } catch (Exception e) {
                log.error("Error al serializar Macronutrientes a JSON: {}", e.getMessage());
            }
        }

        return entity;
    }

    /**
     * Convierte una lista de entidades a DTOs
     */
    public List<NutricionResponseDto> toResponseDtoList(List<Nutricion> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Delegación a ComidaDiariaMapper para respuestas individuales
     */
    public ComidaDiariaResponseDto toComidaResponse(ComidaDiaria entity) {
        return comidaMapper.toResponseDto(entity);
    }

    /**
     * Delegación a ComidaDiariaMapper para creación de entidades de comida
     */
    public ComidaDiaria toEntity(ComidaDiariaRequestDto request, Nutricion plan) {
        return comidaMapper.toEntity(request, plan);
    }

   public ComidaDiaria toComidaEntity(ComidaDiariaResponseDto dto) {
        if (dto == null) return null;

        ComidaDiaria entity = new ComidaDiaria();
        entity.setNombreAlimento(dto.getNombreAlimento());
        entity.setMomento(dto.getMomento());
        entity.setCalorias(dto.getCalorias());
        entity.setProteina(dto.getProteina());
        entity.setCarbohidratos(dto.getCarbohidratos());
        entity.setGrasas(dto.getGrasas());
        entity.setImagenUrl(dto.getImagenUrl());
        entity.setFecha(LocalDate.now()); 
        return entity;
    }
}