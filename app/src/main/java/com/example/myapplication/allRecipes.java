package com.example.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class allRecipes extends Fragment {

    private ListView listViewRecipes;
    private FirebaseFirestore db;
    private List<Recipe> recipeList;
    private ArrayAdapter<Recipe> adapter; // Add adapter as a class member

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        // Set the item click listener
        listViewRecipes.setOnItemClickListener((parent, view1, position, id) -> {
            // Retrieve the selected recipe
            Recipe selectedRecipe = recipeList.get(position);

            // Start a new activity to view the recipe details
            Intent intent = new Intent(getContext(), RecipeDetailsVisitor.class);
            // Pass the selected recipe data to the new activity
            intent.putExtra("selectedRecipe", selectedRecipe);
            startActivity(intent);
        });

        // Find the search icon and add OnClickListener
        ImageView searchIcon = view.findViewById(R.id.searchIcon);
        searchIcon.setOnClickListener(v -> {
            // Implement your search functionality here
            showSearchDialog();
        });
    }

    // Method to show a search dialog
    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Search Recipe");
        // Set up the input
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Search", (dialog, which) -> {
            String searchText = input.getText().toString().trim();
            searchRecipe(searchText);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Method to search for recipes based on the user's input query
    private void searchRecipe(String searchText) {
        List<Recipe> searchResults = new ArrayList<>();
        for (Recipe recipe : recipeList) {
            if (recipe.getRecipeName().toLowerCase().contains(searchText.toLowerCase())) {
                searchResults.add(recipe);
            }
        }
        if (!searchResults.isEmpty()) {
            // If there are search results, update the list view with the search results
            updateListView(searchResults);
        } else {
            Toast.makeText(getContext(), "No matching recipes found", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to update the list view with search results
    private void updateListView(List<Recipe> searchResults) {
        adapter.clear();
        adapter.addAll(searchResults);
        adapter.notifyDataSetChanged();
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
        adapter = new ArrayAdapter<Recipe>(getContext(), R.layout.list_item_recipe, recipeList) {
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
