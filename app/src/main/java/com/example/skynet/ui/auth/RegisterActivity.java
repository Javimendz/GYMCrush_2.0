package com.example.skynet.ui.auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.RegisterRequest;
import com.example.skynet.data.remote.dto.UsuarioResponseDto;
import com.example.skynet.data.repository.AuthRepository;
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
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final int RC_SIGN_IN = 9001;
    private EditText etNombre, etApellidos, etUsername, etEmail, etPassword, etFechaNacimiento,
            etCiudad, etCodigoPostal, etTelefono, etDireccion, etPais, etDni;
    private AutoCompleteTextView spinnerGenero;
    private TextView tvFaceStatus;
    private PreviewView previewView;
    private AuthRepository authRepository;

    // Biometría
    private Interpreter tfLite;
    private FaceDetector detector;
    private List<Float> faceVector = null;
    private ExecutorService cameraExecutor;
    private boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_registro);

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmailRegistro);
        etPassword = findViewById(R.id.etPasswordRegistro);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        etCiudad = findViewById(R.id.etCiudad);
        etCodigoPostal = findViewById(R.id.etCodigoPostal);
        etTelefono = findViewById(R.id.etTelefono);

        etDireccion = findViewById(R.id.etDireccion);
        etPais = findViewById(R.id.etPais);
        etDni = findViewById(R.id.etDni);
        tvFaceStatus = findViewById(R.id.tvFaceStatus);
        previewView = findViewById(R.id.previewView);

        // Background image loaded via Glide
        ImageView ivBackgroundRegistro = findViewById(R.id.ivBackgroundRegistro);
        if (ivBackgroundRegistro != null) {
            Glide.with(this)
                    .load("file:///android_asset/login.jpg")
                    .into(ivBackgroundRegistro);
        }

        authRepository = new AuthRepository();
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Inicializar ML Kit Face Detector
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

        startCamera();

        // Configurar Selector de Fecha
        etFechaNacimiento.setOnClickListener(v -> showDatePicker());

        // Configurar Selector de Género
        String[] generos = {"Hombre", "Mujer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, generos);
        spinnerGenero.setAdapter(adapter);

        // Botón Escanear Rostro
        findViewById(R.id.btnScanFace).setOnClickListener(v -> {
            if (faceVector != null) {
                faceVector = null;
                tvFaceStatus.setText("Buscando rostro...");
                tvFaceStatus.setTextColor(Color.GRAY);
            }
            isScanning = true;
            Toast.makeText(this, "Capturando biometría...", Toast.LENGTH_SHORT).show();
        });

        // Botón Registrar
        findViewById(R.id.btnRegistrar).setOnClickListener(v -> validarYRegistrar());

        // Botón Volver
        TextView tvVolverLogin = findViewById(R.id.tvVolverLogin);
        setupStyledText(tvVolverLogin);
        tvVolverLogin.setOnClickListener(v -> finish());
    }

    private void setupStyledText(TextView textView) {
        String text = "¿Tienes una cuenta? Inicio sesión Facial o Email";
        SpannableString ss = new SpannableString(text);

        int facialStart = text.indexOf("Facial");
        if (facialStart != -1) {
            ClickableSpan facialClick = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(RegisterActivity.this, FaceLoginActivity.class);
                    startActivity(intent);
                }
                @Override
                public void updateDrawState(@NonNull android.text.TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#3498db"));
                    ds.setUnderlineText(false);
                }
            };
            ss.setSpan(facialClick, facialStart, facialStart + "Facial".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        int emailStart = text.indexOf("Email");
        if (emailStart != -1) {
            ClickableSpan emailClick = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(RegisterActivity.this, EmailLoginActivity.class);
                    startActivity(intent);
                }
                @Override
                public void updateDrawState(@NonNull android.text.TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#3498db"));
                    ds.setUnderlineText(false);
                }
            };
            ss.setSpan(emailClick, emailStart, emailStart + "Email".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(Color.TRANSPARENT);
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

        imageAnalysis.setAnalyzer(cameraExecutor, this::processImageProxy);

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
        } catch (Exception e) {
            Log.e(TAG, "Error al vincular casos de uso de la cámara", e);
        }
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void processImageProxy(ImageProxy imageProxy) {
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            detector.process(image)
                    .addOnSuccessListener(faces -> {
                        if (!faces.isEmpty() && isScanning) {
                            processFace(faces.get(0), imageProxy);
                            isScanning = false;
                        } else if (faces.isEmpty()) {
                            runOnUiThread(() -> tvFaceStatus.setText("Rostro no detectado"));
                        } else {
                            runOnUiThread(() -> tvFaceStatus.setText("Rostro detectado (Listo para escanear)"));
                        }
                    })
                    .addOnCompleteListener(task -> imageProxy.close());
        } else {
            imageProxy.close();
        }
    }

    private void processFace(Face face, ImageProxy imageProxy) {
        Bitmap bitmap = previewView.getBitmap();
        if (bitmap == null) return;

        int rotation = imageProxy.getImageInfo().getRotationDegrees();
        int frameWidth = (rotation == 90 || rotation == 270) ? imageProxy.getHeight() : imageProxy.getWidth();
        int frameHeight = (rotation == 90 || rotation == 270) ? imageProxy.getWidth() : imageProxy.getHeight();

        Rect bounds = face.getBoundingBox();
        float scaleX = (float) bitmap.getWidth() / frameWidth;
        float scaleY = (float) bitmap.getHeight() / frameHeight;

        try {
            int centerX = (int) (bounds.centerX() * scaleX);
            int centerY = (int) (bounds.centerY() * scaleY);
            int faceSize = (int) (Math.max(bounds.width() * scaleX, bounds.height() * scaleY));
            int sizeWithMargin = (int) (faceSize * 1.15f);

            int left = Math.max(0, centerX - sizeWithMargin / 2);
            int top = Math.max(0, centerY - sizeWithMargin / 2);
            int actualWidth = Math.min(sizeWithMargin, bitmap.getWidth() - left);
            int actualHeight = Math.min(sizeWithMargin, bitmap.getHeight() - top);
            int squareSize = Math.min(actualWidth, actualHeight);

            if (squareSize <= 20) return;

            Bitmap faceBitmap = Bitmap.createBitmap(bitmap, left, top, squareSize, squareSize);
            Bitmap scaledFace = Bitmap.createScaledBitmap(faceBitmap, 160, 160, true);

            float[] embedding = runInference(scaledFace);

            runOnUiThread(() -> {
                faceVector = new ArrayList<>();
                for (float f : embedding) faceVector.add(f);
                tvFaceStatus.setText("Biometría capturada con éxito");
                tvFaceStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
                Toast.makeText(this, "Rostro capturado correctamente", Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            Log.e(TAG, "Error procesando rostro", e);
        }
    }

    private float[] runInference(Bitmap bitmap) {
        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * 160 * 160 * 3 * 4);
        imgData.order(ByteOrder.nativeOrder());
        int[] intValues = new int[160 * 160];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        imgData.rewind();
        int pixel = 0;
        for (int i = 0; i < 160; ++i) {
            for (int j = 0; j < 160; ++j) {
                int pixelValue = intValues[pixel++];
                imgData.putFloat((((pixelValue >> 16) & 0xFF) - 127.5f) / 128.0f);
                imgData.putFloat((((pixelValue >> 8) & 0xFF) - 127.5f) / 128.0f);
                imgData.putFloat(((pixelValue & 0xFF) - 127.5f) / 128.0f);
            }
        }

        float[][] output = new float[1][512];
        tfLite.run(imgData, output);

        float[] embedding = output[0];
        float sum = 0;
        for (float v : embedding) sum += v * v;
        float norm = (float) Math.sqrt(sum);
        if (norm > 0) {
            for (int i = 0; i < embedding.length; i++) embedding[i] /= norm;
        }

        return embedding;
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR) - 18;
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
            etFechaNacimiento.setText(date);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void validarYRegistrar() {
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String correo = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String fecha = etFechaNacimiento.getText().toString().trim();
        String genero = spinnerGenero.getText().toString().trim();
        String ciudad = etCiudad.getText().toString().trim();
        String codigoPostal = etCodigoPostal.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String pais = etPais.getText().toString().trim();
        String dni = etDni.getText().toString().trim().toUpperCase();

        if (nombre.isEmpty() || apellidos.isEmpty() || username.isEmpty() || correo.isEmpty() ||
                pass.isEmpty() || fecha.isEmpty() || genero.isEmpty() || ciudad.isEmpty() ||
                codigoPostal.isEmpty() || telefono.isEmpty() || direccion.isEmpty() || pais.isEmpty() || dni.isEmpty()) {
            Toast.makeText(this, "Todos los campos marcados son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.length() < 3 || username.length() > 20) {
            etUsername.setError("El usuario debe tener entre 3 y 20 caracteres");
            etUsername.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etEmail.setError("Formato de correo inválido");
            etEmail.requestFocus();
            return;
        }

        if (etEmail.isEnabled() && pass.length() < 8) {
            etPassword.setError("La contraseña debe tener al menos 8 caracteres");
            etPassword.requestFocus();
            return;
        }

        if (!telefono.matches("^[0-9]{9}$")) {
            etTelefono.setError("El teléfono debe tener exactamente 9 dígitos");
            etTelefono.requestFocus();
            return;
        }

        if (!dni.matches("^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$")) {
            etDni.setError("Formato de DNI inválido (8 números y 1 letra)");
            etDni.requestFocus();
            return;
        }

        if (!codigoPostal.matches("^[0-9]{5}$")) {
            etCodigoPostal.setError("El código postal debe tener 5 dígitos");
            etCodigoPostal.requestFocus();
            return;
        }

        if (faceVector == null) {
            Toast.makeText(this, "Es obligatorio escanear tu rostro para el registro", Toast.LENGTH_LONG).show();
            return;
        }

        RegisterRequest request = new RegisterRequest();
        request.setNombre(nombre);
        request.setApellidos(apellidos);
        request.setUsername(username);
        request.setEmail(correo);
        request.setPassword(pass);
        request.setFechaNacimiento(fecha);
        request.setGenero(genero);

        // 🛠️ Solución al error de validación: Enviar el Set estructurado de forma limpia y explícita
        Set<String> rolesUsuario = new HashSet<>();
        rolesUsuario.add("ROLE_USUARIO");
        request.setRoles(rolesUsuario);

        request.setCiudad(ciudad);
        request.setCodigoPostal(codigoPostal);
        request.setTelefono(telefono);
        request.setDireccion(direccion);
        request.setPais(pais);
        request.setDni(dni);
        request.setFaceVector(faceVector);

        authRepository.register(request, new AuthRepository.RegisterCallback() {
            @Override
            public void onSuccess(UsuarioResponseDto usuario) {
                runOnUiThread(() -> {
                    Toast.makeText(RegisterActivity.this, "¡Registro con éxito! Identifícate", Toast.LENGTH_LONG).show();
                    finish();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    String mensaje = (errorMessage != null && !errorMessage.isEmpty()) ? errorMessage : "Error desconocido";
                    Toast.makeText(RegisterActivity.this, mensaje, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (tfLite != null) {
            tfLite.close();
        }
        if (detector != null) {
            detector.close();
        }
    }
}
