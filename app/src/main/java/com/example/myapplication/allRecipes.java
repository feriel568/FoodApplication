package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class allRecipes extends Fragment {

    private ListView listViewRecipes;
    private FirebaseFirestore db;
    private List<Recipe> recipeList; // Adjusted data structure

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase components
        db = FirebaseFirestore.getInstance();

        // Initialize views
        listViewRecipes = view.findViewById(R.id.listViewRecipes);

        // Initialize recipe list
        recipeList = new ArrayList<>();

        // Retrieve recipes from Firestore
        retrieveRecipes();

        // Inside onViewCreated method after setting the adapter
        listViewRecipes.setOnItemClickListener((parent, view1, position, id) -> {
            // Retrieve the selected recipe
            Recipe selectedRecipe = recipeList.get(position);

            // Start a new activity to view the recipe details
            Intent intent = new Intent(getContext(), RecipeDetailsVisitor.class);
            // Pass the selected recipe data to the new activity
            intent.putExtra("selectedRecipe", selectedRecipe);
            startActivity(intent);
        });

    }

    // Method to retrieve recipes from Firestore
    private void retrieveRecipes() {
        db.collection("recipes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Clear existing recipe list
                        recipeList.clear();
                        // Iterate through query results and populate recipe list
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Recipe recipe = document.toObject(Recipe.class);
                            recipeList.add(recipe);
                        }
                        // Populate list view with recipe names
                        populateListView();
                    } else {
                        Toast.makeText(getContext(), "Failed to retrieve recipes", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    // Method to populate list view with recipe names
    private void populateListView() {
        ArrayAdapter<Recipe> adapter = new ArrayAdapter<Recipe>(getContext(), R.layout.list_item_recipe, recipeList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_recipe, parent, false);
                }

                TextView textView = convertView.findViewById(R.id.textRecipeName);
                ImageView imageView = convertView.findViewById(R.id.imageRecipe);

                Recipe recipe = getItem(position);

                if (recipe != null) {
                    // Set the recipe name
                    textView.setText(recipe.getRecipeName());
                    imageView.setImageResource(R.drawable.restaurant);
                }

                return convertView;
            }
        };
        listViewRecipes.setAdapter(adapter);
    }
}