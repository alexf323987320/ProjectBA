package com.example.alex.bakingapp;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

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
        List<RecipeJson> recipes = UtilsJson.getRecipes();

        //sleep for testing purpose
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (recipes == null) return;
        UtilsDb.saveRecipes(getApplicationContext(), recipes);
        //broadcast about finishing
        Intent intent1 = new Intent(UPDATE_RECIPES_SERVICE_FINISHED);
        sendBroadcast(intent1);
        BackingCountingIdlingResource.decrementCountingIdlingResource();
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        BackingCountingIdlingResource.incrementCountingIdlingResource();
        return super.onStartCommand(intent, flags, startId);
    }

}
