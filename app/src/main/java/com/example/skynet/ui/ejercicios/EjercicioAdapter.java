package com.example.skynet.ui.ejercicios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.TutorialResponseDto;
import com.example.skynet.ui.rutinas.RepositorioRutinas;

import java.util.List;

public class EjercicioAdapter extends RecyclerView.Adapter<EjercicioAdapter.ViewHolder> {

    private List<TutorialResponseDto> listaTutoriales;
    private List<Ejercicio> listaEjercicios;
    private String routineName;
    private boolean mostrarReps = false;
    private boolean isAdmin = false;
    private long currentUserId = -1;
    private OnEjercicioAdminListener adminListener;

    public interface OnEjercicioAdminListener {
        void onEdit(TutorialResponseDto tutorial);
        void onDelete(TutorialResponseDto tutorial);
    }

    public EjercicioAdapter(List<TutorialResponseDto> listaTutoriales) {
        this.listaTutoriales = listaTutoriales;
    }

    public EjercicioAdapter(List<TutorialResponseDto> listaTutoriales, boolean isAdmin, long currentUserId, OnEjercicioAdminListener listener) {
        this.listaTutoriales = listaTutoriales;
        this.isAdmin = isAdmin;
        this.currentUserId = currentUserId;
        this.adminListener = listener;
    }

    public EjercicioAdapter(List<Ejercicio> listaEjercicios, boolean mostrarReps, String routineName) {
        this.listaEjercicios = listaEjercicios;
        this.mostrarReps = mostrarReps;
        this.routineName = routineName;
    }

    public void setListaEjercicios(List<Ejercicio> nuevaLista) {
        this.listaEjercicios = nuevaLista;
    }

    public EjercicioAdapter(List<Ejercicio> listaEjercicios, boolean mostrarReps) {
        this.listaEjercicios = listaEjercicios;
        this.mostrarReps = mostrarReps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.ejercicio_item_biblioteca, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nombre = "", categoria = "", descripcion = "", duracion = "", urlVideo = "";
        String musculoTag = ""; // Nueva variable para el tag de músculo/dificultad
        Long id = -1L;
        int imagenRes = R.drawable.logo;

        // Variables para las etiquetas del reproductor
        String subtituloArmado = "";
        Integer duracionMinima = null;
        String etiquetaAzul = "";

        if (listaTutoriales != null) {
            TutorialResponseDto t = listaTutoriales.get(position);
            nombre = t.getTitulo();
            categoria = t.getNombreCategoria();
            descripcion = t.getDescripcion();

            // Corregimos la duración para evitar "null min" o "0 min"
            if (t.getDuracionMin() != null && t.getDuracionMin() > 0) {
                duracion = t.getDuracionMin() + " min";
                duracionMinima = t.getDuracionMin();
            } else {
                duracion = null;
                duracionMinima = 0;
            }

            urlVideo = t.getUrlVideo();
            id = t.getId();
            musculoTag = t.getMusculoObjetivo() != null ? t.getMusculoObjetivo().toUpperCase() : "GENERAL";

            holder.txtRepsSeries.setVisibility(View.GONE);
            holder.cbCompletado.setVisibility(View.GONE);

            // ARMAMOS LOS DATOS EXTRA PARA EL REPRODUCTOR
            String musculo = t.getMusculoObjetivo() != null ? t.getMusculoObjetivo() : "";
            String equipo = t.getEquipamiento() != null ? t.getEquipamiento() : "";
            if (!musculo.isEmpty() && !equipo.isEmpty()) {
                subtituloArmado = musculo + " - " + equipo;
            } else {
                subtituloArmado = musculo + equipo; // Concatena el que quede si falta alguno
            }

            duracionMinima = t.getDuracionMin();

            if (t.getNombreCategoria() != null && !t.getNombreCategoria().isEmpty()) {
                etiquetaAzul = "Categoría: " + t.getNombreCategoria().toUpperCase();
            }

        } else {
            Ejercicio e = listaEjercicios.get(position);
            nombre = e.getNombre();
            categoria = e.getCategoria() != null ? e.getCategoria() : "Personalizado";
            descripcion = e.getDescripcion() != null ? e.getDescripcion() : "Sin descripción";
            duracion = e.getDuracion(); // Ej: "20 min" o "20"
            urlVideo = e.getUrlVideo();
            id = e.getId();

            String dificultad = e.getDificultad() != null ? e.getDificultad() : "MEDIA";
            musculoTag = dificultad.toUpperCase();

            // DATOS PARA EL REPRODUCTOR (Basado en Ejercicio)
            subtituloArmado = categoria;
            
            // Intentamos parsear la duración para el tag del reproductor
            if (duracion != null && !duracion.equalsIgnoreCase("n/a")) {
                try {
                    String cleanDur = duracion.toLowerCase().replace("min", "").trim();
                    duracionMinima = Integer.parseInt(cleanDur);
                } catch (Exception ex) {
                    duracionMinima = 0;
                }
            } else {
                duracionMinima = 0;
                duracion = null; // Para que se oculte el tag
            }

            etiquetaAzul = "Intensidad: " + dificultad.toUpperCase();

            if (e.getImagenResId() != 0) {
                imagenRes = e.getImagenResId();
            }

            if (mostrarReps) {
                holder.txtRepsSeries.setVisibility(View.VISIBLE);
                holder.txtRepsSeries.setText(e.getSeries() + " Series x " + e.getRepeticiones() + " Reps");
                holder.cbCompletado.setVisibility(View.VISIBLE);

                // Manejar el estado del checkbox sin disparar listeners recursivos
                holder.cbCompletado.setOnCheckedChangeListener(null);
                holder.cbCompletado.setChecked(e.isSeleccionado());
                holder.cbCompletado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    e.setSeleccionado(isChecked);
                    // Guardar progreso en el repositorio para que persista
                    RepositorioRutinas.guardarDatosEnLocal(buttonView.getContext());
                });
            } else {
                holder.txtRepsSeries.setVisibility(View.GONE);
                holder.cbCompletado.setVisibility(View.GONE);
            }
        }

        holder.txtNombre.setText(nombre);
        holder.txtCategoria.setText(categoria);
        holder.txtDescripcion.setText(descripcion);
        holder.txtDificultad.setText("Duración: " + duracion); // Mantenemos el oculto por compatibilidad
        
        // Actualizamos los nuevos Tags visuales
        if (duracion != null && !duracion.isEmpty() && !duracion.equalsIgnoreCase("null") && !duracion.equalsIgnoreCase("n/a") && !duracion.contains("null")) {
            holder.layoutDuracionTag.setVisibility(View.VISIBLE);
            holder.tvDuracionTag.setText(duracion);
        } else {
            holder.layoutDuracionTag.setVisibility(View.GONE);
        }

        if (musculoTag != null && !musculoTag.isEmpty() && !musculoTag.equals("null")) {
            holder.layoutMusculoTag.setVisibility(View.VISIBLE);
            holder.tvMusculoTag.setText(musculoTag);
        } else {
            holder.layoutMusculoTag.setVisibility(View.GONE);
        }

        // Tag Global vs Personal vs Clásico
        if (listaTutoriales != null) {
            TutorialResponseDto t = listaTutoriales.get(position);
            holder.layoutGlobalTag.setVisibility(View.VISIBLE);
            if (t.isEsGlobal()) {
                holder.tvGlobalTagItem.setText("OFICIAL");
                holder.layoutGlobalTag.setBackgroundResource(R.drawable.bg_chip_global);
            } else if (t.getUsuarioId() != null && t.getUsuarioId() == currentUserId) {
                holder.tvGlobalTagItem.setText("EJERCICIO CLÁSICO");
                holder.layoutGlobalTag.setBackgroundResource(R.drawable.bg_chip_personal); // Podríamos usar uno específico si existiera
            } else {
                holder.tvGlobalTagItem.setText("PERSONAL");
                holder.layoutGlobalTag.setBackgroundResource(R.drawable.bg_chip_personal);
            }
        } else if (listaEjercicios != null) {
            Ejercicio e = listaEjercicios.get(position);
            holder.layoutGlobalTag.setVisibility(View.VISIBLE);
            if (e.isEsGlobal()) {
                holder.tvGlobalTagItem.setText("OFICIAL");
                holder.layoutGlobalTag.setBackgroundResource(R.drawable.bg_chip_global);
            } else if (!e.isEsGlobal() && e.getId() != null) { // Para Ejercicio (clase local) es más ambiguo
                holder.tvGlobalTagItem.setText("EJERCICIO CLÁSICO");
                holder.layoutGlobalTag.setBackgroundResource(R.drawable.bg_chip_personal);
            } else {
                holder.tvGlobalTagItem.setText("PERSONAL");
                holder.layoutGlobalTag.setBackgroundResource(R.drawable.bg_chip_personal);
            }
        } else {
            holder.layoutGlobalTag.setVisibility(View.GONE);
        }

        boolean isOwner = false;
        if (listaTutoriales != null) {
            TutorialResponseDto t = listaTutoriales.get(position);
            isOwner = t.getUsuarioId() != null && t.getUsuarioId() == currentUserId;
        }
        boolean canManage = isAdmin || isOwner;

        if (canManage && listaTutoriales != null) {
            holder.imgMenu.setVisibility(View.VISIBLE);
            holder.imgMenu.setOnClickListener(v -> {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(v.getContext(), v);
                popup.getMenu().add("Editar");
                popup.getMenu().add("Eliminar");
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Editar")) {
                        if (adminListener != null) adminListener.onEdit(listaTutoriales.get(position));
                    } else if (item.getTitle().equals("Eliminar")) {
                        if (adminListener != null) adminListener.onDelete(listaTutoriales.get(position));
                    }
                    return true;
                });
                popup.show();
            });
        } else {
            holder.imgMenu.setVisibility(View.GONE);
        }

        if (urlVideo != null && !urlVideo.isEmpty()) {
            String urlToLoad = urlVideo;
            if (urlToLoad.contains("youtube.com") || urlToLoad.contains("youtu.be")) {
                String videoId = "";
                if (urlToLoad.contains("v=")) {
                    videoId = urlToLoad.split("v=")[1].split("&")[0];
                } else if (urlToLoad.contains("youtu.be/")) {
                    videoId = urlToLoad.split("youtu.be/")[1].split("\\?")[0];
                }
                if (!videoId.isEmpty()) {
                    urlToLoad = "https://img.youtube.com/vi/" + videoId + "/0.jpg";
                }
            }

            Glide.with(holder.itemView.getContext())
                    .load(urlToLoad)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .centerCrop()
                    .into(holder.imgFoto);
        } else if (imagenRes != R.drawable.logo) {
            holder.imgFoto.setImageResource(imagenRes);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load("file:///android_asset/login.jpg")
                    .centerCrop()
                    .into(holder.imgFoto);
        }

        // VARIABLES FINALES PARA ENVIAR AL ONCLICK
        final Long finalId = id;
        final String finalUrl = urlVideo;
        final String finalNombre = nombre;
        final String finalDesc = descripcion;
        final String finalSubtitulo = subtituloArmado;
        final Integer finalDuracionMin = duracionMinima;
        final String finalEtiquetaAzul = etiquetaAzul;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.Intent intent = new android.content.Intent(v.getContext(), com.example.skynet.ui.rutinas.DetalleRutinaActivity.class);
                intent.putExtra("NOMBRE_RUTINA", finalNombre);
                intent.putExtra("URL_VIDEO", finalUrl);
                intent.putExtra("DESCRIPCION", finalDesc);
                intent.putExtra("DURACION", finalDuracionMin);
                intent.putExtra("INTENSIDAD", finalEtiquetaAzul);
                intent.putExtra("TUTORIAL_ID", finalId);
                intent.putExtra("SUBTITULO", finalSubtitulo);
                intent.putExtra("PARENT_ROUTINE_NAME", routineName); // Pasar el nombre de la rutina padre
                intent.putExtra("ES_EJERCICIO_BIBLIOTECA", true); // Bandera para diferenciar
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listaTutoriales != null) return listaTutoriales.size();
        if (listaEjercicios != null) return listaEjercicios.size();
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCategoria, txtDescripcion, txtDificultad, txtRepsSeries;
        TextView tvDuracionTag, tvMusculoTag, tvGlobalTagItem;
        ImageView imgFoto, imgMenu;
        CheckBox cbCompletado;
        View layoutDuracionTag, layoutMusculoTag, layoutGlobalTag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.tvNombreEj);
            txtCategoria = itemView.findViewById(R.id.tvCatEj);
            txtDescripcion = itemView.findViewById(R.id.tvDescripcionEj);
            txtDificultad = itemView.findViewById(R.id.tvDificultad);
            txtRepsSeries = itemView.findViewById(R.id.tvRepsSeries);
            tvDuracionTag = itemView.findViewById(R.id.tvDuracionTagItem);
            tvMusculoTag = itemView.findViewById(R.id.tvMusculoTagItem);
            tvGlobalTagItem = itemView.findViewById(R.id.tvGlobalTagItem);
            imgFoto = itemView.findViewById(R.id.imgEjercicioIcono);
            cbCompletado = itemView.findViewById(R.id.cbCompletado);
            imgMenu = itemView.findViewById(R.id.imgMenuEjercicio);
            layoutDuracionTag = itemView.findViewById(R.id.layoutDuracionTag);
            layoutMusculoTag = itemView.findViewById(R.id.layoutMusculoTag);
            layoutGlobalTag = itemView.findViewById(R.id.layoutGlobalTag);
        }
    }
}