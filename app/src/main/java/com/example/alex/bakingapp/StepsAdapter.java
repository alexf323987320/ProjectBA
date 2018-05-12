package com.example.alex.bakingapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alex.bakingapp.json.IngredientJson;
import com.example.alex.bakingapp.json.RecipeJson;
import com.example.alex.bakingapp.json.StepJson;


public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder> {

    private RecipeJson mRecipe;
    private OnClickListener mOnClickListener;

    //returns ingredient, step, or string (caption)
    public interface OnClickListener {
        void onClick(Object object);
    }

    StepsAdapter(RecipeJson recipe, OnClickListener onClickListener) {
        super();
        mRecipe = recipe;
        mOnClickListener = onClickListener;
    }

    //different layouts for caption, step and ingredient
    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == (mRecipe.ingredients.size() + 1)) {
            return R.layout.item_view_caption;
        } else if(position > mRecipe.ingredients.size() + 1) {
            return R.layout.item_view_step;
        } else {
            return R.layout.item_view_ingredient;
        }
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = null;

        if (viewType == R.layout.item_view_caption) {
            view = layoutInflater.inflate(R.layout.item_view_caption, parent, false);
        } else if (viewType == R.layout.item_view_ingredient) {
            view = layoutInflater.inflate(R.layout.item_view_ingredient, parent, false);
        } else if (viewType == R.layout.item_view_step) {
            view = layoutInflater.inflate(R.layout.item_view_step, parent, false);
        }
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        int viewType = holder.getItemViewType();

        if (viewType == R.layout.item_view_caption) {
            final String text = position == 0 ?
                    holder.mNameTv.getContext().getString(R.string.ingredients_caption) :
                    holder.mNameTv.getContext().getString(R.string.steps_caption);
            holder.mNameTv.setText(text);
            if (mOnClickListener != null) {
                holder.mNameTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnClickListener.onClick(text);
                    }
                });
            }
        } else if (viewType == R.layout.item_view_ingredient) {
            final IngredientJson ingredientJson = mRecipe.ingredients.get(position - 1);
            String text = String.valueOf(position) + ". " + ingredientJson.ingredient + ", " +
                    String.valueOf(ingredientJson.quantity) + " " + ingredientJson.measure;
            holder.mNameTv.setText(text);
            if (mOnClickListener != null) {
                holder.mNameTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnClickListener.onClick(ingredientJson);
                    }
                });
            }
        } else if (viewType == R.layout.item_view_step) {
            final StepJson stepJson = mRecipe.steps.get(position - mRecipe.ingredients.size() - 2);
            String text = String.valueOf(stepJson.id) + ". " + stepJson.shortDescription;
            holder.mNameTv.setText(text);
            if (mOnClickListener != null) {
                holder.mNameTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnClickListener.onClick(stepJson);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mRecipe == null ? 0 : mRecipe.ingredients.size() + 1 + mRecipe.steps.size() + 1;
    }

    class StepViewHolder extends RecyclerView.ViewHolder{

        TextView mNameTv;

        StepViewHolder(View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.name_tv);
        }

    }
}