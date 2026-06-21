package com.example.skynet.ui.dieta;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.ComidaDiariaRequestDto;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.ViewHolder> {

    private List<ComidaDiariaRequestDto> items = new ArrayList<>();
    private final Set<Integer> selectedPositions = new HashSet<>();

    public void setItems(List<ComidaDiariaRequestDto> items) {
        this.items = items;
        this.selectedPositions.clear();
        notifyDataSetChanged();
    }

    public List<ComidaDiariaRequestDto> getSelectedItems() {
        List<ComidaDiariaRequestDto> selected = new ArrayList<>();
        for (Integer pos : selectedPositions) {
            selected.add(items.get(pos));
        }
        return selected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComidaDiariaRequestDto item = items.get(position);
        holder.tvName.setText(item.getNombreAlimento() != null ? item.getNombreAlimento() : "Alimento");

        double cal = item.getCalorias() != null ? item.getCalorias() : 0;
        double prot = item.getProteina() != null ? item.getProteina() : 0;
        double carb = item.getCarbohidratos() != null ? item.getCarbohidratos() : 0;
        double fat = item.getGrasas() != null ? item.getGrasas() : 0;

        String macros = String.format(Locale.getDefault(), "%.0f kcal | P: %.0fg | C: %.0fg | G: %.0fg",
                cal, prot, carb, fat);
        holder.tvMacros.setText(macros);

        holder.cbSelected.setOnCheckedChangeListener(null);
        holder.cbSelected.setChecked(selectedPositions.contains(position));

        holder.cbSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPositions.add(position);
            } else {
                selectedPositions.remove(position);
            }
        });

        if (item.getImagenUrl() != null && !item.getImagenUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImagenUrl())
                    .placeholder(R.drawable.ic_compras)
                    .error(R.drawable.ic_compras)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.ic_compras);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelected;
        ImageView ivImage;
        TextView tvName, tvMacros;

        ViewHolder(View itemView) {
            super(itemView);
            cbSelected = itemView.findViewById(R.id.cbSelected);
            ivImage = itemView.findViewById(R.id.ivFoodImage);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvMacros = itemView.findViewById(R.id.tvFoodMacros);
        }
    }
}