package com.example.skynet.ui.ejercicios;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PruebaEjercicioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cargamos tu fragmento de ejercicios directamente
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new EjerciciosFragment())
                .commit();
    }
}