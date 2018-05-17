package com.example.alex.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.alex.bakingapp.db.RecipesContract;
import com.example.alex.bakingapp.db.UtilsDb;
import com.example.alex.bakingapp.json.RecipeJson;

import java.util.List;


public class IngredientsWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(getApplicationContext(), intent);
    }

    //remote adapter for the list
    public static class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        Context mContext;
        RecipeJson mRecipe;

        ListRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            List<RecipeJson> recipes = UtilsDb.getRecipes(mContext, RecipesContract.RecipesTable.TABLE_NAME + "." + RecipesContract.RecipesTable.COLUMN_IS_FAVORITE);
            if (recipes.size() == 0) {
                mRecipe = null;
            } else {
                mRecipe = recipes.get(0);
            }
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            return mRecipe == null ? 0 : mRecipe.ingredients.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.item_view_widget_ingredient);
            rv.setTextViewText(R.id.name_tv, (position + 1) + ". " + mRecipe.ingredients.get(position).ingredient);
            rv.setOnClickFillInIntent(R.id.linear_layout_la, new Intent());
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
