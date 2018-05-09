package com.example.alex.bakingapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.alex.bakingapp.db.UtilsDb;
import com.example.alex.bakingapp.json.RecipeJson;
import com.example.alex.bakingapp.json.UtilsJson;

import java.util.List;

public class UpdateRecipesService extends IntentService {

    public static String UPDATE_RECIPES_SERVICE_FINISHED = "update_recipes_service_finished";
    private static String TAG = "UpdateRecipesService";

    public UpdateRecipesService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        List<RecipeJson> recipes = UtilsJson.getRecipes();
        if (recipes == null) return;
        UtilsDb.saveRecipes(getApplicationContext(), recipes);
        //broadcast about finishing
        Intent intent1 = new Intent(UPDATE_RECIPES_SERVICE_FINISHED);
        sendBroadcast(intent1);
    }
}
