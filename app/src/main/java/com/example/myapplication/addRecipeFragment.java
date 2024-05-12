package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class addRecipeFragment extends Fragment {

    private EditText editTextRecipeName;
    private EditText editTextIngredients;
    private EditText editTextInstructions;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_recipe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        editTextRecipeName = view.findViewById(R.id.editTextRecipeName);
        editTextIngredients = view.findViewById(R.id.editTextIngredients);
        editTextInstructions = view.findViewById(R.id.editTextInstructions);
        Button buttonAddRecipe = view.findViewById(R.id.buttonAddRecipe);

        // Set onClickListener for Add Recipe button
        buttonAddRecipe.setOnClickListener(v -> addRecipeToFirestore());

        // Set onClickListener for Add Ingredient button
        Button buttonAddIngredient = view.findViewById(R.id.buttonAddIngredient);
        buttonAddIngredient.setOnClickListener(v -> addIngredient());

        // Set onClickListener for Add Instruction button
        Button buttonAddInstruction = view.findViewById(R.id.buttonAddInstruction);
        buttonAddInstruction.setOnClickListener(v -> {
            // Append a new EditText for adding Instruction dynamically
            addInstruction();
        });

        // Find the ImageView for the search icon
        ImageView imageViewMenu = view.findViewById(R.id.search);

        // Set OnClickListener to show dropdown menu
        imageViewMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), v);
            popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                // Handle menu item clicks here
                if (item.getItemId() == R.id.nav_edit_profile) {
                    // Display a toast for Edit Profile
                    Toast.makeText(requireContext(), "Edit Profile Clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == R.id.nav_logout) {
                    // Perform logout
                    logout();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    // Method to add ingredient
    private void addIngredient() {
        String ingredient = editTextIngredients.getText().toString().trim();
        if (!ingredient.isEmpty()) {
            editTextIngredients.append("\n" + ingredient);
            editTextIngredients.setSelection(editTextIngredients.getText().length());
        }
    }

    // Method to add instruction
    private void addInstruction() {
        String instruction = editTextInstructions.getText().toString().trim();
        if (!instruction.isEmpty()) {
            editTextInstructions.append("\n" + instruction);
            editTextInstructions.setSelection(editTextInstructions.getText().length());
        }
    }

    // Method to add recipe to Firestore
    private void addRecipeToFirestore() {
        // Get current user UID
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assert currentUser != null;
        String userId = currentUser.getUid();

        // Get recipe details from EditText fields
        String recipeName = editTextRecipeName.getText().toString().trim();
        String ingredients = editTextIngredients.getText().toString().trim();
        String instructions = editTextInstructions.getText().toString().trim();

        // Check if any field is empty
        if (recipeName.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique recipe ID
        String recipeId = UUID.randomUUID().toString();

        // Create a new recipe object
        Recipe recipe = new Recipe(recipeName, ingredients, instructions, userId);

        // Add the recipe to Firestore
        db.collection("recipes").document(recipeId).set(recipe)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Recipe added successfully", Toast.LENGTH_SHORT).show();
                    // Clear EditText fields after successful addition
                    editTextRecipeName.setText("");
                    editTextIngredients.setText("");
                    editTextInstructions.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add recipe", Toast.LENGTH_SHORT).show());
    }

    // Method to handle logout action
    private void logout() {
        FirebaseAuth.getInstance().signOut(); // Sign out the current user from Firebase Authentication
        Intent intent = new Intent(requireContext(), SignInActivity.class);
        startActivity(intent); // Navigate to the sign-in activity
        requireActivity().finish(); // Finish the current activity
    }
}
