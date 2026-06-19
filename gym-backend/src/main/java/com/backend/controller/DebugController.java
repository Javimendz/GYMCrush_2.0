package com.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/debug")
public class DebugController {

    @GetMapping("/my-ip")
    public String getMyIp() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            // El servidor de Render hace una petición de salida a ipify
            String ip = restTemplate.getForObject("https://api.ipify.org", String.class);
            
            log.info("DIAGNÓSTICO DE IP: La IP de salida detectada es {}", ip);
            
            return "La IP de salida de este servidor Render es: " + ip;
        } catch (Exception e) {
            log.error("Error al detectar la IP de salida", e);
            return "Error al detectar la IP: " + e.getMessage();
        }
    }
}