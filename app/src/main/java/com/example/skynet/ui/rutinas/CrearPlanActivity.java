package com.example.skynet.ui.rutinas;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.skynet.R;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.PlanRequestDto;
import com.example.skynet.data.remote.dto.PlanResponseDto;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearPlanActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etDescripcion;
    private Spinner spinnerObjetivo, spinnerNivel;
    private Button btnGuardar;
    private Long planId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_plan);

        etNombre = findViewById(R.id.etNombrePlan);
        etDescripcion = findViewById(R.id.etDescripcionPlan);
        spinnerObjetivo = findViewById(R.id.spinnerObjetivo);
        spinnerNivel = findViewById(R.id.spinnerNivel);
        btnGuardar = findViewById(R.id.btnGuardarPlan);

        setupSpinners();

        planId = getIntent().getLongExtra("PLAN_ID", -1L);
        if (planId != -1L) {
            String nombre = getIntent().getStringExtra("PLAN_NOMBRE");
            String descripcion = getIntent().getStringExtra("PLAN_DESCRIPCION");
            String objetivo = getIntent().getStringExtra("PLAN_OBJETIVO");
            String nivel = getIntent().getStringExtra("PLAN_NIVEL");

            etNombre.setText(nombre);
            etDescripcion.setText(descripcion != null && !descripcion.equals("null") ? descripcion : "");
            
            if (objetivo != null) {
                int pos = getIndex(spinnerObjetivo, objetivo);
                spinnerObjetivo.setSelection(pos);
            }
            if (nivel != null) {
                int pos = getIndex(spinnerNivel, nivel);
                spinnerNivel.setSelection(pos);
            }
            
            btnGuardar.setText("Actualizar Plan");
        }

        btnGuardar.setOnClickListener(v -> guardarPlan());
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    private void setupSpinners() {
        String[] objetivos = {"GANAR_MUSCULO", "PERDER_PESO", "MANTENER_FORMA"};
        String[] niveles = {"PRINCIPIANTE", "INTERMEDIO", "AVANZADO"};

        // Adaptador para Objetivo
        ArrayAdapter<String> adapterObj = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, objetivos) {
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View v = super.getView(position, convertView, parent);
                if (v instanceof android.widget.TextView) {
                    ((android.widget.TextView) v).setTextColor(android.graphics.Color.WHITE);
                }
                return v;
            }

            @Override
            public android.view.View getDropDownView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View v = super.getDropDownView(position, convertView, parent);
                v.setBackgroundColor(android.graphics.Color.parseColor("#1A2238")); // card_surface
                if (v instanceof android.widget.TextView) {
                    ((android.widget.TextView) v).setTextColor(android.graphics.Color.WHITE);
                }
                return v;
            }
        };
        adapterObj.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerObjetivo.setAdapter(adapterObj);

        // Adaptador para Nivel
        ArrayAdapter<String> adapterNiv = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, niveles) {
            @Override
            public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View v = super.getView(position, convertView, parent);
                if (v instanceof android.widget.TextView) {
                    ((android.widget.TextView) v).setTextColor(android.graphics.Color.WHITE);
                }
                return v;
            }

            @Override
            public android.view.View getDropDownView(int position, android.view.View convertView, android.view.ViewGroup parent) {
                android.view.View v = super.getDropDownView(position, convertView, parent);
                v.setBackgroundColor(android.graphics.Color.parseColor("#1A2238")); // card_surface
                if (v instanceof android.widget.TextView) {
                    ((android.widget.TextView) v).setTextColor(android.graphics.Color.WHITE);
                }
                return v;
            }
        };
        adapterNiv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNivel.setAdapter(adapterNiv);
    }

    private void guardarPlan() {
        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String objetivo = spinnerObjetivo.getSelectedItem().toString();
        String nivel = spinnerNivel.getSelectedItem().toString();

        if (nombre.isEmpty()) {
            etNombre.setError("Campo obligatorio");
            return;
        }

        PlanRequestDto dto = new PlanRequestDto(nombre, descripcion, objetivo, nivel);

        android.content.SharedPreferences prefs = getSharedPreferences("DatosUsuario", android.content.Context.MODE_PRIVATE);
        java.util.Set<String> roles = prefs.getStringSet("roles", new java.util.HashSet<>());
        boolean isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");
        dto.setEsGlobal(isAdmin);

        if (planId != -1L) {
            RetrofitClient.getApiService().actualizarPlan(planId, dto).enqueue(new Callback<ApiResponseDto<PlanResponseDto>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<PlanResponseDto>> call, Response<ApiResponseDto<PlanResponseDto>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CrearPlanActivity.this, "Plan actualizado correctamente", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(CrearPlanActivity.this, "Error al actualizar el plan", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseDto<PlanResponseDto>> call, Throwable t) {
                    Toast.makeText(CrearPlanActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            RetrofitClient.getApiService().crearPlan(dto).enqueue(new Callback<ApiResponseDto<PlanResponseDto>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<PlanResponseDto>> call, Response<ApiResponseDto<PlanResponseDto>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CrearPlanActivity.this, "Plan creado correctamente", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(CrearPlanActivity.this, "Error al crear el plan", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseDto<PlanResponseDto>> call, Throwable t) {
                    Toast.makeText(CrearPlanActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
