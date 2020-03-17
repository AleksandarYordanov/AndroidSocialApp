package com.example.androidpostsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.androidpostsapp.R;
import com.example.androidpostsapp.databinding.ActivityWritePostBinding;
import com.example.androidpostsapp.models.Post;
import com.example.androidpostsapp.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WritePostActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityWritePostBinding binding;
    FirebaseUser mUser;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_write_post);

        binding.btnCancel.setOnClickListener(this);
        binding.btnPost.setOnClickListener(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                binding.txtUsername.setText(user.getUsername());
                if (user.getImageURL().equals("default")){
                    binding.profilePictureWritePost.setImageResource(R.drawable.as);
                }else {
                    Glide.with(WritePostActivity.this).load(user.getImageURL()).into(binding.profilePictureWritePost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(WritePostActivity.this, "DATA GET FAILED", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_cancel:
        finish();
        break;

        case R.id.btn_post:
        writeNewPost(mUser.getUid(),binding.txtPost.getText().toString());
        break;
    }
}

    private void writeNewPost(String userId, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (!body.isEmpty()){
            String key = mDatabase.child("posts").push().getKey();

             Post post = new Post(userId, body,key);
            Map<String, Object> postValues = post.toMap();
            List<String> arr = new ArrayList<>();
            arr.add("udo");
            postValues.put("stars",arr);



            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/posts/" + key, postValues);
            //childUpdates.put("/posts/"+key+"/stars",userId);
            childUpdates.put("/user-posts/" + userId +"/"+key ,key );

            mDatabase.updateChildren(childUpdates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            finish();
                        }
                    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(WritePostActivity.this, "Fail Save Data", Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, "BODY IS EMPTY", Toast.LENGTH_SHORT).show();
        }



    }

    }
