package com.example.skynet.ui.ejercicios;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.example.skynet.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DetalleEjercicioActivity extends AppCompatActivity {

    private Ejercicio ejercicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ejercicio);

        ejercicio = getIntent().getParcelableExtra("ejercicio");
        if (ejercicio == null) {
            finish();
            return;
        }

        setupToolbar();
        setupHeader();
        setupViewPager();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupHeader() {
        TextView tvTitulo = findViewById(R.id.tvTituloEjercicio);
        ImageView ivImagen = findViewById(R.id.ivDetalleEjercicio);

        tvTitulo.setText(ejercicio.getNombre());
        
        if (ejercicio.getUrlVideo() != null && !ejercicio.getUrlVideo().isEmpty()) {
            Glide.with(this)
                .load(ejercicio.getUrlVideo())
                .placeholder(R.drawable.ic_workout)
                .error(R.drawable.ic_workout)
                .centerCrop()
                .into(ivImagen);
        }
    }

    private void setupViewPager() {
        TabLayout tabLayout = findViewById(R.id.tabLayoutEjercicio);
        ViewPager2 viewPager = findViewById(R.id.viewPagerEjercicio);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return ResumenEjercicioFragment.newInstance(ejercicio);
                    case 1: return HistorialEjercicioFragment.newInstance(ejercicio);
                    case 2: return IndicacionesEjercicioFragment.newInstance(ejercicio);
                    default: return ResumenEjercicioFragment.newInstance(ejercicio);
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Resumen"); break;
                case 1: tab.setText("Historial"); break;
                case 2: tab.setText("Indicaciones"); break;
            }
        }).attach();
    }
}