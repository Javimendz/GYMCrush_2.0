package com.backend.controller;

import com.backend.dto.NutricionResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.INutricionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.backend.dto.NutricionRequestDto;
import com.backend.service.FatSecretService;

@RestController
@RequestMapping("/api/v1/nutricion")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Nutrición", description = "Motor de generación de planes nutricionales automáticos")
public class NutricionController {

        private final INutricionService nutricionService;
        private final FatSecretService fatSecretService;

        // -----------------------------------------------------------------------
        // POST /api/v1/nutricion/generar-plan
        // Genera un plan nutricional personalizado basado en los datos físicos
        // -----------------------------------------------------------------------
        @PostMapping("/generar-plan")
        @PreAuthorize("hasRole('ADMIN') or hasRole('USUARIO')")
        @io.swagger.v3.oas.annotations.Operation(summary = "Generar plan automático", description = "Calcula y genera un plan nutricional usando la fórmula de Mifflin-St Jeor")
        public ResponseEntity<ApiResponseDto<NutricionResponseDto>> generarPlan(
                        @Valid @RequestBody NutricionRequestDto request) {

                log.info("Recibida petición para generar plan. Usuario ID: {}, Objetivo: {}",
                                request.getUsuarioId(), request.getObjetivo());

                NutricionResponseDto plan = nutricionService.generarPlanAutomatico(request);

                return ResponseEntity.status(HttpStatus.CREATED).body(
                                ApiResponseDto.<NutricionResponseDto>builder()
                                                .mensaje("Plan nutricional personalizado generado con éxito")
                                                .success(true)
                                                .datos(plan)
                                                .build());
        }

        // -----------------------------------------------------------------------
        // GET /api/v1/nutricion/ultimo-plan/{usuarioId}
        // Recupera el último plan generado para un usuario
        // -----------------------------------------------------------------------
        @GetMapping("/ultimo-plan/{usuarioId}")
        @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
        @io.swagger.v3.oas.annotations.Operation(summary = "Obtener último plan", description = "Recupera el plan nutricional más reciente activo del usuario")
        public ResponseEntity<ApiResponseDto<NutricionResponseDto>> obtenerUltimoPlan(
                        @PathVariable("usuarioId") Long usuarioId) {

                log.info("Consultando último plan nutricional del usuario ID: {}", usuarioId);
                NutricionResponseDto response = nutricionService.obtenerUltimoPlanPorUsuario(usuarioId);

                return ResponseEntity.ok(
                                ApiResponseDto.<NutricionResponseDto>builder()
                                                .mensaje("Plan recuperado correctamente")
                                                .success(true)
                                                .datos(response)
                                                .build());
        }

        // -----------------------------------------------------------------------
        // PATCH /api/v1/nutricion/plan/{id}/nombre
        // Actualiza el nombre de un plan existente
        // -----------------------------------------------------------------------
        @PatchMapping("/plan/{id}/nombre")
        @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
        @io.swagger.v3.oas.annotations.Operation(summary = "Actualizar nombre del plan", description = "Permite renombrar un plan nutricional guardado")
        public ResponseEntity<ApiResponseDto<NutricionResponseDto>> actualizarNombre(
                        @PathVariable("id") Long id,
                        @RequestParam("nombre") String nombre) {

                log.info("Petición para renombrar plan ID: {} a '{}'", id, nombre);

                NutricionResponseDto actualizado = nutricionService.actualizarNombrePlan(id, nombre);

                return ResponseEntity.ok(
                                ApiResponseDto.<NutricionResponseDto>builder()
                                                .mensaje("Nombre del plan actualizado correctamente")
                                                .success(true)
                                                .datos(actualizado)
                                                .build());
        }

        // -----------------------------------------------------------------------
        // GET /api/v1/nutricion/historial/{usuarioId}
        // Recupera el historial completo de planes de un usuario
        // -----------------------------------------------------------------------
        @GetMapping("/historial/{usuarioId}")
        @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
        public ResponseEntity<ApiResponseDto<Page<NutricionResponseDto>>> obtenerHistorial(
                        @PathVariable("usuarioId") Long usuarioId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                log.info("Consultando historial paginado del usuario ID: {}", usuarioId);

                Page<NutricionResponseDto> historial = nutricionService.obtenerHistorial(usuarioId, page, size);

                return ResponseEntity.ok(
                                ApiResponseDto.<Page<NutricionResponseDto>>builder()
                                                .mensaje("Historial recuperado")
                                                .success(true)
                                                .datos(historial)
                                                .build());
        }

        @GetMapping("/plan/{id}")
        public ResponseEntity<ApiResponseDto<NutricionResponseDto>> obtenerPlanPorId(@PathVariable Long id) {
                NutricionResponseDto plan = nutricionService.obtenerPlanPorId(id);
                return ResponseEntity.ok(new ApiResponseDto<>("Plan encontrado", plan, true));
        }

        @GetMapping("/buscar")
        @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
        @io.swagger.v3.oas.annotations.Operation(summary = "Buscar alimentos", description = "Busca recetas o alimentos por nombre usando la API de FatSecret")
        public ResponseEntity<ApiResponseDto<String>> buscarAlimentos(@RequestParam("query") String query) {

                log.info("Buscando alimentos con el término: {}", query);

                // Llamamos al servicio de FatSecret
                String resultado = fatSecretService.buscarAlimentosPorNombre(query);

                return ResponseEntity.ok(
                                ApiResponseDto.<String>builder()
                                                .mensaje("Resultados de búsqueda obtenidos")
                                                .success(true)
                                                .datos(resultado)
                                                .build());
        }

        // -----------------------------------------------------------------------
        // POST /api/v1/nutricion/guardar-plan/{usuarioId}
        // Persiste un plan generado previamente (transient) en la base de datos
        // -----------------------------------------------------------------------
        @PostMapping("/guardar-plan/{usuarioId}")
        @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
        @io.swagger.v3.oas.annotations.Operation(summary = "Guardar plan", description = "Persiste un plan nutricional generado o editado por el usuario")
        public ResponseEntity<ApiResponseDto<NutricionResponseDto>> guardarPlan(
                        @PathVariable("usuarioId") Long usuarioId,
                        @RequestBody NutricionResponseDto plan) {

                log.info("Petición para guardar plan del usuario ID: {}", usuarioId);

                NutricionResponseDto guardado = nutricionService.guardarPlan(usuarioId, plan);

                return ResponseEntity.status(HttpStatus.CREATED).body(
                                ApiResponseDto.<NutricionResponseDto>builder()
                                                .mensaje("Plan nutricional guardado correctamente")
                                                .success(true)
                                                .datos(guardado)
                                                .build());
        }

}
