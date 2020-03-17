package com.example.androidpostsapp.activities;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidpostsapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUserAdapterViewHolder extends RecyclerView.ViewHolder {

    CircleImageView profilePicture;
    TextView username, gender , birthday;
    ImageButton messageButton;
    public SearchUserAdapterViewHolder(View v) {
        super(v);

        profilePicture = v.findViewById(R.id.profile_picture_profile_search);
        username = v.findViewById(R.id.txt_username_profile_search);
        gender = v.findViewById(R.id.txt_gender_profile_search);
        birthday = v.findViewById(R.id.txt_birthday_profile_search);
        messageButton = v.findViewById(R.id.imgbtn_search_msg);


    }
}
