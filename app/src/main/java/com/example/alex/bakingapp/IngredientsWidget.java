package com.example.alex.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.example.alex.bakingapp.db.RecipesContract;
import com.example.alex.bakingapp.db.UtilsDb;
import com.example.alex.bakingapp.json.RecipeJson;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, final AppWidgetManager appWidgetManager, final int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredients);
        //favorite recipe
        List<RecipeJson> recipes = UtilsDb.getRecipes(context, RecipesContract.RecipesTable.TABLE_NAME + "." + RecipesContract.RecipesTable.COLUMN_IS_FAVORITE);
        RecipeJson recipe = null;
        String recipeName = "";
        if (recipes.size() != 0) {
            recipe = recipes.get(0);
            recipeName = recipe.name;
        }

        Intent intent;
        //intent for opening StepsActivity from items
        intent = new Intent(context, StepsActivity.class);
        intent.putExtra(StepsActivity.RECIPE_KEY, recipe);
//        PendingIntent template = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent template = TaskStackBuilder.create(context).addNextIntentWithParentStack(intent).getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.list_lv, template);

        //the same intent for the caption
        views.setOnClickPendingIntent(R.id.name_tv, template);
        views.setTextViewText(R.id.name_tv, recipeName);

        //setting remote adapter
        intent = new Intent(context, IngredientsWidgetService.class);
        //it's not possible to send serializable extra (recipe) here because of using intent instead PendingIntent
        //also it's not supposed to put changeable data through extra, because intent is used only in the service's constructor
        //so we should use onDataSetChanged (happens after call notifyAppWidgetViewDataChanged) in the remote adapter and determine the favorite item dynamically
        //intent.putExtra(StepsActivity.RECIPE_ID_EXTRA_ID, recipeId);
        views.setRemoteAdapter(R.id.list_lv, intent);

        //setting empty view
        intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.empty_view_tv, pendingIntent);
        views.setEmptyView(R.id.list_lv, R.id.empty_view_tv);

        // Instruct widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list_lv);
            }
        }, 1000);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        updateWidgets(context, appWidgetManager, appWidgetIds);
    }

    //for calling for manual updating
    public static void updateWidgets(Context context, @Nullable AppWidgetManager appWidgetManager, @Nullable int[] appWidgetIds) {

        if (appWidgetManager == null) {
            appWidgetManager = (AppWidgetManager) context.getSystemService(Context.APPWIDGET_SERVICE);
        }
        if (appWidgetIds == null) {
            appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, IngredientsWidget.class));
        }
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

