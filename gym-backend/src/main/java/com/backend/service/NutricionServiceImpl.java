package com.backend.service;

import com.backend.domain.*;
import com.backend.domain.enums.EnumObjetivo;
import com.backend.dto.*;
import com.backend.exceptions.ResourceNotFoundException;
import com.backend.mapper.NutricionMapper;
import com.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.backend.domain.enums.EnumNivelActividad;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class NutricionServiceImpl implements INutricionService {

    private final UsuarioRepository usuarioRepository;
    private final DietaRepository dietaRepository;
    private final NutricionRepository nutricionRepository;
    private final ObjectMapper objectMapper;
    private final NutricionMapper nutricionMapper;
    private final FatSecretService fatSecretService;
    private final ComidaDiariaRepository comidaDiariaRepository;

   @Override
    @Transactional
    public NutricionResponseDto generarPlanAutomatico(NutricionRequestDto request) {
        validarDatosFisicos(request);
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        int edad = (request.getEdad() != null) ? request.getEdad() : 25;
        String genero = (request.getGenero() != null) ? request.getGenero() : "MASCULINO";
        if (usuario.getPerfil() != null) {
            if (usuario.getPerfil().getFechaNacimiento() != null) {
                edad = Period.between(usuario.getPerfil().getFechaNacimiento(), LocalDate.now()).getYears();
            }
            if (usuario.getPerfil().getGenero() != null)
                genero = usuario.getPerfil().getGenero().name();
        }

        int kcalObjetivo = calcularCalorias(request, edad, genero);
        double proteinas = request.getPeso() * 2.0;
        double grasas = request.getPeso() * 0.8;
        double carbs = Math.max(0, (kcalObjetivo - (proteinas * 4 + grasas * 9)) / 4);

        String jsonFatSecret = obtenerMenu(kcalObjetivo / 3, proteinas / 3, carbs / 3, grasas / 3, "{}");
        List<FatSecretRecipeDto> recetas = new ArrayList<>();
        try {
            FatSecretResponseDto fs = objectMapper.readValue(jsonFatSecret, FatSecretResponseDto.class);
            if (fs.getRecipes() != null && fs.getRecipes().getRecipe() != null) {
                recetas = new ArrayList<>(fs.getRecipes().getRecipe());
                Collections.shuffle(recetas); // Barajar para que no sean siempre las mismas
            }
        } catch (Exception e) {
            log.warn("Error parseando recetas: {}", e.getMessage());
        }

        Set<String> usadas = new HashSet<>();
        FatSecretRecipeDto r1 = getRecetaAleatoria(recetas, usadas);
        FatSecretRecipeDto r2 = getRecetaAleatoria(recetas, usadas);
        FatSecretRecipeDto r3 = getRecetaAleatoria(recetas, usadas);

        Dieta dietaBase = dietaRepository
                .findByCategoriaDietaNombre(
                        resolverCategoria(EnumObjetivo.valueOf(request.getObjetivo().toUpperCase())))
                .stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("No hay dietas"));

       Nutricion plan = Nutricion.builder()
            .usuario(usuario)
            .nombreDieta("Plan personalizado")
            .tipoDieta(dietaBase.getNombre())
            .caloriasObjetivo(kcalObjetivo)
            .dieta(dietaBase)
            .macronutrientesJson(jsonFatSecret)
            .fechaGeneracion(LocalDateTime.now())
            .build();

        String dImg = "https://images.unsplash.com/photo-1490645935967-10de6ba17061";
        plan.setComidas(List.of(
                crearComida("Desayuno", getNombre(r1, "Desayuno Avena"), getImagen(r1, dImg), kcalObjetivo, proteinas,
                        carbs, grasas, 0.3, plan),
                crearComida("Almuerzo", getNombre(r2, "Pollo Saludable"), getImagen(r2, dImg), kcalObjetivo, proteinas,
                        carbs, grasas, 0.4, plan),
                crearComida("Cena", getNombre(r3, "Ensalada Ligera"), getImagen(r3, dImg), kcalObjetivo, proteinas,
                        carbs, grasas, 0.3, plan)));


    // Seteamos las comidas pero NO llamamos al repository.save()
    plan.setComidas(List.of(
            crearComida("Desayuno", getNombre(r1, "Desayuno Avena"), getImagen(r1, dImg), kcalObjetivo, proteinas, carbs, grasas, 0.3, plan),
            crearComida("Almuerzo", getNombre(r2, "Pollo Saludable"), getImagen(r2, dImg), kcalObjetivo, proteinas, carbs, grasas, 0.4, plan),
            crearComida("Cena", getNombre(r3, "Ensalada Ligera"), getImagen(r3, dImg), kcalObjetivo, proteinas, carbs, grasas, 0.3, plan)
    ));

    // IMPORTANTE: Quitamos el nutricionRepository.save(plan)
    // El ID del plan llegará nulo a Android, lo cual es correcto para un plan "borrador"
    return nutricionMapper.toResponseDto(plan);

    }

    @Override
    @Transactional
    public NutricionResponseDto generarPlanSemanal(NutricionRequestDto request) {
        validarDatosFisicos(request);
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        int edad = (request.getEdad() != null) ? request.getEdad() : 25;
        String genero = (request.getGenero() != null) ? request.getGenero() : "MASCULINO";

        int kcal = calcularCalorias(request, edad, genero);
        double p = request.getPeso() * 2.0;
        double g = request.getPeso() * 0.8;
        double c = Math.max(0, (kcal - (p * 4 + g * 9)) / 4);

        String json = fatSecretService.buscarRecetasPorMacros(kcal / 3, (int) (p / 3), (int) (c / 3), (int) (g / 3));
        List<FatSecretRecipeDto> recetas = new ArrayList<>();
        try {
            FatSecretResponseDto fs = objectMapper.readValue(json, FatSecretResponseDto.class);
            if (fs.getRecipes() != null && fs.getRecipes().getRecipe() != null) {
                recetas = new ArrayList<>(fs.getRecipes().getRecipe());
                Collections.shuffle(recetas); // Fundamental para variedad
            }
        } catch (Exception e) {
            log.warn("Error: {}", e.getMessage());
        }

        Dieta dietaBase = dietaRepository
                .findByCategoriaDietaNombre(
                        resolverCategoria(EnumObjetivo.valueOf(request.getObjetivo().toUpperCase())))
                .stream().findFirst().orElseThrow(() -> new ResourceNotFoundException("No hay dietas"));

        Nutricion plan = Nutricion.builder().usuario(usuario).nombreDieta("Plan semanal")
                .tipoDieta(dietaBase.getNombre()).caloriasObjetivo(kcal).dieta(dietaBase).macronutrientesJson(json)
                .fechaGeneracion(LocalDateTime.now()).build();

        List<ComidaDiaria> comidas = new ArrayList<>();
        String[] dias = { "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo" };
        Set<String> usadasEnEstePlan = new HashSet<>();

        for (String dia : dias) {
            FatSecretRecipeDto r1 = getRecetaAleatoria(recetas, usadasEnEstePlan);
            FatSecretRecipeDto r2 = getRecetaAleatoria(recetas, usadasEnEstePlan);
            FatSecretRecipeDto r3 = getRecetaAleatoria(recetas, usadasEnEstePlan);

            String dImg = "https://images.unsplash.com/photo-1490645935967-10de6ba17061";
            comidas.add(crearComida(dia + " - Desayuno", getNombre(r1, "Desayuno Energético"), getImagen(r1, dImg),
                    kcal, p, c, g, 0.3, plan));
            comidas.add(crearComida(dia + " - Almuerzo", getNombre(r2, "Almuerzo Proteico"), getImagen(r2, dImg), kcal,
                    p, c, g, 0.4, plan));
            comidas.add(crearComida(dia + " - Cena", getNombre(r3, "Cena Ligera"), getImagen(r3, dImg), kcal, p, c, g,
                    0.3, plan));
        }

        plan.setComidas(comidas);
        return nutricionMapper.toResponseDto(nutricionRepository.save(plan));
    }

    @Override
    @Transactional
    public NutricionResponseDto actualizarNombrePlan(Long id, String nuevoNombre) {
        Nutricion plan = nutricionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No encontrado"));
        plan.setNombreDieta(nuevoNombre);
        return nutricionMapper.toResponseDto(nutricionRepository.save(plan));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NutricionResponseDto> obtenerHistorial(Long usuarioId, int page, int size) {
        if (!usuarioRepository.existsById(usuarioId))
            throw new ResourceNotFoundException("Usuario no encontrado");
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaGeneracion").descending());
        return nutricionRepository.findByUsuarioId(usuarioId, pageable).map(nutricionMapper::toResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public NutricionResponseDto obtenerUltimoPlanPorUsuario(Long usuarioId) {
        return nutricionRepository.findFirstByUsuarioIdOrderByIdDesc(usuarioId).map(nutricionMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("No hay planes"));
    }

    private String getNombre(FatSecretRecipeDto r, String d) {
        return (r != null && r.getRecipeName() != null) ? r.getRecipeName() : d;
    }

    private String getImagen(FatSecretRecipeDto r, String d) {
        return (r != null && r.getRecipeImage() != null) ? r.getRecipeImage() : d;
    }

    private ComidaDiaria crearComida(String m, String n, String i, double k, double p, double c, double g, double por,
            Nutricion plan) {
        return ComidaDiaria.builder().momento(m).nombreAlimento(n).imagenUrl(i)
                .calorias((double) Math.round(k * por)).proteina((double) Math.round(p * por))
                .carbohidratos((double) Math.round(c * por)).grasas((double) Math.round(g * por))
                .plan(plan).fecha(LocalDate.now()).build();
    }

    private void validarDatosFisicos(NutricionRequestDto r) {
        if (r.getPeso() == null || r.getPeso() <= 0 || r.getAltura() == null || r.getAltura() <= 0)
            throw new IllegalArgumentException("Datos inválidos");
    }

    private int calcularCalorias(NutricionRequestDto r, int edad, String genero) {
        boolean h = genero.matches("(?i)HOMBRE|MASCULINO");
        double alt = r.getAltura() < 3 ? r.getAltura() * 100 : r.getAltura();
        double tmb = (10 * r.getPeso()) + (6.25 * alt) - (5 * edad) + (h ? 5 : -161);
        double tdee = tmb * EnumNivelActividad.valueOf(r.getNivelActividad().toUpperCase()).getFactor();
        EnumObjetivo obj = EnumObjetivo.valueOf(r.getObjetivo().toUpperCase());
        return switch (obj) {
            case GANAR -> (int) (tdee * 1.10);
            case PERDER -> (int) (tdee * 0.80);
            case MANTENER -> (int) tdee;
        };
    }

    private String resolverCategoria(EnumObjetivo o) {
        return switch (o) {
            case GANAR -> "VOLUMEN";
            case PERDER -> "DEFINICION";
            case MANTENER -> "MANTENIMIENTO";
        };
    }

    private String obtenerMenu(double k, double p, double c, double g, String f) {
    try {
        log.info("### Intento de conexión a FatSecret (Kcal: {}) ###", (int)k);
        String res = fatSecretService.buscarRecetasPorMacros((int) k, (int) p, (int) c, (int) g);
        
        // Si FatSecret responde pero con un error en el JSON
        if (res == null || res.contains("\"error\"")) {
            String ipDetectada = obtenerIpDeSalida();
            log.warn("### FATSECRET RECHAZÓ LA PETICIÓN ###");
            log.warn("IP de salida del servidor: {}", ipDetectada);
            log.warn("Respuesta de la API: {}", res);
            return f; // Devuelve el plan de respaldo
        }

        log.info("### CONEXIÓN EXITOSA CON FATSECRET ###");
        return res;

    } catch (Exception e) {
        // Si hay un error de red, timeout o 403 Forbidden
        String ipDetectada = obtenerIpDeSalida();
        log.error("### ERROR CRÍTICO DE CONEXIÓN ###");
        log.error("IP que está siendo bloqueada: {}", ipDetectada);
        log.error("Mensaje de error: {}", e.getMessage());
        return f; // Devuelve el plan de respaldo
    }
}

/**
 * Método auxiliar para detectar la IP real en el momento del fallo
 */
private String obtenerIpDeSalida() {
    try {
        org.springframework.web.client.RestTemplate rt = new org.springframework.web.client.RestTemplate();
        return rt.getForObject("https://api.ipify.org", String.class);
    } catch (Exception e) {
        return "No se pudo detectar la IP: " + e.getMessage();
    }
}
    private FatSecretRecipeDto getRecetaAleatoria(List<FatSecretRecipeDto> r, Set<String> u) {
        if (r == null || r.isEmpty())
            return null;
        List<FatSecretRecipeDto> d = r.stream().filter(x -> x.getRecipeId() != null && !u.contains(x.getRecipeId()))
                .toList();
        if (d.isEmpty())
            d = r;
        FatSecretRecipeDto sel = d.get(new Random().nextInt(d.size()));
        if (sel.getRecipeId() != null)
            u.add(sel.getRecipeId());
        return sel;
    }

    @Override
    @Transactional(readOnly = true)
    public NutricionResponseDto obtenerPlanPorId(Long id) {
        return nutricionRepository.findById(id)
                .map(nutricionMapper::toResponseDto)
                .orElseThrow(() -> new ResourceNotFoundException("Plan nutricional no encontrado"));
    }

   @Override
@Transactional
public NutricionResponseDto guardarPlan(Long usuarioId, NutricionResponseDto planDto) {
    Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

    // Convertimos el DTO a Entidad
    Nutricion plan = nutricionMapper.toEntity(planDto);
    plan.setUsuario(usuario);
    plan.setFechaGeneracion(LocalDateTime.now());

    // 2. IMPORTANTE: Guardamos primero el plan para obtener su ID real
    Nutricion planGuardado = nutricionRepository.save(plan);

    // 3. Vínculo y Guardado manual de las comidas (Hijos)
    if (planDto.getComidas() != null && !planDto.getComidas().isEmpty()) {
        List<ComidaDiaria> comidasEntities = planDto.getComidas().stream().map(cDto -> {
            ComidaDiaria c = nutricionMapper.toComidaEntity(cDto); // Asegúrate de tener este mapper
            c.setPlan(planGuardado); // Vinculamos al ID real recién creado
            c.setFecha(LocalDate.now());
            return c;
        }).toList();
        
        // Guardamos las comidas explícitamente en la DB
        comidaDiariaRepository.saveAll(comidasEntities);
        planGuardado.setComidas(comidasEntities);
    }

    return nutricionMapper.toResponseDto(planGuardado);
}
}