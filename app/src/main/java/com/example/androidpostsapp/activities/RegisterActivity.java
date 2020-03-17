package com.example.androidpostsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.RequestOptions;
import com.example.androidpostsapp.R;

import com.example.androidpostsapp.databinding.ActivityRegisterBinding;
import com.example.androidpostsapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

                                final User user;
                                final HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("id", userid);
                                hashMap.put("username", username);
                                if (gender.equals("Female")){
                                    hashMap.put("imageURL", "profilePictures/defaultAvatarFemale.jpg");
                                    user = new User(userid,username,"profilePictures/defaultAvatarFemale.jpg",birthday,gender);
                                }else {
                                    hashMap.put("imageURL", "profilePictures/defaultAvatarMale.jpg");
                                    user = new User(userid,username,"profilePictures/defaultAvatarMale.jpg",birthday,gender);
                                }

                                hashMap.put("gender",gender);
                                hashMap.put("birthday",birthday);


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
