package com.example.fitshare.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.fitshare.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.fitshare.database.DatabaseHelper;
import com.example.fitshare.utils.SessionManager;

public class AddWorkoutActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Spinner workoutTypeSpinner;
    private EditText descriptionInput;
    private EditText durationInput;
    private Button saveButton;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);

        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        initializeViews();
        setupToolbar();
        setupSpinner();
        setupClickListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        workoutTypeSpinner = findViewById(R.id.workoutTypeSpinner);
        descriptionInput = findViewById(R.id.descriptionInput);
        durationInput = findViewById(R.id.durationInput);
        saveButton = findViewById(R.id.saveButton);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Add Workout");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupSpinner() {
        String[] workoutTypes = {
                "Select workout type",
                "Running",
                "Cycling",
                "Swimming",
                "Weightlifting",
                "Yoga",
                "HIIT",
                "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                workoutTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutTypeSpinner.setAdapter(adapter);
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> validateAndSaveWorkout());
    }

    private void validateAndSaveWorkout() {
        // Validate workout type selection
        int selectedPosition = workoutTypeSpinner.getSelectedItemPosition();
        if (selectedPosition == 0) {
            Toast.makeText(this, "Please select a workout type", Toast.LENGTH_SHORT).show();
            return;
        }
        String workoutType = workoutTypeSpinner.getSelectedItem().toString();

        // Validate description
        String description = descriptionInput.getText().toString().trim();
        if (TextUtils.isEmpty(description)) {
            descriptionInput.setError("Please enter a description");
            return;
        }

        // Validate duration
        String durationStr = durationInput.getText().toString().trim();
        if (TextUtils.isEmpty(durationStr)) {
            durationInput.setError("Please enter duration");
            return;
        }

        try {
            int duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                durationInput.setError("Duration must be greater than 0");
                return;
            }

            // Save workout
            int userId = sessionManager.getUserId();
            long result = dbHelper.addWorkout(userId, workoutType, description, duration);

            if (result != -1) {
                Toast.makeText(this, "Workout saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save workout", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            durationInput.setError("Please enter a valid number");
        }
    }
}
