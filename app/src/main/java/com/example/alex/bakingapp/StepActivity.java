package com.example.alex.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.example.alex.bakingapp.json.RecipeJson;

public class StepActivity extends AppCompatActivity {

    public static final String STEP_NUMBER_KEY = "step_number";
    public static final String CURRENT_PAGE_KEY = "current_page";
    public static final int WRONG_STEP_NUMBER = -1000;

    private RecipeJson mRecipeJson;
    private int mStepNumber;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mRecipeJson = (RecipeJson) getIntent().getSerializableExtra(StepsActivity.RECIPE_KEY);
        mStepNumber = getIntent().getIntExtra(STEP_NUMBER_KEY, WRONG_STEP_NUMBER);
        if (mRecipeJson == null || mStepNumber == WRONG_STEP_NUMBER) {
            throw new IllegalArgumentException("Wrong StepActivity parameters");
        }

        mViewPager = findViewById(R.id.view_pager);
        //adapter for mViewPager
        PagerAdapter adapter = new StepPagerAdapter(getSupportFragmentManager(), mRecipeJson, savedInstanceState == null);
        mViewPager.setAdapter(adapter);

        //positioning viewPager only for newly created activity, if rotated - restored automatically
        if (savedInstanceState == null) {
            mViewPager.setCurrentItem(mStepNumber);
        }

        //reaction on changing page
        //onPageSelected not triggered on first creation and on rotation if savedcurrentpage==0
        //and vice versa triggered on manual changing page and on rotation if savedcurrentpage!=0
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            Boolean mFirstCall = true;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                boolean pageIsRestoringFromRotation = mFirstCall && savedInstanceState != null && position == savedInstanceState.getInt(CURRENT_PAGE_KEY);
                mFirstCall = false;
                if (!pageIsRestoringFromRotation) {
                    StepFragment.onPageSelected(getSupportFragmentManager().getFragments(), mRecipeJson.steps.get(position));
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = getParentActivityIntent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_PAGE_KEY, mViewPager.getCurrentItem());
    }

    //***********StepPagerAdapter*******************
    private class StepPagerAdapter extends FragmentPagerAdapter{

        private RecipeJson mRecipeJson;
        private boolean mCreatedForTheFirstTime;

        StepPagerAdapter(FragmentManager fm, RecipeJson recipeJson, boolean createdForTheFirstTime) {
            super(fm);
            mRecipeJson = recipeJson;
            mCreatedForTheFirstTime = createdForTheFirstTime;
        }

        @Override
        public Fragment getItem(int position) {
            StepFragment fragment = new StepFragment();
            Bundle args = new Bundle();
            args.putSerializable(StepFragment.STEP_KEY, mRecipeJson.steps.get(position));
            //first fragment
            if (mCreatedForTheFirstTime) {
                mCreatedForTheFirstTime = false;
                args.putBoolean(StepFragment.ARG_PLAY_WHEN_READY_KEY, true);
            }
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return mRecipeJson == null ? 0 : mRecipeJson.steps.size();
        }
    }
    //***********End*StepPagerAdapter*******************
}
