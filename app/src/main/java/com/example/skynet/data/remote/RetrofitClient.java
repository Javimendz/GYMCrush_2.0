package com.example.skynet.data.remote;

import com.example.skynet.Config;
import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = Config.BASE_URL;
    private static Retrofit retrofit = null;
    private static Context context;
    private static boolean isSessionExpiredHandled = false;

    public static void init(Context ctx) {
        context = ctx.getApplicationContext();
    }

    public static ApiService getApiService() {
        if (context == null) {
            throw new IllegalStateException("RetrofitClient must be initialized in Application.onCreate() before use.");
        }
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        
                        // Obtener el token de SharedPreferences
                        SharedPreferences prefs = context.getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
                        String token = prefs.getString("auth_token", "");

                        Request.Builder requestBuilder = original.newBuilder();
                        if (!token.isEmpty()) {
                            requestBuilder.header("Authorization", "Bearer " + token);
                            // Si enviamos un token, permitimos que se maneje una posible expiración
                            isSessionExpiredHandled = false;
                        }
                        
                        okhttp3.Response response = chain.proceed(requestBuilder.build());

                        // Manejar errores de autorización (401 o 403)
                        if (response.code() == 401 && !original.url().encodedPath().contains("auth/login")) {                            synchronized (RetrofitClient.class) {
                                if (!isSessionExpiredHandled) {
                                    isSessionExpiredHandled = true;
                                    
                                    // Limpiar token caducado
                                    prefs.edit().remove("auth_token").apply();

                                    // Mostrar mensaje de sesión expirada usando un Handler para el hilo principal
                                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                                        android.widget.Toast.makeText(context, "Tu sesión ha expirado. Por favor, inicia sesión de nuevo.", android.widget.Toast.LENGTH_LONG).show();
                                    });

                                    // Redirigir al login
                                    android.content.Intent intent = new android.content.Intent(context, com.example.skynet.ui.auth.LoginActivity.class);
                                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                }
                            }
                        }

                        return response;
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
