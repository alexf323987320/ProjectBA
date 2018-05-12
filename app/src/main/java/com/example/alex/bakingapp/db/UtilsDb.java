package com.example.alex.bakingapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.example.alex.bakingapp.UpdateRecipesService;
import com.example.alex.bakingapp.json.IngredientJson;
import com.example.alex.bakingapp.json.RecipeJson;
import com.example.alex.bakingapp.db.RecipesContract.RecipesTable;
import com.example.alex.bakingapp.db.RecipesContract.IngredientsTable;
import com.example.alex.bakingapp.db.RecipesContract.StepsTable;
import com.example.alex.bakingapp.json.StepJson;

import java.util.ArrayList;
import java.util.List;

public class UtilsDb {

    public static void saveRecipes(Context appContext, List<RecipeJson> recipes){
        RecipesOpenHelper recipesOpenHelper = new RecipesOpenHelper(appContext);
        SQLiteDatabase db = recipesOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            //1. Delete all recipes
            db.delete(RecipesTable.TABLE_NAME, null, null);
            db.delete(StepsTable.TABLE_NAME, null, null);
            db.delete(IngredientsTable.TABLE_NAME, null, null);
            //2. Save all recipes
            for (RecipeJson recipe : recipes) {
                ContentValues cv1 = new ContentValues();
                cv1.put(RecipesTable.COLUMN_ID, recipe.id);
                cv1.put(RecipesTable.COLUMN_NAME, recipe.name);
                cv1.put(RecipesTable.COLUMN_IMAGE, recipe.image);
                cv1.put(RecipesTable.COLUMN_IS_FAVORITE, recipe.isFavorite);
                db.insert(RecipesTable.TABLE_NAME, null, cv1);
                for (IngredientJson ingredient : recipe.ingredients) {
                    ContentValues cv2 = new ContentValues();
                    cv2.put(IngredientsTable.COLUMN_INGREDIENT, ingredient.ingredient);
                    cv2.put(IngredientsTable.COLUMN_MEASURE, ingredient.measure);
                    cv2.put(IngredientsTable.COLUMN_RECIPE_ID, recipe.id);
                    cv2.put(IngredientsTable.COLUMN_QUANTITY, ingredient.quantity);
                    db.insert(IngredientsTable.TABLE_NAME, null, cv2);
                }
                for (StepJson step : recipe.steps) {
                    ContentValues cv3 = new ContentValues();
                    cv3.put(StepsTable.COLUMN_ID, step.id);
                    cv3.put(StepsTable.COLUMN_DESCRIPTION, step.description);
                    cv3.put(StepsTable.COLUMN_RECIPE_ID, recipe.id);
                    cv3.put(StepsTable.COLUMN_SHORT_DESCRIPTION, step.shortDescription);
                    cv3.put(StepsTable.COLUMN_THUMBNAIL_URL, step.thumbnailURL);
                    cv3.put(StepsTable.COLUMN_VIDEO_URL, step.videoURL);
                    db.insert(StepsTable.TABLE_NAME, null, cv3);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public static List<RecipeJson> getRecipes(Context appContext) {
        RecipesOpenHelper recipesOpenHelper = new RecipesOpenHelper(appContext);
        SQLiteDatabase db = recipesOpenHelper.getReadableDatabase();

        String order = RecipesTable.COLUMN_ID;
        Cursor cursor1 = db.query(RecipesTable.TABLE_NAME, null, null, null, null, null, order);

        order = IngredientsTable.COLUMN_RECIPE_ID + "," + IngredientsTable.COLUMN_INGREDIENT;
        Cursor cursor2 = db.query(IngredientsTable.TABLE_NAME, null, null, null, null, null, order);

        order = StepsTable.COLUMN_RECIPE_ID + "," + StepsTable.COLUMN_ID;
        Cursor cursor3 = db.query(StepsTable.TABLE_NAME, null, null, null, null, null, order);

        List<RecipeJson> recipes = new ArrayList<>();
        while (cursor1.moveToNext()) {
            RecipeJson recipe = new RecipeJson();
            recipe.id = cursor1.getInt(cursor1.getColumnIndex(RecipesTable.COLUMN_ID));
            recipe.image = cursor1.getString(cursor1.getColumnIndex(RecipesTable.COLUMN_IMAGE));
            recipe.name = cursor1.getString(cursor1.getColumnIndex(RecipesTable.COLUMN_NAME));
            recipe.isFavorite = cursor1.getInt(cursor1.getColumnIndex(RecipesTable.COLUMN_IS_FAVORITE)) != 0;
            recipe.ingredients = new ArrayList<>();
            if (cursor2.moveToFirst()) {
                do {
                    if (recipe.id == cursor2.getInt(cursor2.getColumnIndex(IngredientsTable.COLUMN_RECIPE_ID))) {
                        IngredientJson ingredient = new IngredientJson();
                        ingredient.ingredient = cursor2.getString(cursor2.getColumnIndex(IngredientsTable.COLUMN_INGREDIENT));
                        ingredient.measure = cursor2.getString(cursor2.getColumnIndex(IngredientsTable.COLUMN_MEASURE));
                        ingredient.quantity = cursor2.getFloat(cursor2.getColumnIndex(IngredientsTable.COLUMN_QUANTITY));
                        recipe.ingredients.add(ingredient);
                    }
                } while (cursor2.moveToNext());
            }
            recipe.steps = new ArrayList<>();
            if (cursor3.moveToFirst()) {
                do {
                    if (recipe.id == cursor3.getInt(cursor3.getColumnIndex(StepsTable.COLUMN_RECIPE_ID))) {
                        StepJson step = new StepJson();
                        step.id = cursor3.getInt(cursor3.getColumnIndex(StepsTable.COLUMN_ID));
                        step.description = cursor3.getString(cursor3.getColumnIndex(StepsTable.COLUMN_DESCRIPTION));
                        step.shortDescription = cursor3.getString(cursor3.getColumnIndex(StepsTable.COLUMN_SHORT_DESCRIPTION));
                        step.thumbnailURL = cursor3.getString(cursor3.getColumnIndex(StepsTable.COLUMN_THUMBNAIL_URL));
                        step.videoURL = cursor3.getString(cursor3.getColumnIndex(StepsTable.COLUMN_THUMBNAIL_URL));
                        recipe.steps.add(step);
                    }
                } while (cursor3.moveToNext()) ;
            }
            recipes.add(recipe);
        }

        cursor1.close();
        cursor2.close();
        cursor3.close();

        return recipes;
    }

    //change the favorite recipe
    public static void updateFavorite(Context appContext, int newFavoriteId) {
        SQLiteDatabase db = new RecipesOpenHelper(appContext).getReadableDatabase();
        db.beginTransaction();
        ContentValues cv = new ContentValues();
        cv.put(RecipesTable.COLUMN_IS_FAVORITE, false);
        db.update(RecipesTable.TABLE_NAME, cv, RecipesTable.COLUMN_IS_FAVORITE, null);
        cv.put(RecipesTable.COLUMN_IS_FAVORITE, true);
        db.update(RecipesTable.TABLE_NAME, cv, RecipesTable.COLUMN_ID + "=" + newFavoriteId, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        //broadcast about finishing
        Intent intent1 = new Intent(UpdateRecipesService.UPDATE_RECIPES_SERVICE_FINISHED);
        appContext.sendBroadcast(intent1);
    }
}