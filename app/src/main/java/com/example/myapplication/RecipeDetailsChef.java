package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;


import com.google.firebase.auth.FirebaseAuth;

public class RecipeDetailsChef extends AppCompatActivity {

    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details_chef);



        // Find the ImageView for the hamburger icon
        ImageView imageViewMenu = findViewById(R.id.hamburger);

        // Set OnClickListener to show dropdown menu
        imageViewMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(RecipeDetailsChef.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                // Handle menu item clicks here
                if (item.getItemId() == R.id.nav_edit_profile) {
                    // Display a toast for Edit Profile
                    Intent intent = new Intent(this, editProfileChef.class);
                    startActivity(intent);                    return true;
                } else if (item.getItemId() == R.id.nav_logout) {
                    // Display a toast for Logout
                    logout();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });





        // Find the ImageView for the back arrow
        ImageView imageViewBack = findViewById(R.id.back);

// Set OnClickListener to go back to the previous page
        imageViewBack.setOnClickListener(v -> {
            finish();
        });


        //Receive the Recipe object passed from the previous activity
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");

        // Display the recipe details in the layout
        if (recipe != null) {
            TextView textViewRecipeName = findViewById(R.id.textViewRecipeName);
            TextView textViewIngredients = findViewById(R.id.textViewIngredients);
            TextView textViewInstructions = findViewById(R.id.textViewInstructions);

            textViewRecipeName.setText(recipe.getRecipeName());
            textViewIngredients.setText(recipe.getIngredients());
            textViewInstructions.setText(recipe.getInstructions());

            // Prepare formatted text for ingredients
            StringBuilder ingredientsText = new StringBuilder();
            for (String ingredient : recipe.getIngredients().split("\n")) {
                ingredientsText.append("- ").append(ingredient).append("\n");
            }
            textViewIngredients.setText(ingredientsText.toString().trim());

            // Prepare formatted text for instructions
            StringBuilder instructionsText = new StringBuilder();
            for (String instruction : recipe.getInstructions().split("\n")) {
                instructionsText.append(". ").append(instruction).append("\n");
            }
            textViewInstructions.setText(instructionsText.toString().trim());

        }


    }
    private void logout() {
        FirebaseAuth.getInstance().signOut(); // Sign out the current user from Firebase Authentication
        Intent intent = new Intent(RecipeDetailsChef.this, SignInActivity.class);
        startActivity(intent); // Navigate to the sign-in activity
        finish(); // Finish the current activity
    }
}