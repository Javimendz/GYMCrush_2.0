package com.example.skynet.ui.contacto;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.skynet.R;
import com.example.skynet.ui.support.ChatSupportFragment;
import com.example.skynet.ui.support.TicketListFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ContactoFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacto, container, false);

        // Botón volver
        view.findViewById(R.id.btnBackContacto).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // Botón AI Support
        view.findViewById(R.id.btnAiSupport).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ChatSupportFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Botón Gestión de Tickets
        View btnTickets = view.findViewById(R.id.btnMisTickets);
        TextView tvTicketsTitle = view.findViewById(R.id.tvTicketsTitle);
        TextView tvTicketsSubtitle = view.findViewById(R.id.tvTicketsSubtitle);

        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        java.util.Set<String> roles = prefs.getStringSet("roles", new java.util.HashSet<>());
        boolean isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");

        if (isAdmin) {
            tvTicketsTitle.setText("Panel de Tickets");
            tvTicketsSubtitle.setText("Gestionar incidencias de usuarios");
        }

        btnTickets.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new TicketListFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Inicializar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_contacto);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Configurar botones de redes sociales
        setupSocialButtons(view);

        return view;
    }

    private void setupSocialButtons(View view) {
        // WhatsApp
        view.findViewById(R.id.icon_whatsapp).setOnClickListener(v -> {
            String url = "https://api.whatsapp.com/send?phone=34611111111";
            abrirIntent(url, "WhatsApp no instalado");
        });

        // Instagram
        view.findViewById(R.id.icon_instagram).setOnClickListener(v -> {
            String url = "https://www.instagram.com/gymcrush_oficial";
            abrirIntent(url, "Instagram no instalado");
        });

        // Teléfono
        view.findViewById(R.id.icon_phone).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:600000000"));
            startActivity(intent);
        });

        // Email
        view.findViewById(R.id.icon_email).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:contacto@gymcrush.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta GYMCrush");
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "No hay aplicación de correo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirIntent(String url, String mensajeError) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(getContext(), mensajeError, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        LatLng murciaCentro = new LatLng(37.9870, -1.1300);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setCompassEnabled(true);

        map.addMarker(new MarkerOptions()
                .position(murciaCentro)
                .title("GYMCrush Center")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(murciaCentro)
                .zoom(15.5f)
                .tilt(45)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
