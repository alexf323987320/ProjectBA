package com.example.alex.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.example.alex.bakingapp.json.StepJson;

public class StepFragment extends Fragment{

    private StepJson mStepJson;
    private boolean mIsLand;
    private boolean mIsTablet;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mIsLand = getResources().getBoolean(R.bool.isLand);
        mIsTablet = getResources().getBoolean(R.bool.isTablet);

        View rootView = null;
        if (mIsLand && !mIsTablet) {
             rootView = inflater.inflate(R.layout.fragment_step_land, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_step, container, false);
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            mStepJson = (StepJson) arguments.getSerializable(StepActivity.STEP_EXTRA_ID);
        }
        if (mStepJson == null) {
            throw new IllegalArgumentException("Argument for step fragment is not set");
        }

        if (mIsLand && !mIsTablet) {
            SurfaceView surfaceView = rootView.findViewById(R.id.surface_sv);
            surfaceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSystemBars();
                }
            });
            //show action bar according full screen mode
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    } else {
                        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                    }
                }
            });
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsLand && !mIsTablet) {
            hideSystemBars();
        }
    }

    private void hideSystemBars() {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                //View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                 View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
}

}
