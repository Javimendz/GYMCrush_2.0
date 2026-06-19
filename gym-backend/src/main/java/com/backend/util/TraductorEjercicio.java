package com.backend.util;

import java.util.HashMap;
import java.util.Map;

public class TraductorEjercicio {

    private static final Map<String, String> diccionario = new HashMap<>();

    static {
        // --- PARTES DEL CUERPO (bodyPart) ---
        diccionario.put("back", "Espalda");
        diccionario.put("cardio", "Cardio");
        diccionario.put("chest", "Pecho");
        diccionario.put("lower arms", "Antebrazos");
        diccionario.put("lower legs", "Piernas (Gemelos)");
        diccionario.put("neck", "Cuello");
        diccionario.put("shoulders", "Hombros");
        diccionario.put("upper arms", "Brazos");
        diccionario.put("upper legs", "Piernas (Muslos)");
        diccionario.put("waist", "Cintura/Abdomen");
        diccionario.put("lower legs", "pierna");
        // --- EQUIPAMIENTO (equipment) ---
        diccionario.put("assisted", "Asistido");
        diccionario.put("band", "Banda elástica");
        diccionario.put("barbell", "Barra");
        diccionario.put("body weight", "Peso corporal");
        diccionario.put("bosu ball", "Bosu");
        diccionario.put("cable", "Polea");
        diccionario.put("dumbbell", "Mancuerna");
        diccionario.put("elliptical machine", "Elíptica");
        diccionario.put("ez barbell", "Barra EZ");
        diccionario.put("hammer", "Martillo");
        diccionario.put("kettlebell", "Pesa rusa");
        diccionario.put("leverage machine", "Máquina de palanca");
        diccionario.put("medicine ball", "Balón medicinal");
        diccionario.put("olympic barbell", "Barra olímpica");
        diccionario.put("resistance band", "Banda de resistencia");
        diccionario.put("roller", "Rodillo");
        diccionario.put("rope", "Cuerda");
        diccionario.put("skierg machine", "Máquina de esquí");
        diccionario.put("sled machine", "Trineo");
        diccionario.put("smith machine", "Máquina Smith/Multipower");
        diccionario.put("stability ball", "Pelota de estabilidad");
        diccionario.put("stationary bike", "Bicicleta estática");
        diccionario.put("stepper", "Step");
        diccionario.put("tire", "Neumático");
        diccionario.put("trap bar", "Barra hexagonal");
        diccionario.put("upper body ergometer", "Ergómetro de torso");
        diccionario.put("weighted", "Con peso añadido");
        diccionario.put("wheel roller", "Rueda abdominal");

        // --- MÚSCULOS OBJETIVO (target) ---
        diccionario.put("abs", "Abdominales");
        diccionario.put("abductors", "Abductores");
        diccionario.put("adductors", "Aductores");
        diccionario.put("biceps", "Bíceps");
        diccionario.put("calves", "Gemelos");
        diccionario.put("delts", "Deltoides");
        diccionario.put("forearms", "Antebrazos");
        diccionario.put("glutes", "Glúteos");
        diccionario.put("hamstrings", "Isquiotibiales");
        diccionario.put("lats", "Dorsales");
        diccionario.put("levator scapulae", "Elevador de la escápula");
        diccionario.put("pectorals", "Pectorales");
        diccionario.put("quads", "Cuádriceps");
        diccionario.put("serratus anterior", "Serrato anterior");
        diccionario.put("spine", "Erectores espinales");
        diccionario.put("traps", "Trapecios");
        diccionario.put("triceps", "Tríceps");
        diccionario.put("upper back", "Espalda superior");
    }


//  Declaramos el mapa como final
private static final Map<String, String> busquedaInversa = new HashMap<>();

//  Usamos un bloque static para llenarlo sin límites
static {
    busquedaInversa.put("piernas", "legs");
    busquedaInversa.put("pecho", "chest");
    busquedaInversa.put("espalda", "back");
    busquedaInversa.put("hombros", "shoulders");
    busquedaInversa.put("brazos", "arms");
    busquedaInversa.put("abdominales", "abs");
    busquedaInversa.put("abdomen", "abs");
    busquedaInversa.put("sentadilla", "squat");
    busquedaInversa.put("sentadillas", "squat");
    busquedaInversa.put("flexiones", "push up");
    busquedaInversa.put("biceps", "biceps");
    busquedaInversa.put("triceps", "triceps");
    busquedaInversa.put("gluteos", "glutes");
    busquedaInversa.put("cuadriceps", "quads");
    busquedaInversa.put("isquios", "hamstrings");
    busquedaInversa.put("gemelos", "calves");
    busquedaInversa.put("antebrazos", "forearms");
    busquedaInversa.put("cardio", "cardio");
    busquedaInversa.put("pesas", "dumbbell");
    busquedaInversa.put("barra", "barbell");
    busquedaInversa.put("polea", "cable");
}

public static String traducirParaBusqueda(String terminoEspanol) {
    if (terminoEspanol == null || terminoEspanol.isEmpty()) return "";
    
    // Buscamos en el diccionario, si no está, enviamos la palabra original
    return busquedaInversa.getOrDefault(terminoEspanol.toLowerCase().trim(), terminoEspanol);
}

    public static String traducir(String textoIngles) {
        if (textoIngles == null || textoIngles.isEmpty()) return "No especificado";
        return diccionario.getOrDefault(textoIngles.toLowerCase(), 
               textoIngles.substring(0, 1).toUpperCase() + textoIngles.substring(1));
    }
}