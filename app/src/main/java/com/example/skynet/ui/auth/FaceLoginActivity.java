package com.example.skynet.ui.auth;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.skynet.R;
import com.example.skynet.data.remote.ApiService;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.AuthResponse;
import com.example.skynet.data.remote.dto.FaceLoginRequest;
import com.example.skynet.ui.main.DesarrolloActivity;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaceLoginActivity extends AppCompatActivity {

    private static final String TAG = "FaceLoginActivity";
    private static final int PERMISSION_CODE = 1001;

    private PreviewView previewView;
    private EditText etEmail;
    private TextView tvStatus, tvMessage;
    private ProgressBar progressBar;
    private ExecutorService cameraExecutor;
    private FaceDetector detector;
    private Interpreter tfLite;
    private ApiService apiService;

    private boolean isProcessing = false;
    private boolean isCapturing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_login);

        initViews();
        apiService = RetrofitClient.getApiService();
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Configuración de ML Kit para detección de rostros
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                .build();
        detector = FaceDetection.getClient(options);

        // Cargar TFLite Model
        try {
            tfLite = new Interpreter(loadModelFile());
        } catch (IOException e) {
            Log.e(TAG, "Error al cargar modelo TFLite", e);
        }

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
        }
    }

    private void initViews() {
        previewView = findViewById(R.id.previewView);
        etEmail = findViewById(R.id.etEmail);
        tvStatus = findViewById(R.id.tvStatus);
        tvMessage = findViewById(R.id.tvMessage);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.btnCapture).setOnClickListener(v -> captureFace());
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("facenet.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Error iniciando cámara", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, image -> {
            if (!isProcessing) {
                analyzeImage(image);
            } else {
                image.close();
            }
        });

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }

    private void analyzeImage(ImageProxy imageProxy) {
        @SuppressWarnings("UnsafeOptInUsageError")
        android.media.Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

            detector.process(image)
                    .addOnSuccessListener(faces -> {
                        if (!faces.isEmpty() && isCapturing) {
                            isCapturing = false;
                            processFace(faces.get(0), imageProxy);
                        } else if (!faces.isEmpty() && !isProcessing) {
                            updateUIStatus("¡Rostro detectado!", "Procesando rasgos faciales...");
                        } else {
                            updateUIStatus("Alinea tu rostro", "Buscando rostro...");
                        }
                    })
                    .addOnCompleteListener(task -> imageProxy.close());
        } else {
            imageProxy.close();
        }
    }

    private void processFace(Face face, ImageProxy imageProxy) {
        Bitmap bitmap = previewView.getBitmap();
        if (bitmap == null) {
            isProcessing = false;
            return;
        }

        // Mapear coordenadas de la cámara a las coordenadas del Bitmap de la vista
        int rotation = imageProxy.getImageInfo().getRotationDegrees();
        int frameWidth = (rotation == 90 || rotation == 270) ? imageProxy.getHeight() : imageProxy.getWidth();
        int frameHeight = (rotation == 90 || rotation == 270) ? imageProxy.getWidth() : imageProxy.getHeight();

        Rect bounds = face.getBoundingBox();
        float scaleX = (float) bitmap.getWidth() / frameWidth;
        float scaleY = (float) bitmap.getHeight() / frameHeight;

        try {
            // Calcular un recorte cuadrado con un 15% de margen (mejor para FaceNet)
            int centerX = (int) (bounds.centerX() * scaleX);
            int centerY = (int) (bounds.centerY() * scaleY);
            int faceSize = (int) (Math.max(bounds.width() * scaleX, bounds.height() * scaleY));
            int sizeWithMargin = (int) (faceSize * 1.15f);

            int left = Math.max(0, centerX - sizeWithMargin / 2);
            int top = Math.max(0, centerY - sizeWithMargin / 2);
            int actualWidth = Math.min(sizeWithMargin, bitmap.getWidth() - left);
            int actualHeight = Math.min(sizeWithMargin, bitmap.getHeight() - top);
            int squareSize = Math.min(actualWidth, actualHeight);

            if (squareSize <= 20) {
                isProcessing = false;
                return;
            }

            Bitmap faceBitmap = Bitmap.createBitmap(bitmap, left, top, squareSize, squareSize);
            Bitmap scaledFace = Bitmap.createScaledBitmap(faceBitmap, 160, 160, true);

            float[] embedding = runInference(scaledFace);

            List<Float> faceVector = new ArrayList<>();
            for (float f : embedding) faceVector.add(f);

            String userEmail = etEmail.getText().toString().trim();
            sendFaceLogin(new FaceLoginRequest(userEmail, faceVector));

        } catch (Exception e) {
            Log.e(TAG, "Error procesando rostro", e);
            handleError("Error al procesar el rostro");
        }
    }

    private float[] runInference(Bitmap bitmap) {
        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * 160 * 160 * 3 * 4);
        imgData.order(ByteOrder.nativeOrder());
        int[] intValues = new int[160 * 160];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        imgData.rewind();
        for (int i = 0; i < 160; ++i) {
            for (int j = 0; j < 160; ++j) {
                int pixelValue = intValues[i * 160 + j];
                // Normalización exacta FaceNet [-1, 1]
                imgData.putFloat((((pixelValue >> 16) & 0xFF) - 127.5f) / 128.0f);
                imgData.putFloat((((pixelValue >> 8) & 0xFF) - 127.5f) / 128.0f);
                imgData.putFloat(((pixelValue & 0xFF) - 127.5f) / 128.0f);
            }
        }

        float[][] output = new float[1][512];
        tfLite.run(imgData, output);

        // Normalización L2 (Indispensable para que la distancia sea < 0.4)
        float[] embedding = output[0];
        float sum = 0;
        for (float v : embedding) sum += v * v;
        float norm = (float) Math.sqrt(sum);
        if (norm > 0) {
            for (int i = 0; i < embedding.length; i++) embedding[i] /= norm;
        }

        return embedding;
    }

    private void captureFace() {
        if (isProcessing) return;

        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            etEmail.setError("Introduce tu email");
            return;
        }

        isProcessing = true;
        isCapturing = true;

        progressBar.setVisibility(View.VISIBLE);
        updateUIStatus("Analizando...", "Capturando rasgos faciales...");
    }

    private void sendFaceLogin(FaceLoginRequest request) {
        apiService.faceLogin(request).enqueue(new Callback<ApiResponseDto<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<AuthResponse>> call, Response<ApiResponseDto<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    AuthResponse authResponseDto = response.body().getDatos();
                    
                    SharedPreferences prefs = getSharedPreferences("DatosUsuario", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    
                    String nombreAMostrar = authResponseDto.getNombre();
                    if (nombreAMostrar == null || nombreAMostrar.isEmpty()) {
                        nombreAMostrar = authResponseDto.getUsername();
                    }
                    
                    editor.putString("nombre_usuario", nombreAMostrar);
                    editor.putString("auth_token", authResponseDto.getAccessToken());
                    editor.putLong("user_id", authResponseDto.getId());
                    
                    if (authResponseDto.getRoles() != null) {
                        Set<String> rolesSet = new HashSet<>(authResponseDto.getRoles());
                        editor.putStringSet("roles", rolesSet);
                    }
                    editor.apply();

                    Toast.makeText(FaceLoginActivity.this, "¡Bienvenido, " + nombreAMostrar + "!", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(FaceLoginActivity.this, DesarrolloActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    handleError("No se reconoció el rostro o usuario no encontrado.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<AuthResponse>> call, Throwable t) {
                handleError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void updateUIStatus(String status, String message) {
        runOnUiThread(() -> {
            tvStatus.setText(status);
            tvMessage.setText(message);
        });
    }

    private void handleError(String error) {
        runOnUiThread(() -> {
            isProcessing = false;
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        });
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permisos de cámara denegados", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        if (detector != null) detector.close();
        if (tfLite != null) tfLite.close();
    }
}
