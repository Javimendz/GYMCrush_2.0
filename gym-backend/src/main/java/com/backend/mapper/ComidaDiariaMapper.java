package com.backend.mapper;

import com.backend.domain.ComidaDiaria;
import com.backend.domain.Nutricion;
import com.backend.dto.ComidaDiariaRequestDto;
import com.backend.dto.ComidaDiariaResponseDto;
import org.springframework.stereotype.Component;

@Component
public class ComidaDiariaMapper {

    /**
     * De Entidad a Response DTO (Para mostrar en el Frontend)
     */
    public ComidaDiariaResponseDto toResponseDto(ComidaDiaria entity) {
        if (entity == null)
            return null;

        ComidaDiariaResponseDto dto = new ComidaDiariaResponseDto();

        dto.setId(entity.getId());
        dto.setMomento(entity.getMomento());
        dto.setNombreAlimento(entity.getNombreAlimento());
        dto.setCalorias(entity.getCalorias());

        dto.setProteina(entity.getProteina());
        dto.setCarbohidratos(entity.getCarbohidratos());
        dto.setGrasas(entity.getGrasas());
        dto.setFecha(entity.getFecha());
        dto.setImagenUrl(
                (entity.getImagenUrl() == null || entity.getImagenUrl().isBlank())
                        ? "https://images.unsplash.com/photo-1490645935967-10de6ba17061?q=80&w=500"
                        : entity.getImagenUrl());

        return dto;
    }

    /**
     * De Request DTO a Entidad (Para guardar en la Base de Datos)
     * Requiere la entidad Nutricion para establecer la relación jerárquica
     */
    public ComidaDiaria toEntity(ComidaDiariaRequestDto request, Nutricion plan) {
        if (request == null)
            return null;

        ComidaDiaria entity = new ComidaDiaria();
        entity.setMomento(request.getMomento());
        entity.setNombreAlimento(request.getNombreAlimento());
        entity.setCalorias(request.getCalorias());
        entity.setImagenUrl(request.getImagenUrl());
        // Mapeo directo de número a número
        entity.setFecha(java.time.LocalDate.now());
        entity.setProteina(request.getProteina());
        entity.setCarbohidratos(request.getCarbohidratos());
        entity.setGrasas(request.getGrasas());

        entity.setPlan(plan);
        return entity;
    }
}