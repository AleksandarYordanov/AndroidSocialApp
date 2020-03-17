package com.example.androidpostsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidpostsapp.R;
import com.example.androidpostsapp.models.Post;

import com.example.androidpostsapp.databinding.ActivityProfileBinding;
import com.example.androidpostsapp.models.User;
import com.example.androidpostsapp.storage.GlideApp;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityProfileBinding binding;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    private DatabaseReference mReference;
    private StorageReference mStorageRef;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        authenticate();

        setBindingElements();

    }

    private void authenticate() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();






        String uid = mFirebaseUser.getUid();
        System.out.println();
    }

    private void setBindingElements() {

        mReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();



        mReference.child("Users").child(mFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;

                StorageReference fileRef = mStorageRef.child(user.getImageURL());

                GlideApp.with(ProfileActivity.this)
                        .load(fileRef)
                        .into(binding.profilePictureProfile);

                binding.profileUsername.setText(user.getUsername());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});

            binding.profileBtnEditProfile.setOnClickListener(this);
            binding.profileImgbtnEditProfile.setOnClickListener(this);


     binding.recycleViewProfile.setLayoutManager(new LinearLayoutManager(this));
     binding.recycleViewProfile.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
     binding.recycleViewProfile.setAdapter(newAdapter());

      //Set Profile Selected
      binding.bottomNavigation.setSelectedItemId(R.id.profile);
      //Perform Item Select Listener
      itemSelectListenerMenue();
    }

    private RecyclerView.Adapter newAdapter() {

         Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("posts")
                .orderByChild("authorUId")
                .equalTo(mFirebaseUser.getUid())
                .limitToLast(50);

        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setQuery(query, Post.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
            @Override
            public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new PostViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.post_layout, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final PostViewHolder holder, int position, @NonNull final Post model) {

                final DatabaseReference postRef = mReference.child("posts").child(model.getId());
                holder.bodyText.setText(model.getBody());
                holder.deleteButton.setVisibility(View.VISIBLE);
                holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postRef.removeValue();
                    }
                });
              //  DatabaseReference curPostRef = mReference.child("posts").child();

                final DatabaseReference likesRef = postRef.child("stars");
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        holder.starsCounter.setText(""+ (dataSnapshot.getChildrenCount()-1));
                        for (final DataSnapshot child : dataSnapshot.getChildren()) {

                            if(child.getKey().equals(mFirebaseUser.getUid())){
                                holder.starButton.setImageResource(R.drawable.ic_star_pressed);
                                holder.starButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        child.getRef().removeValue();
                                    }
                                });
                                break;
                            }else {
                                holder.starButton.setImageResource(R.drawable.ic_star_border_black_24dp);
                                holder.starButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Map<String,Object> likeUpdate = new HashMap<>();
                                        likeUpdate.put(mFirebaseUser.getUid(),mFirebaseUser.getUid());
                                        likesRef.updateChildren(likeUpdate);
                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





                DatabaseReference uReference = mReference.child("Users").child(model.getAuthorUId());


                uReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        assert user != null;



                        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                        StorageReference fileRef = mStorageRef.child(user.getImageURL());


                        GlideApp.with(ProfileActivity.this)
                                .load(fileRef)
                                .into(holder.profilePicture);

                        holder.username.setText(user.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };
    }

    private void itemSelectListenerMenue() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(),SearchActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.chat:
                        startActivity(new Intent(getApplicationContext(), ChatHolderActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.logOut:
                        mFirebaseAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_imgbtn_edit_profile:
                redirectEditProfile();
                break;
            case R.id.profile_btn_edit_profile:
                redirectEditProfile();
                break;



        }
    }

    private void redirectEditProfile() {
        startActivity(new Intent(this,EditProfileActivity.class));
    }
}
