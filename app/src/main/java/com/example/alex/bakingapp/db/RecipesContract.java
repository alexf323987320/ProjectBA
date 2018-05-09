package com.example.alex.bakingapp.db;

import android.provider.BaseColumns;

public class RecipesContract {

    public static final int DB_VERSION = 3;
    public static final String DB_FILE_NAME = "database.db";

    public static class RecipesTable implements BaseColumns {

        public static final String TABLE_NAME = "recipes";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "(\n" +
                        _ID +                       " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                        COLUMN_ID +                 " STRING NOT NULL UNIQUE,\n" +
                        COLUMN_NAME +               " STRING NOT NULL,\n" +
                        COLUMN_IS_FAVORITE +        " BOOLEAN,\n" +
                        COLUMN_IMAGE +              " STRING\n" +
                        ");";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    }

    public static class IngredientsTable implements BaseColumns {
        public static final String TABLE_NAME = "ingredients";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_MEASURE = "measure";
        public static final String COLUMN_INGREDIENT = "ingredient";
        public static final String COLUMN_QUANTITY = "quantity";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "(\n" +
                        _ID +                       " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                        COLUMN_RECIPE_ID +          " INTEGER NOT NULL,\n" +
                        COLUMN_MEASURE +            " STRING,\n" +
                        COLUMN_INGREDIENT +         " STRING NOT NULL,\n" +
                        COLUMN_QUANTITY +           " REAL NOT NULL\n" +
                        ");";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    }

    public static class StepsTable implements BaseColumns {
        public static final String TABLE_NAME = "steps";
        public static final String COLUMN_RECIPE_ID = "recipe_id";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_SHORT_DESCRIPTION = "shortDescription";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_VIDEO_URL = "videoUrl";
        public static final String COLUMN_THUMBNAIL_URL = "thumbnailUrl";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + "(\n" +
                        _ID +                       " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                        COLUMN_RECIPE_ID +          " INTEGER NOT NULL,\n" +
                        COLUMN_ID +                 " INTEGER NOT NULL,\n" +
                        COLUMN_SHORT_DESCRIPTION +  " STRING,\n" +
                        COLUMN_DESCRIPTION +        " STRING,\n" +
                        COLUMN_VIDEO_URL +          " STRING,\n" +
                        COLUMN_THUMBNAIL_URL +      " STRING\n" +
                        ");";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

    }

}
