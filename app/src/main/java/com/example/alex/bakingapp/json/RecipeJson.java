package com.example.alex.bakingapp.json;

import java.io.Serializable;
import java.util.List;

public class RecipeJson implements Serializable {

    public Integer id;
    public String name;
    public List<IngredientJson> ingredients;
    public List<StepJson> steps;
    public Integer servings;
    public String image;

    public Boolean isFavorite;

}
