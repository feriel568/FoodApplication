package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class VisitorHomeActivity extends AppCompatActivity {
    private NavController navController;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_home);

        mAuth = FirebaseAuth.getInstance(); // Initialisation de FirebaseAuth

        navController = Navigation.findNavController(this, R.id.allRecipes);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bottom);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                navController.navigate(R.id.allRecipes);
                return true;
            } else if (item.getItemId() == R.id.logout) {
                logout();
                return true;
            } else {
                return false;
            }
        });
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(VisitorHomeActivity.this, SignInActivity.class); // Utilisation de VisitorHomeActivity.this au lieu de requireContext()
        startActivity(intent);
        finish();
    }
}
