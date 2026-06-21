package com.example.skynet.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {
    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    
                    // Get token from SharedPreferences (same as RetrofitClient)
                    SharedPreferences prefs = context.getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
                    String token = prefs.getString("auth_token", "");
                    
                    Request.Builder builder = original.newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
                    
                    // Only add Authorization header if we have a token and it's likely a request to our backend
                    // or if the external API requires it. For now, adding it consistently as in Retrofit.
                    if (!token.isEmpty()) {
                        builder.header("Authorization", "Bearer " + token);
                    }
                    
                    return chain.proceed(builder.build());
                })
                .build();
        
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
