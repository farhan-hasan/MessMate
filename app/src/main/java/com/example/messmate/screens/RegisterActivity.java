package com.example.messmate.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.messmate.R;
import com.example.messmate.models.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class RegisterActivity extends AppCompatActivity {
    EditText inEmail, inPassword, inName, inConfirmPassword, inPhone;
    Button RegisterButton, RegisterActivityLoginButton;
    //String emailRegex="[a-zA-Z]{3,10}\\s*[a-zA-Z]*";
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    FirebaseDatabase database;
    DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inEmail = findViewById(R.id.emailEditText);
        inPassword = findViewById(R.id.passwordEditText);
        inName = findViewById(R.id.nameEditText);
        inConfirmPassword = findViewById(R.id.confirmPasswordEditText);
        inPhone = findViewById(R.id.phoneEditText);

        RegisterButton = findViewById(R.id.registerButton);
        RegisterActivityLoginButton = findViewById(R.id.registerActivityLoginButton);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        RegisterActivityLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the new activity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });


        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the new activity
                // Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                //   startActivity(intent);
                PerForAuth();
            }
        });

    }
    private Bitmap drawPersonIcon() {
        Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, 200, 200, paint);
        return bitmap;
    }

    private void PerForAuth() {
        String name = inName.getText().toString();
        String phone = inPhone.getText().toString();
        String email = inEmail.getText().toString();
        String password = inPassword.getText().toString();
        String confirmPassword = inConfirmPassword.getText().toString();
        // String name=inName.getText().toString();
        //String phone=inPhone.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inEmail.setError("Enter Valid Email");
        } else if (password.isEmpty() || password.length() < 8) {
            inPassword.setError("Enter 8 Characters");
        } else if (!confirmPassword.equals(password)) {
            inConfirmPassword.setError("Password is not matching");

        } else {

            progressDialog.setMessage("Registration in progress...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

//                   Data insert in database
                        {
                            database = FirebaseDatabase.getInstance();
                            userRef = database.getReference();
                            String key = email.replace(".", "");
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("username", name);
                            userData.put("email", email);
                            userData.put("phone", phone);
                            userData.put("key", key);
                            userData.put("mess_name", "");
                            userData.put("is_resident", false);
                            userData.put("breakfast_amount", 0);
                            userData.put("lunch_amount", 0);
                            userData.put("dinner_amount", 0);
                            userData.put("meal_amount", 0);

                            userRef.child("users").child(key).setValue(userData)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Data saved", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                        }

                        {
                            String key = email.replace(".", "");
                            // Generate a dummy image
                            Bitmap bitmap = drawPersonIcon();

                            // Convert the bitmap to a byte array
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            // Upload the byte array to Firebase Storage
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            StorageReference imagesRef = storageRef.child("user_images/" + key + ".jpg");

                            UploadTask uploadTask = imagesRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    System.out.println("Upload failed: " + exception.getMessage());
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Handle successful uploads
                                    System.out.println("Upload successful!");
                                }
                            });
                        }

                        startActivity(intent);
                        Toast.makeText(RegisterActivity.this, "Registration Complete", Toast.LENGTH_SHORT).show();

                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "You are already Registered", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

    }
}