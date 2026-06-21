package com.example.skynet.data.remote;

import com.example.skynet.data.remote.dto.FaceLoginRequest;
import com.example.skynet.data.remote.dto.AuthResponse;
import com.example.skynet.data.remote.dto.CategoriaRequestDto;
import com.example.skynet.data.remote.dto.CategoriaResponseDto;
import com.example.skynet.data.remote.dto.CategoriaTutorialRequestDto;
import com.example.skynet.data.remote.dto.CategoriaTutorialResponseDto;
import com.example.skynet.data.remote.dto.ExerciseApiRequestDto;
import com.example.skynet.data.remote.dto.LoginRequest;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.NotificacionResponseDto;
import com.example.skynet.data.remote.dto.NutricionHistorialResponse;
import com.example.skynet.data.remote.dto.PerfilRequestDto;
import com.example.skynet.data.remote.dto.PerfilResponseDto;
import com.example.skynet.data.remote.dto.RegisterRequest;
import com.example.skynet.data.remote.dto.SaludRequestDto;
import com.example.skynet.data.remote.dto.SaludResponseDto;
import com.example.skynet.data.remote.dto.TicketRequestDto;
import com.example.skynet.data.remote.dto.TicketResponseDto;
import com.example.skynet.data.remote.dto.TutorialRequestDto;
import com.example.skynet.data.remote.dto.TutorialResponseDto;
import com.example.skynet.data.remote.dto.EntrenamientoRequestDto;
import com.example.skynet.data.remote.dto.EntrenamientoResponseDto;
import com.example.skynet.data.remote.dto.HorarioRequestDto;
import com.example.skynet.data.remote.dto.HorarioResponseDto;
import com.example.skynet.data.remote.dto.AccesoResponseDto;
import com.example.skynet.data.remote.dto.ActividadRequestDto;
import com.example.skynet.data.remote.dto.ActividadResponseDto;
import com.example.skynet.data.remote.dto.ReservaResponseDto;
import com.example.skynet.data.remote.dto.SalaRequestDto;
import com.example.skynet.data.remote.dto.SalaResponseDto;
import com.example.skynet.data.remote.dto.UsuarioResponseDto;
import com.example.skynet.data.remote.dto.VisualizacionRequestDto;
import com.example.skynet.data.remote.dto.VisualizacionResponseDto;
import com.example.skynet.data.remote.dto.ComidaDiariaRequestDto;
import com.example.skynet.data.remote.dto.ComidaDiariaResponseDto;
import com.example.skynet.data.remote.dto.DietaResponseDto;
import com.example.skynet.data.remote.dto.NutricionResponseDto;
import com.example.skynet.data.remote.dto.PerfilFisicoRequest;
import com.example.skynet.data.remote.dto.RutinaRequestDto;
import com.example.skynet.data.remote.dto.RutinaResponseDto;
import com.example.skynet.data.remote.dto.PlanResponseDto;
import com.example.skynet.data.remote.dto.PlanRequestDto;
import com.example.skynet.data.remote.dto.DetallePlanRequestDto;
import com.example.skynet.data.remote.dto.DetallePlanResponseDto;
import com.example.skynet.data.remote.dto.PageResponseDto;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.*;



public interface ApiService {
    // --- NUEVOS ENDPOINTS PARA COMIDAS DIARIAS ---
    @POST("api/v1/comidas")
    Call<ApiResponseDto<ComidaDiariaResponseDto>> añadirComida(@Body ComidaDiariaRequestDto request);
    @GET("api/v1/planes/{planId}/ejercicios")
    Call<ApiResponseDto<List<DetallePlanResponseDto>>> obtenerEjerciciosDelPlan(@Path("planId") Long planId);

    @GET("api/v1/nutricion/historial/{usuarioId}")
    Call<ApiResponseDto<NutricionHistorialResponse>> getHistorial(
            @Path("usuarioId") Long usuarioId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("api/v1/visualizaciones/usuario/{usuarioId}/tutorial/{tutorialId}")
    Call<ApiResponseDto<VisualizacionResponseDto>> obtenerProgreso(
            @Path("usuarioId") Long usuarioId,
            @Path("tutorialId") Long tutorialId
    );

    @GET("api/v1/entrenamientos")
    Call<ApiResponseDto<List<EntrenamientoResponseDto>>> listarEntrenamientosCatalogo();

    @GET("api/v1/entrenamientos/globales")
    Call<ApiResponseDto<List<EntrenamientoResponseDto>>> obtenerProgramasGlobales(
            @Query("intensidad") String intensidad,
            @Query("objetivo") String objetivo
    );
    @DELETE("api/v1/planes/ejercicios-detalle/{id}")
    Call<ApiResponseDto<Void>> eliminarEjercicioDelPlan(@Path("id") Long id);
    @DELETE("api/v1/comidas/{id}")
    Call<ApiResponseDto<Void>> eliminarComida(@Path("id") Long id);

    @POST("api/v1/comidas/batch/{planId}")
    Call<ApiResponseDto<Void>> guardarMuchasComidas(@Path("planId") Long planId, @Body List<ComidaDiariaRequestDto> comidas);

    // Endpoint de Inicio de Sesión
    @POST("api/v1/auth/login")
    Call<ApiResponseDto<AuthResponse>> login(@Body LoginRequest loginRequest);

    @POST("api/v1/auth/google-login")
    Call<ApiResponseDto<AuthResponse>> googleLogin(@Body com.example.skynet.data.remote.dto.GoogleLoginRequest googleLoginRequest);

    @POST("api/v1/auth/face-login")
    Call<ApiResponseDto<AuthResponse>> faceLogin(@Body FaceLoginRequest faceLoginRequest);

    // Endpoint de Registro
    @POST("api/v1/auth/register")
    Call<ApiResponseDto<UsuarioResponseDto>> register(@Body RegisterRequest registerRequest);

    // Endpoints de Perfil
    @GET("api/v1/perfil/{id}")
    Call<ApiResponseDto<PerfilResponseDto>> getPerfilById(@Path("id") Long id);

    @PUT("api/v1/perfil/{id}")
    Call<ApiResponseDto<PerfilResponseDto>> updatePerfil(@Path("id") Long id, @Body PerfilRequestDto perfilRequestDto);

    @GET("api/v1/perfil")
    Call<ApiResponseDto<List<PerfilResponseDto>>> findAllPerfiles();

    @GET("api/v1/perfil/entrenadores")
    Call<ApiResponseDto<List<PerfilResponseDto>>> findEntrenadores();

    // Endpoints de Salud
    @GET("api/v1/salud/usuario/{id}")
    Call<ApiResponseDto<SaludResponseDto>> getSaludActual(@Path("id") Long id);

    @GET("api/v1/salud/usuario/{id}/historial")
    Call<ApiResponseDto<List<SaludResponseDto>>> getHistorialSalud(@Path("id") Long id);

    @POST("api/v1/salud/usuario/{id}")
    Call<ApiResponseDto<SaludResponseDto>> registrarSalud(@Path("id") Long id, @Body SaludRequestDto saludRequestDto);

    // Endpoints de Tutoriales (Ejercicios)
    @GET("api/v1/tutoriales")
    Call<ApiResponseDto<List<TutorialResponseDto>>> getTutoriales();

    @GET("api/v1/tutoriales/publicos")
    Call<ApiResponseDto<List<TutorialResponseDto>>> getTutorialesPublicos();

    @GET("api/v1/tutoriales/categoria/{catId}")
    Call<ApiResponseDto<List<TutorialResponseDto>>> getTutorialesByCategoria(@Path("catId") Long catId);

    @GET("api/v1/categorias-tutorial")
    Call<ApiResponseDto<List<CategoriaTutorialResponseDto>>> getCategorias();

    @POST("api/v1/categorias-tutorial")
    Call<ApiResponseDto<CategoriaTutorialResponseDto>> crearCategoria(@Body CategoriaTutorialRequestDto categoriaTutorialRequestDto);

    @GET("api/v1/tutoriales/biblioteca/buscar")
    Call<ApiResponseDto<List<ExerciseApiRequestDto>>> buscarEnBiblioteca(@Query("nombre") String nombre);

    // --- ENDPOINTS PARA CATEGORÍAS DE ACTIVIDAD ---
    @GET("api/v1/categorias")
    Call<ApiResponseDto<List<CategoriaResponseDto>>> getCategoriasActividad();

    @POST("api/v1/categorias")
    Call<ApiResponseDto<CategoriaResponseDto>> crearCategoriaActividad(@Body CategoriaRequestDto dto);

    @DELETE("api/v1/categorias/{id}")
    Call<ApiResponseDto<Void>> eliminarCategoriaActividad(@Path("id") Long id);

    @POST("api/v1/tutoriales")
    Call<ApiResponseDto<TutorialResponseDto>> crearTutorial(@Body TutorialRequestDto tutorialRequestDto);

    @PUT("api/v1/tutoriales/{id}")
    Call<ApiResponseDto<TutorialResponseDto>> actualizarTutorial(@Path("id") Long id, @Body TutorialRequestDto tutorialRequestDto);

    @DELETE("api/v1/tutoriales/{id}")
    Call<ApiResponseDto<Void>> eliminarTutorial(@Path("id") Long id);

    // Endpoints de Visualizaciones (Progreso)
    @POST("api/v1/visualizaciones/progreso")
    Call<ApiResponseDto<VisualizacionResponseDto>> guardarProgreso(@Body VisualizacionRequestDto visualizacionRequestDto);

    @GET("api/v1/visualizaciones/usuario/{usuarioId}/historial")
    Call<ApiResponseDto<List<VisualizacionResponseDto>>> getHistorialVisualizaciones(@Path("usuarioId") Long usuarioId);

    @GET("api/v1/visualizaciones/usuario/{usuarioId}/pendientes")
    Call<ApiResponseDto<List<VisualizacionResponseDto>>> getVideosPendientes(@Path("usuarioId") Long usuarioId);

    @GET("api/v1/visualizaciones/populares")
    Call<ApiResponseDto<List<TutorialResponseDto>>> getTutorialesPopulares();

    @DELETE("api/v1/visualizaciones/{id}")
    Call<ApiResponseDto<Void>> eliminarVisualizacion(@Path("id") Long id);

    // --- NUEVOS ENDPOINTS PARA HORARIOS ---
    @GET("api/v1/horarios")
    Call<ApiResponseDto<List<HorarioResponseDto>>> getTodosLosHorarios();

    @GET("api/v1/horarios/dia/{dia}")
    Call<ApiResponseDto<List<HorarioResponseDto>>> getHorariosPorDia(@Path("dia") String dia);

    @GET("api/v1/horarios/{id}")
    Call<ApiResponseDto<HorarioResponseDto>> getHorarioById(@Path("id") Long id);

    @POST("api/v1/horarios")
    Call<ApiResponseDto<HorarioResponseDto>> crearHorario(@Body HorarioRequestDto horario);

    @PUT("api/v1/horarios/{id}")
    Call<ApiResponseDto<HorarioResponseDto>> actualizarHorario(@Path("id") Long id, @Body HorarioRequestDto horario);

    @DELETE("api/v1/horarios/{id}")
    Call<ApiResponseDto<Void>> eliminarHorario(@Path("id") Long id);

    // --- NUEVOS ENDPOINTS PARA ACTIVIDADES ---
    @GET("api/v1/actividades")
    Call<ApiResponseDto<List<ActividadResponseDto>>> getTodasLasActividades();

    @GET("api/v1/actividades/{id}")
    Call<ApiResponseDto<ActividadResponseDto>> getActividadById(@Path("id") Long id);

    @POST("api/v1/actividades")
    Call<ApiResponseDto<ActividadResponseDto>> crearActividad(@Body ActividadRequestDto dto);

    @PUT("api/v1/actividades/{id}")
    Call<ApiResponseDto<ActividadResponseDto>> actualizarActividad(@Path("id") Long id, @Body ActividadRequestDto dto);

    @DELETE("api/v1/actividades/{id}")
    Call<ApiResponseDto<Void>> eliminarActividad(@Path("id") Long id);

    @GET("api/v1/actividades/buscar")
    Call<ApiResponseDto<List<ActividadResponseDto>>> buscarActividadPorNombre(@Query("nombre") String nombre);

    @GET("api/v1/actividades/dia/{dia}")
    Call<ApiResponseDto<List<ActividadResponseDto>>> getActividadesPorDia(@Path("dia") String dia);

    @GET("api/v1/actividades/precio-max")
    Call<ApiResponseDto<List<ActividadResponseDto>>> filtrarActividadesPorPrecio(@Query("precio") Integer precio);

    // --- NUEVOS ENDPOINTS PARA RESERVAS ---
    @POST("api/v1/reservas/usuario/{uId}/horario/{hId}")
    Call<ApiResponseDto<ReservaResponseDto>> crearReserva(@Path("uId") Long usuarioId, @Path("hId") Long horarioId);

    @GET("api/v1/reservas/usuario/{usuarioId}")
    Call<ApiResponseDto<List<ReservaResponseDto>>> getMisReservas(@Path("usuarioId") Long usuarioId);

    @PATCH("api/v1/reservas/{reservaId}/cancelar")
    Call<ApiResponseDto<Void>> cancelarReserva(@Path("reservaId") Long reservaId);

    @PATCH("api/v1/reservas/{reservaId}/confirmar")
    Call<ApiResponseDto<Void>> confirmarAsistencia(@Path("reservaId") Long reservaId);

    @GET("api/v1/reservas/dia/{dia}")
    Call<ApiResponseDto<List<ReservaResponseDto>>> getReservasPorDia(@Path("dia") String dia);

    @GET("api/v1/reservas/clase/{horarioId}/asistentes")
    Call<ApiResponseDto<List<ReservaResponseDto>>> obtenerAsistentesClase(@Path("horarioId") Long horarioId);

    // --- NUEVOS ENDPOINTS PARA SALAS ---
    @GET("api/v1/salas")
    Call<ApiResponseDto<List<SalaResponseDto>>> getTodasLasSalas();

    @GET("api/v1/salas/activas")
    Call<ApiResponseDto<List<SalaResponseDto>>> getSalasActivas();

    @GET("api/v1/salas/{id}")
    Call<ApiResponseDto<SalaResponseDto>> getSalaById(@Path("id") Long id);

    @POST("api/v1/salas")
    Call<ApiResponseDto<SalaResponseDto>> crearSala(@Body SalaRequestDto salaRequestDto);

    @PUT("api/v1/salas/{id}")
    Call<ApiResponseDto<SalaResponseDto>> actualizarSala(@Path("id") Long id, @Body SalaRequestDto salaRequestDto);

    @PATCH("api/v1/salas/{id}/estado")
    Call<ApiResponseDto<Void>> cambiarEstadoSala(@Path("id") Long id, @Query("activa") Boolean activa);

    @DELETE("api/v1/salas/{id}")
    Call<ApiResponseDto<Void>> eliminarSala(@Path("id") Long id);

    // --- NUEVOS ENDPOINTS PARA RUTINAS ---
    @POST("api/v1/rutinas")
    @Headers("Content-Type: application/json")
    Call<ApiResponseDto<RutinaResponseDto>> asignarEntrenamiento(@Body RutinaRequestDto dto);

    @GET("api/v1/rutinas/usuario/{usuarioId}")
    Call<ApiResponseDto<List<RutinaResponseDto>>> getRutinaDiaria(
            @Path("usuarioId") Long usuarioId,
            @Query("fecha") String fecha
    );

    @GET("api/v1/rutinas/usuario/{usuarioId}/rango")
    Call<List<RutinaResponseDto>> getRutinasPorRango(
            @Path("usuarioId") Long usuarioId,
            @Query("inicio") String inicio,
            @Query("fin") String fin
    );

    @PATCH("api/v1/rutinas/{id}/completar")
    Call<ApiResponseDto<RutinaResponseDto>> completarRutina(@Path("id") Long id);

    @DELETE("api/v1/rutinas/{id}")
    Call<ApiResponseDto<Void>> eliminarRutina(@Path("id") Long id);

    // --- NUEVOS ENDPOINTS PARA PLANES DE ENTRENAMIENTO ---
    @GET("api/v1/planes")
    Call<ApiResponseDto<List<PlanResponseDto>>> getPlanes();

    @GET("api/v1/planes/{id}")
    Call<ApiResponseDto<PlanResponseDto>> getPlanPorId(@Path("id") Long id);

    @POST("api/v1/planes/{planId}/suscribir/{usuarioId}")
    Call<ApiResponseDto<Void>> suscribirUsuarioAPlan(@Path("planId") Long planId, @Path("usuarioId") Long usuarioId);



    @POST("api/v1/planes")
    Call<ApiResponseDto<PlanResponseDto>> crearPlan(@Body PlanRequestDto dto);

    @PUT("api/v1/planes/{id}")
    Call<ApiResponseDto<PlanResponseDto>> actualizarPlan(@Path("id") Long id, @Body PlanRequestDto dto);

    @DELETE("api/v1/planes/{id}")
    Call<ApiResponseDto<Void>> eliminarPlanMaestro(@Path("id") Long id);

    @POST("api/v1/planes/{planId}/ejercicios")
    Call<PlanResponseDto> añadirEjercicioAlPlan(@Path("planId") Long planId, @Body List<DetallePlanRequestDto> dto);



    // --- NUEVOS ENDPOINTS PARA ENTRENAMIENTOS ---
    @GET("api/v1/entrenamientos/usuario/{usuarioId}")
    Call<ApiResponseDto<List<EntrenamientoResponseDto>>> getEntrenamientosUsuario(@Path("usuarioId") Long usuarioId);

    @POST("api/v1/entrenamientos")
    Call<ApiResponseDto<EntrenamientoResponseDto>> crearEntrenamiento(@Body EntrenamientoRequestDto request);

    @DELETE("api/v1/entrenamientos/{id}")
    Call<ApiResponseDto<Void>> eliminarEntrenamiento(@Path("id") Long id);

    // --- NUEVOS ENDPOINTS PARA DIETAS (CATÁLOGO) ---
    @GET("api/v1/dietas")
    Call<ApiResponseDto<List<DietaResponseDto>>> listarDietas();

    @GET("api/v1/dietas/{id}")
    Call<ApiResponseDto<DietaResponseDto>> obtenerDietaPorId(@Path("id") Long id);

    // --- NUEVOS ENDPOINTS PARA NUTRICIÓN (PLANES PERSONALIZADOS) ---
    @POST("api/v1/nutricion/generar-plan")
    Call<ApiResponseDto<NutricionResponseDto>> generarPlanNutricional(@Body PerfilFisicoRequest request);
    @GET("api/v1/nutricion/ultimo-plan/{usuarioId}")
    Call<ApiResponseDto<NutricionResponseDto>> obtenerUltimoPlan(@Path("usuarioId") Long usuarioId);

    @GET("api/v1/perfil/usuario/{usuarioId}")
    Call<ApiResponseDto<PerfilResponseDto>> getPerfilByUsuarioId(@Path("usuarioId") Long usuarioId);
    @GET("api/v1/nutricion/plan/{id}")
    Call<ApiResponseDto<NutricionResponseDto>> obtenerPlanPorId(@Path("id") Long id);

    @GET("api/v1/nutricion/historial/{usuarioId}")
    Call<ApiResponseDto<PageResponseDto<NutricionResponseDto>>> obtenerHistorialNutricional(
            @Path("usuarioId") Long usuarioId,
            @Query("page") int page,
            @Query("size") int size
    );

    @PATCH("api/v1/nutricion/plan/{id}/nombre")
    Call<ApiResponseDto<NutricionResponseDto>> actualizarNombrePlan(@Path("id") Long planId, @Query("nombre") String nombre);

    @POST("api/v1/nutricion/guardar-plan/{usuarioId}")
    Call<ApiResponseDto<NutricionResponseDto>> guardarPlan(@Path("usuarioId") Long usuarioId, @Body NutricionResponseDto plan);

    // --- NUEVOS ENDPOINTS PARA BÚSQUEDA DE ALIMENTOS (FATSECRET) ---
    @GET("api/v1/nutricion/buscar")
    Call<ApiResponseDto<String>> buscarAlimentos(@Query("query") String query);

    // --- ENDPOINTS WGER (EJERCICIOS EXTERNOS) ---
    @GET("api/v1/wger/ejercicios")
    Call<ApiResponseDto<com.example.skynet.data.remote.dto.WgerEjerciciosResponseDto>> getWgerEjercicios(
            @Query("page") Integer page,
            @Query("size") Integer size,
            @Query("language") String language,
            @Query("muscleId") Integer muscleId,
            @Query("categoryId") Integer categoryId
    );

    @GET("api/v1/wger/musculos")
    Call<ApiResponseDto<List<com.example.skynet.data.remote.dto.WgerMusculoDto>>> getWgerMusculos();

    @GET("api/v1/wger/categorias")
    Call<ApiResponseDto<List<com.example.skynet.data.remote.dto.WgerCategoriaDto>>> getWgerCategorias();

    @PATCH("api/v1/usuarios/{id}/asignar-entrenador")
    Call<ApiResponseDto<Void>> asignarRolEntrenador(@Path("id") Long id);

    @DELETE("api/v1/usuarios/{id}/quitar-entrenador")
    Call<ApiResponseDto<Void>> quitarRolEntrenador(@Path("id") Long id);

    // --- NUEVO ENDPOINT PARA HISTORIAL DE CHAT ---
    @GET("api/v1/chat/historial")
    Call<List<com.example.skynet.ui.social.SocialFragment.ChatMessageDto>> getHistorialChat();

    @GET("api/v1/chat/historial/{ticketId}")
    Call<List<com.example.skynet.ui.social.SocialFragment.ChatMessageDto>> getHistorialTicket(@Path("ticketId") String ticketId);

    // --- NOTIFICACIONES ---
    @GET("api/notificaciones/usuario/{usuarioId}")
    Call<List<NotificacionResponseDto>> listarNotificaciones(@Path("usuarioId") Long usuarioId);

    @PATCH("api/notificaciones/{id}/leer")
    Call<Void> marcarNotificacionComoLeida(@Path("id") Long id);

    @GET("api/notificaciones/usuario/{usuarioId}/count")
    Call<Long> contarNotificacionesNoLeidas(@Path("usuarioId") Long usuarioId);

    @DELETE("api/notificaciones/{id}")
    Call<Void> eliminarNotificacion(@Path("id") Long id);

    // --- ACCESOS (QR Y AFORO) ---
    @GET("api/v1/accesos/generar-qr")
    Call<ApiResponseDto<String>> generarTokenQr();

    @GET("api/v1/accesos/aforo")
    Call<ApiResponseDto<Long>> obtenerAforoActual();

    @GET("api/v1/accesos/historial")
    Call<ApiResponseDto<List<AccesoResponseDto>>> obtenerHistorialAccesos();

    @POST("api/v1/accesos/procesar-qr")
    Call<ApiResponseDto<AccesoResponseDto>> procesarQr(@Query("token") String token);

    @POST("api/v1/accesos/validar-entrada")
    Call<ApiResponseDto<Void>> validarEntrada(@Body Map<String, String> body);

    @POST("api/v1/accesos/validar-salida")
    Call<ApiResponseDto<Void>> validarSalida(@Body Map<String, String> body);

    // --- TICKETS DE SOPORTE ---
    @POST("api/v1/tickets/usuario/{usuarioId}")
    Call<ApiResponseDto<TicketResponseDto>> crearTicket(@Path("usuarioId") Long usuarioId, @Body TicketRequestDto dto);

    @GET("api/v1/tickets/usuario/{usuarioId}")
    Call<ApiResponseDto<List<TicketResponseDto>>> verMisTickets(@Path("usuarioId") Long usuarioId);

    @GET("api/v1/tickets")
    Call<ApiResponseDto<List<TicketResponseDto>>> verTodosLosTickets();

    @PATCH("api/v1/tickets/{id}/estado")
    Call<ApiResponseDto<TicketResponseDto>> resolverTicket(@Path("id") Long id, @Query("nuevoEstado") String nuevoEstado);
}
