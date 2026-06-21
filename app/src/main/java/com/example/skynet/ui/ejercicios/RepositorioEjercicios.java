package com.example.skynet.ui.ejercicios;

import com.example.skynet.R;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.TutorialResponseDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepositorioEjercicios {

    /**
     * Interfaz para recibir los ejercicios de forma asíncrona desde el servidor.
     */
    public interface RepositorioCallback {
        void onLoaded(List<Ejercicio> ejercicios);
        void onError(String mensaje);
    }

    /**
     * Obtiene el catálogo de ejercicios (Tutoriales) desde el backend.
     * Convierte los TutorialResponseDto en objetos Ejercicio para la UI.
     */
    public static void getTodosAsync(RepositorioCallback callback) {
        RetrofitClient.getApiService().getTutoriales().enqueue(new Callback<ApiResponseDto<List<TutorialResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<TutorialResponseDto>>> call, Response<ApiResponseDto<List<TutorialResponseDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TutorialResponseDto> datos = response.body().getDatos();
                    List<Ejercicio> lista = new ArrayList<>();
                    for (TutorialResponseDto t : datos) {
                        Ejercicio e = new Ejercicio(
                                t.getId(),
                                t.getTitulo(),
                                t.getNombreCategoria(),
                                t.getDescripcion(),
                                t.getDuracionMin() + " min",
                                t.getUrlImagen() != null ? t.getUrlImagen() : t.getUrlVideo(), // Prefer urlImagen, fallback to urlVideo if it contains an image URL
                                R.drawable.ic_workout,
                                t.getMusculoObjetivo()
                        );
                        e.setUrlImagen(t.getUrlImagen());
                        e.setEsGlobal(t.isEsGlobal());
                        lista.add(e);
                    }
                    callback.onLoaded(lista);
                } else {
                    callback.onError("Error al obtener catálogo");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<TutorialResponseDto>>> call, Throwable t) {
                callback.onError("Fallo de red: " + t.getMessage());
            }
        });
    }

    /**
     * Mantiene compatibilidad con código antiguo que no sea asíncrono, 
     * aunque lo ideal es migrar todo a getTodosAsync.
     */
    public static List<Ejercicio> getTodos() {
        List<Ejercicio> lista = new ArrayList<>();
        
        // ID 1: Sentadilla con Barra
        lista.add(new Ejercicio(1L,
                "Sentadilla con Barra", 
                "Piernas", 
                "La sentadilla con barra es el ejercicio estrella para ganar fuerza y masa muscular en el tren inferior.", 
                "4 x 10", 
                "https://www.youtube.com/watch?v=gcNh17Ckjgg", 
                R.drawable.ic_launcher_background, 
                "Media 🔥🔥"
        ));
        
        // ID 2: Peso Muerto
        lista.add(new Ejercicio(2L,
                "Peso Muerto", 
                "Piernas", 
                "Levantamiento de potencia que trabaja toda la cadena posterior: glúteos, isquios y espalda baja.", 
                "3 x 8", 
                "https://www.youtube.com/watch?v=ytGaGIn3SjE",
                R.drawable.ic_launcher_background, 
                "Alta 🔥🔥🔥"
        ));

        // ID 3: Zancadas
        lista.add(new Ejercicio(3L,
                "Zancadas", 
                "Piernas", 
                "Excelente ejercicio unilateral para trabajar cuádriceps, glúteos y mejorar el equilibrio.", 
                "3 x 12 por pierna", 
                "https://www.youtube.com/watch?v=D7KaRcUTQeE", 
                R.drawable.ic_launcher_background, 
                "Baja 🔥"
        ));

        // ID 4: Press de Banca
        lista.add(new Ejercicio(4L,
                "Press de Banca", 
                "Pecho", 
                "Ejercicio básico multiarticular para el desarrollo de los pectorales, tríceps y deltoides anterior.", 
                "4 x 12", 
                "https://www.youtube.com/watch?v=vthMCtgVtFw", 
                R.drawable.ic_launcher_background, 
                "Media 🔥🔥"
        ));

        // ID 5: Flexiones
        lista.add(new Ejercicio(5L,
                "Flexiones", 
                "Pecho", 
                "Ejercicio de peso corporal clásico para fortalecer el pecho, hombros y tríceps.", 
                "3 x 20", 
                "https://www.youtube.com/watch?v=yY7Pmq_A8Ok", 
                R.drawable.ic_launcher_background, 
                "Baja 🔥"
        ));

        // ID 6: Dominadas
        lista.add(new Ejercicio(6L,
                "Dominadas", 
                "Espalda", 
                "Ejercicio fundamental de tracción superior para trabajar el dorsal ancho y bíceps.", 
                "3 x Al fallo", 
                "https://www.youtube.com/watch?v=eGo4IYlbE5g", 
                R.drawable.ic_launcher_background, 
                "Alta 🔥🔥🔥"
        ));

        // ID 7: Jalón al Pecho
        lista.add(new Ejercicio(7L,
                "Jalón al Pecho", 
                "Espalda", 
                "Máquina de tracción que permite trabajar el dorsal ancho de forma controlada.", 
                "4 x 10", 
                "https://www.youtube.com/watch?v=CAwf7n6Luuc", 
                R.drawable.ic_launcher_background, 
                "Media 🔥🔥"
        ));

        // ID 8: Press Militar
        lista.add(new Ejercicio(8L,
                "Press Militar", 
                "Hombro", 
                "Empuje vertical por encima de la cabeza para desarrollar la fuerza y tamaño del hombro.", 
                "4 x 8", 
                "https://www.youtube.com/watch?v=2yjwxt_fshE", 
                R.drawable.ic_launcher_background, 
                "Alta 🔥🔥🔥"
        ));

        // ID 9: Curl de Bíceps
        lista.add(new Ejercicio(9L,
                "Curl de Bíceps", 
                "Brazo", 
                "Ejercicio de aislamiento para fortalecer y dar forma al bíceps braquial.", 
                "3 x 15", 
                "https://www.youtube.com/watch?v=ykJmrZ5v0Oo", 
                R.drawable.ic_launcher_background, 
                "Baja 🔥"
        ));

        // ID 10: Plancha Abdominal
        lista.add(new Ejercicio(10L,
                "Plancha Abdominal", 
                "Core", 
                "Mantén el cuerpo recto como una tabla apoyado en los antebrazos para fortalecer el abdomen.", 
                "3 x 45s", 
                "https://www.youtube.com/watch?v=TvxNkmjdhMM", 
                R.drawable.ic_launcher_background, 
                "Baja 🔥"
        ));

        // ID 11: Burpees
        lista.add(new Ejercicio(11L,
                "Burpees", 
                "Cardio", 
                "Ejercicio de cuerpo completo explosivo que mejora la capacidad cardiovascular y quema grasa.", 
                "4 x 15", 
                "https://www.youtube.com/watch?v=auBLPXO8Fww", 
                R.drawable.ic_launcher_background, 
                "Alta 🔥🔥🔥"
        ));

        return lista;
    }

    public static List<String> getCategorias() {
        List<String> cats = new ArrayList<>();
        cats.add("Todos");
        cats.add("Piernas");
        cats.add("Pecho");
        cats.add("Espalda");
        cats.add("Hombro");
        cats.add("Brazo");
        cats.add("Core");
        cats.add("Cardio");
        return cats;
    }
}
