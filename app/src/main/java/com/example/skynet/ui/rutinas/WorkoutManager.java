package com.example.skynet.ui.rutinas;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import com.example.skynet.ui.ejercicios.Ejercicio;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;

public class WorkoutManager {
    private static WorkoutManager instance;
    private static final String PREF_NAME = "WorkoutState";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_REAL_TIME_BASE = "real_time_base";
    private static final String KEY_IS_ACTIVE = "is_active";
    private static final String KEY_EXERCISES = "exercises";

    private long startTime;
    private boolean isActive = false;
    private List<Ejercicio> currentExercises = new ArrayList<>();
    private String lastExerciseName = "";

    private WorkoutManager() {}

    public static synchronized WorkoutManager getInstance() {
        if (instance == null) {
            instance = new WorkoutManager();
        }
        return instance;
    }

    public void startWorkout(Context context, List<Ejercicio> exercises) {
        this.startTime = SystemClock.elapsedRealtime();
        this.isActive = true;
        this.currentExercises = exercises != null ? exercises : new ArrayList<>();
        updateLastExercise();
        saveState(context);
    }

    public void stopWorkout(Context context) {
        this.isActive = false;
        this.currentExercises.clear();
        this.lastExerciseName = "";
        clearState(context);
    }

    public void restoreState(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.isActive = prefs.getBoolean(KEY_IS_ACTIVE, false);
        if (this.isActive) {
            // Restore the original anchor time.
            // SystemClock.elapsedRealtime() is stable across app process deaths.
            this.startTime = prefs.getLong(KEY_START_TIME, 0);

            String json = prefs.getString(KEY_EXERCISES, null);
            if (json != null) {
                Gson gson = new Gson();
                this.currentExercises = gson.fromJson(json, new TypeToken<List<Ejercicio>>(){}.getType());
            }
            updateLastExercise();
        }
    }

    public void saveState(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_IS_ACTIVE, isActive);
        editor.putLong(KEY_START_TIME, startTime);
        editor.putLong(KEY_REAL_TIME_BASE, SystemClock.elapsedRealtime());
        editor.putString(KEY_EXERCISES, new Gson().toJson(currentExercises));
        editor.apply();
    }

    private void clearState(Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }

    public boolean isActive() {
        return isActive;
    }

    public long getStartTime() {
        return startTime;
    }

    public String getLastExerciseName() {
        return lastExerciseName;
    }

    public List<Ejercicio> getCurrentExercises() {
        return currentExercises;
    }

    public void setExercises(Context context, List<Ejercicio> exercises) {
        this.currentExercises = exercises;
        updateLastExercise();
        saveState(context);
    }

    private void updateLastExercise() {
        if (currentExercises != null && !currentExercises.isEmpty()) {
            this.lastExerciseName = currentExercises.get(currentExercises.size() - 1).getNombre();
        } else {
            this.lastExerciseName = "Empezar entrenamiento";
        }
    }
}
