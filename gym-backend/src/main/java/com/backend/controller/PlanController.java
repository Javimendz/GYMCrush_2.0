package com.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.backend.domain.Usuario;
import com.backend.dto.DetallePlanRequestDto;
import com.backend.dto.PlanRequestDto;
import com.backend.dto.PlanResponseDto;
import com.backend.security.dto.ApiResponseDto;
import com.backend.service.IPlanService;
import com.backend.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/planes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Planes de Entrenamiento", description = "Gestión del catálogo principal de planes y suscripciones")
public class PlanController {

    private final IPlanService planService;
private final UsuarioRepository usuarioRepo; 
    // --- ENDPOINTS DEL PLAN MAESTRO ---

    @GetMapping
    @Operation(summary = "Catálogo de planes filtrado")
    public ResponseEntity<ApiResponseDto<List<PlanResponseDto>>> listarPlanes(
        org.springframework.security.core.Authentication auth) {
    
    //  Buscamos al usuario que hace la petición para filtrar por su ID
        Usuario user = usuarioRepo.findByUsername(auth.getName()).orElseThrow();
    
    //  Llamamos al método filtrado
        List<PlanResponseDto> planes = planService.listarPlanesParaUsuario(user.getId());
    
        return ResponseEntity.ok(ApiResponseDto.success("Catálogo obtenido", planes));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un plan por ID")
    public ResponseEntity<ApiResponseDto<PlanResponseDto>> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.success("Plan encontrado", planService.obtenerPorId(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')") 
    @Operation(summary = "Crear un nuevo Plan (Maestro o Personal)")
    public ResponseEntity<ApiResponseDto<PlanResponseDto>> crearPlan(
            @Valid @RequestBody PlanRequestDto dto,
            org.springframework.security.core.Authentication auth) { 
        
        // Pasamos el username al servicio
        PlanResponseDto response = planService.crearPlan(dto, auth.getName());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDto.success("Plan creado con éxito", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')") // Usuario puede borrar su propio plan 
    @Operation(summary = "Eliminar un Plan")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(@PathVariable Long id) {
        planService.eliminarPlan(id);
        return ResponseEntity.ok(ApiResponseDto.success("Plan eliminado correctamente", null));
    }

    // --- ENDPOINTS DE INTERACCIÓN Y DETALLES ---

    @PostMapping("/{planId}/suscribir/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')")
    @Operation(summary = "Suscribir usuario", description = "Asigna las rutinas del plan a la agenda del usuario")
    public ResponseEntity<ApiResponseDto<Void>> suscribir(@PathVariable Long planId, @PathVariable Long usuarioId,
            org.springframework.security.core.Authentication auth) {
        String usernameLogueado = auth.getName();
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().contains("ADMIN"));

        // 2. Se los pasamos al servicio para que valide
        planService.suscribirUsuarioAPlan(usuarioId, planId, usernameLogueado, esAdmin);

        return ResponseEntity.ok(ApiResponseDto.success("¡Plan activado! Revisa tu agenda de hoy", null));
    }

    
@Operation(summary = "Añadir ejercicios al plan", description = "Añade una lista de entrenamientos a un plan maestro")
@PostMapping("/{id}/ejercicios") // Asegúrate de que la ruta sea esta
@PreAuthorize("hasAnyRole('ADMIN', 'USUARIO')") // Solo ADMIN o el propietario del plan pueden añadir ejercicios
public ResponseEntity<ApiResponseDto<PlanResponseDto>> añadirEjerciciosAlPlan(
        @PathVariable Long id, 
        @Valid @RequestBody List<DetallePlanRequestDto> ejerciciosDto) {
    
    log.info("Añadiendo lista de {} ejercicios al plan ID {}", ejerciciosDto.size(), id);
    
    // Llamamos al service que ya actualizamos para recibir List
    PlanResponseDto actualizado = planService.añadirEjercicioAlPlan(id, ejerciciosDto);
    
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponseDto.success("Ejercicios añadidos correctamente", actualizado));
}
}