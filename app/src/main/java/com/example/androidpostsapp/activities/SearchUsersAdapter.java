package com.example.androidpostsapp.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidpostsapp.R;
import com.example.androidpostsapp.models.User;
import com.example.androidpostsapp.storage.GlideApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class SearchUsersAdapter extends RecyclerView.Adapter<SearchUserAdapterViewHolder> {

    private List<User> mData;
    private LayoutInflater mInflater;
    private StorageReference mStorageRef;
    private Context context;


    public SearchUsersAdapter(List<User> mData, Context context) {
        this.mData = mData;

        this.mInflater = LayoutInflater.from(context);
        this.mStorageRef = FirebaseStorage.getInstance().getReference();
        this.context = context;
    }

    @NonNull
    @Override
    public SearchUserAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.profile_search_layout, parent, false);
        return new SearchUserAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUserAdapterViewHolder holder, int position) {
        User user = mData.get(position);
        holder.username.setText(user.getUsername());
        holder.gender.setText(user.getGender());
        holder.birthday.setText(user.getBirthday());

        StorageReference fileRef = mStorageRef.child(user.getImageURL());

        GlideApp.with(context)
                .load(fileRef)
                .into(holder.profilePicture);

        holder.messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
