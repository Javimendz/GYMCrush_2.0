package com.example.skynet.ui.view;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.skynet.R;
import com.github.chrisbanes.photoview.PhotoView;

public class EspacioFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_espacio, container, false);

        // Configurar los clicks para el "Tour Virtual" (Zoom)
        setupClick(view.findViewById(R.id.cardMusculacion), R.drawable.musculacion);
        setupClick(view.findViewById(R.id.cardSpinning), R.drawable.spinning);
        setupClick(view.findViewById(R.id.cardCardio), R.drawable.cardio);
        setupClick(view.findViewById(R.id.cardPesoLibre), R.drawable.peso_libre);

        return view;
    }

    private void setupClick(View view, int imageResId) {
        if (view != null) {
            view.setOnClickListener(v -> mostrarImagenZoom(imageResId));
        }
    }

    private void mostrarImagenZoom(int imageResId) {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_tour_virtual);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

        PhotoView photoView = dialog.findViewById(R.id.photo_view);
        photoView.setImageResource(imageResId);

        // Botón para cerrar
        View btnCerrar = dialog.findViewById(R.id.btn_cerrar_tour);
        if (btnCerrar != null) {
            btnCerrar.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }
}
