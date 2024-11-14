package com.example.fitshare.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import com.example.fitshare.R;
import com.example.fitshare.database.DatabaseHelper;
import com.example.fitshare.utils.SessionManager;

public class AddPhotoDialog extends Dialog {
    private Uri imageUri;
    private ImageView previewImage;
    private EditText captionInput;
    private Button saveButton;
    private Button cancelButton;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private OnPhotoAddedListener listener;

    public AddPhotoDialog(Context context, Uri imageUri) {
        super(context);
        this.imageUri = imageUri;
        dbHelper = new DatabaseHelper(context);
        sessionManager = new SessionManager(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_photo);

        initializeViews();
        loadPreviewImage();
        setupClickListeners();
    }

    private void initializeViews() {
        previewImage = findViewById(R.id.previewImage);
        captionInput = findViewById(R.id.captionInput);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void loadPreviewImage() {
        Glide.with(getContext())
                .load(imageUri)
                .centerCrop()
                .into(previewImage);
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> savePhoto());
        cancelButton.setOnClickListener(v -> dismiss());
    }

    private void savePhoto() {
        String caption = captionInput.getText().toString().trim();
        if (caption.isEmpty()) {
            Toast.makeText(getContext(), "Please add a caption", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save image to internal storage
        String imagePath = saveImageToInternalStorage(imageUri);
        if (imagePath != null) {
            // Save to database
            long result = dbHelper.addPhoto(sessionManager.getUserId(), imagePath, caption);
            if (result != -1) {
                Toast.makeText(getContext(), "Photo added successfully",
                        Toast.LENGTH_SHORT).show();
                if (listener != null) {
                    listener.onPhotoAdded();
                }
                dismiss();
            } else {
                Toast.makeText(getContext(), "Failed to add photo",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String saveImageToInternalStorage(Uri sourceUri) {
        try {
            InputStream inputStream = getContext().getContentResolver()
                    .openInputStream(sourceUri);
            File outputDir = getContext().getFilesDir();
            File outputFile = new File(outputDir, "photo_"
                    + System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();
            return outputFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setOnPhotoAddedListener(OnPhotoAddedListener listener) {
        this.listener = listener;
    }

    public interface OnPhotoAddedListener {
        void onPhotoAdded();
    }
}
