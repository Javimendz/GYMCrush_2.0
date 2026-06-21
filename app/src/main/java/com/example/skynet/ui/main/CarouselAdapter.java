package com.example.skynet.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.skynet.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {

    private List<CarouselItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CarouselItem item);
    }

    public CarouselAdapter(List<CarouselItem> items) {
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateItems(List<CarouselItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }




    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel_info, parent, false);
        return new CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        CarouselItem item = items.get(position);
        holder.tvTag.setText(item.getTag());
        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.ivIcon.setImageResource(item.getIconRes());
        
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            holder.ivRecipeImage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade()) // Efecto elegante
                    .placeholder(R.drawable.bg_login_modern)
                    .error(Glide.with(holder.itemView.getContext()).load("file:///android_asset/login.jpg").centerCrop())
                    .centerCrop()
                    .into(holder.ivRecipeImage);
            // No aplicamos fondo si hay imagen para no interferir con la transparencia
            holder.itemView.findViewById(R.id.containerCarousel).setBackground(null);
        } else {
            holder.ivRecipeImage.setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.containerCarousel).setBackgroundResource(item.getBgRes());
        }

        if (item.getProgress() >= 0) {
            holder.progress.setVisibility(View.VISIBLE);
            holder.progress.setProgress(item.getProgress());
        } else {
            holder.progress.setVisibility(View.GONE);
        }

        // Click para abrir la URL de la receta o disparar el listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            } else if (item.getRecipeUrl() != null && !item.getRecipeUrl().isEmpty()) {
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(item.getRecipeUrl()));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CarouselViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvTitle, tvDescription;
        ImageView ivIcon, ivRecipeImage;
        LinearProgressIndicator progress;

        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tvTag);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            ivRecipeImage = itemView.findViewById(R.id.ivRecipeImage);
            progress = itemView.findViewById(R.id.carouselProgress);
        }
    }

    public static class CarouselItem {
        private String tag;
        private String title;
        private String description;
        private String imageUrl;
        private String recipeUrl;
        private int iconRes;
        private int bgRes;
        private int progress;
        private Long id; // Nuevo: para tutoriales/ejercicios
        private String fullDescription; // Nuevo: descripción detallada

        public CarouselItem(String tag, String title, String description, int iconRes, int bgRes, int progress) {
            this(tag, title, description, null, null, iconRes, bgRes, progress);
        }

        public CarouselItem(String tag, String title, String description, String imageUrl, String recipeUrl, int iconRes, int bgRes, int progress) {
            this(tag, title, description, imageUrl, recipeUrl, iconRes, bgRes, progress, null, null);
        }

        public CarouselItem(String tag, String title, String description, String imageUrl, String recipeUrl, int iconRes, int bgRes, int progress, Long id, String fullDescription) {
            this.tag = tag;
            this.title = title;
            this.description = description;
            this.imageUrl = imageUrl;
            this.recipeUrl = recipeUrl;
            this.iconRes = iconRes;
            this.bgRes = bgRes;
            this.progress = progress;
            this.id = id;
            this.fullDescription = fullDescription;
        }

        public String getTag() { return tag; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getImageUrl() { return imageUrl; }
        public String getRecipeUrl() { return recipeUrl; }
        public int getIconRes() { return iconRes; }
        public int getBgRes() { return bgRes; }
        public int getProgress() { return progress; }
        public Long getId() { return id; }
        public String getFullDescription() { return fullDescription; }
    }
}
