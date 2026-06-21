package com.example.skynet.ui.rutinas;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.EntrenamientoResponseDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class GestionarEjerciciosPlanActivity extends AppCompatActivity {

    private Long planId;
    private PlanDetalleViewModel viewModel;
    private DetallePlanAdapter adapter;
    private List<EntrenamientoResponseDto> catalogoActual = new ArrayList<>();
    private View layoutVacio;

    // Vistas del Header
    private TextView tvNombreHeader, tvNivel, tvObjetivo, tvDescripcion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_ejercicios_plan);

        planId = getIntent().getLongExtra("PLAN_ID", -1);
        String nombrePlan = getIntent().getStringExtra("PLAN_NOMBRE");
        String nivelPlan = getIntent().getStringExtra("PLAN_NIVEL");
        String objetivoPlan = getIntent().getStringExtra("PLAN_OBJETIVO");
        String descripcionPlan = getIntent().getStringExtra("PLAN_DESCRIPCION");

        // 1. Configurar Barra Superior
        Toolbar toolbar = findViewById(R.id.toolbarGestionPlan);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(""); // Título vacío para usar el header personalizado
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 2. Inicializar Vistas del Header
        tvNombreHeader = findViewById(R.id.tvNombrePlanHeader);
        tvNivel = findViewById(R.id.tvNivelPlan);
        tvObjetivo = findViewById(R.id.tvObjetivoPlan);
        tvDescripcion = findViewById(R.id.tvDescripcionPlan);

        if (tvNombreHeader != null) tvNombreHeader.setText(nombrePlan != null ? nombrePlan : "Gestión del Plan");
        if (tvNivel != null) tvNivel.setText(nivelPlan != null ? nivelPlan : "PRINCIPIANTE");
        if (tvObjetivo != null) tvObjetivo.setText(objetivoPlan != null ? objetivoPlan.replace("_", " ") : "GENERAL");
        if (tvDescripcion != null) tvDescripcion.setText(descripcionPlan != null && !descripcionPlan.equals("null") ? descripcionPlan : "Sin descripción disponible para este plan.");

        // 3. Inicializar Lista
        layoutVacio = findViewById(R.id.layoutVacioPlan);
        RecyclerView rv = findViewById(R.id.rvDetallesPlan);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DetallePlanAdapter(new ArrayList<>(), detalle -> {
            viewModel.eliminarEjercicioDelPlan(detalle.getId(), planId);
        });
        rv.setAdapter(adapter);

        // 3. ViewModel y Observadores
        viewModel = new ViewModelProvider(this).get(PlanDetalleViewModel.class);

        viewModel.getEjerciciosPlan().observe(this, lista -> {
            adapter.updateList(lista);
            // Si la lista está vacía, mostramos un mensaje para que no sea solo una pantalla azul
            if (lista == null || lista.isEmpty()) {
                if (layoutVacio != null) layoutVacio.setVisibility(View.VISIBLE);
            } else {
                if (layoutVacio != null) layoutVacio.setVisibility(View.GONE);
            }
        });

        viewModel.getCatalogoEntrenamientos().observe(this, entrenamientos -> {
            this.catalogoActual = entrenamientos;
        });

        // 4. Cargar datos iniciales
        if (planId != -1) {
            viewModel.cargarEjerciciosDelPlan(planId);
            viewModel.cargarCatalogoParaSpinner();
        }

        FloatingActionButton fabAdd = findViewById(R.id.fabAddEjercicioDetalle);
        fabAdd.setOnClickListener(v -> mostrarDialogoAñadir());
    }

    private void mostrarDialogoAñadir() {
        // Validación: No abrir si el catálogo no ha cargado
        if (catalogoActual == null || catalogoActual.isEmpty()) {
            Toast.makeText(this, "Cargando catálogo, intenta de nuevo...", Toast.LENGTH_SHORT).show();
            viewModel.cargarCatalogoParaSpinner();
            return;
        }

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_anadir_ejercicio_plan);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // --- SOLUCIÓN AL NULLPOINTER: Buscar dentro de 'dialog' ---
        Spinner spinnerEjercicios = dialog.findViewById(R.id.spinnerEjerciciosCatalogo);
        Spinner spinnerDias = dialog.findViewById(R.id.spinnerDiaSemana);
        EditText etOrden = dialog.findViewById(R.id.etOrdenPlan);
        Button btnGuardar = dialog.findViewById(R.id.btnGuardarDetallePlan);

        // Llenar Spinner de Ejercicios con estilo legible
        List<String> nombres = new ArrayList<>();
        for (EntrenamientoResponseDto e : catalogoActual) nombres.add(e.getNombre());
        ArrayAdapter<String> adapterEj = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        adapterEj.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEjercicios.setAdapter(adapterEj);

        // Llenar Spinner de Días con estilo legible
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        ArrayAdapter<String> adapterDi = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dias);
        adapterDi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDias.setAdapter(adapterDi);

        btnGuardar.setOnClickListener(v -> {
            String ordenStr = etOrden.getText().toString();
            if (ordenStr.isEmpty()) {
                etOrden.setError("Introduce el orden");
                return;
            }

            int pos = spinnerEjercicios.getSelectedItemPosition();
            Long idEntrenamiento = catalogoActual.get(pos).getId();
            int dia = spinnerDias.getSelectedItemPosition() + 1;
            int orden = Integer.parseInt(ordenStr);

            viewModel.añadirEjercicioAlPlan(planId, idEntrenamiento, dia, orden);
            dialog.dismiss();
        });

        dialog.show();
    }
}