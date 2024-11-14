package com.example.fitshare.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.fitshare.R;
import com.example.fitshare.database.DatabaseHelper;
import com.example.fitshare.models.Photo;
import com.example.fitshare.utils.SessionManager;
import com.example.fitshare.utils.Utils;
import java.io.File;

public class PhotoDetailDialog extends Dialog {
    private Photo photo;
    private ImageView photoImage;
    private TextView userNameText;
    private TextView captionText;
    private TextView dateText;
    private Button deleteButton;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private OnPhotoDeletedListener listener;

    public interface OnPhotoDeletedListener {
        void onPhotoDeleted();
    }

    public PhotoDetailDialog(Context context, Photo photo) {
        super(context, R.style.FullWidthDialog);  // Use custom style
        this.photo = photo;
        dbHelper = new DatabaseHelper(context);
        sessionManager = new SessionManager(context);
    }

    public void setOnPhotoDeletedListener(OnPhotoDeletedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_photo_detail);

        // Set dialog width to match parent
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
        // Initialize and setup views
        initializeViews();
        loadPhotoDetails();
        setupDeleteButton();
    }

    private void initializeViews() {
        photoImage = findViewById(R.id.photoImage);
        userNameText = findViewById(R.id.userNameText);
        captionText = findViewById(R.id.captionText);
        dateText = findViewById(R.id.dateText);
        deleteButton = findViewById(R.id.deleteButton);
    }

    private void loadPhotoDetails() {
        userNameText.setText(photo.getUserName());
        captionText.setText(photo.getCaption());
        dateText.setText(Utils.formatDateTime(photo.getUploadDate()));

        try {
            Glide.with(getContext())
                    .load(new File(photo.getImagePath()))
                    .into(photoImage);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDeleteButton() {
        // Only show delete button if the photo belongs to the current user
        if (photo.getUserId() == sessionManager.getUserId()) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v -> deletePhoto());
        } else {
            deleteButton.setVisibility(View.GONE);
        }
    }

    private void deletePhoto() {
        // Delete from database
        boolean deleted = dbHelper.deletePhoto(photo.getId());
        if (deleted) {
            // Delete the actual image file
            try {
                File imageFile = new File(photo.getImagePath());
                if (imageFile.exists()) {
                    imageFile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(getContext(), "Photo deleted successfully", Toast.LENGTH_SHORT).show();
            if (listener != null) {
                listener.onPhotoDeleted();
            }
            dismiss();
        } else {
            Toast.makeText(getContext(), "Failed to delete photo", Toast.LENGTH_SHORT).show();
        }
    }
}