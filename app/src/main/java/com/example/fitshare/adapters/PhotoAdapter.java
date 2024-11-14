package com.example.fitshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.example.fitshare.R;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.fitshare.dialogs.PhotoDetailDialog;
import com.example.fitshare.models.Photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private Context context;
    private List<Photo> photos = new ArrayList<>();

    public PhotoAdapter(Context context) {
        this.context = context;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    @Override
    public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhotoViewHolder holder, int position) {
        Photo photo = photos.get(position);
        holder.bind(photo);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImage;
        TextView userNameText;
        TextView captionText;

        PhotoViewHolder(View itemView) {
            super(itemView);
            photoImage = itemView.findViewById(R.id.photoImage);
            userNameText = itemView.findViewById(R.id.userNameText);
            captionText = itemView.findViewById(R.id.captionText);
        }

        void bind(Photo photo) {
            userNameText.setText(photo.getUserName());
            captionText.setText(photo.getCaption());

            try {
                Glide.with(context)
                        .load(new File(photo.getImagePath()))
                        .centerCrop()
                        .into(photoImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

            itemView.setOnClickListener(v -> {
                PhotoDetailDialog dialog = new PhotoDetailDialog(context, photo);
                dialog.setOnPhotoDeletedListener(() -> {
                    // Remove the photo from the list and refresh
                    int position = photos.indexOf(photo);
                    if (position != -1) {
                        photos.remove(position);
                        notifyItemRemoved(position);
                    }
                });
                dialog.show();
            });
        }
    }
}
