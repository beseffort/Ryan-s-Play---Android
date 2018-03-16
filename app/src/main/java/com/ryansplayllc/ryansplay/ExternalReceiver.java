package com.ryansplayllc.ryansplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ExternalReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            Bundle extras = intent.getExtras();
            //if(!AndroidMobilePushApp.inBackground){
            //MessageReceivingService.sendToApp(extras, context);
            SharedPreferences notificationStatusSP=context.getSharedPreferences("notificationstatus", Context.MODE_PRIVATE);
            boolean isNotificationsEnable = (Boolean) Boolean.parseBoolean(notificationStatusSP.getString("notificationstatus",null));


            try{
                if (intent.getExtras().get("from")!=null && isNotificationsEnable)
                    MessageReceivingService.saveToLog(extras, context);
            }catch (Exception e){
                MessageReceivingService.saveToLog(extras, context);
            }


        }
    }
}

