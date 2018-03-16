package com.ryansplayllc.ryansplay.models;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by nimaikrsna on 7/14/2015.
 */
public class NotificationResponse extends Activity {

    Bundle extras;
    String notifResp;
    public void onCreate(Bundle savedInstanceState){
        try{
            extras = getIntent().getExtras();
            notifResp = extras.getString("action");

            if(notifResp.equalsIgnoreCase("cancel")){
                final NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.cancelAll();
            }


        }catch(Exception e){

        }
    }
}
