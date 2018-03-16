package com.ryansplayllc.ryansplay.services;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.facebook.login.LoginManager;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.LauncherScreenActivity;
import com.ryansplayllc.ryansplay.Profile;
import com.ryansplayllc.ryansplay.SignUpActivity;
import com.ryansplayllc.ryansplay.Utility;
import com.ryansplayllc.ryansplay.receivers.UserReciver;

public class UserService extends IntentService {

	public UserService() {
		super("User");
		// roll bar
		Rollbar.setIncludeLogcat(true);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getStringExtra("action");
		if (action.equals("signup")) {
			signUp();
		} else if (action.equals("logout")) {
			logout();
		}
	}

	private void signUp() {
            Utility.fbSignUpStatus1 = true;
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            // params
            JSONObject params = new JSONObject();
            try {
                Utility.user.setRpCode(SignUpActivity.userPromoCode+"");

                params.put("first_name", Utility.user.getFirstName());
                params.put("last_name", Utility.user.getLastName());
                params.put("username", Utility.user.getUserName());
                params.put("email", Utility.user.getEmail());
                params.put("gender",Utility.user.getGender());
                params.put("photo_remote_url",Utility.user.getProfilePicURL());
                params.put("promo_code",Utility.user.getRpCode());
                params.put("uuid",Utility.uuid);
                params.put("push_token",Utility.pushToken);
                params.put("os_type",Utility.osType);

                if (Utility.user.getProvoider() == null) {
                    params.put("password", Utility.user.getPassword());
                } else {
                    params.put("provider", Utility.user.getProvoider());
                }
            } catch (Exception exception)
            {
            }

            // volley request
            JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                    Utility.signUpURL, params, new Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject responseObject) {

                    Log.e("**make a request sign up sucess", "" + responseObject.toString());
                    try{
                        if(responseObject.getBoolean("status")){

                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(UserReciver.ACTION_RESP);
                            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            broadcastIntent.putExtra("value",
                                    responseObject.toString());
                            if (Utility.user.getProvoider() != null) {

                                broadcastIntent.putExtra("action","signup facebook");


                            } else {

                                broadcastIntent.putExtra("action", "signup");
                            }
                            broadcastIntent.putExtra("error", false);
                            sendBroadcast(broadcastIntent);
                        }else{
                            Utility.fbSignUpStatus = false;
                            Utility.fbSignUpStatus1 = false;



                            Toast.makeText(getApplicationContext(),""+responseObject.getString("message"),Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();
                            SignUpActivity.progressDialog.dismiss();


                        }
                    }catch (Exception e){

                    }
                }
            }, new ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {


                    Log.e("sign up error", "" + error.networkResponse);
                        /*Intent broadcastIntent = new Intent();
						broadcastIntent.setAction(UserReciver.ACTION_RESP);
						broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
						broadcastIntent.putExtra("action", "signup");
						broadcastIntent.putExtra("error", true);
						sendBroadcast(broadcastIntent);*/
                }
            });

            queue.add(request);
            queue.start();

	}

	private void logout() {
		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
		// volley request
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
				Method.DELETE, Utility.logoutURL, null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject responseObject) {
						Intent broadcastIntent = new Intent();
						broadcastIntent.setAction(UserReciver.ACTION_RESP);
						broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
						broadcastIntent.putExtra("action", "logout");
						broadcastIntent.putExtra("value",
								responseObject.toString());
						broadcastIntent.putExtra("error", false);
						sendBroadcast(broadcastIntent);
					}
				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Intent broadcastIntent = new Intent();
						broadcastIntent.setAction(UserReciver.ACTION_RESP);
						broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
						broadcastIntent.putExtra("action", "logout");
						broadcastIntent.putExtra("error", true);
						sendBroadcast(broadcastIntent);
					}
				}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("Content-Type", "application/x-www-form-urlencoded");
				params.put("Authorization",
						Utility.getAccessKey(getApplicationContext()));
				return params;
			}
		};
		queue.add(jsonObjectRequest);
		queue.start();

	}

}
