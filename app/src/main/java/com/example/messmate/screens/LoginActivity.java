package com.example.messmate.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.messmate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText inEmail,inPassword;
    Button LoginButton,LoginActivityRegisterButton;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inEmail=findViewById(R.id.emailEditText);
        inPassword=findViewById(R.id.passwordEditText);

        LoginButton = findViewById(R.id.button3);
        mAuth=FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        LoginActivityRegisterButton = findViewById(R.id.loginActivityRegisterButton);
        LoginActivityRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the new activity
                 Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                  startActivity(intent);

            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the new activity
                perforLogin();

               // Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                //startActivity(intent);

            }
        });

    }

    private void perforLogin() {
        String email=inEmail.getText().toString();
        String password=inPassword.getText().toString();
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            inEmail.setError("Enter Valid Email");
        } else if (password.isEmpty()||password.length()<8) {
            inPassword.setError("Enter 8 Characters");
        }
        else {
            progressDialog.setMessage("Login...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        Toast.makeText(LoginActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Unable to login", Toast.LENGTH_SHORT).show();

                    }
                }
            });
    }
}}