package com.example.alex.bakingapp;

import android.support.annotation.Nullable;
import android.support.test.espresso.idling.CountingIdlingResource;

public class BackingCountingIdlingResource {

    @Nullable
    private static CountingIdlingResource mCountingIdlingResource;

    @Nullable
    public static CountingIdlingResource getCountingIdlingResource() {
        return mCountingIdlingResource;
    }

    public static void createCountingIdlingResource() {
        mCountingIdlingResource = new CountingIdlingResource(BackingCountingIdlingResource.class.getName());
    }

    public static void incrementCountingIdlingResource() {
        if (mCountingIdlingResource != null) {
            mCountingIdlingResource.increment();
        }
    }

    public static void decrementCountingIdlingResource() {
        if (mCountingIdlingResource != null) {
            mCountingIdlingResource.decrement();
        }
    }

}