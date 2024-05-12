package com.example.myapplication;

import java.io.Serializable;

//Recipe class implements the Serializable interface. This is necessary to allow instances of Recipe to be passed between activities using intents.
public class Recipe  implements Serializable {

    private String recipeName;
    private String ingredients;
    private String instructions;
    private String userId;

    private String imageUrl;

    // Default constructor (required for Firestore)

        public Recipe() {

        }
    // Constructor with parameters
    public Recipe(String recipeName, String ingredients, String instructions, String userId ) {
        this.recipeName = recipeName;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.userId = userId;
    }

    // Getters and setters (you can generate these using IDE shortcuts)
    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
