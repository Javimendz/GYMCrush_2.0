package com.example.skynet.ui.rutinas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;

public class PruebaRutinasActivity extends AppCompatActivity {

    private RutinaGuardadaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Usamos el diseño del fragment que ya tienes hecho
        setContentView(R.layout.rutina_fragment_lista);

        RecyclerView rv = findViewById(R.id.rvMisRutinas);
        // Usamos MaterialButton porque es el que pusimos en el XML rediseñado
        View btnIrACrear = findViewById(R.id.btnIrACrear);

        // Configuramos la lista
        rv.setLayoutManager(new LinearLayoutManager(this));
        
        // CORRECCIÓN: Ahora el repositorio usa getRutinas() y devuelve objetos completos
        adapter = new RutinaGuardadaAdapter(RepositorioRutinas.getRutinas());
        rv.setAdapter(adapter);

        // El botón nos lleva a tu pantalla de "Nueva Rutina"
        btnIrACrear.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearRutinaActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cuando vuelves de crear la rutina, la lista se actualiza
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
