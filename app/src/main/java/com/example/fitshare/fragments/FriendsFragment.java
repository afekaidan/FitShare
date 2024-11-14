package com.example.fitshare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitshare.R;
import com.example.fitshare.adapters.FriendAdapter;
import com.example.fitshare.database.DatabaseHelper;
import com.example.fitshare.models.User;
import com.example.fitshare.utils.SessionManager;
import java.util.List;

public class FriendsFragment extends Fragment implements FriendAdapter.OnFriendActionListener {
    private SearchView searchView;
    private RecyclerView friendsRecyclerView;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private FriendAdapter friendAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        dbHelper = new DatabaseHelper(getContext());
        sessionManager = new SessionManager(getContext());

        initializeViews(view);
        setupRecyclerView();
        setupSearchView();
        loadFriends();

        return view;
    }

    private void initializeViews(View view) {
        searchView = view.findViewById(R.id.searchView);
        friendsRecyclerView = view.findViewById(R.id.friendsRecyclerView);
    }

    private void setupRecyclerView() {
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendAdapter = new FriendAdapter(getContext(), this);
        friendsRecyclerView.setAdapter(friendAdapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 3) {
                    searchUsers(newText);
                } else if (newText.isEmpty()) {
                    loadFriends();
                }
                return true;
            }
        });
    }

    private void loadFriends() {
        List<User> friends = dbHelper.getUserFriends(sessionManager.getUserId());
        friendAdapter.setFriends(friends);
    }

    private void searchUsers(String query) {
        if (query.isEmpty()) {
            loadFriends();
        } else {
            List<User> users = dbHelper.searchUsers(query, sessionManager.getUserId());
            friendAdapter.setFriends(users);
        }
    }

    // Implement OnFriendActionListener methods
    @Override
    public void onAddFriend(User user) {
        try {
            dbHelper.addFriend(sessionManager.getUserId(), user.getId());
            Toast.makeText(getContext(), "Added " + user.getName() + " as friend", Toast.LENGTH_SHORT).show();
            loadFriends();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to add friend", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemoveFriend(User user) {
        try {
            dbHelper.removeFriend(sessionManager.getUserId(), user.getId());
            Toast.makeText(getContext(), "Removed " + user.getName() + " from friends", Toast.LENGTH_SHORT).show();
            loadFriends();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to remove friend", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFriends();
    }
}