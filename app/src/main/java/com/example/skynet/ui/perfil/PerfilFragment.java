package com.example.skynet.ui.perfil;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.PerfilRequestDto;
import com.example.skynet.data.remote.dto.PerfilResponseDto;
import com.example.skynet.data.remote.dto.SaludRequestDto;
import com.example.skynet.data.remote.dto.SaludResponseDto;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

public class PerfilFragment extends Fragment {

    private PerfilViewModel viewModel;
    private ImageView ivFotoPerfil;
    private TextView tvNombrePerfil, tvEmailPerfil, tvTelefono, tvDireccion, tvBio;
    private TextView tvPeso, tvAltura, tvEdad;
    private SharedPreferences prefs;
    private Long userId;
    private PerfilResponseDto currentPerfil;
    private Long perfilId;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar el selector de imágenes moderno
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            Bitmap bitmap;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                bitmap = android.graphics.ImageDecoder.decodeBitmap(
                                        android.graphics.ImageDecoder.createSource(requireActivity().getContentResolver(), uri),
                                        (decoder, info, source) -> decoder.setMutableRequired(true)
                                );
                            } else {
                                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                            }
                            actualizarFotoPerfil(bitmap);
                        } catch (IOException e) {
                            Toast.makeText(getContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        prefs = requireActivity().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        userId = prefs.getLong("user_id", -1L);

        initViews(view);
        viewModel = new ViewModelProvider(this).get(PerfilViewModel.class);
        setupObservers();

        if (userId != -1L) {
            viewModel.cargarDatosCompletos(userId);
        } else {
            Toast.makeText(getContext(), "Error: No se encontró el ID de usuario", Toast.LENGTH_SHORT).show();
        }

        view.findViewById(R.id.btnEditarPerfil).setOnClickListener(v -> mostrarDialogoEditar());
        view.findViewById(R.id.fabEditarFoto).setOnClickListener(v -> abrirGaleria());

        view.findViewById(R.id.btnVolverPerfil).setOnClickListener(v -> {
            if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).mostrarHome();
            } else if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void initViews(View view) {
        ivFotoPerfil = view.findViewById(R.id.ivFotoPerfil);
        tvNombrePerfil = view.findViewById(R.id.tvNombrePerfil);
        tvEmailPerfil = view.findViewById(R.id.tvEmailPerfil);
        tvTelefono = view.findViewById(R.id.tvTelefono);
        tvDireccion = view.findViewById(R.id.tvDireccion);
        tvBio = view.findViewById(R.id.tvBio);
        tvPeso = view.findViewById(R.id.tvPeso);
        tvAltura = view.findViewById(R.id.tvAltura);
        tvEdad = view.findViewById(R.id.tvEdad);
    }

    private void setupObservers() {
        viewModel.getPerfil().observe(getViewLifecycleOwner(), perfil -> {
            if (perfil != null) {
                this.perfilId = perfil.getId(); // <--- GUARDAMOS EL ID REAL DEL PERFIL
                currentPerfil = perfil;
                actualizarUI(perfil);
            }
        });

        viewModel.getSalud().observe(getViewLifecycleOwner(), salud -> {
            if (salud != null) {
                actualizarSaludUI(salud);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null && !errorMsg.isEmpty()) {
                Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void actualizarUI(PerfilResponseDto perfil) {
        String nombreCompleto = perfil.getNombreCompleto().trim();
        String finalNombre;
        if (nombreCompleto.isEmpty()) {
            finalNombre = prefs.getString("nombre_usuario", "Usuario GYMCrush");
        } else {
            finalNombre = nombreCompleto;
        }
        tvNombrePerfil.setText(finalNombre);

        String finalEmail;
        if (perfil.getCorreo() != null && !perfil.getCorreo().isEmpty()) {
            finalEmail = perfil.getCorreo();
        } else {
            finalEmail = prefs.getString("email_usuario", "usuario@gymcrush.com");
        }
        tvEmailPerfil.setText(finalEmail);

        // Sincronizar con SharedPreferences para que otras pantallas (Home) se actualicen
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nombre_usuario", finalNombre);
        editor.putString("email_usuario", finalEmail);
        editor.apply();

        tvTelefono.setText(perfil.getTelefono() != null ? perfil.getTelefono() : "--");

        String direccion = (perfil.getDireccion() != null ? perfil.getDireccion() : "").trim();
        String ciudad = (perfil.getCiudad() != null ? perfil.getCiudad() : "").trim();
        if (direccion.isEmpty() && ciudad.isEmpty()) {
            tvDireccion.setText("Dirección no definida");
        } else {
            tvDireccion.setText(direccion + (direccion.isEmpty() || ciudad.isEmpty() ? "" : ", ") + ciudad);
        }

        tvBio.setText(perfil.getBio() != null && !perfil.getBio().isEmpty() ? perfil.getBio() : "Sin biografía definida.");

        // Mostrar peso y altura si vienen en el DTO de perfil (fallback/inicial)
        if (perfil.getPeso() != null && perfil.getPeso() > 0) {
            tvPeso.setText(perfil.getPeso() + " kg");
        }
        if (perfil.getEstatura() != null && perfil.getEstatura() > 0) {
            double alturaCm = perfil.getEstatura() < 3 ? perfil.getEstatura() * 100 : perfil.getEstatura();
            tvAltura.setText((int)alturaCm + " cm");
        }

        if (perfil.getFechaNacimiento() != null && !perfil.getFechaNacimiento().isEmpty()) {
            try {
                LocalDate fechaNac = LocalDate.parse(perfil.getFechaNacimiento());
                int edad = Period.between(fechaNac, LocalDate.now()).getYears();
                tvEdad.setText(edad + " años");
            } catch (Exception e) {
                tvEdad.setText("--");
            }
        }

        if (perfil.getFoto() != null && !perfil.getFoto().isEmpty()) {
            byte[] decodedString = Base64.decode(perfil.getFoto(), Base64.DEFAULT);
            Glide.with(this)
                    .asBitmap()
                    .load(decodedString)
                    .circleCrop()
                    .placeholder(R.drawable.logo)
                    .into(ivFotoPerfil);
        } else {
            Glide.with(this).load(R.drawable.logo).circleCrop().into(ivFotoPerfil);
        }
    }

    private void actualizarSaludUI(SaludResponseDto salud) {
        if (salud.getPeso() != null) {
            tvPeso.setText(salud.getPeso() + " kg");
            if (currentPerfil != null) currentPerfil.setPeso(salud.getPeso());
        }
        if (salud.getEstatura() != null) {
            double alturaCm = salud.getEstatura() < 3 ? salud.getEstatura() * 100 : salud.getEstatura();
            tvAltura.setText((int)alturaCm + " cm");
            if (currentPerfil != null) currentPerfil.setEstatura(salud.getEstatura());
        }
        if (salud.getImc() != null && currentPerfil != null) {
            currentPerfil.setImc(salud.getImc());
        }
    }

    private void abrirGaleria() {
        imagePickerLauncher.launch("image/*");
    }

    private void actualizarFotoPerfil(Bitmap bitmap) {
        // Redimensionar para no exceder límites del backend
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        // Usar NO_WRAP para evitar saltos de línea en el JSON
        String encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);

        if (currentPerfil != null && perfilId != null) {
            PerfilRequestDto request = new PerfilRequestDto();
            request.setCodigoPostal(currentPerfil.getCodigoPostal());
            request.setNombre(currentPerfil.getNombre());
            request.setApellidos(currentPerfil.getApellidos());
            request.setTelefono(currentPerfil.getTelefono());
            request.setDni(currentPerfil.getDni());
            request.setPais(currentPerfil.getPais());
            request.setFechaNacimiento(currentPerfil.getFechaNacimiento());
            request.setDireccion(currentPerfil.getDireccion());
            request.setCiudad(currentPerfil.getCiudad());
            request.setGenero(currentPerfil.getGenero());
            request.setCorreo(currentPerfil.getCorreo());

            request.setFoto(encodedImage); // Enviamos la imagen en Base64


            if (currentPerfil.getFechaNacimiento() != null) {
                request.setFechaNacimiento(currentPerfil.getFechaNacimiento());
            } else {
                request.setFechaNacimiento("1990-01-01"); // Un valor por defecto para pasar el @NotNull
            }

            if (currentPerfil.getGenero() != null) {
                request.setGenero(currentPerfil.getGenero());
            } else {
                request.setGenero("NO_ESPECIFICADO"); // Asumiendo que tienes este valor en tu Enum
            }

            viewModel.actualizarPerfil(perfilId, request);
            Toast.makeText(getContext(), "Actualizando foto de perfil...", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoEditar() {
        if (currentPerfil == null) return;

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_editar_perfil);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextInputEditText etNombre = dialog.findViewById(R.id.et_editar_nombre);
        TextInputEditText etApellidos = dialog.findViewById(R.id.et_editar_apellidos);
        TextInputEditText etEmail = dialog.findViewById(R.id.et_editar_email);
        TextInputEditText etDni = dialog.findViewById(R.id.et_editar_dni);
        TextInputEditText etFechaNac = dialog.findViewById(R.id.et_editar_fecha_nac);
        TextInputEditText etCiudad = dialog.findViewById(R.id.et_editar_ciudad);
        TextInputEditText etCp = dialog.findViewById(R.id.et_editar_cp);
        TextInputEditText etPais = dialog.findViewById(R.id.et_editar_pais);
        TextInputEditText etGenero = dialog.findViewById(R.id.et_editar_genero);
        TextInputEditText etPeso = dialog.findViewById(R.id.et_editar_peso);
        TextInputEditText etAltura = dialog.findViewById(R.id.et_editar_altura);
        TextInputEditText etTelefono = dialog.findViewById(R.id.et_editar_telefono);
        TextInputEditText etDireccion = dialog.findViewById(R.id.et_editar_direccion);
        TextInputEditText etBio = dialog.findViewById(R.id.et_editar_bio);

        // Rellenar datos actuales
        etNombre.setText(currentPerfil.getNombre());
        etApellidos.setText(currentPerfil.getApellidos());
        etEmail.setText(currentPerfil.getCorreo());
        etDni.setText(currentPerfil.getDni());
        etFechaNac.setText(currentPerfil.getFechaNacimiento());
        etCiudad.setText(currentPerfil.getCiudad());
        etCp.setText(currentPerfil.getCodigoPostal());
        etPais.setText(currentPerfil.getPais());
        etGenero.setText(currentPerfil.getGenero());
        etPeso.setText(currentPerfil.getPeso() != null ? String.valueOf(currentPerfil.getPeso()) : "");
        etAltura.setText(currentPerfil.getEstatura() != null ? String.valueOf(currentPerfil.getEstatura()) : "");
        etTelefono.setText(currentPerfil.getTelefono());
        etDireccion.setText(currentPerfil.getDireccion());
        etBio.setText(currentPerfil.getBio());

        dialog.findViewById(R.id.btn_cancelar_edicion).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.btn_guardar_edicion).setOnClickListener(v -> {
            PerfilRequestDto perfilRequest = new PerfilRequestDto();
            
            // Datos básicos - Se toma lo del EditText, si está vacío se podría manejar pero aquí guardamos lo que haya
            perfilRequest.setNombre(etNombre.getText().toString().trim());
            perfilRequest.setApellidos(etApellidos.getText().toString().trim());
            perfilRequest.setCorreo(etEmail.getText().toString().trim());
            perfilRequest.setTelefono(etTelefono.getText().toString().trim());
            perfilRequest.setDireccion(etDireccion.getText().toString().trim());
            perfilRequest.setBio(etBio.getText().toString().trim());
            perfilRequest.setDni(etDni.getText().toString().trim());
            perfilRequest.setFechaNacimiento(etFechaNac.getText().toString().trim());
            perfilRequest.setCiudad(etCiudad.getText().toString().trim());
            perfilRequest.setCodigoPostal(etCp.getText().toString().trim());
            perfilRequest.setPais(etPais.getText().toString().trim());
            perfilRequest.setGenero(etGenero.getText().toString().trim());
            
            // Mantener datos que no están en este diálogo
            perfilRequest.setFoto(currentPerfil.getFoto());

            // 1. Actualizar perfil
            viewModel.actualizarPerfil(perfilId, perfilRequest);

            // 2. Procesar salud (peso/altura) independientemente
            String pesoStr = etPeso.getText().toString().trim();
            String alturaStr = etAltura.getText().toString().trim();

            if (!pesoStr.isEmpty() && !alturaStr.isEmpty()) {
                try {
                    double nPeso = Double.parseDouble(pesoStr);
                    double nAltura = Double.parseDouble(alturaStr);

                    if (nPeso > 0 && nAltura > 0) {
                        SaludRequestDto saludRequest = new SaludRequestDto();
                        saludRequest.setPeso(nPeso);
                        saludRequest.setEstatura(nAltura);
                        saludRequest.setNivelActividad(currentPerfil.getNivelActividad() != null ? currentPerfil.getNivelActividad() : "MODERADO");
                        saludRequest.setComentario("Actualización desde perfil");
                        
                        viewModel.registrarSalud(userId, saludRequest);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Formato de peso o altura inválido", Toast.LENGTH_SHORT).show();
                }
            }

            dialog.dismiss();
            Toast.makeText(getContext(), "Guardando cambios...", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
}