package com.backend.service;

import com.backend.domain.CategoriaTutorial;
import com.backend.domain.Tutorial;
import com.backend.domain.Usuario;
import com.backend.dto.CategoriaTutorialResponseDto;
import com.backend.dto.TutorialRequestDto;
import com.backend.dto.TutorialResponseDto;
import com.backend.exceptions.BusinessException;
import com.backend.mapper.CategoriaTutorialMapper;
import com.backend.mapper.TutorialMapper;
import com.backend.repository.CategoriaTutorialRepository;
import com.backend.repository.TutorialRepository;
import com.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TutorialServiceImp implements ITutorialService {

    private final TutorialRepository tutorialRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaTutorialRepository categoriaRepository;
    private final TutorialMapper tutorialMapper;
    private final CategoriaTutorialMapper categoriaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TutorialResponseDto> obtenerTodos() {
        return tutorialRepository.findAll().stream()
                .map(tutorialMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutorialResponseDto> listarGlobales() {
        return tutorialRepository.findByEsGlobalTrue().stream()
                .map(tutorialMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutorialResponseDto> buscarPorTitulo(String titulo) {
        return tutorialRepository.findByTituloContainingIgnoreCase(titulo).stream()
                .map(tutorialMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public TutorialResponseDto crear(TutorialRequestDto dto, String username) {
        log.info("Iniciando creación de tutorial por: {}", username);

        // 1. Obtener autor
        Usuario autor = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        // 2. Validar duplicados
        if (tutorialRepository.existsByTituloIgnoreCase(dto.getTitulo().trim())) {
            throw new BusinessException("Ya existe un ejercicio con ese nombre.");
        }

        // 3. Obtener categoría
        CategoriaTutorial categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new BusinessException("Categoría no válida."));

        // 4. Mapear DTO a Entidad
        Tutorial tutorial = tutorialMapper.toEntity(dto);
        tutorial.setCategoria(categoria);

        // 5. Determinar si es Admin y configurar visibilidad
        boolean isAdmin = autor.getRoles().stream()
                .anyMatch(rol -> rol.getName().equals("ROLE_ADMIN"));

        if (isAdmin) {
            tutorial.setUsuario(null);
            tutorial.setEsGlobal(true);
        } else {
            tutorial.setUsuario(autor);
            tutorial.setEsGlobal(false);
        }

        log.info("Guardando tutorial: {} (Global: {})", tutorial.getTitulo(), tutorial.getEsGlobal());
        return tutorialMapper.toDto(tutorialRepository.save(tutorial));
    }

    @Override
    @Transactional
    public TutorialResponseDto actualizar(Long id, TutorialRequestDto dto) {
        Tutorial tutorial = tutorialRepository.findById(id)
                .orElseThrow(() -> new BusinessException("El tutorial no existe."));

        tutorial.setTitulo(dto.getTitulo().trim());
        tutorial.setDescripcion(dto.getDescripcion());
        tutorial.setUrlVideo(dto.getUrlVideo());
        tutorial.setMusculoObjetivo(dto.getMusculoObjetivo());
        tutorial.setEquipamiento(dto.getEquipamiento());
        tutorial.setDuracionMin(dto.getDuracionMin());

        if (dto.getEsGlobal() != null) {
            tutorial.setEsGlobal(dto.getEsGlobal());
        }

        return tutorialMapper.toDto(tutorialRepository.save(tutorial));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutorialResponseDto> obtenerPorCategoria(Long categoriaId) {
        return tutorialRepository.findByCategoriaId(categoriaId).stream()
                .map(tutorialMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutorialResponseDto> listarParaUsuario(Long usuarioId) {
        return tutorialRepository.findGlobalesYDelUsuario(usuarioId).stream()
                .map(tutorialMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TutorialResponseDto> listarParaUsuarioYGlobales(String username) {
        Usuario user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        return listarParaUsuario(user.getId());
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!tutorialRepository.existsById(id)) {
            throw new BusinessException("No se puede eliminar: El tutorial no existe");
        }
        tutorialRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaTutorialResponseDto> listarCategorias() {
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toDto)
                .toList();
    }
}