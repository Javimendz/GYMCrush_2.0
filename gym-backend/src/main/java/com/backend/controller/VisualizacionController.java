package com.backend.controller;

import com.backend.dto.TutorialResponseDto;
import com.backend.dto.VisualizacionRequestDto;
import com.backend.dto.VisualizacionResponseDto;
import com.backend.service.IVisualizacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.backend.security.dto.ApiResponseDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/v1/visualizaciones")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Visualizaciones", description = "Seguimiento de progreso en videotutoriales")
public class VisualizacionController {

    private final IVisualizacionService visualizacionService;

    @PostMapping("/progreso")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @Operation(summary = "Guardar progreso del video", description = "Crea o actualiza el segundo exacto donde se quedó el usuario")
    public ResponseEntity<ApiResponseDto<VisualizacionResponseDto>> guardar(
            @Valid @RequestBody VisualizacionRequestDto dto) {
            
        log.info("Guardando progreso de visualización para el usuario ID: {}", dto.getUsuarioId());
        VisualizacionResponseDto progreso = visualizacionService.guardarProgreso(dto);
        
        return ResponseEntity.ok(ApiResponseDto.<VisualizacionResponseDto>builder()
                .mensaje("Progreso guardado correctamente")
                .success(true)
                .datos(progreso)
                .build());
    }

    @GetMapping("/usuario/{usuarioId}/historial")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @Operation(summary = "Videos terminados", description = "Lista de tutoriales marcados como completados")
    public ResponseEntity<ApiResponseDto<List<VisualizacionResponseDto>>> historial(
            @PathVariable("usuarioId") Long usuarioId) { 
            
        log.info("Obteniendo historial de videos completados para usuario ID: {}", usuarioId);
        List<VisualizacionResponseDto> historial = visualizacionService.obtenerHistorial(usuarioId);
        
        return ResponseEntity.ok(ApiResponseDto.<List<VisualizacionResponseDto>>builder()
                .mensaje("Historial de videos completados recuperado")
                .success(true)
                .datos(historial)
                .build());
    }

    @GetMapping("/usuario/{usuarioId}/pendientes")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @Operation(summary = "Sección 'Continuar viendo'", description = "Videos empezados pero no terminados")
    public ResponseEntity<ApiResponseDto<List<VisualizacionResponseDto>>> pendientes(
            @PathVariable("usuarioId") Long usuarioId) { 
            
        log.info("Obteniendo lista de 'Continuar viendo' para usuario ID: {}", usuarioId);
        List<VisualizacionResponseDto> pendientes = visualizacionService.obtenerContinuarViendo(usuarioId);
        
        return ResponseEntity.ok(ApiResponseDto.<List<VisualizacionResponseDto>>builder()
                .mensaje("Lista de videos pendientes recuperada")
                .success(true)
                .datos(pendientes)
                .build());
    }

    @GetMapping("/populares")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @Operation(summary = "Top 3 vídeos más populares", description = "Retorna los 3 tutoriales con mayor número de visualizaciones en el gimnasio")
    public ResponseEntity<ApiResponseDto<List<TutorialResponseDto>>> obtenerPopulares() {
        
        log.info("Consultando el top 3 de videos más populares");
        List<TutorialResponseDto> populares = visualizacionService.obtenerPopulares();
        
        return ResponseEntity.ok(ApiResponseDto.<List<TutorialResponseDto>>builder()
                .mensaje("Top videos populares recuperado")
                .success(true)
                .datos(populares)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USUARIO', 'ADMIN')")
    @Operation(summary = "Eliminar del historial", description = "Permite a un usuario borrar un registro de su historial de visualizaciones")
    public ResponseEntity<ApiResponseDto<Void>> eliminar(
            @PathVariable("id") Long id) { 
            
        log.info("Eliminando registro de visualización ID: {}", id);
        visualizacionService.borrarDeHistorial(id);
        
        return ResponseEntity.ok(ApiResponseDto.<Void>builder()
                .mensaje("Video eliminado del historial correctamente")
                .success(true)
                .build());
    }

    @GetMapping("/usuario/{usuarioId}/tutorial/{tutorialId}")
     public ResponseEntity<ApiResponseDto<VisualizacionResponseDto>> obtenerProgreso(
                @PathVariable Long usuarioId, 
                @PathVariable Long tutorialId) {
        
        VisualizacionResponseDto datos = visualizacionService.obtenerProgreso(usuarioId, tutorialId);
        return ResponseEntity.ok(new ApiResponseDto<>("Progreso recuperado", datos, true));
     }
}