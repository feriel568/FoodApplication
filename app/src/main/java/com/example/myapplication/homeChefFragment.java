package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class homeChefFragment extends Fragment {

    private ListView listViewRecipes;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private List<Recipe> recipeList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_chef, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        listViewRecipes = view.findViewById(R.id.listViewRecipes);

        // Initialize recipe list
        recipeList = new ArrayList<>();

        // Retrieve recipes from Firestore
        retrieveRecipes();

        // Set the item click listener
        listViewRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click, navigate to detail view
                Recipe selectedRecipe = recipeList.get(position);
                // Assuming you have a RecipeDetailActivity
                Intent intent = new Intent(getContext(), RecipeDetailsChef.class);
                // Pass the selected recipe to the detail activity
                intent.putExtra("recipe", selectedRecipe);
                startActivity(intent);
            }
        });

        // Find the ImageView for the hamburger icon
        ImageView imageViewMenu = view.findViewById(R.id.search);

        // Set OnClickListener to show dropdown menu
        imageViewMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                // Handle menu item clicks here
                if (item.getItemId() == R.id.nav_edit_profile) {
                    // Display a toast for Edit Profile
                    Intent intent = new Intent(requireActivity(), editProfileChef.class);
                   startActivity(intent);

                    return true;
                } else if (item.getItemId() == R.id.nav_logout) {
                    // Display a toast for Logout
                    logout();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    // Method to retrieve recipes from Firestore
    private void retrieveRecipes() {
        // Get current user UID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();

        // Query Firestore for recipes created by the current user
        db.collection("recipes")
                .whereEqualTo("userId", userId)
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

                Recipe recipe = getItem(position);

                if (recipe != null) {
                    ImageView imageView = convertView.findViewById(R.id.imageRecipe);
                    imageView.setImageResource(R.drawable.restaurant); // Set the image from the layout
                    textView.setText(recipe.getRecipeName());
                }

                return convertView;
            }
        };
        listViewRecipes.setAdapter(adapter);
    }

    private void logout(){
        mAuth.signOut();
        Intent intent = new Intent(requireContext(),SignInActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}
