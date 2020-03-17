package com.example.androidpostsapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.androidpostsapp.R;
import com.example.androidpostsapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    ActivityLoginBinding binding ;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        binding.txtSignUp.setOnClickListener(this);
        binding.btnLogin.setOnClickListener(this);
    }


    private void signInWithEmailAndPassword(String email, String password) {
        if (checkEmailAndPassword(email,password)){
            auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean checkEmailAndPassword(String email, String password) {
        if (email.isEmpty()||password.isEmpty()){
            Toast.makeText(LoginActivity.this,"All fields are required",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void redirectRegister() {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                signInWithEmailAndPassword(binding.editTextEmail.getText().toString(),
                        binding.editTextPassword.getText().toString());
                break;

            case R.id.txt_signUp:
                redirectRegister();
                break;

        }
    }
}
