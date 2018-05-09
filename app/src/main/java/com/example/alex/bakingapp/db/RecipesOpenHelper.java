package com.example.alex.bakingapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipesOpenHelper extends SQLiteOpenHelper{

    public RecipesOpenHelper(Context appContext) {
        super(appContext, RecipesContract.DB_FILE_NAME, null, RecipesContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RecipesContract.RecipesTable.CREATE_TABLE);
        db.execSQL(RecipesContract.IngredientsTable.CREATE_TABLE);
        db.execSQL(RecipesContract.StepsTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(RecipesContract.RecipesTable.DROP_TABLE);
        db.execSQL(RecipesContract.IngredientsTable.DROP_TABLE);
        db.execSQL(RecipesContract.StepsTable.DROP_TABLE);
        onCreate(db);
    }
}
