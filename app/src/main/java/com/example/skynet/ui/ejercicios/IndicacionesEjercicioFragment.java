package com.example.skynet.ui.ejercicios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.skynet.R;

public class IndicacionesEjercicioFragment extends Fragment {

    public static IndicacionesEjercicioFragment newInstance(Ejercicio ejercicio) {
        IndicacionesEjercicioFragment fragment = new IndicacionesEjercicioFragment();
        Bundle args = new Bundle();
        args.putParcelable("ejercicio", ejercicio);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indicaciones_ejercicio, container, false);
        
        TextView tvInstrucciones = view.findViewById(R.id.tvInstrucciones);
        
        if (getArguments() != null) {
            Ejercicio ejercicio = getArguments().getParcelable("ejercicio");
            if (ejercicio != null && ejercicio.getDescripcion() != null) {
                tvInstrucciones.setText(ejercicio.getDescripcion());
            }
        }
        
        return view;
    }
}