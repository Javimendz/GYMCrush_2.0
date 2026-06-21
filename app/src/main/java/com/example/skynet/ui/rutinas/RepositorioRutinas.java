package com.example.skynet.ui.rutinas;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.EntrenamientoResponseDto;
import com.example.skynet.ui.ejercicios.Ejercicio;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioRutinas {

    private static Map<String, List<Ejercicio>> datosRutinas = new HashMap<>();
    private static List<RutinaModel> listaRutinas = new ArrayList<>();
    private static final String PREFS_NAME = "GymCrush_RutinasPersistencia";
    private static final String KEY_DATOS_RUTINAS = "datos_rutinas_json";

    public static void cargarDatosDesdeLocal(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        
        String jsonEjercicios = prefs.getString(KEY_DATOS_RUTINAS, null);
        if (jsonEjercicios != null) {
            java.lang.reflect.Type type = new TypeToken<Map<String, List<Ejercicio>>>(){}.getType();
            Map<String, List<Ejercicio>> cargados = gson.fromJson(jsonEjercicios, type);
            if (cargados != null) {
                datosRutinas.clear();
                datosRutinas.putAll(cargados);
            }
        }

        String jsonLista = prefs.getString("lista_rutinas_model_json", null);
        if (jsonLista != null) {
            java.lang.reflect.Type type = new TypeToken<List<RutinaModel>>(){}.getType();
            List<RutinaModel> cargada = gson.fromJson(jsonLista, type);
            if (cargada != null) {
                listaRutinas.clear();
                listaRutinas.addAll(cargada);
            }
        }
    }

    public static void guardarDatosEnLocal(Context context) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        
        String jsonEjercicios = gson.toJson(datosRutinas);
        String jsonLista = gson.toJson(listaRutinas);
        
        prefs.edit()
                .putString(KEY_DATOS_RUTINAS, jsonEjercicios)
                .putString("lista_rutinas_model_json", jsonLista)
                .apply();
    }

    public static class RutinaModel {
        public Long id;
        public String nombre;
        public String duracion;
        public String dificultad;
        public String descripcion;
        public String urlVideo;
        public int imagenResId;
        public boolean completada = false;
        public List<Ejercicio> ejercicios;

        public RutinaModel(String nombre, String duracion, String dificultad, int imagenResId) {
            this(null, nombre, duracion, dificultad, "", "", imagenResId, new ArrayList<>());
        }

        public RutinaModel(String nombre, String duracion, String dificultad, int imagenResId, List<Ejercicio> ejercicios) {
            this(null, nombre, duracion, dificultad, "", "", imagenResId, ejercicios);
        }

        public RutinaModel(Long id, String nombre, String duracion, String dificultad, String descripcion, String urlVideo, int imagenResId, List<Ejercicio> ejercicios) {
            this.id = id;
            this.nombre = nombre;
            this.duracion = duracion;
            this.dificultad = dificultad;
            this.descripcion = descripcion;
            this.urlVideo = urlVideo;
            this.imagenResId = imagenResId;
            this.ejercicios = ejercicios;
        }
    }

    public static List<RutinaModel> getRutinas() {
        return listaRutinas;
    }

    public static RutinaModel getRutinaPorNombre(String nombre) {
        if (nombre == null) return null;
        for (RutinaModel r : listaRutinas) {
            if (r.nombre.equalsIgnoreCase(nombre)) return r;
        }
        return null;
    }

    private static void agregarRutinaInicial(String nombre, String duracion, String dificultad, int img, List<Ejercicio> ejs) {
        listaRutinas.add(new RutinaModel(nombre, duracion, dificultad, img, ejs));
        datosRutinas.put(nombre, ejs);
    }

    public static void guardarRutinaCompleta(Context context, String nombre, List<Ejercicio> ejercicios) {
        guardarRutinaCompletaConPortada(context, null, nombre, ejercicios, null, null, null);
    }

    public static void guardarRutinaCompletaConPortada(Context context, Long id, String nombre, List<Ejercicio> ejercicios, String urlPortada, String duracion, String descripcion) {
        int imgRes = (urlPortada == null || urlPortada.isEmpty()) ? R.drawable.ic_launcher_background : 0;
        // Evitar duplicados por nombre para mantener consistencia
        listaRutinas.removeIf(r -> r.nombre.equals(nombre));
        listaRutinas.add(new RutinaModel(id, nombre, duracion, "Media", descripcion, urlPortada, imgRes, ejercicios));
        datosRutinas.put(nombre, ejercicios);
        guardarDatosEnLocal(context);
    }

    public static List<Ejercicio> getEjerciciosDeRutina(String nombre) {
        if (nombre == null) return new ArrayList<>();
        String nombreBusqueda = nombre.toLowerCase().trim();
        for (String key : datosRutinas.keySet()) {
            if (key.toLowerCase().trim().equals(nombreBusqueda)) {
                return datosRutinas.get(key);
            }
        }
        return new ArrayList<>();
    }

    public static void eliminarRutina(Context context, String nombre) {
        datosRutinas.remove(nombre);
        listaRutinas.removeIf(r -> r.nombre.equals(nombre));
        guardarDatosEnLocal(context);
    }

    public static void sincronizarConServidor(Context context, List<EntrenamientoResponseDto> entrenamientos) {
        List<RutinaModel> nuevasRutinas = new ArrayList<>();
        boolean huboCambios = false;
        for (EntrenamientoResponseDto e : entrenamientos) {
            // Sincronización insensible a mayúsculas/minúsculas para evitar pérdida de datos
            String nombreNormalizado = e.getNombre() != null ? e.getNombre().toLowerCase().trim() : "";
            List<Ejercicio> ejs = null;
            
            // Buscar en el mapa con el nombre normalizado
            for (String key : datosRutinas.keySet()) {
                if (key.toLowerCase().trim().equals(nombreNormalizado)) {
                    ejs = datosRutinas.get(key);
                    break;
                }
            }

            if (ejs == null) ejs = new ArrayList<>();
            
            RutinaModel model = new RutinaModel(
                    e.getId(),
                    e.getNombre(),
                    e.getDuracion() + " min",
                    e.getIntensidad(),
                    e.getDescripcion(),
                    e.getUrlVideo(),
                    (e.getUrlVideo() == null || e.getUrlVideo().isEmpty()) ? R.drawable.ic_launcher_background : 0,
                    ejs
            );

            // Preservar el estado completado local si ya existía
            for (RutinaModel anterior : listaRutinas) {
                if ((anterior.id != null && anterior.id.equals(e.getId())) || 
                    (anterior.nombre != null && anterior.nombre.equalsIgnoreCase(e.getNombre()))) {
                    model.completada = anterior.completada;
                    // También preservamos el estado de los ejercicios individuales
                    if (anterior.ejercicios != null && model.ejercicios != null) {
                        for (Ejercicio nuevoEj : model.ejercicios) {
                            for (Ejercicio viejoEj : anterior.ejercicios) {
                                if (nuevoEj.getNombre() != null && nuevoEj.getNombre().equalsIgnoreCase(viejoEj.getNombre())) {
                                    nuevoEj.setSeleccionado(viejoEj.isSeleccionado());
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
            }

            nuevasRutinas.add(model);
            
            // Asegurar que el mapa conserve la asociación
            if (!ejs.isEmpty()) {
                datosRutinas.put(e.getNombre(), ejs);
                huboCambios = true;
            }
        }
        
        // Mantener rutinas locales que no tienen ID (las hardcodeadas iniciales) si no están en el servidor
        for (RutinaModel local : listaRutinas) {
            if (local.id == null) {
                boolean yaExiste = nuevasRutinas.stream().anyMatch(n -> n.nombre.equals(local.nombre));
                if (!yaExiste) nuevasRutinas.add(local);
            }
        }

        listaRutinas.clear();
        listaRutinas.addAll(nuevasRutinas);
        if (huboCambios) guardarDatosEnLocal(context);
    }

    public static void marcarComoCompletada(Context context, String nombre) {
        for (RutinaModel r : listaRutinas) {
            if (r.nombre.equalsIgnoreCase(nombre)) {
                r.completada = true;
                // Marcar todos sus ejercicios como completados también
                List<Ejercicio> ejs = getEjerciciosDeRutina(nombre);
                if (ejs != null) {
                    for (Ejercicio e : ejs) e.setSeleccionado(true);
                }
                break;
            }
        }
        guardarDatosEnLocal(context);
    }

    public static void setEjercicioCompletado(Context context, String routineName, String exerciseName, boolean completado) {
        if (routineName == null || exerciseName == null) {
            Log.e("RepositorioRutinas", "setEjercicioCompletado: routineName o exerciseName es null");
            return;
        }
        
        String rSearch = routineName.trim().toLowerCase();
        String exSearch = exerciseName.trim().toLowerCase();
        
        Log.d("RepositorioRutinas", "Intentando marcar ejercicio: [" + exSearch + "] en rutina: [" + rSearch + "]");
        
        List<Ejercicio> ejs = getEjerciciosDeRutina(routineName);
        
        if (ejs == null || ejs.isEmpty()) {
            // Intento desesperado: buscar en todas las rutinas si la principal falló
            Log.w("RepositorioRutinas", "Rutina no encontrada por nombre exacto. Buscando en todas...");
            for (String key : datosRutinas.keySet()) {
                if (key.trim().toLowerCase().contains(rSearch) || rSearch.contains(key.trim().toLowerCase())) {
                    ejs = datosRutinas.get(key);
                    Log.d("RepositorioRutinas", "Encontrada posible coincidencia: " + key);
                    break;
                }
            }
        }

        if (ejs != null && !ejs.isEmpty()) {
            boolean exerciseFound = false;
            for (Ejercicio e : ejs) {
                if (e.getNombre() != null) {
                    String currentExName = e.getNombre().trim().toLowerCase();
                    if (currentExName.equals(exSearch)) {
                        e.setSeleccionado(completado);
                        exerciseFound = true;
                        Log.d("RepositorioRutinas", "¡Ejercicio marcado!: " + e.getNombre());
                    }
                }
            }

            if (exerciseFound) {
                // Recalcular si toda la rutina está completada
                boolean allDone = true;
                for (Ejercicio e : ejs) {
                    if (!e.isSeleccionado()) {
                        allDone = false;
                        break;
                    }
                }

                // Actualizar estado de la rutina en la lista global y asegurar sincronización de objetos
                for (RutinaModel r : listaRutinas) {
                    if (r.nombre != null && r.nombre.trim().equalsIgnoreCase(routineName.trim())) {
                        if (r.ejercicios != null) {
                            for (Ejercicio e : r.ejercicios) {
                                if (e.getNombre() != null && e.getNombre().trim().toLowerCase().equals(exSearch)) {
                                    e.setSeleccionado(completado);
                                }
                            }
                        }
                        r.completada = allDone;
                        break;
                    }
                }
                
                guardarDatosEnLocal(context);
                
                // Notificar a la UI
                android.content.Intent intent = new android.content.Intent("com.example.gymcrush.ACTUALIZAR_LISTA_EJERCICIOS");
                intent.setPackage(context.getPackageName());
                context.sendBroadcast(intent);
            } else {
                Log.e("RepositorioRutinas", "No se encontró el ejercicio '" + exerciseName + "' dentro de la rutina.");
            }
        } else {
            Log.e("RepositorioRutinas", "No se pudo encontrar la rutina: " + routineName);
        }
    }
}