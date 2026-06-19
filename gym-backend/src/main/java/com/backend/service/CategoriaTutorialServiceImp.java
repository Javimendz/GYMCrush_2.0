package com.backend.service;

import com.backend.domain.CategoriaTutorial;
import com.backend.dto.CategoriaTutorialRequestDto;
import com.backend.dto.CategoriaTutorialResponseDto;
import com.backend.mapper.CategoriaTutorialMapper;
import com.backend.repository.CategoriaTutorialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para la gestión de categorías de tutoriales.
 * <p>
 * Esta clase maneja las operaciones CRUD para categorías que organizan
 * los tutoriales educativos disponibles en la plataforma del gimnasio.
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaTutorialServiceImp implements ICategoriaTutorialService {

    /** Repositorio para el acceso a datos de categorías de tutoriales */
    private final CategoriaTutorialRepository categoriaRepository;

    /** Mapper para convertir entre entidades y DTOs de categorías */
    private final CategoriaTutorialMapper categoriaMapper;

    /**
     * Obtiene todas las categorías de tutoriales disponibles.
     * <p>
     * Retorna una lista completa de todas las categorías que organizan
     * los tutoriales educativos del sistema.
     * </p>
     *
     * @return Lista de DTOs con todas las categorías
     */
    @Override
    @Transactional(readOnly = true)
    public List<CategoriaTutorialResponseDto> listarTodas() {
        log.info("Listando todas las categorías de tutoriales");

        // Obtener todas las categorías y convertirlas a DTOs
        List<CategoriaTutorialResponseDto> categorias = categoriaRepository.findAll().stream()
                .map(categoriaMapper::toDto)
                .toList();
        log.debug("Total de categorías encontradas: {}", categorias.size());
        return categorias;
    }

    /**
     * Crea una nueva categoría de tutoriales en el sistema.
     * <p>
     * Convierte el DTO a entidad, la guarda en la base de datos
     * y retorna el DTO con el ID asignado y cantidad de tutoriales.
     * </p>
     *
     * @param dto DTO con los datos de la nueva categoría
     * @return DTO con los datos de la categoría creada incluyendo su ID
     */
    @Override
    @Transactional
    public CategoriaTutorialResponseDto crear(CategoriaTutorialRequestDto dto) {
        log.info("Creando nueva categoría de tutorial: {}", dto.getNombre());
        if (categoriaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            log.warn("Intento de crear categoría duplicada: {}", dto.getNombre());
            throw new IllegalArgumentException("Ya existe una categoría de tutorial con el nombre: " + dto.getNombre());
        }
        // Convertir DTO a entidad y guardar en la base de datos
        CategoriaTutorial entity = categoriaMapper.toEntity(dto);
        CategoriaTutorialResponseDto resultado = categoriaMapper.toDto(categoriaRepository.save(entity));
        log.info("Categoría creada exitosamente con ID: {}", resultado.getId());
        return resultado;
    }

    /**
     * Busca una categoría específica por su identificador único.
     * <p>
     * Lanza excepción si la categoría no existe en la base de datos.
     * </p>
     *
     * @param id Identificador único de la categoría a buscar
     * @return DTO con los datos de la categoría encontrada
     * @throws RuntimeException si la categoría no existe
     */
    @Override
    @Transactional(readOnly = true)
    public CategoriaTutorialResponseDto buscarPorId(Long id) {
        log.info("Buscando categoría por ID: {}", id);

        // Buscar la categoría o lanzar excepción si no existe
        CategoriaTutorial cat = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        log.debug("Categoría encontrada: {}", cat.getNombre());
        return categoriaMapper.toDto(cat);
    }

    /**
     * Elimina una categoría de tutoriales del sistema.
     * <p>
     * Verifica si la categoría existe antes de proceder a su eliminación.
     * </p>
     *
     * @param id Identificador único de la categoría a eliminar
     * @throws RuntimeException si la categoría no existe
     */
    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando categoría con ID: {}", id);
        if (!categoriaRepository.existsById(id)) {
            log.warn("No se puede eliminar: La categoría con ID {} no existe", id);
            throw new RuntimeException("La categoría no existe");
        }
        categoriaRepository.deleteById(id);
        log.info("Categoría con ID {} eliminada exitosamente", id);
    }

    /**
     * Actualiza una categoría de tutoriales existente.
     * <p>
     * Valida que el nuevo nombre no esté siendo usado por otra categoría.
     * </p>
     *
     * @param id  Identificador de la categoría a modificar
     * @param dto Datos actualizados
     * @return DTO de la categoría actualizada
     */
    @Override
    @Transactional
    public CategoriaTutorialResponseDto actualizar(Long id, CategoriaTutorialRequestDto dto) {
        log.info("Actualizando categoría de tutorial con ID: {}", id);

        // Buscar la categoría existente
        CategoriaTutorial existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));

        // Si el nombre cambia, verificar que el nuevo no exista ya
        if (!existente.getNombre().equalsIgnoreCase(dto.getNombre()) &&
                categoriaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            log.warn("Conflicto: Ya existe otra categoría con el nombre {}", dto.getNombre());
            throw new IllegalArgumentException(
                    "No se puede actualizar: El nombre '" + dto.getNombre() + "' ya está en uso.");
        }

        // Actualizar los campos (puedes usar el mapper o hacerlo manualmente)
        existente.setNombre(dto.getNombre());

        // Guardar y retornar
        CategoriaTutorial guardada = categoriaRepository.save(existente);
        log.info("Categoría {} actualizada exitosamente", id);

        return categoriaMapper.toDto(guardada);
    }
}