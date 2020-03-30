package com.example.androidpostsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.example.androidpostsapp.R;

import com.example.androidpostsapp.databinding.ActivityRegisterBinding;
import com.example.androidpostsapp.models.AppUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.dao.UserDao;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.types.AccountDetails;
import co.chatsdk.firebase.wrappers.UserWrapper;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String[] GENDERS = new String[] {
            "Male", "Female"
    };

    private ActivityRegisterBinding binding;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        binding.btnRegister.setOnClickListener(this);

        binding.txtRedirectLogIn.setOnClickListener(this);
        binding.registerBirthday.setMaxDate(System.currentTimeMillis());


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, GENDERS);

        binding.registerGender.setAdapter(adapter);
        binding.registerGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.registerGender.showDropDown();
            }
        });

    }




    private void register(final String username, String email, String password , final String gender , final String birthday){
        if (checkRegisterDetails(username,email,password,gender,birthday)) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                assert firebaseUser != null;
                                final String userid = firebaseUser.getUid();

                                reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                                final AppUser appUser;
                                final HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("id", userid);
                                hashMap.put("username", username);
                                if (gender.equals("Female")){
                                    hashMap.put("imageURL", "profilePictures/defaultAvatarFemale.jpg");
                                    appUser = new AppUser(userid,username,"profilePictures/defaultAvatarFemale.jpg",birthday,gender);
                                }else {
                                    hashMap.put("imageURL", "profilePictures/defaultAvatarMale.jpg");
                                    appUser = new AppUser(userid,username,"profilePictures/defaultAvatarMale.jpg",birthday,gender);
                                }

                                hashMap.put("gender",gender);
                                hashMap.put("birthday",birthday);




                                ChatSDK.auth().authenticate().subscribe();


                                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Client client = new Client("4WTJKK5KGA", "e8cc2912e9f24ae7bf639c150a8cb63c");
                                            Index index = client.getIndex("users");

                                            hashMap.put("objectID",userid);
                                                List<JSONObject> userList = new ArrayList<>();
                                                userList.add(new JSONObject(hashMap));

                                                index.addObjectsAsync(new JSONArray(userList),null);




                                            User currentUser = ChatSDK.currentUser();
                                            currentUser.setName(username);
                                            if (gender.equals("Female")){
                                                String femaleUrl = "https://firebasestorage.googleapis.com/v0/b/varnafreeuniversitypost.appspot.com/o/profilePictures%2FdefaultAvatarFemale.jpg?alt=media&token=034b1de7-ac28-4535-9777-ebcb49079233";
                                                currentUser.setAvatarURL(femaleUrl);
                                            }else {
                                                String maleUrl = "https://firebasestorage.googleapis.com/v0/b/varnafreeuniversitypost.appspot.com/o/profilePictures%2FdefaultAvatarMale.jpg?alt=media&token=867a020d-bee7-499e-bc11-b95312e501eb";
                                                currentUser.setAvatarURL(maleUrl);
                                            }

                                            currentUser.update();
                                            UserWrapper userWrapper = new UserWrapper(currentUser);
                                            userWrapper.push().subscribe();


                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, "You cant register with this email or password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean checkRegisterDetails(String username, String email, String password, String gender, String birthday) {
       if (username.isEmpty()||email.isEmpty()||password.isEmpty() || gender.isEmpty()||birthday.isEmpty()){
           Toast.makeText(RegisterActivity.this,"All fields are required",Toast.LENGTH_LONG).show();
           return false;
       }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                String dateString = String.format("%d/%d/%d",binding.registerBirthday.getDayOfMonth(),
                        binding.registerBirthday.getMonth(),
                        binding.registerBirthday.getYear());
                register(binding.registerUsername.getText().toString(),
                        binding.registerEmail.getText().toString(),
                        binding.registerPassword.getText().toString(),
                        binding.registerGender.getText().toString(),
                        dateString);
                break;

            case R.id.txt_redirectLogIn:
                redirectLogin();

        }
    }

    private void redirectLogin() {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
