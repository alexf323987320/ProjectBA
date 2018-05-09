package com.example.alex.bakingapp.json;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class UtilsJson {

    public static final String JSON_LINK = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    public static final String BASE_JSON_URL = "https://d17h27t6h515a5.cloudfront.net/";
    public static final String ENDING_JSON_URL = "topher/2017/May/59121517_baking/baking.json";

    @Nullable
    public static List<RecipeJson> getRecipes() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_JSON_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RecipesService service = retrofit.create(RecipesService.class);
        Call<List<RecipeJson>> recipeCall = service.getRecipes();
        Response<List<RecipeJson>> response = null;
        try {
             response = recipeCall.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null || !response.isSuccessful()) {
            return null;
        } else {
            return response.body();
        }
    }

    private interface RecipesService {
        @GET(ENDING_JSON_URL)
        Call<List<RecipeJson>> getRecipes();
    }
}
