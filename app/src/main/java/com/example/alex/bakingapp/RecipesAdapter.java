package com.example.alex.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alex.bakingapp.json.RecipeJson;

import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

    private List<RecipeJson> mRecipes;

    public RecipesAdapter(List<RecipeJson> recipes) {
        super();
        mRecipes = recipes;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_item_view, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipeJson recipe = mRecipes.get(position);
        holder.mNameTv.setText(recipe.name);
        if (recipe.isFavorite) {
            holder.mIsFavoriteIv.setVisibility(View.VISIBLE);
        } else {
            holder.mIsFavoriteIv.setVisibility(View.INVISIBLE);
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
        ImageView mIsFavoriteIv;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.recipe_name_tv);
            mIsFavoriteIv = itemView.findViewById(R.id.is_favorite_iv);
        }

    }
}