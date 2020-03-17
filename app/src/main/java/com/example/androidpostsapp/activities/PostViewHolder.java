package com.example.androidpostsapp.activities;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.androidpostsapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public  class PostViewHolder extends RecyclerView.ViewHolder {


    CircleImageView profilePicture;
    TextView username, starsCounter , bodyText;
    ImageView starButton;
    ImageButton deleteButton;
    public PostViewHolder(View v) {
        super(v);

        profilePicture = v.findViewById(R.id.profile_picture_post_home);
        username = v.findViewById(R.id.txt_username_post_home);
        starsCounter = v.findViewById(R.id.stars_counter);
        starButton = v.findViewById(R.id.star_btn);
        bodyText = v.findViewById(R.id.card_text);
        deleteButton = v.findViewById(R.id.post_layout_imgbtn_delete);


    }
}
