package com.example.alex.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.alex.bakingapp.json.RecipeJson;

import java.util.List;
import java.util.Random;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

    private List<RecipeJson> mRecipes;

    RecipesAdapter(List<RecipeJson> recipes, OnClickListener onClickListener) {
        super();
        mRecipes = recipes;
        mOnClickListener = onClickListener;
    }

    private OnClickListener mOnClickListener;

    public interface OnClickListener {
        void OnClick(RecipeJson recipeJson);
    }


    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, final int position) {
        RecipeJson recipe = mRecipes.get(position);
        holder.mNameTv.setText(recipe.name);
        if (recipe.isFavorite) {
            holder.mIsFavoriteIv.setVisibility(View.VISIBLE);
        } else {
            holder.mIsFavoriteIv.setVisibility(View.INVISIBLE);
        }
        if (mOnClickListener != null) {
            holder.mNameTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnClickListener.OnClick(mRecipes.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mRecipes == null ? 0 : mRecipes.size();
    }

    public void setRecipes(List<RecipeJson> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder{

        TextView mNameTv;
        CardView mCardCv;
        ImageView mIsFavoriteIv;

        RecipeViewHolder(View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mCardCv = itemView.findViewById(R.id.card_cv);
            mIsFavoriteIv = itemView.findViewById(R.id.is_favorite_iv);
        }

    }
}