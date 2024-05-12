package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.myapplication.Recipe;

public class RecipeDetailsVisitor extends AppCompatActivity {

    private TextView textViewRecipeName;
    private TextView textViewIngredients;
    private TextView textViewInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details_visitor);

        // Find the ImageView for the back arrow
        ImageView imageViewBack = findViewById(R.id.back);

// Set OnClickListener to go back to the previous page
        imageViewBack.setOnClickListener(v -> {
            finish();
        });

        Recipe recipe = (Recipe) getIntent().getSerializableExtra("selectedRecipe");

        // Display the recipe details in the layout
        if (recipe != null) {
            TextView textViewRecipeName = findViewById(R.id.textViewRecipeName1);
            TextView textViewIngredients = findViewById(R.id.textViewIngredients1);
            TextView textViewInstructions = findViewById(R.id.textViewInstructions1);

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
}
