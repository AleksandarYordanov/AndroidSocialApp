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


import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.example.androidpostsapp.R;

import com.example.androidpostsapp.databinding.ActivityEditProfileBinding;
import com.example.androidpostsapp.models.AppUser;


import com.example.androidpostsapp.storage.GlideApp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.firebase.wrappers.UserWrapper;


public class EditProfileActivity extends BaseActivity implements View.OnClickListener {

    private static final int RESULT_LOAD_IMG = 100;
    private ActivityEditProfileBinding binding;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;





    private DatabaseReference reference;
    private StorageReference mStorageRef;

    boolean pictureSelected;
    Uri selectedImgUri;

    private AppUser curAppUser;
  private  Map<String, Object> userUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);







       this.binding = DataBindingUtil.setContentView(this,R.layout.activity_edit_profile);
        setBindingElements();


    }

    private void setBindingElements() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mFirebaseUser.getUid());
        mStorageRef = FirebaseStorage.getInstance().getReference();

        binding.editProfileTxtEditPhoto.setOnClickListener(this);
        binding.editProfileBtnComplete.setOnClickListener(this);
        binding.editProfileBtnBack.setOnClickListener(this);



        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AppUser appUser = dataSnapshot.getValue(AppUser.class);
                assert appUser != null;
                curAppUser = appUser;
                String[] bdayString = appUser.getBirthday().split("/");
                List<Integer> bdayInt = new ArrayList<>();

                for (String s : bdayString) {
                    bdayInt.add(Integer.parseInt(s));
                }

                StorageReference fileRef = mStorageRef.child(appUser.getImageURL());


                GlideApp.with(EditProfileActivity.this)
                        .load(fileRef)
                        .into(binding.editProfilePictureProfile);




                binding.editProfileTxtEditUsername.setText(appUser.getUsername());
                binding.editProfileTxtEditEmail.setText(mFirebaseUser.getEmail());

                binding.editProfileTxtEditGender.setText(appUser.getGender());
                binding.editProfileDatePickerBirthday.updateDate(bdayInt.get(2),bdayInt.get(1),bdayInt.get(0));






            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});
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
          userUpdates = new HashMap<>();

        if (pictureSelected){
            final String urlForImage = (String.format("profilePictures/%s/%d",mFirebaseUser.getUid(),System.currentTimeMillis()));
            StorageReference fileRef = mStorageRef.child(urlForImage);

            fileRef.putFile(selectedImgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String uristr = String.valueOf(uri);
                                    User currentUser = ChatSDK.currentUser();
                                    currentUser.setAvatarURL(String.valueOf(uri));
                                    userUpdates.put("imageURL", urlForImage);
                                    checkInfoFieldAndPopulateUserUpdates();
                                    pushData();

                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            System.out.println(exception);
                        }
                    });
        }else {
            checkInfoFieldAndPopulateUserUpdates();
            pushData();
        }







    }

    private void pushData() {



        if (!userUpdates.isEmpty()) {
            this.reference.updateChildren(userUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    if(userUpdates.containsKey("username")){
                        User currentUser = ChatSDK.currentUser();
                        currentUser.setName(userUpdates.get("username").toString());
                        currentUser.update();

                    }

                    PushChatSDKUser();
                    //Search Api Update
                    Client client = new Client("4WTJKK5KGA", "e8cc2912e9f24ae7bf639c150a8cb63c");
                    Index index = client.getIndex("users");
                    index.partialUpdateObjectAsync(new JSONObject(userUpdates), mFirebaseUser.getUid(), true, null);


                    finish();



                }
            });



        }
    }

    private void PushChatSDKUser() {


        UserWrapper userWrapper = new UserWrapper(ChatSDK.currentUser());
        userWrapper.push().subscribe();
    }

    private void checkInfoFieldAndPopulateUserUpdates() {
        String gender = binding.editProfileTxtEditGender.getText().toString();
        String username = binding.editProfileTxtEditUsername.getText().toString();
        String birthday = String.format("%d/%d/%d",binding.editProfileDatePickerBirthday.getDayOfMonth(),
                binding.editProfileDatePickerBirthday.getMonth(),
                binding.editProfileDatePickerBirthday.getYear());


        if(!curAppUser.getGender().equals(gender)){
            userUpdates.put("gender",gender);
        }

        if (!curAppUser.getUsername().equals(username)){
            userUpdates.put("username",username);
        }
        if (!curAppUser.getBirthday().equals(birthday)){
            userUpdates.put("birthday",username);
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
