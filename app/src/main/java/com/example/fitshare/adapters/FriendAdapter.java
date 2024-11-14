package com.example.fitshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitshare.database.DatabaseHelper;
import com.example.fitshare.models.User;
import com.example.fitshare.utils.SessionManager;

import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.List;
import com.example.fitshare.R;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    public interface OnFriendActionListener {
        void onAddFriend(User user);
        void onRemoveFriend(User user);
    }

    private Context context;
    private List<User> users = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private OnFriendActionListener listener;

    public FriendAdapter(Context context, OnFriendActionListener listener) {
        this.context = context;
        this.listener = listener;
        this.dbHelper = new DatabaseHelper(context);
        this.sessionManager = new SessionManager(context);
    }

    public void setFriends(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage;
        TextView nameText;
        Button actionButton;

        FriendViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            nameText = itemView.findViewById(R.id.nameText);
            actionButton = itemView.findViewById(R.id.actionButton);
        }

        void bind(User user) {
            nameText.setText(user.getName());

            boolean isFriend = dbHelper.isFriend(sessionManager.getUserId(), user.getId());
            if (isFriend) {
                actionButton.setText("Remove");
                actionButton.setOnClickListener(v -> listener.onRemoveFriend(user));
            } else {
                actionButton.setText("Add");
                actionButton.setOnClickListener(v -> listener.onAddFriend(user));
            }
        }
    }
}
