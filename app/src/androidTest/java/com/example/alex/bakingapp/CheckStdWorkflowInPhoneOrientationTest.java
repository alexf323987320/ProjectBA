package com.example.alex.bakingapp;


import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.support.v7.widget.RecyclerView;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.alex.bakingapp.db.UtilsDb;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CheckStdWorkflowInPhoneOrientationTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @BeforeClass
    public static void clearDb() {
        UtilsDb.deleteAllRecipes(InstrumentationRegistry.getTargetContext());
        //register idling resource
        BackingCountingIdlingResource.createCountingIdlingResource();
        IdlingRegistry.getInstance().register(BackingCountingIdlingResource.getCountingIdlingResource());
    }


    @Test
    public void mainActivityTest() {

        //Agree with the db update
        ViewInteraction appCompatButton = onView(allOf(withText("Yes"), isDescendantOfA(withId(R.id.buttonPanel))));
        appCompatButton.perform(click());

        //Click first recipe
        onView(withId(R.id.recipes_rv)).perform(actionOnItemAtPosition(0, click()));
        //Make the recipe favorite
        onView(withId(R.id.fab)).perform(click());

        //quantity of rows in the adapter
        RecyclerView rv = getCurrentActivity().findViewById(R.id.steps_rv);
        int itemCount = rv.getAdapter().getItemCount();
        //scroll to the last position - because of using autohiding toolbar we need to use this workaround to be sure first step visible on the screen
        onView(withId(R.id.steps_rv)).perform(RecyclerViewActions.scrollToPosition(itemCount - 1));
        //Click on the first step
        onView(withId(R.id.steps_rv)).perform(RecyclerViewActions.actionOnHolderItem(firstStep(rv), ViewActions.click()));
        //click up
        ViewInteraction appCompatImageButton2 = onView(allOf(withContentDescription("Navigate up"), isDescendantOfA(withId(R.id.toolbar))));
        appCompatImageButton2.perform(click());
        //again
        appCompatImageButton2.perform(click());
        //check that we have one favorite element
        onView(withId(R.id.recipes_rv)).perform(RecyclerViewActions.scrollTo(hasDescendant(allOf(withId(R.id.is_favorite_iv), ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))));
    }

    @After
    public void unregisterIdlingResources() {
        IdlingRegistry.getInstance().unregister(BackingCountingIdlingResource.getCountingIdlingResource());
    }

    //*************Helper*methods******************
    public Activity getCurrentActivity() {
        final Activity[] currentActivity = new Activity[1];
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                currentActivity[0] = (Activity) ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED).toArray()[0];
            }
        });
        return currentActivity[0];
    }

    public static Matcher<StepsAdapter.StepViewHolder> firstStep(final RecyclerView rv) {
        return new TypeSafeMatcher<StepsAdapter.StepViewHolder>() {
            @Override
            protected boolean matchesSafely(StepsAdapter.StepViewHolder item) {
                return item.mNameTv.getText().subSequence(0, 2).equals("0.") && item.getItemViewType() == R.layout.item_view_step;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("View holder after Steps");
            }
        };
    }

    //**************End*helper*methods******************
}