package com.szip.sleepee.Controller;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.szip.sleepee.R;

public class NotificationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If this activity is the root activity of the task, the app is not running
        if (isTaskRoot()) {
            // Start the app before finishing
//            final Intent parentIntent = new Intent(this, FeaturesActivity.class);
//            parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            final Intent startAppIntent = new Intent(this, MainActivity.class);
            startAppIntent.putExtras(getIntent().getExtras());
            startActivity(startAppIntent);
        }

        // Now finish, which will drop the user in to the activity that was at the top
        //  of the task stack
        finish();
    }
}