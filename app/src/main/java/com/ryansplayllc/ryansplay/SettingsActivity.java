package com.ryansplayllc.ryansplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

//import com.facebook.Session;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.receivers.UserReciver;
import com.ryansplayllc.ryansplay.services.UserService;

public class SettingsActivity extends Activity implements
        OnClickListener {

    private ImageButton backImageButton;
    //footer variables
    private FrameLayout leaderBoardFooterButton;
    private FrameLayout profileFooterButton;
    private FrameLayout homeFooterButton;
    private FrameLayout settingsFooterButton;
    private FrameLayout playsFooterButton;

    private Button logoutButton;
    private UserReciver userReciver;

    private IntentFilter filter = new IntentFilter(UserReciver.ACTION_RESP);
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private AlertDialog networkErrorDialog;

    private TextView playerNameTextView;
    private TextView noOfPlaysTextView;
    private RelativeLayout meatTeamNav;

    //Navigation Selections
    private RelativeLayout changepasswordNav;
    private RelativeLayout editprofilenav ;
    private RelativeLayout faqnav,termsandconditions;

    //notificatiion switch
    private Switch notificationSwitch;
    SharedPreferences notifPref;
    SharedPreferences.Editor notifEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(com.ryansplayllc.ryansplay.R.layout.activity_settings);

        // roll bar
        Rollbar.setIncludeLogcat(true);

        logoutButton = (Button) findViewById(com.ryansplayllc.ryansplay.R.id.settings_bt_logout);
        backImageButton = (ImageButton) findViewById(com.ryansplayllc.ryansplay.R.id.ibt_back);
        meatTeamNav=(RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.meetteamnav);
        notificationSwitch = (Switch) findViewById(R.id.notfication_switch);

        // footer
        leaderBoardFooterButton =    (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board);
        profileFooterButton =        (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_account);
        homeFooterButton =           (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_home);
        settingsFooterButton =       (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_settings);
        playsFooterButton =          (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_plays);

        termsandconditions = (RelativeLayout) findViewById(R.id.termsandconditions);

        // footer click listeners
        termsandconditions.setOnClickListener(this);
        leaderBoardFooterButton.setOnClickListener(this);
        profileFooterButton.setOnClickListener(this);
        homeFooterButton.setOnClickListener(this);
        settingsFooterButton.setOnClickListener(this);
        playsFooterButton.setOnClickListener(this);

        logoutButton.setOnClickListener(this);
        backImageButton.setVisibility(View.GONE);
        // Setting selected state
        settingsFooterButton.setSelected(true);
        notifPref  =getSharedPreferences("notificationstatus",MODE_PRIVATE);
        notifEditor= notifPref.edit();

        if(Utility.isNotificationsEnable){
            Log.e("settings activity","true");
            notificationSwitch.setSelected(true);
        }else{
            Log.e("settings activity","false");
            notificationSwitch.setSelected(false);
        }

        //listener for notification switch
        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                {

                    notifEditor.putString("notificationstatus","true");
                    notifEditor.commit();
                    Utility.isNotificationsEnable=true;

                }
                else {

                    Utility.isNotificationsEnable=false;
                    notifEditor.putString("notificationstatus","false");
                    notifEditor.commit();
                }
            }
        });
        if(notifPref.getString("notificationstatus","false").equals("true"))
        {
            notificationSwitch.setChecked(true);
        }
        else
        {
            notificationSwitch.setChecked(false);
        }
        // Listener
        backImageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        userReciver = new UserReciver();
        registerReceiver(userReciver, filter);

        //  playerNameTextView = (TextView) findViewById(R.id.my_tv_player_name);
        //  noOfPlaysTextView = (TextView) findViewById(R.id.my_tv_no_of_plays);

        //Navigation click listeners
        changepasswordNav =(RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.changepasswordnav);
        editprofilenav=(RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.editprofilenav);
        faqnav =(RelativeLayout) findViewById(R.id.faq);

        changepasswordNav.setOnClickListener(this);
        editprofilenav.setOnClickListener(this);
        faqnav.setOnClickListener(this);

        //Changing signout button text font
        Typeface bebasfont = Typeface.createFromAsset(getAssets(), "helvetica_neue_condensed_bold.ttf");
        logoutButton.setTypeface(bebasfont,Typeface.BOLD);


        meatTeamNav.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent meetteamnavscreen=new Intent(getBaseContext(),UmpireScreenActivity.class);
                startActivity(meetteamnavscreen);*/
            }
        });
    }



    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_home:
                intent = new Intent(getApplicationContext(),
                        GameHomeScreenActivity.class);
                // Toast.makeText(getApplication(), getIntent().getStringExtra("homestatus"), Toast.LENGTH_SHORT).show();

                startActivity(intent);
                finish();
//
                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board:
                intent = new Intent(getApplicationContext(),
                        LeaderBoardActivity.class);
                startActivity(intent);
                finish();
                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_account:
                intent = new Intent(getApplicationContext(),
                        ChangeProfileInfoActivity.class);
                startActivity(intent);
                finish();
                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_plays:
                intent = new Intent(getApplicationContext(), Plays.class);
                startActivity(intent);
                finish();

                break;

            case com.ryansplayllc.ryansplay.R.id.ibt_back:
                finish();
                break;
            case com.ryansplayllc.ryansplay.R.id.changepasswordnav :
                Intent changepasswordscreennav=new Intent(getBaseContext(),ChangePasswordActivity.class);
                startActivity(changepasswordscreennav);

                break;

            case com.ryansplayllc.ryansplay.R.id.editprofilenav :
                intent = new Intent(getApplicationContext(),
                        ChangeProfileInfoActivity.class);
                intent.putExtra("settings",true);
                startActivity(intent);

                break;

            case R.id.termsandconditions:
                Intent i = new Intent(SettingsActivity.this,TermsPolicyActivity.class);
                startActivity(i);
                break;
            case R.id.faq :
                intent = new Intent(getApplicationContext(),
                        Faq.class);
                startActivity(intent);

                break;

            case com.ryansplayllc.ryansplay.R.id.settings_bt_logout:

                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        SettingsActivity.this);
                builder.setMessage("I want to sign out.").setTitle(
                        "Sign out");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(Utility.user.getProvoider().equals("facebook"))
                        {
//                        Session session = Session.getActiveSession();
//                        session.closeAndClearTokenInformation();
                            LoginManager.getInstance().logOut();

                        }

                        Intent userService = new Intent(getApplicationContext(),
                                UserService.class);
                        userService.putExtra("action", "logout");
                        Utility.deleteCache(getApplicationContext());
                        SettingsActivity.this.startService(userService);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            showNetworkErrorDialog();
        } else {
            //    Utility.updateHeaderUI(playerNameTextView, noOfPlaysTextView);
        }
    }




    private void showNetworkErrorDialog() {
        AlertDialog.Builder networkErrorBuilder = new AlertDialog.Builder(
                SettingsActivity.this);
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

                    }
                });
        networkErrorDialog = networkErrorBuilder.create();
        networkErrorDialog.show();
    }





    @Override
    protected void onPause() {
        super.onPause();

        Log.e("settings", "paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("settings","stopped");

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.e("settings","onpostresumed");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("settings","destroyed");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeScreenIntent = new Intent(SettingsActivity.this,GameHomeScreenActivity.class);

        startActivity(homeScreenIntent);
        finish();
    }
}
