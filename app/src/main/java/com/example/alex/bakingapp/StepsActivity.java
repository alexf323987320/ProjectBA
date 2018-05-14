package com.example.alex.bakingapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;

import com.example.alex.bakingapp.db.UtilsDb;
import com.example.alex.bakingapp.json.RecipeJson;
import com.example.alex.bakingapp.json.StepJson;

public class StepsActivity extends AppCompatActivity implements StepsAdapter.OnClickListener{

    private FloatingActionButton mFab;

    public RecipeJson mRecipeJson;
    public static final String RECIPE_EXTRA_ID = "recipe";

    private boolean mIsTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipeJson = (RecipeJson) getIntent().getSerializableExtra(RECIPE_EXTRA_ID);
        setContentView(R.layout.activity_steps);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mRecipeJson.name);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mFab = findViewById(R.id.fab);
        setFabImage();
        //update favorite mark
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRecipeJson.isFavorite) {
                    UtilsDb.updateFavorite(getApplicationContext(), -1);
                    mRecipeJson.isFavorite = false;
                } else {
                    UtilsDb.updateFavorite(getApplicationContext(), mRecipeJson.id);
                    mRecipeJson.isFavorite = true;
                }
                setFabImage();
            }
        });

        mIsTablet = getResources().getBoolean(R.bool.isTablet);
        if (savedInstanceState == null && mIsTablet) {
            loadStepFragment(mRecipeJson.steps.get(0));
        }
    }

    private void loadStepFragment(StepJson stepJson) {
        StepFragment fragment = new StepFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(StepActivity.STEP_EXTRA_ID, stepJson);
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction().replace(R.id.step_frame, fragment).commit();
    }

    private void setFabImage() {

        //Workaround for lollipop
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
            return;
        }

        if (mRecipeJson.isFavorite) {
            //mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_filled, getTheme()));
            mFab.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            //mFab.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border, getTheme()));
            //mFab.setImageDrawable(getDrawable(R.drawable.ic_favorite_border));
            mFab.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = getParentActivityIntent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Click from the StepsFragment (Recycler view onClick)
    @Override
    public void onClick(Object object) {
        if (object instanceof StepJson) {
            if (mIsTablet) {
                loadStepFragment((StepJson) object);
            } else {
                Intent intent = new Intent(this, StepActivity.class);
                intent.putExtra(StepActivity.STEP_EXTRA_ID, (StepJson) object);
                startActivity(intent);
            }
        }
    }

}