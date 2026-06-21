package com.example.skynet.ui.support;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.example.skynet.data.remote.dto.TicketRequestDto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import com.example.skynet.data.remote.ApiService;
import com.example.skynet.data.remote.RetrofitClient;
import com.example.skynet.data.remote.dto.ApiResponseDto;
import com.example.skynet.data.remote.dto.TicketResponseDto;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketListFragment extends Fragment implements TicketAdapter.OnTicketClickListener {

    private RecyclerView rvTickets;
    private TicketAdapter adapter;
    private List<TicketResponseDto> ticketList;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ApiService apiService;
    private boolean isAdmin = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_list, container, false);

        apiService = RetrofitClient.getApiService();
        rvTickets = view.findViewById(R.id.rvTickets);
        progressBar = view.findViewById(R.id.pbTickets);
        tvEmpty = view.findViewById(R.id.tvEmptyTickets);

        ticketList = new ArrayList<>();
        adapter = new TicketAdapter(ticketList, this);
        rvTickets.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTickets.setAdapter(adapter);

        view.findViewById(R.id.btnBackTickets).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddTicket);
        fabAdd.setOnClickListener(v -> mostrarDialogoCrearTicket());

        checkUserRole();
        loadTickets();

        return view;
    }

    private void checkUserRole() {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        java.util.Set<String> roles = prefs.getStringSet("roles", new java.util.HashSet<>());
        isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");
        
        TextView tvTitle = getView() != null ? getView().findViewById(R.id.tvTitleTickets) : null;
        if (tvTitle != null) {
            tvTitle.setText(isAdmin ? "Gestión de Tickets (Admin)" : "Mis Tickets");
        }
    }

    private void loadTickets() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);

        if (userId == -1) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ApiResponseDto<List<TicketResponseDto>>> call;
        if (isAdmin) {
            call = apiService.verTodosLosTickets();
        } else {
            call = apiService.verMisTickets(userId);
        }

        call.enqueue(new Callback<ApiResponseDto<List<TicketResponseDto>>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<List<TicketResponseDto>>> call, Response<ApiResponseDto<List<TicketResponseDto>>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    ticketList.clear();
                    ticketList.addAll(response.body().getDatos());
                    adapter.notifyDataSetChanged();
                    
                    if (ticketList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), "Error al cargar tickets", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<List<TicketResponseDto>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTicketClick(TicketResponseDto ticket) {
        if (!isAdmin) {
            String[] opciones = {"Chat de Soporte", "Ver Detalle", "Marcar como Resuelto"};
            new AlertDialog.Builder(getContext())
                    .setTitle("Ticket #" + ticket.getId())
                    .setItems(opciones, (dialog, which) -> {
                        if (which == 0) {
                            abrirChatTicket(ticket);
                        } else if (which == 2) {
                            actualizarEstadoTicket(ticket.getId(), "RESUELTO");
                        }
                    })
                    .setNegativeButton("Cerrar", null)
                    .show();
            return;
        }

        // Si es admin, mostrar diálogo con más opciones
        String[] opcionesAdmin = {"Cambiar Estado", "Chat con Usuario", "Ver Detalle"};
        new AlertDialog.Builder(getContext())
                .setTitle("Gestionar Ticket #" + ticket.getId())
                .setItems(opcionesAdmin, (dialog, which) -> {
                    if (which == 0) {
                        mostrarDialogoEstados(ticket);
                    } else if (which == 1) {
                        abrirChatTicket(ticket);
                    } else {
                        mostrarDetalleTicket(ticket);
                    }
                })
                .setNegativeButton("Cerrar", null)
                .show();
    }

    private void mostrarDialogoEstados(TicketResponseDto ticket) {
        String[] nombresMostrar = {"Pendiente", "Abierto", "Resuelto", "Cerrado"};
        String[] estadosBackend = {"PENDIENTE", "ABIERTO", "RESUELTO", "CERRADO"};
        new AlertDialog.Builder(getContext())
                .setTitle("Seleccionar nuevo estado")
                .setItems(nombresMostrar, (dialog, which) -> {
                    actualizarEstadoTicket(ticket.getId(), estadosBackend[which]);
                })
                .show();
    }

    private void abrirChatTicket(TicketResponseDto ticket) {
        if ("CERRADO".equalsIgnoreCase(ticket.getEstado())) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Ticket Cerrado")
                    .setMessage("Este ticket ha sido cerrado y no admite más mensajes.")
                    .setPositiveButton("Entendido", null)
                    .show();
            return;
        }

        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, TicketChatFragment.newInstance(ticket.getId(), ticket.getAsunto(), ticket.getEstado()))
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void mostrarDialogoCrearTicket() {
        Context context = getContext();
        if (context == null) return;

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText etAsunto = new EditText(context);
        etAsunto.setHint("Asunto (ej: Error en pago)");
        layout.addView(etAsunto);

        final EditText etMensaje = new EditText(context);
        etMensaje.setHint("Explica tu problema detalladamente...");
        etMensaje.setMinLines(3);
        layout.addView(etMensaje);

        new AlertDialog.Builder(context)
                .setTitle("Nuevo Ticket de Soporte")
                .setView(layout)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String asunto = etAsunto.getText().toString().trim();
                    String mensaje = etMensaje.getText().toString().trim();

                    if (asunto.isEmpty() || mensaje.isEmpty()) {
                        Toast.makeText(context, "Por favor rellena todos los campos", Toast.LENGTH_SHORT).show();
                    } else {
                        crearTicketManual(asunto, mensaje);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void crearTicketManual(String asunto, String mensaje) {
        SharedPreferences prefs = requireContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        long userId = prefs.getLong("user_id", -1);

        if (userId == -1) return;

        progressBar.setVisibility(View.VISIBLE);
        TicketRequestDto request = new TicketRequestDto(asunto, mensaje);

        apiService.crearTicket(userId, request).enqueue(new Callback<ApiResponseDto<TicketResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<TicketResponseDto>> call, Response<ApiResponseDto<TicketResponseDto>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ticket creado correctamente", Toast.LENGTH_SHORT).show();
                    loadTickets();
                } else {
                    Toast.makeText(getContext(), "Error al crear ticket", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<TicketResponseDto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDetalleTicket(TicketResponseDto ticket) {
        new AlertDialog.Builder(getContext())
                .setTitle("Ticket #" + ticket.getId())
                .setMessage("Asunto: " + ticket.getAsunto() + "\n\n" +
                           "Mensaje: " + ticket.getMensaje() + "\n\n" +
                           "Estado: " + ticket.getEstado())
                .setPositiveButton("OK", null)
                .show();
    }

    private void actualizarEstadoTicket(Long ticketId, String nuevoEstado) {
        progressBar.setVisibility(View.VISIBLE);
        apiService.resolverTicket(ticketId, nuevoEstado).enqueue(new Callback<ApiResponseDto<TicketResponseDto>>() {
            @Override
            public void onResponse(Call<ApiResponseDto<TicketResponseDto>> call, Response<ApiResponseDto<TicketResponseDto>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Estado actualizado a: " + nuevoEstado, Toast.LENGTH_SHORT).show();
                    loadTickets(); // Recargar lista
                } else {
                    Toast.makeText(getContext(), "Error al actualizar estado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<TicketResponseDto>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
