package com.ryansplayllc.ryansplay;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.Random;

/*
 * This service is designed to run in the background and receive messages from gcm. If the app is in the foreground
 * when a message is received, it will immediately be posted. If the app is not in the foreground, the message will be saved
 * and a notification is posted to the NotificationManager.
 */
public class MessageReceivingService extends Service {
    private GoogleCloudMessaging gcm;


    public static void sendToApp(Bundle extras, Context context){

        Log.e("notification received", "" + extras.toString());
        Intent newIntent = new Intent();
        newIntent.setClass(context, FirstSplashScreen.class);
        newIntent.putExtras(extras);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(newIntent);
    }

    public void onCreate(){
        super.onCreate();
        final String preferences = getString(R.string.preferences);
        // In later versions multi_process is no longer the default
        gcm = GoogleCloudMessaging.getInstance(getBaseContext());
            register();
        // Let AndroidMobilePushApp know we have just initialized and there may be stored messages
        //sendToApp(new Bundle(), this);
    }

    protected static void saveToLog(Bundle extras, Context context){

        Log.e("notification received 1", "" + extras.toString());

        Log.e("notification received 2", "" + extras.toString());
        String numOfMissedMessages = context.getString(R.string.num_of_missed_messages);
        int linesOfMessageCount = 0;

            postNotification(new Intent(context, FirstSplashScreen.class), context, extras);

    }

    protected static void postNotification(Intent intentAction, Context context,Bundle extras){
        final NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        //random number for notification id
        Random randomGenerator = new Random();
           int randomInt = randomGenerator.nextInt(100);





        Log.e("notification received 6", " ");
        //final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentAction, Notification.DEFAULT_LIGHTS|Notification.FLAG_AUTO_CANCEL);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intentAction,
                PendingIntent.FLAG_ONE_SHOT);
        PendingIntent dismissIntent = DismissNotification.getDismissIntent(randomInt, context);

        if(extras.getString("message").startsWith("You are disconnected")){
            final Notification notification = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("Game disconnected!")
                    .setContentText(extras.getString("message"))
                    .setContentIntent(null)

                            //.setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(extras.getString("message")+". Would you like to rejoin the game?"))
                    .addAction(R.drawable.tick,"YES",pendingIntent)
                    .addAction(R.drawable.close,"NO",dismissIntent)
                    .getNotification();

            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;

            mNotificationManager.cancelAll();
            mNotificationManager.notify(randomInt, notification);



        }else{
            final Notification notification = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("RP code used!")
                    .setContentText(extras.getString("message"))
                    .setContentIntent(null)

                            //.setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(extras.getString("message")))
                    .getNotification();

            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;

            mNotificationManager.cancelAll();
            mNotificationManager.notify(randomInt, notification);

           quit(context);
        }
    }

    public static void quit(Context context)
    {
        try{
            int pid = android.os.Process.myPid();

            android.os.Process.killProcess(pid);

            System.exit(0);}
        catch (Exception e)
        {
            Toast.makeText(context,"Error Occured in quit",Toast.LENGTH_LONG).show();
        }
    }

    private void register()
    {
        new AsyncTask()
        {
            protected Object doInBackground(final Object... params) {
                String token;
                try {
                    token = gcm.register(getString(R.string.project_number));

                    Utility.pushToken = token;

                    SharedPreferences pushTokenSP=getSharedPreferences("pushtoken",MODE_PRIVATE);
                    SharedPreferences.Editor  pushTokenEditor=pushTokenSP.edit();
                    pushTokenEditor.putString("pushtoken",token+"");
                    pushTokenEditor.commit();

                    Log.i("**registrationId", token);
                } 
                catch (IOException e) {
                    Log.i("Registration Error", e.getMessage());
                }
                return true;
            }
        }.execute(null, null, null);
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

}