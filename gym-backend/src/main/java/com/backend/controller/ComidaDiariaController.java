package com.backend.controller;

import com.backend.dto.ComidaDiariaRequestDto;
import com.backend.dto.ComidaDiariaResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IComidaDiariaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comidas")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Comida Diaria", description = "Operaciones relacionadas con los platos de un plan nutricional")
public class ComidaDiariaController {

        private final IComidaDiariaService comidaService;

        /**
         * POST /api/comidas
         * Añade un nuevo plato a un plan existente.
         */
        @PostMapping
        @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
        @io.swagger.v3.oas.annotations.Operation(summary = "Añadir comida", description = "Añade un nuevo plato a un plan nutricional existente")
        public ResponseEntity<ApiResponseDto<ComidaDiariaResponseDto>> añadirComida(
                        @Valid @RequestBody ComidaDiariaRequestDto request) {

                log.info("Solicitud para añadir comida al plan");
                ComidaDiariaResponseDto nuevaComida = comidaService.añadirComida(request);

                return ResponseEntity.status(HttpStatus.CREATED).body(
                                ApiResponseDto.<ComidaDiariaResponseDto>builder()
                                                .mensaje("Comida añadida exitosamente al plan")
                                                .success(true)
                                                .datos(nuevaComida)
                                                .build());
        }

        /**
         * DELETE /api/comidas/{id}
         * Elimina un plato específico de la base de datos.
         */
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
        @io.swagger.v3.oas.annotations.Operation(summary = "Eliminar comida", description = "Elimina un plato específico de la base de datos")
        public ResponseEntity<ApiResponseDto<Void>> eliminarComida(
                        @PathVariable("id") Long id) {

                log.info("Solicitud para eliminar comida ID: {}", id);
                comidaService.eliminarComida(id);

                return ResponseEntity.ok(
                                ApiResponseDto.<Void>builder()
                                                .mensaje("Plato eliminado correctamente")
                                                .success(true)
                                                .build());
        }

        /**
         * POST /api/comidas/batch/{planId}
         * Útil si quieres guardar una lista de platos de golpe (ej: tras llamar a
         * FatSecret).
         */
        @PostMapping("/batch/{planId}")
        @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
        @io.swagger.v3.oas.annotations.Operation(summary = "Añadir múltiples comidas", description = "Guarda una lista de platos de golpe en un plan específico")
        public ResponseEntity<ApiResponseDto<Void>> guardarMuchasComidas(
                        @Valid @RequestBody List<ComidaDiariaRequestDto> comidas,
                        @PathVariable("planId") Long planId) {

                log.info("Solicitud para guardar {} comidas en el plan ID: {}", comidas.size(), planId);
                comidaService.guardarComidas(comidas, planId);

                return ResponseEntity.status(HttpStatus.CREATED).body(
                                ApiResponseDto.<Void>builder()
                                                .mensaje("Los " + comidas.size()
                                                                + " platos fueron guardados correctamente en el plan")
                                                .success(true)
                                                .build());
        }

}