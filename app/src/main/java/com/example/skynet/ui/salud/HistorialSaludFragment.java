package com.example.skynet.ui.salud;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skynet.R;

import java.util.ArrayList;

public class HistorialSaludFragment extends Fragment {

    private RecyclerView rvHistorial;
    private HistorialAdapter adapter;
    private SaludViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historial_salud, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(SaludViewModel.class);

        ImageView btnBack = view.findViewById(R.id.btnBackHistorial);
        rvHistorial = view.findViewById(R.id.rvHistorial);

        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).mostrarHome();
            } else {
                getParentFragmentManager().popBackStack();
            }
        });

        setupRecyclerView();
        observarViewModel();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1L);
        
        if (userId != -1L) {
            viewModel.cargarHistorial(userId);
        }
    }

    private void setupRecyclerView() {
        adapter = new HistorialAdapter(new ArrayList<>());
        rvHistorial.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistorial.setAdapter(adapter);
    }

    private void observarViewModel() {
        viewModel.getHistorialSalud().observe(getViewLifecycleOwner(), historial -> {
            if (historial != null) {
                adapter.setHistorial(historial);
            }
        });
    }
}
