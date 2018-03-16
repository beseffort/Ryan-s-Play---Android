package com.ryansplayllc.ryansplay;

import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.*;
//import com.facebook.AppEventsLogger;
import com.facebook.Profile;
//import com.facebook.Request;
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.UiLifecycleHelper;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
//import com.facebook.model.GraphUser;
//import com.facebook.widget.LoginButton;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.models.SoftKeyboardHandledRelativeLayout;
import com.ryansplayllc.ryansplay.receivers.UserReciver;
import com.ryansplayllc.ryansplay.services.UserService;

import org.json.JSONException;
import org.json.JSONObject;

public class LauncherScreenActivity extends FragmentActivity {


    private RelativeLayout signUpButton;
    private TextView loginButton;
    private LoginButton facebookButton;
    private RelativeLayout facebookButtonTopView;

    //facebook signup pop views
    public RelativeLayout fbSignupPopupLayout;
    private Button fbSIgnupSubmitBtn;
    private EditText fbSignupUsername;
    private EditText fbSignupRpCode;
    private ImageView fbSignupPopupCloseIcon;

    // network
    private AlertDialog networkErrorDialog;
    private NetworkInfo networkInfo;
    private ConnectivityManager connectivityManager;
    private ProgressDialog progressDialog;
    private UserReciver userReciver;

    private ArrayList<String> permissions;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accetokenTracker;
    private AccessTokenTracker accessTokenTracker;
    private LoginManager loginManager;
    private Profile profile;
    private CallbackManager callbackmanager;


    private boolean fbCLicked;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());


        // roll bar
        Rollbar.init(this, Utility.rollbarKey, Utility.rollbarEnviroinment);
        Rollbar.setIncludeLogcat(true);

        // network
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        callbackmanager = CallbackManager.Factory.create();

        fbCLicked =false;
        setContentView(com.ryansplayllc.ryansplay.R.layout.activity_launcher_screen);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        Typeface bebasfont = Typeface.createFromAsset(getAssets(), "BebasNeue Bold.ttf");

        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();
        signUpButton = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.launch_sign_up);
        facebookButton = (LoginButton) findViewById(com.ryansplayllc.ryansplay.R.id.sp_bt_fb_sign_up);
        facebookButtonTopView = (RelativeLayout) findViewById(R.id.facebook_button_top_view);

        //fbsignup pop views
        fbSignupPopupLayout = (RelativeLayout) findViewById(R.id.fbsignup_popup_layout);
        fbSIgnupSubmitBtn = (Button) findViewById(R.id.fbsignup_popup_signupsubmit);
        fbSignupUsername =  (EditText) findViewById(R.id.fbsignup_popup_username);
        fbSignupRpCode   =  (EditText) findViewById(R.id.fbsignup_popup_rpcode);
        fbSignupPopupCloseIcon = (ImageView) findViewById(R.id.fbsignup_popup_close_icon);
        fbSIgnupSubmitBtn.setTypeface(bebasfont);

        loginButton = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.launch_sign_in);
        signUpButton.setEnabled(true);
        facebookButtonTopView.bringToFront();
        permissions = new ArrayList<>();
        permissions.add("email");
        permissions.add("user_birthday");
        permissions.add("public_profile");
        permissions.add("user_friends");
        permissions.add("user_location");
        facebookButton.setReadPermissions(Arrays.asList("email", "user_friends"));


        // Callback registration
        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                fbCLicked=true;
                profile = Profile.getCurrentProfile();



                if (profile != null) {
                    Log.e("fbprofile launcher ", profile.getProfilePictureUri(120, 120) + "");
                    Log.e("fbuserprofile json", profile.getName().toString());


                    Utility.user.setProfilePicURL(profile.getProfilePictureUri(120, 120) + "");


                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    Log.e("fbuser json", object.toString());
                                    Log.e("graph response json", response.toString());
                                    // Application code
                                    try {

                                        Utility.user.setProvoider("facebook");

                                        Utility.signUpURL = Utility.baseURL + "/v2/registration/facebook";
                                        Utility.user.setEmail(object.getString("email"));
                                        Utility.user.setGender(object.getString("gender"));
                                        Utility.user.setFirstName(object.getString("first_name"));
                                        Utility.user.setLastName(object.getString("last_name"));
                                        Utility.user.setUserName(object.getString("name"));


//                                                Utility.user.setBirthday(object.getString("birthday"));

                                        //do something with the data here


                                        fbSignupUsername.clearComposingText();
                                        fbSignupRpCode.clearComposingText();
                                        fbSignupPopupLayout.setVisibility(View.VISIBLE);
                                        fbSignupPopupLayout.bringToFront();
                                        signUpButton.setEnabled(false);
                                        facebookButton.setEnabled(false);
                                        fbSIgnupSubmitBtn.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                               progressDialog = new ProgressDialog(LauncherScreenActivity.this);
                                                progressDialog.setMessage("Loading...");
                                                progressDialog.show();

                                              fbSignupSubmit();
                                                signUpButton.setEnabled(true);
                                                facebookButton.setEnabled(true);
                                            }
                                        });


                                        Log.e("user other info", Utility.user.getEmail() + Utility.user.getGender() + Utility.user.getProfilePicURL());
                                    } catch (JSONException e) {
                                        e.printStackTrace(); //something's seriously wrong here
                                    }
                                }
                            });
                    request.executeAsync();

                }





            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });


        if(fbCLicked) {
            loginManager.logOut();
            facebookButton.performClick();
        }
        loginButton.setEnabled(true);
        // Listener
        signUpButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherScreenActivity.this,
                        SignUpActivity.class));

            }
        });


        loginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        fbSignupPopupCloseIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                fbSignupPopupLayout.setVisibility(View.GONE);
                signUpButton.setEnabled(true);
                facebookButton.setEnabled(true);

                InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(fbSignupUsername.getWindowToken(), 0);
            }
        });
        //setting progress dialogue
        progressDialog = new ProgressDialog(LauncherScreenActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        //progressDialog.show();



    }

    public void fbSignupSubmit()
    {

        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(fbSignupUsername.getWindowToken(), 0);

        String username=fbSignupUsername.getText().toString();
        String rpCode =  " ";
        if(username.length()==0)
        {
            fbSignupUsername.setError("Username should not be empty");
            progressDialog.dismiss();
            return;
        }

        if( !(username.length() >= 3 && username.length() <=20 ))
        {
            fbSignupUsername.setError("Username should contain atleast 3-20 characters");
            progressDialog.dismiss();
            return;
        }

        if(isUsernameUppercaseFound())
        {

            fbSignupUsername.setError("Username should not contain uppercase characters");
            progressDialog.dismiss();
            return;
        }


        Log.e("username contains (.)",username.contains(".")+"");

        if(username.contains("."))
        {
            char a;
            int n=0;
            for(int i=0;i<username.length();i++)
            {
                a=username.charAt(i);
                if(a == '.') {
                    n++;
                }
            }
            if(n>1)
            {
                fbSignupUsername.setError("username can contain maximum of one period(.)");
            }
        }


        rpCode = fbSignupRpCode.getText().toString();
        Utility.user.setUserName(username+"");
        Utility.user.setRpCode(rpCode+"");
        Intent userService = new Intent(getApplicationContext(),
                UserService.class);
        userService.putExtra("action", "signup");
        startService(userService);
        progressDialog.dismiss();
    }

    private boolean isUsernameUppercaseFound() {
        boolean isCharUpper =false;
        for(int i=0;i<fbSignupUsername.getText().toString().length();i++)
        {
            Character c = fbSignupUsername.getText().toString().charAt(i);
            if(c.isUpperCase(c))
            {
                isCharUpper=true;
                break;
            }
            Log.e("uppercase status",c.isUpperCase(c)+"");
        }
        return isCharUpper;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
Log.e("on destroy","");

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.e("on postresume","");

        //for profile pic null case calling facebook login fucntionality by assigning click on facebook button programatically
        if(fbCLicked) {
            if (profile == null) {
                LoginManager.getInstance().logOut();
                facebookButton.performClick();
                fbCLicked =false;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        LoginManager.getInstance().logOut();
        finish();
    }


}