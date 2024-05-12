package com.example.myapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class editProfileChef extends AppCompatActivity {
    EditText editTextEmail, editTextUsername;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_chef);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);


        ImageView imageViewBack = findViewById(R.id.back);

// Set OnClickListener to go back to the previous page
        imageViewBack.setOnClickListener(v -> {
            finish();
        });

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Get user document from Firestore
            db.collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String email = document.getString("email");
                                    String username = document.getString("username");

                                    editTextEmail.setText(email);
                                    editTextUsername.setText(username);
                                } else {
                                    Toast.makeText(editProfileChef.this, "User data not found.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(editProfileChef.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }
}
