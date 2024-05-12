package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ChefHomeActivity extends AppCompatActivity {

    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_home);

        navController = Navigation.findNavController(this, R.id.homeChefFragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bottom);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                navController.navigate(R.id.homeChefFragment);
                return true;
            } else if (item.getItemId() == R.id.recipe) {
                navController.navigate(R.id.addRecipeFragment);
                return true;

            }
            else {
                return false;
            }
        });




    }
}