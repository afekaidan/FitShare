package com.example.fitshare.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.fitshare.MainActivity;
import com.example.fitshare.R;
import com.example.fitshare.activities.AddWorkoutActivity;
import com.example.fitshare.database.DatabaseHelper;
import com.example.fitshare.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private CircleImageView profileImage;
    private TextView nameText;
    private TextView workoutCountText;
    private TextView friendsCountText;
    private FloatingActionButton addWorkoutFab;
    private Button logoutButton;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        sessionManager = new SessionManager(requireContext());

        initializeViews(view);
        loadUserData();
        setupClickListeners();

        return view;
    }

    private void initializeViews(View view) {
        profileImage = view.findViewById(R.id.profileImage);
        nameText = view.findViewById(R.id.nameText);
        workoutCountText = view.findViewById(R.id.workoutCountText);
        friendsCountText = view.findViewById(R.id.friendsCountText);
        addWorkoutFab = view.findViewById(R.id.addWorkoutFab);
        logoutButton = view.findViewById(R.id.logoutButton);

        nameText.setText(sessionManager.getUserName());
    }

    private void loadUserData() {
        int userId = sessionManager.getUserId();
        int workoutCount = dbHelper.getUserWorkoutCount(userId);
        int friendsCount = dbHelper.getUserFriendsCount(userId);

        workoutCountText.setText(workoutCount + " Workouts");
        friendsCountText.setText(friendsCount + " Friends");
    }

    private void setupClickListeners() {
        addWorkoutFab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddWorkoutActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(requireContext(), MainActivity.class));
            requireActivity().finish();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData(); // Refresh data when returning to profile
    }
}