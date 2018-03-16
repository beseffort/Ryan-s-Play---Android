package com.ryansplayllc.ryansplay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by nimaikrsna on 8/19/2015.
 */
public class HandleSessionFail
{


    public static void HandleSessionFail(Activity activity)
    {
        Intent intent = new Intent(activity,LoginActivity.class);
        activity.finish();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Toast.makeText(activity.getApplicationContext(),"Session Expired",Toast.LENGTH_SHORT).show();
        activity.startActivity(intent);
    }
}
