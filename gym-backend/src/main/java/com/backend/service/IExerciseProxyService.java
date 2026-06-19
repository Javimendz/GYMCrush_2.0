package com.backend.service;

import java.util.List;
import com.backend.dto.ExerciseApiRequestDto;

/**
 * Interfaz para el servicio de búsqueda de ejercicios en la API externa (ExerciseDB).
 * Proporciona un contrato para buscar ejercicios por nombre.
 */
public interface IExerciseProxyService {

    /**
     * Realiza una consulta a la API externa para obtener una lista de ejercicios
     * que coincidan con el nombre proporcionado.
     *
     * @param nombre El nombre del ejercicio a buscar (ej: "push up").
     * @return Una lista de objetos {@link ExerciseApiRequestDto} con la información de los ejercicios.
     */
List<ExerciseApiRequestDto> buscarPorNombre(String nombre);}