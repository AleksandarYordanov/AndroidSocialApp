package com.example.androidpostsapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.bumptech.glide.Glide;
import com.example.androidpostsapp.R;

import com.example.androidpostsapp.databinding.ActivityEditProfileBinding;
import com.example.androidpostsapp.models.User;


import com.example.androidpostsapp.storage.GlideApp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RESULT_LOAD_IMG = 100;
    private ActivityEditProfileBinding binding;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;





    private DatabaseReference reference;
    private StorageReference mStorageRef;

    boolean pictureSelected;
    Uri selectedImgUri;

    private User curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);




        authenticate();



       this.binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_profile);
        setBindingElements();


    }

    private void setBindingElements() {
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mFirebaseUser.getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference();

        binding.editProfileTxtEditPhoto.setOnClickListener(this);
        binding.editProfileBtnComplete.setOnClickListener(this);
        binding.editProfileBtnBack.setOnClickListener(this);



        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                curUser = user;
                String[] bdayString = user.getBirthday().split("/");
                List<Integer> bdayInt = new ArrayList<>();

                for (String s : bdayString) {
                    bdayInt.add(Integer.parseInt(s));
                }

                StorageReference fileRef = mStorageRef.child(user.getImageURL());


                GlideApp.with(EditProfileActivity.this)
                        .load(fileRef)
                        .into(binding.editProfilePictureProfile);




                binding.editProfileTxtEditUsername.setText(user.getUsername());
                binding.editProfileTxtEditEmail.setText(mFirebaseUser.getEmail());

                binding.editProfileTxtEditGender.setText(user.getGender());
                binding.editProfileDatePickerBirthday.updateDate(bdayInt.get(2),bdayInt.get(1),bdayInt.get(0));






            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});
    }



    private void authenticate() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();






        String uid = mFirebaseUser.getUid();
        System.out.println();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile_txt_edit_photo:
                pickImageFromGallery();

                break;
            case R.id.edit_profile_btn_complete:
                updateUser();
                break;

            case R.id.edit_profile_btn_back:
                finish();
                break;


        }
    }

    private String getFileExtention(Uri uri ){

        return uri.getPath().substring(uri.getPath().lastIndexOf("."));
    }

    private void updateUser() {
          final Map<String, Object> userUpdates = new HashMap<>();
        if (pictureSelected){
//            String path = String.format("/profilePictures/%s/defaultAvatarFemale.jpg",mFirebaseUser.getUid());
//            StorageReference picRef = mStorageRef.child(path);
//            picRef.putFile(selectedImgUri);

         //   StorageReference fileRef = mStorageRef.child(String.format("%s%s",mFirebaseUser.getUid(),getFileExtention(selectedImgUri)));
            final String urlForImage = (String.format("profilePictures/%s/%d",mFirebaseUser.getUid(),System.currentTimeMillis()));
            StorageReference fileRef = mStorageRef.child(urlForImage);

            userUpdates.put("imageURL", urlForImage);
            fileRef.putFile(selectedImgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String gender = binding.editProfileTxtEditGender.getText().toString();
                            String username = binding.editProfileTxtEditUsername.getText().toString();
                            String birthday = String.format("%d/%d/%d",binding.editProfileDatePickerBirthday.getDayOfMonth(),
                                    binding.editProfileDatePickerBirthday.getMonth(),
                                    binding.editProfileDatePickerBirthday.getYear());
                            if(!curUser.getGender().equals(gender)){
                                userUpdates.put("gender",gender);
                            }

                            if (!curUser.getUsername().equals(username)){
                                userUpdates.put("username",username);
                            }
                            if (!curUser.getBirthday().equals(birthday)){
                                userUpdates.put("birthday",username);
                            }

                            if (!userUpdates.isEmpty()) {
                                reference.updateChildren(userUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                        Client client = new Client("4WTJKK5KGA", "e8cc2912e9f24ae7bf639c150a8cb63c");
                                        Index index = client.getIndex("users");

                                        index.partialUpdateObjectAsync(new JSONObject(userUpdates), mFirebaseUser.getUid(), true, null);

                                        Intent i = new Intent(EditProfileActivity.this,ProfileActivity.class);

                                        startActivity(i);
                                    }
                                });



                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            System.out.println(exception);
                        }
                    });
        }







    }

    private void pickImageFromGallery() {
        try{
            Intent i = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i,RESULT_LOAD_IMG );
        }catch(Exception exp){
            Log.i("Error",exp.toString());
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                //final Uri imageUri = data.getData();
               selectedImgUri = data.getData();
               pictureSelected = true;
                final InputStream imageStream = getContentResolver().openInputStream(selectedImgUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                binding.editProfilePictureProfile.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}
