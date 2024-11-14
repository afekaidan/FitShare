package com.example.fitshare.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitshare.adapters.PhotoAdapter;
import com.example.fitshare.database.DatabaseHelper;
import com.example.fitshare.dialogs.AddPhotoDialog;
import com.example.fitshare.dialogs.PhotoDetailDialog;
import com.example.fitshare.models.Photo;
import com.example.fitshare.utils.SessionManager;
import com.example.fitshare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class PhotosFragment extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 1;

    private RecyclerView photosRecyclerView;
    private FloatingActionButton addPhotoFab;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private PhotoAdapter photoAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        dbHelper = new DatabaseHelper(getContext());
        sessionManager = new SessionManager(getContext());

        initializeViews(view);
        setupRecyclerView();
        loadPhotos();

        return view;
    }

    private void initializeViews(View view) {
        photosRecyclerView = view.findViewById(R.id.photosRecyclerView);
        addPhotoFab = view.findViewById(R.id.addPhotoFab);

        addPhotoFab.setOnClickListener(v -> openImagePicker());
    }

    private void showPhotoDetail(Photo photo) {
        PhotoDetailDialog dialog = new PhotoDetailDialog(requireContext(), photo);
        dialog.setOnPhotoDeletedListener(() -> loadPhotos()); // Refresh after delete
        dialog.show();
    }

    private void setupRecyclerView() {
        photosRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Changed to 2 columns
        photoAdapter = new PhotoAdapter(getContext());
        photosRecyclerView.setAdapter(photoAdapter);
    }

    private void loadPhotos() {
        List<Photo> photos = dbHelper.getUserAndFriendsPhotos(sessionManager.getUserId());
        photoAdapter.setPhotos(photos);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            showAddPhotoDialog(imageUri);
        }
    }

    private void showAddPhotoDialog(Uri imageUri) {
        AddPhotoDialog dialog = new AddPhotoDialog(getContext(), imageUri);
        dialog.setOnPhotoAddedListener(() -> loadPhotos());
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPhotos();
    }
}
