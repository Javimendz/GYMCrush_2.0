package com.example.skynet.ui.equipo;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.skynet.R;

public class EquipoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equipo, container, false);

        // Botón volver
        view.findViewById(R.id.btnBackEquipo).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Configurar los clicks para abrir el diálogo con la información real de los nombres
        view.findViewById(R.id.card_entrenador1).setOnClickListener(v -> 
            mostrarDialogoInfo("José García", "Especialista en Entrenamiento de Fuerza y Culturismo Natural.\n\nExperto en biomecánica aplicada y optimización metabólica.\nSu misión es llevar tu físico al siguiente nivel con disciplina y ciencia.")
        );

        view.findViewById(R.id.card_entrenadora1).setOnClickListener(v -> 
            mostrarDialogoInfo("María Brotons", "Especialista en Entrenamiento Funcional y Core Training.\n\nEnfocada en la mejora de la postura y el rendimiento deportivo.\nApasionada por el fitness y por ayudar a otros a alcanzar su mejor versión.")
        );

        view.findViewById(R.id.card_entrenador2).setOnClickListener(v -> 
            mostrarDialogoInfo("Javier Méndez", "Entrenador de CrossFit y especialista en Nutrición Deportiva.\n\nFormación avanzada en HIIT y entrenamientos de alta intensidad.\nExperto en la programación de rutinas para pérdida de grasa y ganancia muscular.")
        );

        view.findViewById(R.id.card_entrenadora2).setOnClickListener(v -> 
            mostrarDialogoInfo("Andrea", "Experta en Yoga, Flexibilidad y Salud Holística.\n\nCertificada internacionalmente en movilidad articular y reducción del estrés.\nSus clases te ayudarán a encontrar el equilibrio perfecto entre cuerpo y mente.")
        );

        return view;
    }

    private void mostrarDialogoInfo(String nombre, String descripcion) {
        if (getContext() == null) return;

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_entrenador_info);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvNombre = dialog.findViewById(R.id.tv_dialog_nombre);
        TextView tvDesc = dialog.findViewById(R.id.tv_dialog_descripcion);
        View btnCerrar = dialog.findViewById(R.id.btn_cerrar_dialog);

        tvNombre.setText(nombre);
        tvDesc.setText(descripcion);

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
