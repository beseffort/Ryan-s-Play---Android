package com.ryansplayllc.ryansplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.facebook.login.LoginManager;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.models.SoftKeyboardHandledRelativeLayout;
import com.ryansplayllc.ryansplay.models.User;
import com.ryansplayllc.ryansplay.receivers.UserReciver;

public class GameHomeScreenActivity extends ActionBarActivity implements
        OnClickListener {

    //----------------------home screen variables---------------------//
    public static String staticNop;
    private ArrayList<ActivityInfo> mActivities;

    //--------------------game buttons Declarations-------------------//
    private LinearLayout createNewGameLinearLayout,joinGameLinearLayout;

    //----------------Footer options Declarations-------------------//
    private ImageView leaderBoardFooterButton,profileFooterButton,homeFooterButton,settingsFooterButton,playsFooterButton;
    private RelativeLayout homeFooterLayout;

    //----------------Learn How to play-----------------------------//
    private TextView learnHowToPlay;

    //----------------Promo coe select option----------------------//
    private TextView homePromoSelectText;

    //----------------promo code views declarations----------------//
    private RelativeLayout promoCodeLayout,homePromoLayout,homePromoEmptyLayout,homePromostatusLayout,homePromoContentLayout,homePromoSubmitButton;
    private TextView promoCodeErrText,addedCoins,homeEnterPromoLabel;
    private EditText homePromocodeEditText;
    private ImageView homePromoLayoutCloseIcon;

    //------------------home screen vies declarations-------------//
    ImageView profileIcon;

    // header variables
    private TextView playerNameTextView;
    private TextView noOfPlaysTextView;

    // dialog
    private AlertDialog dialog;

    // network
    private AlertDialog networkErrorDialog;
    private NetworkInfo networkInfo;
    private ConnectivityManager connectivityManager;
    // progress dialog
    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home_screen);
        // roll bar
        Rollbar.setIncludeLogcat(true);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //checking gamelobby status if user doesnt exited from game lobby making user to go to game lobby screen
        if(Utility.enterdGameLobbyStatus)
        {
            finish();
        }



        // network
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        createNewGameLinearLayout = (LinearLayout) findViewById(R.id.gh_ll_create_game);
        joinGameLinearLayout = (LinearLayout) findViewById(R.id.gh_ll_join_game);

        // clearing homeselector screen value
        Utility.clearGame();

        Utility.fbSignUpStatus = false;
        Utility.fbSignUpStatus1 = false;
        // header finding view
        playerNameTextView = (TextView) findViewById(R.id.my_tv_player_name);
        noOfPlaysTextView = (TextView) findViewById(R.id.home_plays_in_hand);

        // footer
        leaderBoardFooterButton =    (ImageView) findViewById(R.id.fo_ibt_leader_board);
        profileFooterButton =        (ImageView) findViewById(R.id.fo_ibt_account);
        homeFooterButton =           (ImageView) findViewById(R.id.fo_ibt_home);
        settingsFooterButton =       (ImageView) findViewById(R.id.fo_ibt_settings);
        playsFooterButton =          (ImageView) findViewById(R.id.fo_ibt_plays);
        homeFooterLayout =          (RelativeLayout) findViewById(R.id.home_footer_layout);


        //promo code layout views
        promoCodeLayout = (RelativeLayout) findViewById(R.id.promoCodeLayout);
        homePromoLayout = (RelativeLayout) findViewById(R.id.home_promo_layout);
        homePromoEmptyLayout = (RelativeLayout) findViewById(R.id.home_promo_final_layout);
        homePromostatusLayout = (RelativeLayout) findViewById(R.id.home_promo_status_layout);
        homePromocodeEditText = (EditText) findViewById(R.id.home_promo_text);
        homePromoSubmitButton = (RelativeLayout) findViewById(R.id.home_promocode_submitbutton);
        homePromoLayoutCloseIcon = (ImageView) findViewById(R.id.home_promo_popupclose_icon);
        promoCodeErrText = (TextView) findViewById(R.id.promoCodeErrText);

        addedCoins = (TextView) findViewById(R.id.home_promo_addsuccess_text);

        homePromoLayoutCloseIcon.bringToFront();

        homeEnterPromoLabel   =(TextView) findViewById(R.id.home_enter_promo_label);

        // footer listener
        leaderBoardFooterButton.setOnClickListener(this);
        profileFooterButton.setOnClickListener(this);
        homeFooterButton.setOnClickListener(this);
        settingsFooterButton.setOnClickListener(this);
        playsFooterButton.setOnClickListener(this);

        homePromoSelectText = (TextView) findViewById(R.id.enterPromoCode);
        //learn how to play
        learnHowToPlay = (TextView)  findViewById(R.id.learn_how_to_play);
        //learn how to play click listner
        learnHowToPlay.setOnClickListener(this);


        //profile icon reference
        profileIcon = (ImageView) findViewById(R.id.home_profile_image);

        //loading user details....
        getUserDetails();
        UrlImageViewHelper.setUrlDrawable(profileIcon, Utility.user.getProfilePicURL());



        // progress bar
        progressDialog = new ProgressDialog(GameHomeScreenActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        // Listener
        createNewGameLinearLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameHomeScreenActivity.this,
                        CreateNewGameActivity.class);
                startActivity(intent);
            }
        });

        joinGameLinearLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameHomeScreenActivity.this,
                        JoinPrivateGame2.class);
                startActivity(intent);
            }
        });

        homePromoSelectText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                //disabling footer options
                disableFooter();

                //disabling creatgame button
                createNewGameLinearLayout.setEnabled(false);
                //disabling join game button
                joinGameLinearLayout.setEnabled(false);

                homePromostatusLayout.clearAnimation();
                homeEnterPromoLabel.clearAnimation();

                homePromoLayout.setVisibility(View.VISIBLE);
                homePromostatusLayout.setVisibility(View.GONE);

                homePromoEmptyLayout.setVisibility(View.GONE);
                promoCodeLayout.setVisibility(View.VISIBLE);

                homeEnterPromoLabel.bringToFront();

            }
        });

        homePromoSubmitButton.setOnClickListener(this);
        homePromoLayoutCloseIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //enabling footer
                    enableFooter();

                    //enabling joingame button
                    joinGameLinearLayout.setEnabled(true);
                    //enabling create game button
                    createNewGameLinearLayout.setEnabled(true);

                    homePromostatusLayout.clearAnimation();
                    homeEnterPromoLabel.clearAnimation();
                    homePromostatusLayout.setVisibility(View.GONE);
                    homePromoLayout.setVisibility(View.GONE);
                    homeFooterLayout.setVisibility(View.VISIBLE);

                    InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(homePromocodeEditText.getWindowToken(), 0);
                }
                catch (Exception e)
                {
                    Log.e("keyboard hide exception",e.toString());
                }
            }
        });


        //soft keyboard hide/show events
        SoftKeyboardHandledRelativeLayout mainView = (SoftKeyboardHandledRelativeLayout) findViewById(R.id.home_screen_content_layout);
        mainView.setOnSoftKeyboardVisibilityChangeListener(
                new SoftKeyboardHandledRelativeLayout.SoftKeyboardVisibilityChangeListener() {

                    @Override
                    public void onSoftKeyboardShow() {

                        if(homePromoLayout.getVisibility()==View.VISIBLE) {
                            homeFooterLayout.setVisibility(View.GONE);
                            Log.e("on keyboard show", "1");

                        }
                    }

                    @Override
                    public void onSoftKeyboardHide()
                    {
                        Log.e("on keyboard hide","1");
                        homeFooterLayout.setVisibility(View.VISIBLE);
                    }
                });

        homeFooterButton.setSelected(true);

        // dialog

        AlertDialog.Builder builder = new AlertDialog.Builder(
                GameHomeScreenActivity.this);
        builder.setMessage("Do you want to exit from app?").setTitle(
                "Exit");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                System.exit(0);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        Log.d("key", Utility.getAccessKey(GameHomeScreenActivity.this));

        AlertDialog.Builder builder1=new AlertDialog.Builder(GameHomeScreenActivity.this);
        builder1.setMessage(Integer.toString(Utility.user.getPlayer().getNoOfPlays()));

        //updating player name in home screen
        playerNameTextView.setText(Utility.user.getUserName());


        try{
            Crashlytics.setUserName(Utility.user.getUserName());
            Crashlytics.setUserIdentifier(Utility.getAccessKey(GameHomeScreenActivity.this));


        }catch(Exception e){
            Intent toFirstScreen = new Intent(GameHomeScreenActivity.this,FirstSplashScreen.class);
            startActivity(toFirstScreen);
            Log.e("crashlytics user name error: ", e.getMessage() + " Access token");


        }

        checkForNotifications();
    }

    private void checkForNotifications() {

        SharedPreferences notifPref  =getSharedPreferences("notificationstatus",MODE_PRIVATE);

        try{
            //Toast.makeText(getApplicationContext(),notifPref.getString("notificationstatus",null)+"",Toast.LENGTH_SHORT).show();
            Utility.isNotificationsEnable = (Boolean) Boolean.parseBoolean(notifPref.getString("notificationstatus",null));

        }catch (Exception e){
            //Toast.makeText(getApplicationContext(),"exception " + e.getMessage(),Toast.LENGTH_SHORT).show();

            Utility.isNotificationsEnable = true;
        }
    }


    public void animatePromoCode(){


        homePromostatusLayout.setVisibility(View.GONE);
        homePromostatusLayout.clearAnimation();
        homeEnterPromoLabel.clearAnimation();

//        loadin slideup animation
        Animation slideup = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.home_promo_slideup);
        Animation fadeAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.home_promo_fadeanim);

        slideup.setFillAfter(true);
        fadeAnim.setFillAfter(true);

//        appliying slideup animation to label "Enter RP Code"
        homeEnterPromoLabel.startAnimation(slideup);
        homePromostatusLayout.startAnimation(fadeAnim);
        homePromostatusLayout.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getUserDetails();
        UrlImageViewHelper.setUrlDrawable(profileIcon, Utility.user.getProfilePicURL());


    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {


            case R.id.home_promocode_submitbutton:
                if(homePromocodeEditText.getText().toString().equals("")){
                    promoCodeErrText.setText("RP code should not be empty.");
                    animatePromoCode();
                }else {
                    submitPromoCode();
                }
                break;
            case R.id.fo_ibt_leader_board:
                intent = new Intent(getApplicationContext(),
                        LeaderBoardActivity.class);
                startActivity(intent);
                break;
            case R.id.fo_ibt_account:
                intent = new Intent(getApplicationContext(),
                        ChangeProfileInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.fo_ibt_plays:
                intent = new Intent(getApplicationContext(), Plays.class);
                startActivity(intent);
                //finish();
                break;
            case R.id.fo_ibt_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.learn_how_to_play :
                intent = new Intent(getApplicationContext(), Rules.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        dialog.show();
    }

    // dialog
    private void showNetworkErrorDialog() {
        AlertDialog.Builder networkErrorBuilder = new AlertDialog.Builder(
                GameHomeScreenActivity.this);
        networkErrorBuilder.setMessage("You are not connected to internet!")
                .setTitle("Internet Connection");
        networkErrorBuilder.setPositiveButton("Connect",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(i);
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

        try
        {

            RequestQueue requestQueue = Volley
                    .newRequestQueue(getApplicationContext());

            JsonObjectRequest userProfileRequest = new JsonObjectRequest(
                    Method.GET, Utility.userProfile + Utility.user.getId(), null,
                    new Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject responseJsonObject) {



                            progressDialog.dismiss();
                            try {
                                if(responseJsonObject.has("player_status")) {
                                    if (responseJsonObject.getString("player_status").equalsIgnoreCase("logged_out")) {
                                        HandleSessionFail.HandleSessionFail(GameHomeScreenActivity.this);

                                    }
                                }

                                Log.e("userresponse", responseJsonObject.toString());

                                if (responseJsonObject.getBoolean("status")) {


                                    Utility.user.jsonParser(responseJsonObject
                                            .getJSONObject("user"));
                                    // temp
                                    Utility.setUserId(GameHomeScreenActivity.this,
                                            Utility.user.getId());
                                    SharedPreferences useridpref = getSharedPreferences("userid", MODE_PRIVATE);
                                    SharedPreferences.Editor useridEditor = useridpref.edit();
                                    useridEditor.putString("userid", Utility.user.getId() + "");
                                    useridEditor.commit();

                                    //noOfPlaysTextView.setText(Integer.toString(Utility.user.getPlayer().getPlayCoins()));
                                    final JSONObject userInfo = responseJsonObject.getJSONObject("user");
                                    noOfPlaysTextView.setText(userInfo.getString("play_coins"));
                                    Utility.user.setTotalNoOfPlays(userInfo.getString("play_coins"));


                                    staticNop = userInfo.getString("play_coins") + "";

                                    //builder.setMessage( Utility.user.getPlayer().getPlayCoins()+"");
                                    //builder.show();
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub//
                                            try {
                                                noOfPlaysTextView.setText(userInfo.getString("play_coins"));
                                                playerNameTextView.setText(Utility.user.getUserName() + "");

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });

                                }
                            } catch (JSONException e) {
                            }
                        }
                    }, new ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    progressDialog.dismiss();

                    Log.e("userresponse error", arg0.toString());

                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    params.put("Authorization",
                            Utility.getAccessKey(GameHomeScreenActivity.this));
                    return params;
                }
            };
            progressDialog.show();
            requestQueue.add(userProfileRequest);
            requestQueue.start();
        }
        catch (Exception e)
        {
            Log.e("getUserdetails exception",e.toString());
        }
    }


    public void submitPromoCode(){

        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(homePromocodeEditText.getWindowToken(), 0);
        homeFooterLayout.setVisibility(View.VISIBLE);

        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(GameHomeScreenActivity.this);

        Utility.promoCode = homePromocodeEditText.getText().toString();

        final JSONObject params = new JSONObject();
        try {
            params.put("redeem_code", Utility.promoCode);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Method.POST, Utility.redeemPromoCode, params, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject responseJsonObject) {
                progressDialog.dismiss();
                try {

                    Log.e("submit promo code","parameters---> "+params.toString()+"response----> "+responseJsonObject.toString());
                    if (responseJsonObject.getBoolean("status"))
                    {
                        if(responseJsonObject.has("player_status")) {
                            if (responseJsonObject.getString("player_status").equalsIgnoreCase("logged_out")) {
                                HandleSessionFail.HandleSessionFail(GameHomeScreenActivity.this);

                            }
                        }
                        else {

                            int initailCoins = Integer.parseInt(noOfPlaysTextView.getText().toString());
                            Log.e("initial coins",initailCoins+"");

                            homePromoEmptyLayout.setVisibility(View.VISIBLE);
                            promoCodeLayout.setVisibility(View.GONE);

                            JSONObject userJsonObject = responseJsonObject
                                    .getJSONObject("user");
                            Utility.user.jsonParser(userJsonObject);

                            int finalCoins = Utility.user.getPlayer().getPlayCoins() - initailCoins;
                            addedCoins.setText("You've successfully added " + finalCoins);
                            Log.e("initial coins",finalCoins+"");


                            getUserDetails();
                        }
                    }
                    else
                    {
                        promoCodeErrText.setText(responseJsonObject.getString("message"));
                        animatePromoCode();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(GameHomeScreenActivity.this,
                            "Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("submit promo code","err");
                progressDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization",
                        Utility.getAccessKey(GameHomeScreenActivity.this));
                return params;
            }

        };
        queue.add(jsonObjectRequest);
        queue.start();



    }


    @Override
    protected void onPause() {
        super.onPause();

        Log.e("homescreen ","paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("homescreen ","stopped");

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        getUserDetails();
        UrlImageViewHelper.setUrlDrawable(profileIcon, Utility.user.getProfilePicURL());
        enableFooter();
        homeFooterLayout.setVisibility(View.VISIBLE);
        Log.e("homescreen ","onpostresumed");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("homescreen ","destroyed");

    }

    // from the link above
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);


        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
        }
    }


    public void disableFooter()
    {
        //disabling footer options
        homeFooterButton.setEnabled(false);
        playsFooterButton.setEnabled(false);
        leaderBoardFooterButton.setEnabled(false);
        settingsFooterButton.setEnabled(false);
        profileFooterButton.setEnabled(false);
    }

    public void enableFooter()
    {
        //enabling footer options
        homeFooterButton.setEnabled(true);
        playsFooterButton.setEnabled(true);
        leaderBoardFooterButton.setEnabled(true);
        settingsFooterButton.setEnabled(true);
        profileFooterButton.setEnabled(true);
    }

}
