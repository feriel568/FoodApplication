package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    EditText editTextEmail , editTextUsername , editTextPassword , editTextConfirmPassword ;
    Button signUpBtn;
    TextView haveAccount, logo;
    RadioButton chefRbtn, userRbtn;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        editTextConfirmPassword = findViewById(R.id.confirmpassword);
        progressBar = findViewById(R.id.progressBar);

        // Initialize Button
        signUpBtn = findViewById(R.id.signUpBtn);

        // Initialize TextViews
        haveAccount = findViewById(R.id.haveAccount);
        logo = findViewById(R.id.logo);

        // Initialize RadioButtons
        chefRbtn = findViewById(R.id.chefRbtn);
        userRbtn = findViewById(R.id.userRbtn);


        //Navigate to sign in activity
        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);


                String  email, username, password, confirmPassword;

                //Récupérer les données saisies par l'utilisateur
                email = String.valueOf(editTextEmail.getText());
                username = String.valueOf(editTextUsername.getText());
                password = String.valueOf(editTextPassword.getText());
                confirmPassword = String.valueOf(editTextConfirmPassword.getText());
                // Check if fields are empty
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "All fields are required", Toast.LENGTH_LONG).show();
                    return;
                }

                // Check if password and confirm password match
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    return;
                }

                //Récupérer type utilisateur
                String userType;
                if (chefRbtn.isChecked()) {
                    userType = "Chef";
                } else if (userRbtn.isChecked()) {
                    userType = "Visitor";
                } else {
                    Toast.makeText(SignUpActivity.this, "Please select user type", Toast.LENGTH_LONG).show();
                    return;
                }

                //méthode de firebase -> crée un utilisateur à partir d'un email et un mdp
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    //Récupérer l'utilisateur connecté
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        // Create a new user document in Firestore
                                        FirebaseFirestore db = FirebaseFirestore.getInstance(); // Initialiser une instance de firestore
                                        DocumentReference userRef = db.collection("users").document(user.getUid());//Création d'un document avec un identifiant unique dans la collection users

                                        // Create a user object with the information to be stored
                                        Map<String, Object> userInfo = new HashMap<>();
                                        userInfo.put("email", email);
                                        userInfo.put("username", username);
                                        userInfo.put("userType", userType);

                                        // Set the user document in Firestore
                                        //Enregistrer les infos de l'utilisateur dans le document
                                        userRef.set(userInfo)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Document successfully written
                                                        Toast.makeText(SignUpActivity.this, "Account created.", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Error writing document
                                                        Toast.makeText(SignUpActivity.this, "Failed to create account." + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpActivity.this, "Authentication failed." , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
