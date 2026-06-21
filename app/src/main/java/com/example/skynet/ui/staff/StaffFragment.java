package com.example.skynet.ui.staff;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skynet.R;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.PerfilResponseDto;
import com.example.skynet.data.remote.dto.RegisterRequest;
import com.example.skynet.data.remote.dto.UsuarioResponseDto;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffFragment extends Fragment {

    private RecyclerView rvStaff;
    private ProgressBar progressBar;
    private ExtendedFloatingActionButton fabAdd;
    private SearchView searchView;
    private StaffAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff, container, false);

        rvStaff = view.findViewById(R.id.rvStaff);
        progressBar = view.findViewById(R.id.pbStaff);
        fabAdd = view.findViewById(R.id.fabAddStaff);
        searchView = view.findViewById(R.id.searchViewStaff);

        rvStaff.setLayoutManager(new LinearLayoutManager(getContext()));

        view.findViewById(R.id.btnBackStaff).setOnClickListener(v -> {
            if (getActivity() instanceof com.example.skynet.ui.main.DesarrolloActivity) {
                ((com.example.skynet.ui.main.DesarrolloActivity) getActivity()).mostrarHome();
            } else if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        fabAdd.setOnClickListener(v -> mostrarOpcionesStaff());

        setupSearch();
        cargarStaff();

        return view;
    }

    private void setupSearch() {
        if (searchView != null) {
            // Aplicar fix para el crash de AppSearch (OneSearchSuggestProvider)
            searchView.setSuggestionsAdapter(null);
            searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            // Evitar que el teclado intente aprender de este campo, lo que puede disparar AppSearch
            searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING);

            // Cambiar color de texto a gris claro
            EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
            if (searchEditText != null) {
                searchEditText.setTextColor(Color.parseColor("#CCCCCC"));
                searchEditText.setHintTextColor(Color.parseColor("#999999"));
            }

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (adapter != null) adapter.filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (adapter != null) adapter.filter(newText);
                    return true;
                }
            });
        }
    }

    private void cargarStaff() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getApiService().findAllPerfiles().enqueue(new Callback<ApiResponseDto<List<PerfilResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<PerfilResponseDto>>> call, Response<ApiResponseDto<List<PerfilResponseDto>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<PerfilResponseDto> lista = response.body().getDatos();
                    if (adapter == null) {
                        adapter = new StaffAdapter(lista, new StaffAdapter.OnStaffInteractionListener() {
                            @Override
                            public void onDeleteRole(PerfilResponseDto perfil) {
                                new MaterialAlertDialogBuilder(requireContext())
                                        .setTitle("Quitar Rol")
                                        .setMessage("¿Estás seguro de que quieres quitarle el rol de staff a " + perfil.getNombre() + "?")
                                        .setPositiveButton("Quitar", (d, w) -> {
                                            Long idParaQuitar = perfil.getUsuarioId();
                                            if (idParaQuitar != null) {
                                                ejecutarQuitarRol(idParaQuitar);
                                            } else {
                                                Toast.makeText(getContext(), "Error: ID de usuario no encontrado", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton("Cancelar", null)
                                        .show();
                            }

                            @Override
                            public void onChangeRole(PerfilResponseDto perfil) {
                                mostrarDialogoCambiarRol(perfil);
                            }
                        });
                        rvStaff.setAdapter(adapter);
                    } else {
                        adapter.updateList(lista);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<PerfilResponseDto>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar staff", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ejecutarQuitarRol(Long id) {
        RetrofitClient.getApiService().quitarRolEntrenador(id).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Rol quitado con éxito", Toast.LENGTH_SHORT).show();
                    cargarStaff();
                } else {
                    Toast.makeText(getContext(), "Error al quitar rol", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoCambiarRol(PerfilResponseDto perfil) {
        String[] roles = {"USUARIO", "ENTRENADOR", "ADMIN"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Cambiar Rol a " + perfil.getNombre())
                .setItems(roles, (dialog, which) -> {
                    Long uId = perfil.getUsuarioId();
                    if (uId == null) {
                        Toast.makeText(getContext(), "Error: ID de usuario no disponible", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (which == 0) ejecutarQuitarRol(uId);
                    else if (which == 1) confirmarAsignacion(uId);
                    else Toast.makeText(getContext(), "Rol ADMIN no implementado individualmente", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void mostrarOpcionesStaff() {
        String[] opciones = {"Crear Nuevo (Registro completo)", "Asignar Rol a Usuario Existente"};
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Panel de Staff")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        mostrarDialogoNuevoEntrenador();
                    } else {
                        mostrarDialogoAsignarEntrenador();
                    }
                })
                .show();
    }

    private void mostrarDialogoAsignarEntrenador() {
        progressBar.setVisibility(View.VISIBLE);
        RetrofitClient.getApiService().findAllPerfiles().enqueue(new Callback<ApiResponseDto<List<PerfilResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<PerfilResponseDto>>> call, Response<ApiResponseDto<List<PerfilResponseDto>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<PerfilResponseDto> usuarios = response.body().getDatos();

                    Spinner spinner = new Spinner(getContext());
                    spinner.setPadding(40, 20, 40, 20);

                    ArrayAdapter<PerfilResponseDto> adapterPerf = new ArrayAdapter<PerfilResponseDto>(requireContext(), android.R.layout.simple_spinner_item, usuarios) {
                        @Override
                        public View getView(int pos, View v, ViewGroup p) {
                            TextView tv = (TextView) super.getView(pos, v, p);
                            tv.setText(getItem(pos).getNombreCompleto() + " (" + getItem(pos).getDni() + ")");
                            tv.setTextColor(Color.WHITE);
                            tv.setTypeface(null, android.graphics.Typeface.BOLD);
                            return tv;
                        }
                        @Override
                        public View getDropDownView(int pos, View v, ViewGroup p) {
                            TextView tv = (TextView) super.getDropDownView(pos, v, p);
                            tv.setText(getItem(pos).getNombreCompleto() + " (" + getItem(pos).getDni() + ")");
                            tv.setTextColor(Color.WHITE);
                            tv.setBackgroundColor(Color.parseColor("#0A1128"));
                            return tv;
                        }
                    };
                    spinner.setAdapter(adapterPerf);

                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Asignar Rol Entrenador")
                            .setMessage("Selecciona un usuario para convertirlo en entrenador:")
                            .setView(spinner)
                            .setPositiveButton("Asignar", (dialog, which) -> {
                                PerfilResponseDto seleccionado = (PerfilResponseDto) spinner.getSelectedItem();
                                if (seleccionado != null && seleccionado.getUsuarioId() != null) {
                                    confirmarAsignacion(seleccionado.getUsuarioId());
                                } else {
                                    Toast.makeText(getContext(), "Selección inválida o ID faltante", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<PerfilResponseDto>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarAsignacion(Long usuarioId) {
        RetrofitClient.getApiService().asignarRolEntrenador(usuarioId).enqueue(new Callback<ApiResponseDto<Void>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<Void>> call, Response<ApiResponseDto<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Rol asignado con éxito!", Toast.LENGTH_SHORT).show();
                    cargarStaff();
                } else {
                    Toast.makeText(getContext(), "Error al asignar rol", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<Void>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoNuevoEntrenador() {
        Dialog dialog = new Dialog(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_nuevo_staff, null);
        dialog.setContentView(view);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        EditText etUsername = view.findViewById(R.id.etStaffUsername);
        EditText etNombre = view.findViewById(R.id.etStaffNombre);
        EditText etApellidos = view.findViewById(R.id.etStaffApellidos);
        EditText etDni = view.findViewById(R.id.etStaffDni);
        EditText etCorreo = view.findViewById(R.id.etStaffCorreo);
        EditText etPass = view.findViewById(R.id.etStaffPass);

        view.findViewById(R.id.btnCancelarStaff).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btnCrearStaff).setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String nom = etNombre.getText().toString().trim();
            String ape = etApellidos.getText().toString().trim();
            String dni = etDni.getText().toString().trim().toUpperCase();
            String mail = etCorreo.getText().toString().trim();
            String pass = etPass.getText().toString().trim();

            if (user.isEmpty() || nom.isEmpty() || ape.isEmpty() || mail.isEmpty() || pass.isEmpty() || dni.isEmpty()) {
                Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validaciones básicas similares a RegisterActivity
            if (user.length() < 3) {
                etUsername.setError("Mínimo 3 caracteres");
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                etCorreo.setError("Email inválido");
                return;
            }
            if (pass.length() < 8) {
                etPass.setError("Mínimo 8 caracteres");
                return;
            }
            if (!dni.matches("^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$")) {
                etDni.setError("DNI inválido");
                return;
            }

            RegisterRequest request = new RegisterRequest();
            request.setUsername(user);
            request.setNombre(nom);
            request.setApellidos(ape);
            request.setDni(dni);
            request.setEmail(mail);
            request.setPassword(pass);
            // Usamos ROLE_ENTRENADOR que es el esperado por el backend
            request.setRoles(new HashSet<>(Collections.singletonList("ROLE_ENTRENADOR")));
            
            // Valores por defecto para evitar nulos si el backend los requiere
            request.setCiudad("Sevilla"); // O un valor genérico
            request.setPais("España");
            request.setTelefono("600000000");
            request.setGenero("Hombre");
            request.setFechaNacimiento("1990-01-01");
            request.setCodigoPostal("41001");
            request.setDireccion("Direccion Gimnasio");

            RetrofitClient.getApiService().register(request).enqueue(new Callback<ApiResponseDto<UsuarioResponseDto>>() {
                @Override
                public void onResponse(Call<ApiResponseDto<UsuarioResponseDto>> call, Response<ApiResponseDto<UsuarioResponseDto>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(getContext(), "Entrenador creado con éxito", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        cargarStaff();
                    } else {
                        String msg = "Error al crear";
                        if (response.body() != null && response.body().getMensaje() != null) {
                            msg = response.body().getMensaje();
                        }
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseDto<UsuarioResponseDto>> call, Throwable t) {
                    Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
