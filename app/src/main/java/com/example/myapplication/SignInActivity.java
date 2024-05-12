package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {

    EditText editTextUEmail, editTextPassword;
    Button signInBtn;
    TextView noAccount;

    FirebaseAuth mAuth;


   @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        mAuth = FirebaseAuth.getInstance();

        editTextUEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        signInBtn = findViewById(R.id.signInBtn);
        noAccount = findViewById(R.id.noAccount);


        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email, password;
                email = String.valueOf(editTextUEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                // Check if fields are empty
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(SignInActivity.this, "Username and password are required", Toast.LENGTH_LONG).show();

                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {


                                if (task.isSuccessful()) {
                                    // Determine user role
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        String userId = user.getUid();
                                        // Query Firestore to get user document
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        DocumentReference userRef = db.collection("users").document(userId);
                                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        String userType = document.getString("userType");
                                                        // Redirect user based on role
                                                        if (userType != null) {
                                                            if (userType.equals("Chef")) {
                                                                Intent intent = new Intent(SignInActivity.this, ChefHomeActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else if (userType.equals("Visitor")) {
                                                                Intent intent = new Intent(SignInActivity.this, VisitorHomeActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(SignInActivity.this, "Unknown user type", Toast.LENGTH_SHORT).show();
                                                            }
                                                        } else {
                                                            Toast.makeText(SignInActivity.this, "User type not found", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        Toast.makeText(SignInActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(SignInActivity.this, "Error getting user document", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(SignInActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}
