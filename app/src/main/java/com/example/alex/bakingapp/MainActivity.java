package com.example.alex.bakingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.alex.bakingapp.db.UtilsDb;
import com.example.alex.bakingapp.json.RecipeJson;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<RecipeJson>> ,
        DialogAboutFillingDb.RespondListener,
        RecipesAdapter.OnClickListener
{

    public static String TAG = "MainActivityTag";
    private static int LOADER_RECIPES_ID = 1;

    private RecipesAdapter mRecipesAdapter;

    private BroadcastReceiver mReceiver;


    //************ Loader callbacks ****************//

    @Override
    @NonNull
    public Loader<List<RecipeJson>> onCreateLoader(int id, Bundle args) {
        return new RecipesLoader(getApplicationContext());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<RecipeJson>> loader, List<RecipeJson> data) {
        Log.d(TAG, "onLoadFinished: ");
        mRecipesAdapter.setRecipes(data);
        if (!((RecipesLoader) loader).mAskedAboutFillingDb && (data == null || data.size() == 0)) {
            ((RecipesLoader) loader).mAskedAboutFillingDb = true;
            showDialogAboutFillingDb();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<RecipeJson>> loader) {
    }

    //************ End loader callbacks ****************//

    //************ Loaders ****************//

    private static class RecipesLoader extends AsyncTaskLoader<List<RecipeJson>> {

        public boolean mAskedAboutFillingDb = false;

        RecipesLoader(Context appContext) {
            super(appContext);
        }

        @Override
        public List<RecipeJson> loadInBackground() {
            return UtilsDb.getRecipes(getContext(), null);
        }

        @Override
        protected void onForceLoad() {
            BackingCountingIdlingResource.incrementCountingIdlingResource();
            super.onForceLoad();
        }

        @Override
        public void deliverResult(@Nullable List<RecipeJson> data) {
            BackingCountingIdlingResource.decrementCountingIdlingResource();
            super.deliverResult(data);
        }
    }

    //************ End loaders****************//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recipesRv = findViewById(R.id.recipes_rv);
        RecyclerView.LayoutManager layout;
        if (getResources().getBoolean(R.bool.isTablet)) {
            layout = new GridLayoutManager(this, 3);
        } else {
            layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
        recipesRv.setLayoutManager(layout);
        mRecipesAdapter = new RecipesAdapter(null, this);
        recipesRv.setAdapter(mRecipesAdapter);

        //updating adapter by loaders
        Loader loader = getSupportLoaderManager().initLoader(LOADER_RECIPES_ID, null, this);
        if (savedInstanceState == null) loader.forceLoad();

        //Receive broadcast from UpdateRecipesService
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(MainActivity.this, R.string.database_updated, Toast.LENGTH_LONG).show();
                Loader loader = getSupportLoaderManager().getLoader(LOADER_RECIPES_ID);
                if (loader != null) loader.forceLoad();
            }
        };
        IntentFilter intentFilter = new IntentFilter(UpdateRecipesService.UPDATE_RECIPES_SERVICE_FINISHED);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_update) {
            updateRecipes();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Read recipes from http and save to db
    public void updateRecipes() {
        if (NetUtils.isConnected(this)) {
            Intent intent = new Intent(this, UpdateRecipesService.class);
            startService(intent);
        } else {
            Snackbar.make(findViewById(R.id.coordinator_layout), R.string.no_internet_connection, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void showDialogAboutFillingDb() {
        DialogFragment dialogFragment = new DialogAboutFillingDb();
        dialogFragment.show(getSupportFragmentManager(), "DialogAboutFillingDb");
    }

    @Override
    public void onDialogPositiveClick() {
        updateRecipes();
    }

    //Recycler view click
    @Override
    public void OnClick(RecipeJson recipeJson) {
        Intent intent = new Intent(this, StepsActivity.class);
        intent.putExtra(StepsActivity.RECIPE_KEY, recipeJson);
        startActivity(intent);
    }
}

