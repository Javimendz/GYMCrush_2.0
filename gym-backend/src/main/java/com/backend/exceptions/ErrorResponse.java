package com.backend.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

//Dto para manejar errores globales
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Para que los campos nulos no salgan en el JSON
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> errors; // Para capturar los fallos del @Valid
}