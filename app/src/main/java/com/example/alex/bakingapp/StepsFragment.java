package com.example.alex.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alex.bakingapp.json.StepJson;

public class StepsFragment extends Fragment implements StepsAdapter.OnClickListener{

    private StepsActivity mStepsActivity;
    private StepsAdapter.OnClickListener mOnClickListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mStepsActivity = (StepsActivity) context;
            mOnClickListener = (StepsAdapter.OnClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " supposed to be StepsActivity and implement StepsAdapter.OnClickListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_steps, container, false);

        RecyclerView stepsRv = rootView.findViewById(R.id.steps_rv);
        stepsRv.setLayoutManager(new LinearLayoutManager(mStepsActivity));
        stepsRv.setAdapter(new StepsAdapter(mStepsActivity.mRecipeJson, this));

        return rootView;
    }

    //Recycler view onClick
    @Override
    public void onClick(Object object) {
        //redirect to the activity
        mOnClickListener.onClick(object);
    }
}
