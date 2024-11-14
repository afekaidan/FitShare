package com.example.fitshare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitshare.R;
import com.example.fitshare.adapters.WorkoutFeedAdapter;
import com.example.fitshare.database.DatabaseHelper;
import com.example.fitshare.models.WorkoutLog;
import com.example.fitshare.utils.SessionManager;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView workoutFeedRecyclerView;
    private TextView welcomeText;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private WorkoutFeedAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        dbHelper = new DatabaseHelper(getContext());
        sessionManager = new SessionManager(getContext());

        initializeViews(view);
        setupRecyclerView();
        loadWorkoutFeed();

        return view;
    }

    private void initializeViews(View view) {
        welcomeText = view.findViewById(R.id.welcomeText);
        workoutFeedRecyclerView = view.findViewById(R.id.workoutFeedRecyclerView);

        String userName = sessionManager.getUserName();
        welcomeText.setText("Welcome back, " + userName + "!");
    }

    private void setupRecyclerView() {
        workoutFeedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WorkoutFeedAdapter(getContext());
        workoutFeedRecyclerView.setAdapter(adapter);
    }

    private void loadWorkoutFeed() {
        List<WorkoutLog> workouts = dbHelper.getAllFriendsWorkouts(sessionManager.getUserId());
        adapter.setWorkouts(workouts);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorkoutFeed(); // Refresh feed when returning to fragment
    }
}