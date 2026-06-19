package com.backend;

import org.springframework.boot.SpringApplication; // Importación necesaria
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}