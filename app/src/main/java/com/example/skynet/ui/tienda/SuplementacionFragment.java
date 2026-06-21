package com.example.skynet.ui.tienda;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skynet.R;
import com.example.skynet.data.model.Suplemento;
import com.example.skynet.ui.servicios.SuplementoAdapter;

import java.util.ArrayList;
import java.util.List;

public class SuplementacionFragment extends Fragment implements SuplementoAdapter.OnSuplementoClickListener {

    private RecyclerView recyclerView;
    private SuplementoAdapter adapter;
    private List<Suplemento> listaSuplementos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suplementacion, container, false);

        // Botón volver
        view.findViewById(R.id.btnBackSuple).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        setupRecyclerView(view);
        cargarDatos();

        return view;
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.rv_suplementos);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        
        listaSuplementos = new ArrayList<>();
        adapter = new SuplementoAdapter(listaSuplementos, this);
        recyclerView.setAdapter(adapter);
    }

    private void cargarDatos() {
        listaSuplementos.clear();
        
        listaSuplementos.add(new Suplemento("1", "Bodylab24 Protein Bar", "Bodylab24 Deluxe Protein Bar Fudge Brownie 12 x 50 g, Protein Bar with 11 g Protein per Bar.", 24.99, "https://m.media-amazon.com/images/I/71PEx1L+w0L._AC_UL320_.jpg", "BARRITAS"));
        listaSuplementos.add(new Suplemento("2", "ZMA Max", "ZMA Max - 180 Capsules - High Dose - Zinc + Magnesium + Vitamin B6 - High-quality Complex.", 18.50, "https://m.media-amazon.com/images/I/71oP4iA2wbL._AC_UL320_.jpg", "SALUD"));
        listaSuplementos.add(new Suplemento("3", "Ultra Omega 3", "Ultra Omega 3 Capsules 2000mg TG Form - 1000mg EPA & 500mg DHA per 2 Capsules.", 22.00, "https://m.media-amazon.com/images/I/810JxVa8S7L._AC_UL320_.jpg", "SALUD"));
        listaSuplementos.add(new Suplemento("4", "Multivitamin Tablets", "Multivitamin Tablets (30 Servings) - High Dose - Multivitamin Capsules with Essential Vitamins.", 15.00, "https://m.media-amazon.com/images/I/71YIwxACV4L._AC_UL320_.jpg", "SALUD"));
        listaSuplementos.add(new Suplemento("5", "ABE Pre Workout", "Applied Nutrition ABE Pre Workout Powder - Ultimate Performance.", 29.99, "https://m.media-amazon.com/images/I/61rNCggtm2L._AC_UL320_.jpg", "PRE-ENTRENO"));
        listaSuplementos.add(new Suplemento("6", "Creatine Creapure", "Creatine Capsules 240 x with 850 mg Creapure Creatine Monohydrate - High-Quality.", 26.50, "https://m.media-amazon.com/images/I/61FKCG24OxL._AC_SY679_.jpg", "CREATINAS"));

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSuplementoClick(Suplemento suplemento) {
        mostrarDialogoDetalles(suplemento.getNombre(), suplemento.getDescripcion());
    }

    @Override
    public void onEditClick(Suplemento suplemento) {
        // Implementar si es necesario
    }

    @Override
    public void onDeleteClick(Suplemento suplemento) {
        // Implementar si es necesario
    }

    private void mostrarDialogoDetalles(String nombre, String detalles) {
        if (getContext() == null) return;

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_entrenador_info); 
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvNombre = dialog.findViewById(R.id.tv_dialog_nombre);
        TextView tvDesc = dialog.findViewById(R.id.tv_dialog_descripcion);
        View btnCerrar = dialog.findViewById(R.id.btn_cerrar_dialog);

        if (tvNombre != null) tvNombre.setText(nombre);
        if (tvDesc != null) tvDesc.setText(detalles);

        if (btnCerrar != null) {
            btnCerrar.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }
}
