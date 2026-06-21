package com.example.skynet;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.skynet.data.remote.RetrofitClient;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;

public class skynet extends Application implements OnMapsSdkInitializedCallback {

    private static skynet instance;

    public static skynet getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Forzar modo oscuro global para evitar destellos blancos en diálogos del sistema (como Google Sign-In)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        
        try {
            // Inicializar Retrofit con el contexto de la aplicación
            RetrofitClient.init(this);
        } catch (Exception e) {
            // Log or handle initialization errors
        }
    }

    @Override
    public void onMapsSdkInitialized(MapsInitializer.Renderer renderer) {
        // Manejar inicialización de mapas si es necesario
    }
}
