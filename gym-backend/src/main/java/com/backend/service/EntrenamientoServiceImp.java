package com.backend.service;

import com.backend.domain.Entrenamiento;
import com.backend.domain.Usuario;
import com.backend.domain.Tutorial;
import com.backend.domain.CategoriaTutorial;
import com.backend.dto.EntrenamientoRequestDto;
import com.backend.dto.EntrenamientoResponseDto;
import com.backend.repository.EntrenamientoRepository;
import com.backend.repository.UsuarioRepository;
import com.backend.repository.TutorialRepository;
import com.backend.repository.CategoriaTutorialRepository;
import com.backend.repository.RutinaRepository;
import com.backend.mapper.EntrenamientoMapper;
import com.backend.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntrenamientoServiceImp implements IEntrenamientoService {

    private final EntrenamientoRepository entrenamientoRepository;
    private final EntrenamientoMapper entrenamientoMapper;
    private final TutorialRepository tutorialRepository;
    private final CategoriaTutorialRepository categoriaTutorialRepository;
    private final UsuarioRepository usuarioRepository;

   @Override // Ahora sí funcionará porque la interfaz tiene 2 parámetros
    @Transactional
    public EntrenamientoResponseDto crear(EntrenamientoRequestDto dto, String username) {
           Entrenamiento entrenamiento = entrenamientoMapper.toEntity(dto);
        log.info("Creando entrenamiento: {} por usuario: {}", dto.getNombre(), username);

        // Mapeo de campos manual para asegurar persistencia
        entrenamiento.setNombre(dto.getNombre());
        entrenamiento.setDescripcion(dto.getDescripcion());
        entrenamiento.setDuracion(dto.getDuracion());
        entrenamiento.setIntensidad(dto.getIntensidad());
        entrenamiento.setCantidadEjercicios(dto.getCantidadEjercicios());
        entrenamiento.setUrlImagen(dto.getUrlImagen());

        //  Lógica de Dueño (Usuario) vs Global (Admin)
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        boolean isAdmin = usuario.getRoles().stream()
                .anyMatch(rol -> rol.getName().equals("ROLE_ADMIN"));

        if (isAdmin) {
            entrenamiento.setUsuario(null); // Es una plantilla global
            entrenamiento.setEsGlobal(true);
        } else {
            entrenamiento.setUsuario(usuario); // Es un entrenamiento personal
            entrenamiento.setEsGlobal(false);
        }

        //  Manejo de Categoría
        if (dto.getCategoriaId() != null) {
            CategoriaTutorial categoria = categoriaTutorialRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
            entrenamiento.setCategoria(categoria);
        } else if (dto.getCategoria() != null && !dto.getCategoria().isEmpty()) {
            CategoriaTutorial categoria = categoriaTutorialRepository.findByNombre(dto.getCategoria())
                    .orElseGet(() -> {
                        CategoriaTutorial nueva = new CategoriaTutorial();
                        nueva.setNombre(dto.getCategoria());
                        return categoriaTutorialRepository.save(nueva);
                    });
            entrenamiento.setCategoria(categoria);
        }

        Entrenamiento saved = entrenamientoRepository.save(entrenamiento);

        // Vincular Tutoriales existentes si los hay
        if (dto.getTutorialesIds() != null && !dto.getTutorialesIds().isEmpty()) {
            List<Tutorial> tutoriales = tutorialRepository.findAllById(dto.getTutorialesIds());
            for (Tutorial t : tutoriales) {
                t.setEntrenamiento(saved);
            }
            tutorialRepository.saveAll(tutoriales);
            saved.setTutoriales(tutoriales);
        }

        //  Crear Tutorial nuevo si se proporciona URL de video
        Tutorial nuevoTutorial = null;
        if (dto.getUrlVideo() != null && !dto.getUrlVideo().isEmpty()) {
            nuevoTutorial = new Tutorial();
            nuevoTutorial.setTitulo(dto.getNombre());
            nuevoTutorial.setUrlVideo(dto.getUrlVideo());
            nuevoTutorial.setDescripcion(dto.getDescripcion());
            nuevoTutorial.setEntrenamiento(saved);
            nuevoTutorial = tutorialRepository.save(nuevoTutorial);
        }

        Tutorial tutorialParaVideo = (nuevoTutorial != null) ? nuevoTutorial :
                (saved.getTutoriales() != null && !saved.getTutoriales().isEmpty() ? saved.getTutoriales().get(0) : null);

        return entrenamientoMapper.toResponseDto(saved, tutorialParaVideo);
    }


    @Override
    @Transactional(readOnly = true)
    public List<EntrenamientoResponseDto> obtenerGlobales(String intensidad, String objetivo) {
        // 1. Buscamos todos los entrenamientos que tengan esGlobal = true
        List<Entrenamiento> entrenamientos = entrenamientoRepository.findByEsGlobalTrue();

        // 2. Filtramos dinámicamente en memoria (o puedes crear un método Query en el Repository)
        return entrenamientos.stream()
                .filter(e -> intensidad == null || e.getIntensidad().equalsIgnoreCase(intensidad))
                // Si añades el campo objetivo en el futuro a tu entidad, lo puedes filtrar aquí:
                // .filter(e -> objetivo == null || e.getObjetivo().equalsIgnoreCase(objetivo))
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    // Método auxiliar para mapear la Entidad al DTO de respuesta
    private EntrenamientoResponseDto convertirADto(Entrenamiento entrenamiento) {
        return EntrenamientoResponseDto.builder()
                .id(entrenamiento.getId())
                .nombre(entrenamiento.getNombre())
                .urlImagen(entrenamiento.getUrlImagen())
                .descripcion(entrenamiento.getDescripcion())
                .duracion(entrenamiento.getDuracion())
                .intensidad(entrenamiento.getIntensidad())
                .cantidadEjercicios(entrenamiento.getCantidadEjercicios())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntrenamientoResponseDto> listarPorUsuario(Long usuarioId) {
        log.info("Buscando entrenamientos (Globales + Propios) para el usuario ID: {}", usuarioId);

        // Usamos la query personalizada del repositorio para filtrar
        List<Entrenamiento> entrenamientos = entrenamientoRepository.findGlobalesYDelUsuario(usuarioId);

        return entrenamientos.stream()
                .map(entrenamiento -> {
                    Tutorial tutorial = (entrenamiento.getTutoriales() != null && !entrenamiento.getTutoriales().isEmpty())
                            ? entrenamiento.getTutoriales().get(0)
                            : null;
                    return entrenamientoMapper.toResponseDto(entrenamiento, tutorial);
                })
                .collect(Collectors.toList());
    }

   @Override
@Transactional(readOnly = true)
public List<EntrenamientoResponseDto> listarTodos() {
    log.info("Listando catálogo completo de entrenamientos (Vista Admin)");
    return entrenamientoRepository.findAllWithTutoriales().stream()
            .map(entrenamiento -> {
                Tutorial tutorial = (entrenamiento.getTutoriales() != null 
                    && !entrenamiento.getTutoriales().isEmpty())
                        ? entrenamiento.getTutoriales().get(0)
                        : null;
                return entrenamientoMapper.toResponseDto(entrenamiento, tutorial);
            })
            .collect(Collectors.toList());
}

    @Override
    @Transactional(readOnly = true)
    public EntrenamientoResponseDto obtenerPorId(Long id) {
        Entrenamiento entrenamiento = entrenamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrenamiento no encontrado con ID: " + id));

        Tutorial tutorial = (entrenamiento.getTutoriales() != null && !entrenamiento.getTutoriales().isEmpty())
                ? entrenamiento.getTutoriales().get(0)
                : null;

        return entrenamientoMapper.toResponseDto(entrenamiento, tutorial);
    }

    @Override
    @Transactional
    public EntrenamientoResponseDto actualizar(Long id, EntrenamientoRequestDto dto) {
        log.info("Actualizando entrenamiento ID: {}", id);

        Entrenamiento existente = entrenamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: No encontrado"));

        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setDuracion(dto.getDuracion());
        existente.setIntensidad(dto.getIntensidad());
        existente.setCantidadEjercicios(dto.getCantidadEjercicios());
        existente.setUrlImagen(dto.getUrlImagen());

        Entrenamiento guardado = entrenamientoRepository.save(existente);

        // Sincronizar URL de video si existe tutorial
        Tutorial tutorial = (guardado.getTutoriales() != null && !guardado.getTutoriales().isEmpty())
                ? guardado.getTutoriales().get(0)
                : null;

        if (tutorial != null && dto.getUrlVideo() != null) {
            tutorial.setUrlVideo(dto.getUrlVideo());
            tutorialRepository.save(tutorial);
        }

        return entrenamientoMapper.toResponseDto(guardado, tutorial);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!entrenamientoRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: ID no existe");
        }
        entrenamientoRepository.deleteById(id);
    }
}