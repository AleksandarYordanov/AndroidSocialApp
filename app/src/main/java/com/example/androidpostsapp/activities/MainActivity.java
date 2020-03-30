package com.example.androidpostsapp.activities;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidpostsapp.R;
import com.example.androidpostsapp.models.AppUser;

import com.example.androidpostsapp.databinding.ActivityMainBinding;
import com.example.androidpostsapp.models.Post;
import com.example.androidpostsapp.storage.GlideApp;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.dao.UserMetaValue;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.session.Configuration;
import co.chatsdk.firebase.FirebaseNetworkAdapter;
import co.chatsdk.firebase.file_storage.FirebaseFileStorageModule;
import co.chatsdk.firebase.wrappers.ThreadWrapper;
import co.chatsdk.firebase.wrappers.UserWrapper;
import co.chatsdk.ui.manager.BaseInterfaceAdapter;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class MainActivity extends BaseActivity implements View.OnClickListener {

     ActivityMainBinding binding;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    private DatabaseReference mDatabaseReference ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        try {
//            // Create a new configuration
//            Configuration.Builder builder = new Configuration.Builder();
//
//            // Perform any other configuration steps (optional)
//            builder.firebaseRootPath("prod");
//            // Initialize the Chat SDK
//            ChatSDK.initialize(getApplicationContext(), builder.build(), FirebaseNetworkAdapter.class, BaseInterfaceAdapter.class);
//
//            // File storage is needed for profile image upload and image messages
//            FirebaseFileStorageModule.activate();
//
//            // Activate any other modules you need.
//            // ...
//
//
//        } catch (Exception e) {
//            // Handle any exceptions
//            e.printStackTrace();
//        }

        super.onCreate(savedInstanceState);





        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();
        this.mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        mDatabaseReference = FirebaseDatabase.getInstance().getReference();


        setBindingElements();







  // User user = ChatSDK.currentUser();
  // user.setName("name");
  // user.update();
  // ChatSDK.core().pushUser().subscribe();


        System.out.println();
      //  ChatSDK.ui().startEditProfileActivity(getApplicationContext(),ChatSDK.currentUserID());









    }

    private void setBindingElements() {

        binding.txtWritePost.setOnClickListener(this);


        binding.recycleViewHome.setLayoutManager(new LinearLayoutManager(this));
        binding.recycleViewHome.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        binding.recycleViewHome.setAdapter(newAdapter());


        //Set Home Selected
        setActionBar(binding.bottomNavigation,R.id.home);

        //Perform Item Select Listener
        String uid = mFirebaseUser.getUid();

        mDatabaseReference.child("Users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AppUser appUser = dataSnapshot.getValue(AppUser.class);


                if (appUser != null) {
                    StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(appUser.getImageURL());

                    GlideApp.with(MainActivity.this)
                            .load(fileRef)
                            .into(binding.profilePictureHome);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    mDatabaseReference.removeEventListener(this);
            }});


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_write_post:
                redirectWritePost();
                break;
        }
    }

    private void redirectWritePost() {
        startActivity(new Intent(MainActivity.this,WritePostActivity.class));
    }

    @NonNull
    private RecyclerView.Adapter newAdapter() {



        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("posts")
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
            protected void onBindViewHolder(@NonNull final PostViewHolder holder, int position, @NonNull Post model) {

                holder.bodyText.setText(model.getBody());

               final DatabaseReference likesRef = mDatabaseReference.child("posts").child(model.getId()).child("stars");
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





                DatabaseReference uReference = mDatabaseReference.child("Users").child(model.getAuthorUId());


               uReference.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       AppUser appUser = dataSnapshot.getValue(AppUser.class);
                       assert appUser != null;

                       StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
                       StorageReference fileRef = mStorageRef.child(appUser.getImageURL());


                       GlideApp.with(MainActivity.this)
                               .load(fileRef)
                               .into(holder.profilePicture);

                       holder.username.setText(appUser.getUsername());
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
            }
        };
    }
}
