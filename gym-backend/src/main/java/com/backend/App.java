package com.backend;

import org.springframework.boot.SpringApplication; // Importación necesaria
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@RestController
@EnableAsync  //Habilitar la ansincronía para el manejo de eventos y notificaciones
@EnableScheduling //Habilitar la ejecución de tareas programadas (como el recordatorio de clases)
@RequestMapping("/api")
public class App {

   
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RequestMapping("/saludo")
    public String holaMundo() {
        return "¡Hola! Andrea!!!!!";
    }

  @Bean
    public WebClient.Builder webClientBuilder() {
        // Configuramos un límite de buffer de 10 MB (10 * 1024 * 1024 bytes)
        int size = 10 * 1024 * 1024;
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        // Le pasamos las estrategias modificadas al builder por defecto
        return WebClient.builder()
                .exchangeStrategies(strategies);
    }
}