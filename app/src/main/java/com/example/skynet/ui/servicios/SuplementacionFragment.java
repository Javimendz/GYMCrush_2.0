package com.example.skynet.ui.servicios;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.skynet.data.model.CartItem;
import com.example.skynet.data.model.Suplemento;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.skynet.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SuplementacionFragment extends Fragment {

    private RecyclerView rvSuplementos;
    private SuplementoAdapter adapter;
    private List<Suplemento> listaSuplementos;
    private final List<CartItem> carrito = new ArrayList<>();
    private TextView tvCartCount;
    private FloatingActionButton fabAdd;
    private boolean isAdmin = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suplementacion, container, false);

        tvCartCount = view.findViewById(R.id.tv_cart_count);
        rvSuplementos = view.findViewById(R.id.rv_suplementos);
        fabAdd = view.findViewById(R.id.fab_add_producto);

        checkAdminRole();

        // Configuración Botón Volver
        View btnBack = view.findViewById(R.id.btnBackSuple);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }

        // Configuración Botón Ver Carrito
        View btnCarrito = view.findViewById(R.id.btn_ver_carrito);
        if (btnCarrito != null) {
            btnCarrito.setOnClickListener(v -> {
                if (carrito.isEmpty()) {
                    Toast.makeText(getContext(), "El carrito está vacío", Toast.LENGTH_SHORT).show();
                } else {
                    mostrarPasarelaPago();
                }
            });
        }

        fabAdd.setOnClickListener(v -> mostrarDialogoProducto(null));

        inicializarDatos();
        configurarRecyclerView();

        return view;
    }

    private void checkAdminRole() {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences("DatosUsuario", Context.MODE_PRIVATE);
        Set<String> roles = prefs.getStringSet("roles", new HashSet<>());
        isAdmin = roles.contains("ROLE_ADMIN") || roles.contains("ADMIN");
        
        if (isAdmin) {
            fabAdd.setVisibility(View.VISIBLE);
        } else {
            fabAdd.setVisibility(View.GONE);
        }
    }

    private void inicializarDatos() {
        listaSuplementos = new ArrayList<>();
        
        listaSuplementos.add(new Suplemento("1", "Bodylab24 Protein Bar", "Bodylab24 Deluxe Protein Bar Fudge Brownie 12 x 50 g, Protein Bar with 11 g Protein per Bar.", 24.99, "https://m.media-amazon.com/images/I/71PEx1L+w0L._AC_UL320_.jpg", "BARRITAS"));
        listaSuplementos.add(new Suplemento("2", "ZMA Max", "ZMA Max - 180 Capsules - High Dose - Zinc + Magnesium + Vitamin B6 - High-quality Complex.", 18.50, "https://m.media-amazon.com/images/I/71oP4iA2wbL._AC_UL320_.jpg", "SALUD"));
        listaSuplementos.add(new Suplemento("3", "Ultra Omega 3", "Ultra Omega 3 Capsules 2000mg TG Form - 1000mg EPA & 500mg DHA per 2 Capsules.", 22.00, "https://m.media-amazon.com/images/I/810JxVa8S7L._AC_UL320_.jpg", "SALUD"));
        listaSuplementos.add(new Suplemento("4", "Multivitamin Tablets", "Multivitamin Tablets (30 Servings) - High Dose - Multivitamin Capsules with Essential Vitamins.", 15.00, "https://m.media-amazon.com/images/I/71YIwxACV4L._AC_UL320_.jpg", "SALUD"));
        listaSuplementos.add(new Suplemento("5", "ABE Pre Workout", "Applied Nutrition ABE Pre Workout Powder - Ultimate Performance.", 29.99, "https://m.media-amazon.com/images/I/61rNCggtm2L._AC_UL320_.jpg", "PRE-ENTRENO"));
        listaSuplementos.add(new Suplemento("6", "Creatine Creapure", "Creatine Capsules 240 x with 850 mg Creapure Creatine Monohydrate - High-Quality.", 26.50, "https://m.media-amazon.com/images/I/61FKCG24OxL._AC_SY679_.jpg", "CREATINAS"));
    }

    private void configurarRecyclerView() {
        adapter = new SuplementoAdapter(listaSuplementos, new SuplementoAdapter.OnSuplementoClickListener() {
            @Override
            public void onSuplementoClick(Suplemento suplemento) {
                mostrarDetalleProducto(suplemento);
            }

            @Override
            public void onEditClick(Suplemento suplemento) {
                mostrarDialogoProducto(suplemento);
            }

            @Override
            public void onDeleteClick(Suplemento suplemento) {
                listaSuplementos.remove(suplemento);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Producto eliminado", Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setAdmin(isAdmin);
        rvSuplementos.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvSuplementos.setAdapter(adapter);
    }

    private void mostrarDialogoProducto(@Nullable Suplemento suplementoExistente) {
        if (getContext() == null) return;

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_edit_producto);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvTitulo = dialog.findViewById(R.id.tv_dialog_titulo);
        TextInputEditText etNombre = dialog.findViewById(R.id.et_nombre_producto);
        TextInputEditText etPrecio = dialog.findViewById(R.id.et_precio_producto);
        TextInputEditText etDesc = dialog.findViewById(R.id.et_desc_producto);
        TextInputEditText etUrl = dialog.findViewById(R.id.et_url_producto);
        MaterialButton btnGuardar = dialog.findViewById(R.id.btn_guardar_producto);

        if (suplementoExistente != null) {
            tvTitulo.setText("EDITAR PRODUCTO");
            etNombre.setText(suplementoExistente.getNombre());
            etPrecio.setText(String.valueOf(suplementoExistente.getPrecio()));
            etDesc.setText(suplementoExistente.getDescripcion());
            etUrl.setText(suplementoExistente.getImageUrl());
        }

        dialog.findViewById(R.id.btn_cancelar_producto).setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString();
            String precioStr = etPrecio.getText().toString();
            String desc = etDesc.getText().toString();
            String url = etUrl.getText().toString();

            if (nombre.isEmpty() || precioStr.isEmpty()) {
                Toast.makeText(getContext(), "Nombre y precio obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double precio = Double.parseDouble(precioStr);

                if (suplementoExistente == null) {
                    Suplemento nuevo = new Suplemento(String.valueOf(System.currentTimeMillis()), nombre, desc, precio, url, "General");
                    listaSuplementos.add(nuevo);
                } else {
                    suplementoExistente.setNombre(nombre);
                    suplementoExistente.setPrecio(precio);
                    suplementoExistente.setDescripcion(desc);
                    suplementoExistente.setImageUrl(url);
                }

                adapter.notifyDataSetChanged();
                dialog.dismiss();
                Toast.makeText(getContext(), "Producto guardado", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Precio no válido", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void mostrarDetalleProducto(Suplemento suplemento) {
        if (getContext() == null) return;

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_detalle_producto);
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        ImageView img = dialog.findViewById(R.id.img_detalle_producto);
        TextView tvNombre = dialog.findViewById(R.id.tv_detalle_nombre);
        TextView tvPrecio = dialog.findViewById(R.id.tv_detalle_precio);
        TextView tvDesc = dialog.findViewById(R.id.tv_detalle_descripcion);
        MaterialButton btnAñadir = dialog.findViewById(R.id.btn_comprar_ahora);
        TextView btnCerrar = dialog.findViewById(R.id.btn_cerrar_detalle);

        Glide.with(this).load(suplemento.getImageUrl()).placeholder(R.drawable.whey).into(img);
        tvNombre.setText(suplemento.getNombre());
        tvPrecio.setText(String.format(Locale.getDefault(), "%.2f€", suplemento.getPrecio()));
        tvDesc.setText(suplemento.getDescripcion());

        btnAñadir.setOnClickListener(v -> {
            añadirAlCarrito(suplemento);
            dialog.dismiss();
        });

        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void añadirAlCarrito(Suplemento suplemento) {
        boolean encontrado = false;
        for (CartItem item : carrito) {
            if (item.getSuplemento().getId().equals(suplemento.getId())) {
                item.setCantidad(item.getCantidad() + 1);
                encontrado = true;
                break;
            }
        }
        if (!encontrado) {
            carrito.add(new CartItem(suplemento, 1));
        }
        actualizarContadorCarrito();
        Toast.makeText(getContext(), "Añadido: " + suplemento.getNombre(), Toast.LENGTH_SHORT).show();
    }

    private void actualizarContadorCarrito() {
        int count = 0;
        for (CartItem item : carrito) count += item.getCantidad();
        
        if (count > 0) {
            tvCartCount.setVisibility(View.VISIBLE);
            tvCartCount.setText(String.valueOf(count));
        } else {
            tvCartCount.setVisibility(View.GONE);
        }
    }

    private double calcularTotalCarrito() {
        double total = 0;
        for (CartItem item : carrito) total += item.getSubtotal();
        return total;
    }

    private void mostrarPasarelaPago() {
        if (getContext() == null) return;

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_pasarela_pago);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView tvResumen = dialog.findViewById(R.id.tv_resumen_pago);
        MaterialButton btnPagar = dialog.findViewById(R.id.btn_pagar_final);
        TextView btnCancelar = dialog.findViewById(R.id.btn_cancelar_pago);

        StringBuilder resumen = new StringBuilder();
        for (CartItem item : carrito) {
            resumen.append(item.getCantidad()).append("x ").append(item.getSuplemento().getNombre())
                    .append(" (").append(String.format(Locale.getDefault(), "%.2f€", item.getSubtotal())).append(")\n");
        }
        resumen.append("\nTOTAL: ").append(String.format(Locale.getDefault(), "%.2f€", calcularTotalCarrito()));
        tvResumen.setText(resumen.toString());

        btnPagar.setOnClickListener(v -> {
            btnPagar.setEnabled(false);
            btnPagar.setText("Conectando con Stripe...");
            
            // Simulación de respuesta de pasarela externa
            v.postDelayed(() -> {
                Toast.makeText(getContext(), "Pago verificado por 3D Secure", Toast.LENGTH_SHORT).show();
                v.postDelayed(() -> {
                    Toast.makeText(getContext(), "¡Pedido realizado con éxito!", Toast.LENGTH_LONG).show();
                    carrito.clear();
                    actualizarContadorCarrito();
                    dialog.dismiss();
                }, 1500);
            }, 2500);
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
