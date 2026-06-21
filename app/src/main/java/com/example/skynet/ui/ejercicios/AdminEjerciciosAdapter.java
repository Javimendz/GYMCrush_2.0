package com.example.skynet.ui.ejercicios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.skynet.R;
import com.example.skynet.data.remote.dto.ExerciseApiRequestDto;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class AdminEjerciciosAdapter extends RecyclerView.Adapter<AdminEjerciciosAdapter.ViewHolder> {

    private final List<ExerciseApiRequestDto> exercises;
    private final OnExerciseClickListener listener;

    public interface OnExerciseClickListener {
        void onAddClick(ExerciseApiRequestDto exercise);
    }

    public AdminEjerciciosAdapter(List<ExerciseApiRequestDto> exercises, OnExerciseClickListener listener) {
        this.exercises = exercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise_external, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseApiRequestDto exercise = exercises.get(position);
        holder.tvName.setText(exercise.getName());
        holder.tvTarget.setText("Objetivo: " + exercise.getTarget());
        holder.tvEquipment.setText("Equipo: " + exercise.getEquipment());

        Glide.with(holder.itemView.getContext())
                .load(exercise.getGifUrl() != null && !exercise.getGifUrl().isEmpty() ? exercise.getGifUrl() : "file:///android_asset/login.jpg")
                .placeholder(android.R.drawable.ic_menu_report_image)
                .error(Glide.with(holder.itemView.getContext()).load("file:///android_asset/login.jpg").centerCrop())
                .centerCrop()
                .into(holder.ivExercise);

        holder.btnAdd.setOnClickListener(v -> listener.onAddClick(exercise));
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivExercise;
        TextView tvName, tvTarget, tvEquipment;
        MaterialButton btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivExercise = itemView.findViewById(R.id.ivExerciseExternal);
            tvName = itemView.findViewById(R.id.tvNameExternal);
            tvTarget = itemView.findViewById(R.id.tvTargetExternal);
            tvEquipment = itemView.findViewById(R.id.tvEquipmentExternal);
            btnAdd = itemView.findViewById(R.id.btnAddExercise);
        }
    }
}
