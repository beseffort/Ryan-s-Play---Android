package com.ryansplayllc.ryansplay.receivers;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.GameHomeScreenActivity;
import com.ryansplayllc.ryansplay.LoginActivity;
import com.ryansplayllc.ryansplay.Profile;
import com.ryansplayllc.ryansplay.Rules;
import com.ryansplayllc.ryansplay.SignUpActivity;
import com.ryansplayllc.ryansplay.Utility;
import com.ryansplayllc.ryansplay.models.User;

public class UserReciver extends BroadcastReceiver {

    public static final String ACTION_RESP = "com.ryansplayllc.ryansplay.intent.action.MESSAGE_PROCESSED";

    @Override
    public void onReceive(Context context, Intent intent) {

        // roll bar
        Rollbar.setIncludeLogcat(true);

        String responseJsonString = intent.getStringExtra("value");
        String action = intent.getStringExtra("action");
        boolean error = intent.getBooleanExtra("error", false);
        if (error) {
            if (LoginActivity.progressDialog != null) {
                LoginActivity.progressDialog.dismiss();
            }
            if (SignUpActivity.progressDialog != null) {
                SignUpActivity.progressDialog.dismiss();
            }
            Toast.makeText(context, "Error Occured", Toast.LENGTH_LONG).show();
        } else {
            if (action.equals("logout")) {
                try {
                    JSONObject responseJsonObject = new JSONObject(
                            responseJsonString);
                    if (responseJsonObject.getBoolean("status")) {
//						if (Session.getActiveSession() != null) {
//							Session.getActiveSession()
//									.closeAndClearTokenInformation();
//						}
//						Session.setActiveSession(null);
                        // storing in shared preferences
                        Utility.setAccessKey(context, "");
                        Utility.setUserId(context, -1L);

                        Utility.user = new User();
                        Toast.makeText(context, "Successfully Logged out",
                                Toast.LENGTH_LONG).show();


                    } else {
                        Toast.makeText(context, "session expired",
                                Toast.LENGTH_LONG).show();
                    }

                    Intent loginScreenIntent = new Intent(context,
                            LoginActivity.class);
                    loginScreenIntent
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                    LoginActivity.progressDialog.dismiss();
                    context.startActivity(loginScreenIntent);
                } catch (JSONException e) {
                    Toast.makeText(context, "Error Occured", Toast.LENGTH_LONG)
                            .show();

                }
            } else {
                try {
                    JSONObject responseJsonObject = new JSONObject(
                            responseJsonString);
                    Log.e("userreceiveresponse",responseJsonString.toString());

                    if (responseJsonObject.getBoolean("status")) {
                        JSONObject userJsonObject = responseJsonObject
                                .getJSONObject("user");
                        Utility.user.jsonParser(userJsonObject);
                        Utility.user.setAccessToken(userJsonObject
                                .getString("access_token"));

                        // storing in shared preferences
                        Utility.setAccessKey(context,
                                Utility.user.getAccessToken());
                        Utility.setUserId(context, Utility.user.getId());

                        if (action.equals("signup"))
                        {
                            Utility.signUpStatus=true;
                            Utility.isNotificationsEnable = true;

                            try{
                                SharedPreferences notifPref  = context.getSharedPreferences("notificationstatus",context.MODE_PRIVATE);
                                SharedPreferences.Editor notifEditor= notifPref.edit();

                                notifEditor.putString("notificationstatus","true");
                                notifEditor.commit();
                            }catch (Exception e){

                            }
//                            Toast.makeText(context,responseJsonString,Toast.LENGTH_SHORT).show();
                            SignUpActivity.progressDialog.dismiss();
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            builder.setMessage(responseJsonObject.toString());
//                            builder.show();
                            Intent myAccountIntent = new Intent(context,
                                    Rules.class);

                            try{
                                Crashlytics.setUserName(Utility.user.getUserName());
                                Crashlytics.setUserIdentifier(userJsonObject.getString("access_token"));

                                Log.e("crashlytics after sign up user name: ", Utility.user.getUserName() + " Access token " + userJsonObject.getString("access_token"));
                            }catch(Exception e){

                            }
                            myAccountIntent.putExtra("sign_up", true);
                            context.startActivity(myAccountIntent);
                        } else if (action.equals("signup facebook")) {

                            Log.e("**make a request user reciever sign up facebook "," " + responseJsonString.toString());
                            if (LoginActivity.progressDialog != null) {
                                LoginActivity.progressDialog.dismiss();
                            }
                            if (SignUpActivity.progressDialog != null) {
                                SignUpActivity.progressDialog.dismiss();
                            }

                            Log.e("userreciever fbsignup","success");
                            Utility.signUpStatus=true;
                            Intent homeScreenIntent = new Intent(context,
                                    Rules.class);
                            homeScreenIntent.putExtra("action","signup facebook");
                            context.startActivity(homeScreenIntent);

                        }
                    } else {
                        if (LoginActivity.progressDialog != null) {
                            LoginActivity.progressDialog.dismiss();
                        }
                        if (SignUpActivity.progressDialog != null) {
                            SignUpActivity.progressDialog.dismiss();
                        }
                        Toast.makeText(context,
                                responseJsonObject.getString("message"),
                                Toast.LENGTH_LONG).show();
                        Log.e("userreciever fbsignup","fail"+responseJsonObject.getString("message"));


                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "Error Occured", Toast.LENGTH_LONG)
                            .show();

                }
            }
        }
    }
}
