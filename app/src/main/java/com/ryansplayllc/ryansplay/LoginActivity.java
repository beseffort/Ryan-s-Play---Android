package com.ryansplayllc.ryansplay;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import com.crashlytics.android.Crashlytics;
//import com.facebook.AppEventsLogger;
//import com.facebook.Request;
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.SessionState;
//
//import com.facebook.UiLifecycleHelper;
//import com.facebook.model.GraphUser;
//import com.facebook.widget.LoginButton;


import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.models.Creator;
import com.ryansplayllc.ryansplay.models.Umpire;
import com.ryansplayllc.ryansplay.receivers.GameReceiver;
import com.ryansplayllc.ryansplay.receivers.UserReciver;
import com.ryansplayllc.ryansplay.services.GameService;

import br.net.bmobile.websocketrails.WebSocketRailsDataCallback;
import br.net.bmobile.websocketrails.WebSocketRailsDispatcher;

import android.view.inputmethod.EditorInfo;

public class LoginActivity extends Activity  implements LocationListener {

    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView loginTextView;
    private TextView signOutTextView;
    private LocationManager locationManager;
    private Button fbLoginButton;
    private Button userLoginButton;
    private TextView cancelSigninButton;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private AlertDialog networkErrorDialog;

    public String overideSession = "false";
    private boolean buttonClicked = false;
    private TextView forgotPasswordTextView;
    public static ProgressDialog progressDialog;
    public static String fbSessionToken = "";


    // public profileselector
    private PopupWindow popupWindow;
    private View loginErrorlayout;

    // user broascast receiver
    private UserReciver userReciver;
    private ArrayList<String> permissions;
    private LoginManager loginManager;
    private CallbackManager callbackManager;
    private String currentaccessToken;
    private AccessTokenTracker accessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(com.ryansplayllc.ryansplay.R.layout.activity_login);
        Typeface bebasfont = Typeface.createFromAsset(getAssets(), "BebasNeue Bold.ttf");


        Rollbar.setIncludeLogcat(true);


        // network
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();

        Utility.deleteCache(getApplicationContext());


        // progress dialog
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        fbLoginButton= (Button) findViewById(R.id.fb_lg_bt);
        fbLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("user_birthday", "email", "public_profile", "user_friends", "user_location"));
            }
        });


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                currentaccessToken = AccessToken.getCurrentAccessToken().getToken();
                if (AccessToken.getCurrentAccessToken() != null) {
                    Log.e("access token", currentaccessToken + "");
                    fbSessionToken = currentaccessToken + "";
                    fbLogin();

                }


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });


        //applying font style to

        // find and assigning views
        emailEditText = (EditText) findViewById(com.ryansplayllc.ryansplay.R.id.login_et_email);
        passwordEditText = (EditText) findViewById(com.ryansplayllc.ryansplay.R.id.login_et_password);
        passwordEditText.setImeActionLabel("Go", EditorInfo.IME_ACTION_DONE);

        // loginTextView = (TextView) findViewById(R.id.login_tv_login);
        // signOutTextView = (TextView) findViewById(R.id.login_tv_sign_up);
        fbLoginButton  = (Button) findViewById(R.id.fb_lg_bt);
        forgotPasswordTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.login_tv_forgot_password);
        userLoginButton=(Button) findViewById(com.ryansplayllc.ryansplay.R.id.lg_bt_login);
        userLoginButton.setTypeface(bebasfont);
        fbLoginButton.setTypeface(bebasfont);



        permissions = new ArrayList<String>();
        permissions.add("email");
        permissions.add("user_location");
        permissions.add("user_birthday");
        permissions.add("public_profile");





//


        //cancel click listener
        cancelSigninButton=(TextView) findViewById(com.ryansplayllc.ryansplay.R.id.cancelsignin);
        cancelSigninButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(LoginActivity.this,LauncherScreenActivity.class);
                startActivity(signUp);
            }
        });

        // forgot password textview on click listener
        forgotPasswordTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });

        // user receiver
        IntentFilter filter = new IntentFilter(UserReciver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        userReciver = new UserReciver();
        registerReceiver(userReciver, filter);




        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();


    }




    public void login(View view) {

        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString();
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
        Matcher emailMatcher = emailPattern.matcher(email);
        if(email.length()==0)
        {
            emailEditText.setError("Enter your Email or Username");
            return;
        }


//        if (!emailMatcher.matches()) {
//            emailEditText.setError("Not a valid e mail");
//            return;
//        }
        if (password.length() == 0) {
            passwordEditText.setError("Password should not be empty");
            return;
        }
        login(email, password);
        progressDialog.show();
    }

    public void forgotPassword() {
        final String email = emailEditText.getText().toString().trim();
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
        Matcher emailMatcher = emailPattern.matcher(email);
        if (!emailMatcher.matches()) {
            emailEditText.setError("Not a valid e mail");
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
        } catch (Exception exception) {

        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Method.POST, Utility.forgotPassword, params,
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {

                            if (jsonObject.getBoolean("status")) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Check your email for further instruction",
                                        Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
//                            Toast.makeText(getApplicationContext(),
//                                    "Error Occured", Toast.LENGTH_SHORT).show();
                            showServerErrorPopup("Error Occured");
                        }
                        progressDialog.dismiss();
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
//                Toast.makeText(getApplicationContext(),
//                        "Error Occured", Toast.LENGTH_SHORT).show();
                showServerErrorPopup("Internal Server Error. Please try again later");

            }
        });
        queue.add(jsonObjectRequest);
        progressDialog.show();
        queue.start();
    }

    public void loginConfirm(JSONObject responseJsonObject){

        try{


            JSONObject userJsonObject = responseJsonObject
                    .getJSONObject("user");
            Utility.user.jsonParser(userJsonObject);
            Utility.user.setAccessToken(userJsonObject
                    .getString("access_token"));

            // storing in shared preferences
            Utility.setAccessKey(LoginActivity.this,
                    Utility.user.getAccessToken());
            AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(Utility.getAccessKey(LoginActivity.this));

            Utility.setUserId(LoginActivity.this,
                    Utility.user.getId());

            try{
                Crashlytics.setUserName(Utility.user.getUserName());
                Crashlytics.setUserIdentifier(Utility.getAccessKey(LoginActivity.this));
            }catch(Exception e){
                Log.e("crashlytics user name error: login activity", e.getMessage() + " Access token ");
            }

            SharedPreferences notifPref  =getSharedPreferences("notificationstatus",MODE_PRIVATE);
            SharedPreferences.Editor notifEditor= notifPref.edit();

            notifEditor.putString("notificationstatus","true");
            notifEditor.commit();

            Utility.isNotificationsEnable = true;
            LoginActivity.progressDialog.dismiss();
            Intent gameScreenIntent = new Intent(
                    LoginActivity.this,
                    GameHomeScreenActivity.class);
            startActivity(gameScreenIntent);

            finish();
        }catch(Exception e){
            Log.e("confirm login exception "," " + e.getMessage());
        }

    }

    private void fbLogin(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        // params
        JSONObject paramsJsonObject = new JSONObject();
        try {
            paramsJsonObject.put("provider_access_token", fbSessionToken);
            paramsJsonObject.put("override_session", overideSession);
            paramsJsonObject.put("uuid",Utility.uuid);
            paramsJsonObject.put("push_token",Utility.pushToken);
            paramsJsonObject.put("os_type",Utility.osType);


        } catch (Exception exception) {
        }

        // volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Method.POST, Utility.baseURL+"/v2/session/facebook_login", paramsJsonObject,
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject responseJsonObject) {
                        try {
                            Log.e("** fblogin",responseJsonObject.toString());
                            if (responseJsonObject.getString("status").equals("true")) {

                                if(responseJsonObject.has("player_status")){

                                    AlertDialog.Builder networkErrorBuilder = new AlertDialog.Builder(
                                            LoginActivity.this);
                                    networkErrorBuilder.setMessage(responseJsonObject.getString("message"));
                                    networkErrorBuilder.setPositiveButton("YES",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    overideSession = "true";
                                                    fbLogin();
                                                }
                                            });
                                    networkErrorBuilder.setNegativeButton("NO",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                    networkErrorDialog = networkErrorBuilder.create();
                                    networkErrorDialog.show();

                                }else{
                                    loginConfirm(responseJsonObject);
                                }


                            } else {
//                                showServerErrorPopup(responseJsonObject.getString("message"));

                                Dialog errorPopupDialog=new Dialog(LoginActivity.this);
                                final AlertDialog.Builder builder = new AlertDialog.Builder(
                                        LoginActivity.this);
                                builder.setMessage(responseJsonObject.getString("message")).setTitle(
                                        "Error");

                                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        progressDialog.dismiss();

                                    }
                                });

                                errorPopupDialog = builder.create();
                                errorPopupDialog.show();

                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            //showServerErrorPopup("Error Occured "+e.getMessage());
//                            Toast.makeText(getApplicationContext(),"Internal server error" ,Toast.LENGTH_SHORT).show();
                            showServerErrorPopup("Error Occured");
                        }
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //showServerErrorPopup("Error Occured " + error.networkResponse);
//                Toast.makeText(getApplicationContext(),"Internal server error occurred and server response " + error.networkResponse ,Toast.LENGTH_SHORT).show();
                showServerErrorPopup("Internal Server Error. Please try again later");
            }
        });
        queue.add(jsonObjectRequest);
        queue.start();
    }

    private void login(String email, String password) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        // params
        JSONObject paramsJsonObject = new JSONObject();
        try {
            paramsJsonObject.put("email", email);
            paramsJsonObject.put("password", password);
            paramsJsonObject.put("override_session",overideSession);
            paramsJsonObject.put("uuid",Utility.uuid);
            paramsJsonObject.put("push_token",Utility.pushToken);
            paramsJsonObject.put("os_type",Utility.osType);

        } catch (Exception exception) {
        }

        // volley request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Method.POST, Utility.loginURL, paramsJsonObject,
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject responseJsonObject) {
                        try {

                            Log.e("after login ",responseJsonObject.toString());

                            if (responseJsonObject.getBoolean("status")) {

                                if(responseJsonObject.has("player_status")){

                                    AlertDialog.Builder networkErrorBuilder = new AlertDialog.Builder(
                                            LoginActivity.this);
                                    networkErrorBuilder.setMessage(responseJsonObject.getString("message"));
                                    networkErrorBuilder.setPositiveButton("YES",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    overideSession = "true";
                                                    login(emailEditText.getText().toString(),passwordEditText.getText().toString());
                                                }
                                            });
                                    networkErrorBuilder.setNegativeButton("NO",
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                    networkErrorDialog = networkErrorBuilder.create();
                                    networkErrorDialog.show();

                                }else{
                                    loginConfirm(responseJsonObject);
                                }

                            } else {
//                                showServerErrorPopup(responseJsonObject.getString("message"));
                                Dialog errorPopupDialog=new Dialog(LoginActivity.this);
                                final AlertDialog.Builder builder = new AlertDialog.Builder(
                                        LoginActivity.this);
                                builder.setMessage(responseJsonObject.getString("message")).setTitle(
                                        "Error");

                                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        progressDialog.dismiss();
                                    }
                                });

                                errorPopupDialog = builder.create();
                                errorPopupDialog.show();
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            showServerErrorPopup("Error Occured");
                        }
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                showServerErrorPopup("Internal Server Error. Please try again later");
            }
        });
        queue.add(jsonObjectRequest);
        queue.start();
    }

    public void showServerErrorPopup(String errorMessage)
    {
        progressDialog.dismiss();
        AlertDialog.Builder errorDialog = new AlertDialog.Builder(LoginActivity.this);
        errorDialog.setMessage(errorMessage);
        errorDialog.setTitle("Error");
        errorDialog.setCancelable(false);
        errorDialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        errorDialog.create();
        errorDialog.show();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            showNetworkErrorDialog();
        } else {
            if (networkErrorDialog != null && networkErrorDialog.isShowing()) {
                networkErrorDialog.dismiss();
            }
            if (isLoged()) {


                getUserDetails();
            } else {

                progressDialog.dismiss();
            }
        }

        SharedPreferences pushTokenSP=getSharedPreferences("pushtoken",MODE_PRIVATE);
        Utility.pushToken = pushTokenSP.getString("pushtoken","default");

        Log.e("registrationid", Utility.pushToken);


    }

    private boolean isLoged() {
        Utility.user.setAccessToken(Utility
                .getAccessKey(LoginActivity.this));

        Log.e("**launcher screen",Utility.getAccessKey(LoginActivity.this)+"");
        if (Utility.getAccessKey(LoginActivity.this).equals("")) {
            return false;
        }
        return true;
    }

    private void showNetworkErrorDialog() {
        AlertDialog.Builder networkErrorBuilder = new AlertDialog.Builder(
                LoginActivity.this);
        networkErrorBuilder.setMessage("You are not connected to internet!")
                .setTitle("Internet Connection");
        networkErrorBuilder.setPositiveButton("Connect",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Settings.ACTION_SETTINGS);
                        startActivityForResult(i, 1);
                        dialog.dismiss();
                    }
                });
        networkErrorBuilder.setNegativeButton("Exit",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        networkErrorDialog = networkErrorBuilder.create();
        networkErrorDialog.show();
    }

    private void getUserDetails() {
        RequestQueue requestQueue = Volley
                .newRequestQueue(getApplicationContext());
        JsonObjectRequest userProfileRequest = new JsonObjectRequest(
                Method.GET, Utility.privateProfile, null,
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseJsonObject) {
                        try {
                            progressDialog.dismiss();
                            if (responseJsonObject.getBoolean("status")) {
                                Utility.user.jsonParser(responseJsonObject
                                        .getJSONObject("user"));
                                // temp
                                Utility.setUserId(LoginActivity.this,
                                        Utility.user.getId());


                                checkForDisconnectedGame();
                                Log.e("user id",Utility.user.getId()+"");


                            } else {
//
                            }
                        } catch (JSONException e) {

                            progressDialog.dismiss();

                        }
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
//
                progressDialog.dismiss();


                AlertDialog.Builder builder1=new AlertDialog.Builder(LoginActivity.this);
//

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization",
                        Utility.getAccessKey(LoginActivity.this));
                return params;
            }
        };

        progressDialog.setMessage("Loading....");
//        progressDialog.show();
        requestQueue.add(userProfileRequest);
        requestQueue.start();
    }

    private void checkForDisconnectedGame() {



        RequestQueue requestQueue = Volley
                .newRequestQueue(getApplicationContext());
        JsonObjectRequest userProfileRequest = new JsonObjectRequest(
                Method.GET, Utility.connectOrDisconnect, null,
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseJsonObject) {

                        Log.e("**Launcher screen positive response","coming positively"+responseJsonObject.toString());
                        try {

                            if (responseJsonObject.getBoolean("status")) {
                                String status = responseJsonObject.getString("player_game_playing_status");

                                // if game is in waiting status
                                if(status.equalsIgnoreCase("waiting")){

                                    Utility.gameStatus = "waiting";

                                    goToWaitingGame(responseJsonObject,"waiting_game");


                                }
                                // if game is in playing status
                                else if(status.equalsIgnoreCase("playing")){
                                    Utility.gameStatus = "playing";
//                                    reJoinedGame(responseJsonObject,Utility.gameStatus);
                                    goToWaitingGame(responseJsonObject,"playing_game");
                                    Utility.disconnectedGame.jsonParser(responseJsonObject,"playing_game");
                                    Log.e("playing  game status ","playing"+Utility.gameStatus);
                                }
                                // if game is diconnected
                                else if(status.equalsIgnoreCase("disconnected")){

                                    String gameStatus = responseJsonObject.getJSONObject("disconnected_game").getString("game_status");

                                    Utility.disconnectedGame.jsonParser(responseJsonObject,"disconnected_game");
                                    //if game is disconnected and created i.e. navigates to game lobby
                                    if(gameStatus.equalsIgnoreCase("created"))
                                        Utility.gameStatus = "created";

                                        //if game is disconnected and inprogress i.e. navigates to game screen of current play
                                    else if(gameStatus.equalsIgnoreCase("inprogress"))
                                        Utility.gameStatus = "inprogress";

                                    reJoinedGame(responseJsonObject, "disconnected_game");


                                }
                                //finish();
                            } else {

                                Intent intent = new Intent(
                                        LoginActivity.this,
                                        GameHomeScreenActivity.class);
                                progressDialog.dismiss();

                                finish();
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            Log.e("check for disconnected games exception "," line 3 " + e.getMessage());
                            /*findViewById(R.id.loadingPanel).setVisibility(
                                    View.GONE);
                            progressDialog.dismiss();
                            signUpButton.setEnabled(true);
                            loginButton.setEnabled(true);*/

                        }
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
//						findViewById(R.id.loadingPanel)
//								.setVisibility(View.GONE);
                progressDialog.dismiss();
                //signUpButton.setEnabled(true);
                //loginButton.setEnabled(true);

                AlertDialog.Builder builder1=new AlertDialog.Builder(LoginActivity.this);
                builder1.setMessage(arg0.networkResponse.statusCode+"");
//
                builder1.show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization",
                        Utility.getAccessKey(LoginActivity.this));
                return params;
            }
        };

        progressDialog.setMessage("Loading....");
//        progressDialog.show();
        requestQueue.add(userProfileRequest);
        requestQueue.start();
    }

    private void goToWaitingGame(JSONObject responseJsonObject,String gameStatus){
        try{


            JSONObject playerJsonObject = responseJsonObject.getJSONObject(gameStatus)
                    .getJSONObject("umpire");

            Umpire umpire = new Umpire();

            Utility.game.getUmpire().setUserName(umpire.getUserName());

            umpire.jsonParser(playerJsonObject);





            Utility.game.JsonParser(responseJsonObject
                    .getJSONObject(gameStatus));
            Utility.game.JsonParser(responseJsonObject
                    .getJSONObject(gameStatus).getJSONObject("private_game"));

            Utility.creator = new Creator();
            Utility.creator.jsonParser(responseJsonObject.getJSONObject(gameStatus).getJSONObject("owner"));

            Log.e("playing status game length ",Utility.disconnectedGame.getNo_of_plays_completed()+"");

            SharedPreferences gameidpref=getSharedPreferences("gameid",MODE_PRIVATE);
            SharedPreferences.Editor gameideditor=gameidpref.edit();
            gameideditor.putString(Utility.user.getUserName(),Utility.game.getId()+";"+Utility.user.getUserName());
            gameideditor.commit();
            progressDialog.dismiss();
            Utility.isUmpire = false;
            Log.e("utility is umpire 11 ","false");

            Log.e("check for waiting games ","game id " + responseJsonObject.toString());
            Log.e("check for waiting games ","game status " + Utility.gameStatus);


                /*Intent gameService = new Intent(getApplicationContext(),
                        GameService.class);
                gameService.putExtra("action", "rejoin");
                gameService.putExtra("game_id", Utility.game.getId());
                gameService.putExtra("umpireWilling", "unwilling");

                this.startService(gameService);*/
            //
            //reJoinedGame(responseJsonObject,"waiting_game");


            gameLobby(gameStatus);


        } catch (JSONException e) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Error " + e.getMessage() + " ",Toast.LENGTH_LONG).show();
        }
    }

    public void gameLobby(String status) {
        // TODO Auto-generated method stub

        try {

            GameService.dispatcher = new WebSocketRailsDispatcher(new URL(
                    Utility.socketBaseURL + "?access_token="
                            + Utility.getAccessKey(getApplicationContext())));
            GameService.dispatcher.connect();
            GameService.chanelName = "private_game_" + Utility.game.getId() + "_"
                    + Utility.game.getGameName();
            GameService.gameChannel = GameService.dispatcher.subscribe(GameService.chanelName);

            GameService.gameChannel.bind("joined", new WebSocketRailsDataCallback() {

                @Override
                public void onDataAvailable(Object data) {
                    // TODO Auto-generated method stub
                    Log.e("testing web sockets","new player was joined " + data);
                    try {
                        GameReceiver.gameJoined = true;
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(GameReceiver.ACTION_RESP);
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        broadcastIntent.putExtra("action", "new_player_joined");
                        broadcastIntent.putExtra("value", data.toString());
                        broadcastIntent.putExtra("error", false);
                        sendBroadcast(broadcastIntent);
                    }catch(Exception e){

                    }
                }
            });
            GameService.gameChannel.bind("left", new WebSocketRailsDataCallback() {

                @Override
                public void onDataAvailable(Object data) {
                    // TODO Auto-generated method stub
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(GameReceiver.ACTION_RESP);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra("action", "player_left");
                    broadcastIntent.putExtra("value", data.toString());
                    broadcastIntent.putExtra("error", false);
                    sendBroadcast(broadcastIntent);
                }
            });
            GameService.gameChannel.bind("disconnected", new WebSocketRailsDataCallback() {

                @Override
                public void onDataAvailable(Object data) {
                    // TODO Auto-generated method stub
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(GameReceiver.ACTION_RESP);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra("action", "player_left");
                    broadcastIntent.putExtra("value", data.toString());
                    broadcastIntent.putExtra("error", false);
                    sendBroadcast(broadcastIntent);
                }
            });
            GameService.gameChannel.bind("umpire_changed",
                    new WebSocketRailsDataCallback() {

                        @Override
                        public void onDataAvailable(Object data) {
                            // TODO Auto-generated method stub
                            Log.e("umpire changed in service",data.toString());
                            Intent broadcastIntent = new Intent();
                            broadcastIntent
                                    .setAction(Utility.UMPIRE_CHANGE_ACTION_RESP);
                            broadcastIntent
                                    .addCategory(Intent.CATEGORY_DEFAULT);
                            broadcastIntent
                                    .putExtra("action", "umpire_changed");
                            broadcastIntent.putExtra("value", data.toString());
                            broadcastIntent.putExtra("error", false);
                            sendBroadcast(broadcastIntent);
                        }
                    });
            GameService.gameChannel.bind("abandoned", new WebSocketRailsDataCallback() {

                @Override
                public void onDataAvailable(Object data) {
                    // TODO Auto-generated method stub
                    try {
                        JSONObject jsonObject = new JSONObject(data.toString());
                        if (jsonObject.getBoolean("status")) {
                            if (GameService.dispatcher != null) {
                                if (GameService.dispatcher.isSubscribed(GameService.chanelName)) {
                                    GameService.dispatcher.unsubscribe(GameService.chanelName);
                                    Log.e("channel subscribe status","channel unsubscribed"+GameService.dispatcher.isSubscribed(GameService.chanelName));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(GameReceiver.ACTION_RESP);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra("action", "abandoned");
                    broadcastIntent.putExtra("value", data.toString());
                    broadcastIntent.putExtra("error", false);
                    sendBroadcast(broadcastIntent);
                }
            });
            GameService.gameChannel.bind("game_started", new WebSocketRailsDataCallback() {

                @Override
                public void onDataAvailable(Object data) {
                    // TODO Auto-generated method stub
                    Log.e("testing the web sockets","game was started " + data);
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(GameReceiver.ACTION_RESP);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra("action", "started");
                    broadcastIntent.putExtra("value", data.toString());
                    broadcastIntent.putExtra("error", false);
                    sendBroadcast(broadcastIntent);
                }
            });

//                 if(!(status.equalsIgnoreCase("playing_game") || status.equalsIgnoreCase("waiting_game"))) {
            GameService.gameChannel.bind("result_published", new WebSocketRailsDataCallback() {

                @Override
                public void onDataAvailable(Object data) {
                    // TODO Auto-generated method stub
                    Log.e("private_game.publish_result 1", "success 1" + data.toString());
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(GameScreenActivity.GAME_RESULT_ACTION_RESP);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra("action", "play_result");
                    broadcastIntent.putExtra("value", data.toString());
                    broadcastIntent.putExtra("error", false);
                    sendBroadcast(broadcastIntent);
                }
            });
//                 }

            GameService.gameChannel.bind("finished", new WebSocketRailsDataCallback() {

                @Override
                public void onDataAvailable(Object data) {
                    // TODO Auto-generated method stub
                    try {

                        Log.e("123456789very important"," " + data);
                        JSONObject jsonObject = new JSONObject(data.toString());
                        if (jsonObject.getBoolean("status")) {
                            if (GameService.dispatcher != null) {
                                if (GameService.dispatcher.isSubscribed(GameService.chanelName)) {
                                    GameService.dispatcher.unsubscribe(GameService.chanelName);

                                }
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(GameReceiver.ACTION_RESP);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra("action", "finished");
                    broadcastIntent.putExtra("value", data.toString());
                    broadcastIntent.putExtra("error", false);
                    sendBroadcast(broadcastIntent);
                }
            });
            GameService.gameChannel.bind("argue_call", new WebSocketRailsDataCallback() {

                @Override
                public void onDataAvailable(Object data) {
                    // TODO Auto-generated method stub
//                    Intent broadcastIntent = new Intent();
//                    broadcastIntent
//                            .setAction(GameScreenActivity.this);
//                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
//                    broadcastIntent.putExtra("value", data.toString());
//                    broadcastIntent.putExtra("error", false);
//                    sendBroadcast(broadcastIntent);
                }
            });
            GameService.gameChannel.bind("guessing_period_closed",
                    new WebSocketRailsDataCallback() {

                        @Override
                        public void onDataAvailable(Object data) {
                            Log.e("guessing period closed ","in game service");
                            Intent broadcastIntent = new Intent();
                            broadcastIntent
                                    .setAction(GameScreenActivity.PLAY_START_ACTION_RESP);
                            broadcastIntent
                                    .addCategory(Intent.CATEGORY_DEFAULT);
                            broadcastIntent.putExtra("action", "play_start");
                            broadcastIntent.putExtra("value", data.toString());
                            broadcastIntent.putExtra("error", false);
                            sendBroadcast(broadcastIntent);
                        }
                    });

            JSONObject message = new JSONObject();
            message.put("game_id", Utility.game.getId());
            GameService.gameChannel.dispatch("joined", message);




            Log.e("status of game waiting og the game "," "+Utility.gameStatus);

            Intent intent;
            if(status.equalsIgnoreCase("playing_game")){

                intent = new Intent(
                        LoginActivity.this,
                        GameScreenActivity.class);
                intent.putExtra("gameStatus","inprogress");
                startActivity(intent);
            }
            else if(status.equalsIgnoreCase("waiting_game"))
            {
                intent = new Intent(
                        LoginActivity.this,
                        GameLobbyActivity.class);

                startActivity(intent);
            }
            progressDialog.dismiss();

            //finish();



        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void reJoinedGame(JSONObject responseJsonObject,String gameStatus) {

        try {
            Log.e("check for disconnected games ",responseJsonObject.toString());
            if (responseJsonObject.getBoolean("status")) {


                //Log.e("check for disconnected games ",responseJsonObject.toString());
                JSONObject playerJsonObject = responseJsonObject.getJSONObject(gameStatus)
                        .getJSONObject("umpire");

                Umpire umpire = new Umpire();
                umpire.jsonParser(playerJsonObject);
                Utility.game.JsonParser(responseJsonObject
                        .getJSONObject(gameStatus));
                Utility.game.JsonParser(responseJsonObject.getJSONObject(gameStatus).getJSONObject("private_game"));
                Utility.creator = new Creator();
                Utility.creator.jsonParser(responseJsonObject.getJSONObject(gameStatus).getJSONObject("owner"));

                SharedPreferences gameidpref=getSharedPreferences("gameid",MODE_PRIVATE);
                SharedPreferences.Editor gameideditor=gameidpref.edit();
                gameideditor.putString(Utility.user.getUserName(),Utility.game.getId()+";"+Utility.user.getUserName());
                gameideditor.commit();
                progressDialog.dismiss();
                Utility.isUmpire = false;
                Log.e("utility is umpire 12 ","false");

                Intent gameService = new Intent(getApplicationContext(),
                        GameService.class);
                gameService.putExtra("action", "rejoin");
                gameService.putExtra("game_id", Utility.game.getId());
                gameService.putExtra("umpireWilling", "unwilling");

                this.startService(gameService);
                //


            } else {
                progressDialog.dismiss();
                Toast.makeText(
                        LoginActivity.this,
                        responseJsonObject
                                .getString("message"),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Error " + e.getMessage() + " ",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }





    @Override
    public void onLocationChanged(Location location) {
        String str = "Latitude: "+location.getLatitude()+"Longitude: "+location.getLongitude();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
// TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}




