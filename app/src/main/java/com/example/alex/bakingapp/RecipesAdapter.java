package com.example.alex.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {

    List<String> fakeData = Arrays.asList("recipe 1", "recipe 2", "recipe 3");

    public RecipesAdapter() {
        super();
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
        holder.mTextView.setText(fakeData.get(position));
    }

    @Override
    public int getItemCount() {
        return fakeData.size();
    }


    class RecipeViewHolder extends RecyclerView.ViewHolder{

        TextView mTextView;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.recipe_name_tv);
        }

    }
}
