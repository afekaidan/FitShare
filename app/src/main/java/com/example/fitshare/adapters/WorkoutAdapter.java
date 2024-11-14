package com.example.fitshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitshare.R;
import com.example.fitshare.models.WorkoutLog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private Context context;
    private List<WorkoutLog> workouts = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public WorkoutAdapter(Context context) {
        this.context = context;
    }

    public void setWorkouts(List<WorkoutLog> workouts) {
        this.workouts = workouts;
        notifyDataSetChanged();
    }

    @Override
    public WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_workout_profile, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WorkoutViewHolder holder, int position) {
        WorkoutLog workout = workouts.get(position);
        holder.bind(workout);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView workoutTypeText;
        TextView descriptionText;
        TextView dateText;
        TextView durationText;

        WorkoutViewHolder(View itemView) {
            super(itemView);
            workoutTypeText = itemView.findViewById(R.id.workoutTypeText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            dateText = itemView.findViewById(R.id.dateText);
            durationText = itemView.findViewById(R.id.durationText);
        }

        void bind(WorkoutLog workout) {
            workoutTypeText.setText(workout.getWorkoutType());
            descriptionText.setText(workout.getDescription());
            dateText.setText(dateFormat.format(workout.getDate()));
            durationText.setText(workout.getDuration() + " minutes");
        }
    }
}