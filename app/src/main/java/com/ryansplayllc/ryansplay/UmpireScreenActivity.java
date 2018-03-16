package com.ryansplayllc.ryansplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.net.bmobile.websocketrails.WebSocketRailsDataCallback;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.GameScreenActivity.UmpireResult;

import com.ryansplayllc.ryansplay.GameScreenActivity.Result;
import com.ryansplayllc.ryansplay.adapters.GameScreenLeaderBoardList;
import com.ryansplayllc.ryansplay.adapters.UmpirePlayByPlayAdapter;
import com.ryansplayllc.ryansplay.models.PlayByPlay;
import com.ryansplayllc.ryansplay.models.Player;
import com.ryansplayllc.ryansplay.models.User;
import com.ryansplayllc.ryansplay.receivers.GameReceiver;
import com.ryansplayllc.ryansplay.services.GameService;

public class UmpireScreenActivity extends Activity implements OnClickListener {



/**************************************************Umpire PlayScreen variables***********************************************************/
 // umpire prediction
    private String positionValue = "NA";
    private int postionId = 0;
    private List<Integer> postions = new ArrayList<Integer>(Arrays.asList(
            R.id.umpire_imageView_1b, R.id.umpire_imageView_3b,
            R.id.umpire_imageView_2b, R.id.umpire_imageView_c,
            R.id.umpire_imageView_cf, R.id.umpire_imageView_lf,
            R.id.umpire_imageView_p,  R.id.umpire_imageView_rf,
            R.id.umpire_imageView_ss, R.id.umpire_imageView_swh));

    //field region selected
    private String regionSelected;
    //variable to is position selected
    public boolean fieldPositionSelected;

    //userNoofPlays variable
    private  String userNoOfPlays;

/*----------------------------------Setters and Getters for umpire screen variables--------------------*/

    public boolean isFieldPositionSelected() {
        return fieldPositionSelected;
    }

    public void setFieldPositionSelected(boolean fieldPositionSelected) {
        this.fieldPositionSelected = fieldPositionSelected;
    }



    public String getUserNoOfPlays() {
        return userNoOfPlays;
    }

    public void setUserNoOfPlays(String userNoOfPlays) {
        this.userNoOfPlays = userNoOfPlays;
    }

    public String getRegionSelected() {
        return regionSelected;
    }

    public void setRegionSelected(String regionSelected) {
        this.regionSelected = regionSelected;
    }

/**************************************************umpire screen Views Declarations*****************************************/

    //position views
    private RelativeLayout rfImageView,cfImageView,lfImageView,oneBImageView,twoBImageView,threeBImageView,ssImageView,pImageView
     ,cImageView,homeRunImageView ,baseHitImageView,hitbypitchumpire,runScoreImageView,umpireWalkImageView,umpireStrikeOutImageView;

    //gamescreenlayout
    private  RelativeLayout gameScreenLayout;

    //darkbackround Layout
    private RelativeLayout usDarkBackgroundLayout;

    //submit pop views variables
    private  RelativeLayout submitPopUpLayout;
    private ImageView umpireConfirmResultPopupClose;
    private RelativeLayout confirmPlayButton;
    private  TextView submitPopUpHitRegion,submitPopupPlayNo;

   //left menu layout variables
    private RelativeLayout usLeftMenusLayout;

    //more list variable initialistaion
    private  RelativeLayout usMoreListContent;
    private ImageButton usMoreIconImage;

    // from header menu of game leader board
    private FrameLayout umpireExitButton;
    private  FrameLayout usInviteFriendsText;
    private FrameLayout umpireAddPlaysMenu;
    private  ImageView umpireMoreMenuCloseIcon;

    private  RelativeLayout umpireScreenMoreMenuContent;
    private  TextView usUserRank;
    private ImageView usAddPlaysIcon;

    public TextView selectYourResultLabel;



    //add plays Layout Initialisation
    private RelativeLayout umpireAddPlaysLayout;
    private ImageButton umpireAddPlaysCloseIcon;
    //header no of Playes
    public TextView noOfPlaysInHeaderUmpire;

    //admoreplayes headerbalanceplays
    public  TextView usBalancePlays;

    Context context;

    private TextView currentPlayText;

    // Forfeit dialog
    private AlertDialog forfietDialog;





    //gamescreen background
    private ImageView gameScreenBackground;

    //pop window
    private PopupWindow popupWindow;
    private View gameInfolayout;






    //initilising umpireLeaderboard ListView variables

    private  ListView umpireScreenLeaderboardList;
    private RelativeLayout leaderboardSelect;
    private RelativeLayout chatListSelect;
    private RelativeLayout playByPlaySelect;
    private RelativeLayout currentPlayerLayout;

    // umpire screen views
    private Button submitButton;
    private Button clearButton;

    // game receiver
    private GameReceiver gameReceiver;

/*******************************************************Umpire Play Screen Receivers*********************************************************/

/*----------------------- connection receiver---------------------------*/

    private BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra("connected", false);
            if (!isConnected) {
                finish();
                return;
            }
        }
    };


    public static final String PLAY_LEFT_RECIEVER = "com.ryansplayllc.ryansplay.intent.action.PLAYLEFT";

/*-----------------------------------Play left Receiver------------------*/

    BroadcastReceiver playLeftReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showGameLeaderBoard();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(com.ryansplayllc.ryansplay.R.layout.umpirescreenland);
        context =UmpireScreenActivity.this;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.FILL_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

        registerReceiver(playLeftReceiver,new IntentFilter(GameScreenActivity.PLAY_LEFT_RECIEVER));


        // roll bar
        Rollbar.setIncludeLogcat(true);

        //Log.e("umpire screen ","actvitiy");

        // submit button
        submitButton = (Button) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_bt_submit);
        clearButton = (Button) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_clear_button);

        rfImageView =       (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_imageView_rf);
        cfImageView =       (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_imageView_cf);
        lfImageView =       (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_imageView_lf);
        oneBImageView =     (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_imageView_1b);
        twoBImageView =     (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_imageView_2b);
        threeBImageView =   (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_imageView_3b);
        ssImageView =       (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_imageView_ss);
        pImageView =        (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_imageView_p);
        cImageView =        (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_imageView_c);
        homeRunImageView =  (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_imageView_home_run);
        baseHitImageView =  (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_imageView_base_hit);
        hitbypitchumpire =      (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_imageView_swh);
        runScoreImageView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_imageView_run_score);
        umpireWalkImageView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_ImageView_walk);
        umpireStrikeOutImageView =(RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_strike_out_imageView);

        selectYourResultLabel = (TextView) findViewById(R.id.selectYourResultLabel);

        currentPlayText = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.currentPlayText);


        currentPlayText.setText("Play "+GameScreenActivity.currentPlay);

        if(GameScreenActivity.currentPlay == 1)
        {
            showSelectYourResultLabel();
        }
        else{
            hideSelectYourResultLabel();
        }

        //top header more icons iniialisation
        usMoreIconImage =(ImageButton) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_more_menu_icon);
        usMoreListContent = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.us_more_menus_layout);
        umpireExitButton =      (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_screen_exit_select);
        //usInviteFriendsText =   (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.us_invite_friends);
        umpireAddPlaysMenu =    (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_add_plays_menu);
        umpireMoreMenuCloseIcon = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.us_more_close_icon);
        umpireAddPlaysLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_addmore_plays_Layout);
        umpireAddPlaysCloseIcon=(ImageButton) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_add_plays_close_icon);

        //Umpire screen leader board variables
        leaderboardSelect = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.us_leaderboard_select);
        playByPlaySelect=     (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.us_play_by_play_select);
        usUserRank =        (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.us_user_rank);
        usAddPlaysIcon = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.us_header_addplays_icon);

        //gamescreen layout
        gameScreenLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_landscape);


        //dark background layout
        usDarkBackgroundLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.us_dark_background_layout);

        //left menu layout intialisation
        usLeftMenusLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.us_leftmenus);

        //header noof plays
        noOfPlaysInHeaderUmpire = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.us_header_no_of_plays);
        usBalancePlays = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.balancelabel1);

        //submit popup initialisations of views
        submitPopUpLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.result_submit_layout);
        confirmPlayButton = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.confirmPlayButton);
        submitPopUpHitRegion = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.submit_popup_hit_region);
        umpireConfirmResultPopupClose = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_confirm_popup_close_button);
        submitPopupPlayNo = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.submit_popup_noofplays);


        //appplying hovers for UmpireScreen leaderboard toptab
        leaderboardSelect.setOnClickListener(this);
//        chatListSelect.setOnClickListener(this);
        playByPlaySelect.setOnClickListener(this);

        leaderboardSelect.setSelected(true);


        showGameLeaderBoard();

        usAddPlaysIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                umpireAddPlaysLayout.setVisibility(View.VISIBLE);

                umpireAddPlaysLayout.bringToFront();
            }
        });

        //top header more icon click listner
        usMoreIconImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                usMoreListContent.setVisibility(View.VISIBLE);
                usMoreListContent.bringToFront();
                umpireAddPlaysLayout.setVisibility(View.GONE);

            }
        });

        umpireMoreMenuCloseIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                usMoreListContent.setVisibility(View.GONE);

                usMoreIconImage.setBackgroundResource(Color.TRANSPARENT);
                usMoreIconImage.setImageResource(com.ryansplayllc.ryansplay.R.drawable.more);
            }
        });

        umpireExitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                usMoreListContent.setVisibility(View.GONE);
                usMoreIconImage.setBackgroundResource(Color.TRANSPARENT);
                usMoreIconImage.setImageResource(com.ryansplayllc.ryansplay.R.drawable.more);
                forfietDialog.show();
            }
        });


        //clicklistner of addplays menu form top header more menus List
        umpireAddPlaysMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                umpireAddPlaysLayout.setVisibility(View.VISIBLE);
                umpireAddPlaysLayout.bringToFront();
                usMoreListContent.setVisibility(View.GONE);
                usMoreIconImage.setBackgroundResource(Color.TRANSPARENT);
                usMoreIconImage.setImageResource(com.ryansplayllc.ryansplay.R.drawable.more);


            }
        });

        umpireAddPlaysCloseIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                umpireAddPlaysLayout.setVisibility(View.GONE);


            }
        });


        //setting default gamescreen background and positions focus with respect to listners

        initialScreenConditions();
        gameScreenBackground=(ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.umpirescreenfield);

        umpireWalkImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gameScreenBackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field);

                setFieldPositionSelected(false);

//                if (umpireWalkImageView.isSelected()) {
//
//                    umpireWalkImageView.setSelected(false);
//                    runScoreImageView.setSelected(false);
//                    initialScreenConditions();
//
//                    //setting position value to na in umpire walk off state
//                    positionValue = "NA";
//                }

//                else {

                    //enabling the homerun, basehit, walk by using below function
                    enableOutImages();
                    swhFieldPositionConditions();
                    focusSwh();

                    umpireWalkImageView.setSelected(true);
                    umpireStrikeOutImageView.setSelected(false);
                    hitbypitchumpire.setSelected(false);

                    positionValue = "WLK";
//                }
                return false;

            }
        });


        hitbypitchumpire.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                setFieldPositionSelected(false);

//                if(hitbypitchumpire.isSelected())
//                {
//                    hitbypitchumpire.setSelected(false);
//                    runScoreImageView.setSelected(false);
//                    initialScreenConditions();
//
//                    positionValue = "NA";
//
//
//                }

//                else {

                    enableOutImages();
                    swhFieldPositionConditions();
                    focusSwh();

                    umpireWalkImageView.setSelected(false);
                    umpireStrikeOutImageView.setSelected(false);
                    hitbypitchumpire.setSelected(true);
                    positionValue = "HBP";

//                }
            }
        });



        umpireStrikeOutImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                setFieldPositionSelected(false);


//                if (umpireStrikeOutImageView.isSelected()) {
//                    umpireStrikeOutImageView.setSelected(false);
//
//                    runScoreImageView.setSelected(false);
//                    initialScreenConditions();
//
//                    positionValue = "NA";
//
//                }
//                else {

                    enableOutImages();
                    swhFieldPositionConditions();
                    focusSwh();

                    umpireStrikeOutImageView.setSelected(true);
                    umpireWalkImageView.setSelected(false);
                    hitbypitchumpire.setSelected(false);

                    positionValue = "SO";


//                }
            }
        });





        homeRunImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (homeRunImageView.isSelected())
                {
                    homeRunImageView.setSelected(false);


                    if(!positionValue.equals("NA")) {
                        //enabling run score only when position selected
                        runScoreImageView.setEnabled(true);
                        runScoreImageView.setAlpha((float) 1.0);

                        //enabling basehit only when position selected
                        baseHitImageView.setEnabled(true);
                        baseHitImageView.setAlpha((float) 1.0);





                    }
                    else
                    {
                        //making initial state when homerun is off and when position in the field is unselected
                        positionsEnable();
                        initialScreenConditions();


                    }


                } else {

                    hideSelectYourResultLabel();

                    if(hitbypitchumpire.isSelected() || umpireStrikeOutImageView.isSelected() || umpireWalkImageView.isSelected()) {
                        umpireWalkImageView.setSelected(false);
                        umpireStrikeOutImageView.setSelected(false);
                        hitbypitchumpire.setSelected(false);
                        positionValue = "NA";


                    }

                    positionsEnable();
//                    deFocusSwh();
                    homeRunImageView.setSelected(true);
                    baseHitImageView.setSelected(true);
                    runScoreImageView.setSelected(true);


                    baseHitImageView.setEnabled(false);
                    baseHitImageView.setAlpha((float) 1.0);// base hit and run score becomes true but cannt be changed to false when home run is true
                    runScoreImageView.setEnabled(false);
                    runScoreImageView.setAlpha((float)1.0);


                    umpireWalkImageView.setSelected(false);
                    umpireStrikeOutImageView.setSelected(false);
                    hitbypitchumpire.setSelected(false);

                    submitButton.setEnabled(true);
                    submitButton.setAlpha((float)1);

                    clearButton.setEnabled(true);
                    clearButton.setAlpha((float)1);





                }

            }
        });
        baseHitImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {



                if (baseHitImageView.isSelected()) {
                    baseHitImageView.setSelected(false);

                    baseHitImageView.setEnabled(true);
                    baseHitImageView.setAlpha((float)1);

                }

                else {

                    enableOutImages();

                    if(hitbypitchumpire.isSelected() || umpireStrikeOutImageView.isSelected() || umpireWalkImageView.isSelected()) {
                        umpireWalkImageView.setSelected(false);
                        umpireStrikeOutImageView.setSelected(false);
                        hitbypitchumpire.setSelected(false);
                        positionValue = "NA";
                    }

                    positionsEnable();

                    baseHitImageView.setSelected(true);
                    deFocusSwh();

                    umpireWalkImageView.setSelected(false);
                    umpireStrikeOutImageView.setSelected(false);
                    hitbypitchumpire.setSelected(false);
                }



            }
        });

        runScoreImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (runScoreImageView.isSelected()) {
                    //making run score off
                    runScoreImageView.setSelected(false);

                    submitButton.setEnabled(true);
                    submitButton.setAlpha((float)1);

                    clearButton.setEnabled(true);
                    clearButton.setAlpha((float)1);
                } else {
                    //making runscore to on
                    runScoreImageView.setSelected(true);

                }

            }
        });


        rfImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (rfImageView.isSelected())
//                {
//                    rfImageView.setSelected(false);
//                    positionsoff();
//                    initialScreenConditions();
//
//                    positionValue ="NA";
//
//                }
//                else
//                {
                     regionSpecificRight();

                    twoBImageView.setSelected(false);
                    rfImageView.setSelected(true);
                    oneBImageView.setSelected(false);

                    positionValue = "RF";

//                }

            }
        });
        cfImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (cfImageView.isSelected())
//                {
//                    cfImageView.setSelected(false);
//                    positionsoff();
//                    initialScreenConditions();
//
//                    positionValue ="NA";
//
//                }

//                else {

                    regionSpecificCenter();
                    cfImageView.setSelected(true);
                    cImageView.setSelected(false);
                    pImageView.setSelected(false);


                    positionValue = "CF";
//                }
            }
        });
        lfImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (lfImageView.isSelected())
//                {
//                    lfImageView.setSelected(false);
//                    positionsoff();
//                    initialScreenConditions();
//                                        positionValue ="NA";
//                }
//                else {

                    regionSpecificLeft();
                    lfImageView.setSelected(true);
                    ssImageView.setSelected(false);
                    threeBImageView.setSelected(false);

                    positionValue = "LF";
//                }
            }
        });
        oneBImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (oneBImageView.isSelected())
//                {
//                    oneBImageView.setSelected(false);
//                    positionsoff();
//                    initialScreenConditions();
//
//                    positionValue ="NA";
//
//                }

//                else {


                    regionSpecificRight();
                    twoBImageView.setSelected(false);
                    rfImageView.setSelected(false);
                    oneBImageView.setSelected(true);

                    positionValue = "B1";
//                }

            }
        });
        twoBImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (twoBImageView.isSelected())
//                {
//                    twoBImageView.setSelected(false);
//                    positionsoff();
//                    initialScreenConditions();
//
//                    positionValue ="NA";
//
//
//                }

//                else {



                    regionSpecificRight();
                    twoBImageView.setSelected(true);
                    rfImageView.setSelected(false);
                    oneBImageView.setSelected(false);

                    positionValue = "B2";

//                }

            }
        });

        threeBImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (threeBImageView.isSelected())
//                {
//                    threeBImageView.setSelected(false);
//
//                    positionsoff();
//
//                    initialScreenConditions();
//
//
//                    positionValue ="NA";
//                }
//
//                else {

                    regionSpecificLeft();
                    ssImageView.setSelected(false);
                    lfImageView.setSelected(false);
                    threeBImageView.setSelected(true);

                    positionValue = "B3";

//                }
            }
        });


        cImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (cImageView.isSelected())
//                {
//                    cImageView.setSelected(false);
//                    positionsoff();
//                    initialScreenConditions();
//
//                    positionValue ="NA";
//
//                }
//                else {

                    regionSpecificCenter();
                    cfImageView.setSelected(false);
                    cImageView.setSelected(true);
                    pImageView.setSelected(false);

                    positionValue = "C";
//                }
            }
        });

        pImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//                if(pImageView.isSelected())
//                {
//                    pImageView.setSelected(false);
//                    positionsoff();
//                    initialScreenConditions();
//
//                    positionValue ="NA";
//                }
//
//                else {


                    regionSpecificCenter();
                    cfImageView.setSelected(false);
                    cImageView.setSelected(false);
                    pImageView.setSelected(true);


                    positionValue = "P";

//                }
            }
        });
        ssImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (ssImageView.isSelected())
//                {
//                    ssImageView.setSelected(false);
//                    positionsoff();
//                    initialScreenConditions();
//
//                    positionValue ="NA";
//
//                }
//
//                else {

                    regionSpecificLeft();
                    lfImageView.setSelected(false);
                    ssImageView.setSelected(true);
                    threeBImageView.setSelected(false);

                    positionValue = "SS";
//                }
            }
        });


        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                gameScreenBackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field);
                positionsoff();
                initialScreenConditions();

                positionValue = "NA";

            }
        });


        //submit confirm result pop click listeners
        confirmPlayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                submitResult();
                Log.e("umpire result submit button", "clicked");
            }
        });

        umpireConfirmResultPopupClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                usDarkBackgroundLayout.setVisibility(View.GONE);
                usLeftMenusLayout.setAlpha((float)1);
                submitPopUpLayout.setVisibility(View.GONE);

            }
        });



        Typeface bebasfont = Typeface.createFromAsset(getAssets(), "BebasNeue Bold.ttf");



        // submit listener
        submitButton.setOnClickListener(this);
        submitButton.setTypeface(bebasfont);
        clearButton.setTypeface(bebasfont);

        // broadcast receiver
        IntentFilter filter = new IntentFilter(GameReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        gameReceiver = new GameReceiver();
        registerReceiver(gameReceiver, filter);
        // connection change service
        IntentFilter connectionFilter = new IntentFilter(
                GameScreenActivity.CONNECTION_ACTION_RESP);
        connectionFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(connectionChangeReceiver, connectionFilter);



        //filling listview of game leaderboard
        umpireScreenLeaderboardList=(ListView) findViewById(com.ryansplayllc.ryansplay.R.id.umpire_screen_leaderboard_listView);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                UmpireScreenActivity.this);
        builder.setMessage(Html.fromHtml("<font color='#1191F0'>Are you sure you want to exit the game?</font>")).setTitle("");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                ProgressDialog alertprogressdialog=new ProgressDialog(UmpireScreenActivity.this);
                SharedPreferences gameObjectSp =getSharedPreferences("gameobject",MODE_PRIVATE);
                SharedPreferences.Editor gameObjectEditor = gameObjectSp.edit();
                gameObjectEditor.putString("gameobject"," ");
                gameObjectEditor.commit();

                back();
                finish();
                Utility.enterdGameLobbyStatus = false;
                Intent i = new Intent(UmpireScreenActivity.this, GameHomeScreenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        forfietDialog = builder.create();
    }

    //input that was passed as  a bean object to get the leader board
    public class LeaderBoardInput{

        private long game_id;
        private int page;
        private int per_page;

        public void setGame_id(long game_id){
            this.game_id = game_id;
        }
        public long getGame_id(){
            return this.game_id;
        }

        public void setPage(int page){
            this.page = page;
        }

        public int getPer_page(){
            return per_page;
        }
        public void setPer_page(int per_page){
            this.per_page = per_page;
        }
    }

    public void showGameLeaderBoard()
    {


        final TextView playerListSize = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.ugl_players_list_size);
        final User userObject = new User();

        LeaderBoardInput leaderBoardInput = new LeaderBoardInput();
        leaderBoardInput.setGame_id(Utility.game.getId());
        leaderBoardInput.setPer_page(50);
        leaderBoardInput.setPage(1);



        GameService.dispatcher.trigger("private_game.get_leaderboard",
                leaderBoardInput, new WebSocketRailsDataCallback() {

                    @Override
                    public void onDataAvailable(Object data) {


                        // TODO Auto-generated method stub
                        try {

                            final List<Player> playersList = new ArrayList<Player>();

                            Log.e("string leader board ",data.toString());

                            JSONObject playersObject = new JSONObject(data.toString());
                            JSONArray playersArray = playersObject.getJSONArray("players");

                            SharedPreferences useridpref= getSharedPreferences("userid",MODE_PRIVATE);
                            Integer userId = Integer.parseInt(useridpref.getString("userid","0"));

                            for(int i=0;i<playersArray.length();i++){
                                Player player = new Player();
                                player.jsonParser((JSONObject) playersArray.get(i));

                                playersList.add(player);
                                Log.e("players List", playersList.get(i).getUsername());

                                if(userId==(player.getUserId())){
                                    //noOfPlaysInHeaderUmpire.setText(player.getNoOfPlays()+"");
                                    //usBalancePlays.setText("Balance: "+player.getNoOfPlays()+" Plays");

                                    setUserNoOfPlays(player.getNoOfPlays()+"");
                                    userObject.setUserRank(player.getWorldRank()+"");

                                    Log.e("player.getWorldRank()",player.getWorldRank()+"");

                                    Log.e("player rank in us if ",userObject.getUserRank()+"");

                                }


                            }
                            Log.e("--player size ",playersList.size()+"");

                            final List<Player> playersListtemp =playersList;
                            //   playersList;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    umpireScreenLeaderboardList.setAdapter( new GameScreenLeaderBoardList(UmpireScreenActivity.this,playersListtemp));
                                    umpireScreenLeaderboardList.setDividerHeight(1);
                                    playerListSize.setText("("+playersList.size()+")");
                                    usUserRank.setText("#"+userObject.getUserRank());


                                    Log.e("player rank in us",Utility.user.getUserRank()+"");
                                    noOfPlaysInHeaderUmpire.setText(getUserNoOfPlays()+"");
                                    usBalancePlays.setText("Balance: "+getUserNoOfPlays()+" Plays");

                                }
                            });

                            Log.e("--player size after gameleaderboardList.add ",playersList.size()+"");


                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.e("--player size IN showGameLeaderBoard()",e.getMessage()+"");

                            e.printStackTrace();
                        }

                    }
                }, new WebSocketRailsDataCallback() {

                    @Override
                    public void onDataAvailable(Object data) {
                        // TODO Auto-generated method stub
                        // progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"error data " + data.toString(),Toast.LENGTH_LONG).show();
                        Log.e("private_game.publish_result ","success " + data.toString());
                    }
                });
    }



    public void showUmpirePlayByPlay()
    {

        RelativeLayout playByPlayTopTabView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.toptab3);
        RelativeLayout leaderBoardTopTab = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.lb_toptab3);

        playByPlayTopTabView.setVisibility(View.VISIBLE);
        leaderBoardTopTab.setVisibility(View.GONE);

        RelativeLayout.LayoutParams Listparams = (RelativeLayout.LayoutParams) umpireScreenLeaderboardList.getLayoutParams();
        Listparams.addRule(RelativeLayout.BELOW, com.ryansplayllc.ryansplay.R.id.toptab3);
        Listparams.topMargin=0;

        umpireScreenLeaderboardList.setLayoutParams(Listparams);


        LeaderBoardInput leaderBoardInput = new LeaderBoardInput();
        leaderBoardInput.setGame_id(Utility.game.getId());
        leaderBoardInput.setPer_page(50);
        leaderBoardInput.setPage(1);
        GameService.dispatcher.trigger("private_game.get_play_by_play",
                leaderBoardInput, new WebSocketRailsDataCallback() {

                    @Override
                    public void onDataAvailable(Object data) {

                        try {

                            final Player player=new Player();
                            final ArrayList<PlayByPlay> playByPlayArrayList = new ArrayList<PlayByPlay>();

                            JSONObject playByPlayJson = new JSONObject(data.toString());
                            JSONArray playbyPlayJsonArray= playByPlayJson.getJSONArray("play_by_play");
                            for (int i=0; i<playbyPlayJsonArray.length();i++)
                            {
                                PlayByPlay playByPlayObject = new PlayByPlay();
                                playByPlayObject.jsonParser((JSONObject)playbyPlayJsonArray.get(i));
                                playByPlayArrayList.add(playByPlayObject);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //thread to update play by play list
                                    umpireScreenLeaderboardList.setAdapter(new UmpirePlayByPlayAdapter(UmpireScreenActivity.this, playByPlayArrayList));
                                    umpireScreenLeaderboardList.setDividerHeight(1);
                                }
                            });

                        }
                        catch (Exception e)
                        {
                            Log.e("showplaybyPlay() in catch", "after trigger in onDataAvailable trigegr" +e.getMessage());

                        }
                    }
                },
                new WebSocketRailsDataCallback() {

                    @Override
                    public void onDataAvailable(Object data) {
                        // TODO Auto-generated method stub

                        Toast.makeText(getApplicationContext(),"error data " + data.toString(),Toast.LENGTH_LONG).show();
                        Log.e("playbyPlay() error" , "after trigger in onDataAvailable trigegr");                    }
                });


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case com.ryansplayllc.ryansplay.R.id.umpire_bt_submit:


                showPopUp();


                break;

            case com.ryansplayllc.ryansplay.R.id.us_leaderboard_select:
                leaderboardSelect.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.game_tab_hover);
                playByPlaySelect.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.game_tab);
                showGameLeaderBoard();

                break;
//
            case com.ryansplayllc.ryansplay.R.id.us_play_by_play_select:
                playByPlaySelect.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.game_tab_hover);
                leaderboardSelect.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.game_tab);
//
                showUmpirePlayByPlay();
                break;

            default:
//                umpireResult(view);
                break;
        }

    }

    private void showPopUp() {

        try {
            JSONObject resultObject = new JSONObject();
            resultObject.put("position",positionValue);
            resultObject.put("baseHit",baseHitImageView.isSelected());
            resultObject.put("homeRun",homeRunImageView.isSelected());
            resultObject.put("runScored",runScoreImageView.isSelected());

            String comments = Utility.showPlayerGuess(resultObject);
            //Toast.makeText(getApplicationContext(),comments,Toast.LENGTH_SHORT).show();

            usDarkBackgroundLayout.setVisibility(View.VISIBLE);
            usDarkBackgroundLayout.bringToFront();
            usLeftMenusLayout.setAlpha((float)0.8);
            gameScreenLayout.setAlpha((float)1);
            gameScreenLayout.bringToFront();


            //making submit result confirm popup view visible
            submitPopUpLayout.setVisibility(View.VISIBLE);
            submitButton.bringToFront();


            submitPopUpHitRegion.setText(comments);
            submitPopupPlayNo.setText("Play #"+GameScreenActivity.currentPlay);
        }
        catch (Exception e)
        {
            Log.e("pop catch",e.toString());
        }

    }


    private void submitResult() {
        try {
            GameScreenActivity.screenStatus = "resume";
            final ProgressDialog progressDialog = new ProgressDialog(UmpireScreenActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Log.e("##positionValue","position value " + positionValue);
            if (positionValue.equals(" ")) {
                Toast.makeText(getApplicationContext(), "Select a postion",
                        Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                submitButton.setEnabled(true);

                clearButton.setEnabled(true);
                clearButton.setAlpha((float)1);

                return;
            }

            // predication class
            Result resultObject = new GameScreenActivity().new Result();
            resultObject.setPosition(positionValue);
            resultObject.setBasehit(baseHitImageView.isSelected());
            resultObject.setHomerun(homeRunImageView.isSelected());
            resultObject.setRunscored(runScoreImageView.isSelected());

            UmpireResult umpireResult = new GameScreenActivity().new UmpireResult();
            umpireResult.setgame_id(Utility.game.getId());
            umpireResult.setplay_number(GameScreenActivity.currentPlay);
            umpireResult.setResult(resultObject);

            Log.e("##positionValue","position value " + positionValue + umpireResult.toString());
            Log.e("##positionValue","baseHitImageView.isSelected() " + baseHitImageView.isSelected());
            Log.e("##positionValue","homeRunImageView " + homeRunImageView.isSelected());
            Log.e("##positionValue","runScoreImageView " + runScoreImageView.isSelected());


            Log.e("##positionValue","game id is " + Utility.game.getId());
            Log.e("##positionValue","current play " + GameScreenActivity.currentPlay);

            GameService.dispatcher.trigger("private_game.publish_result",
                    umpireResult, new WebSocketRailsDataCallback() {

                        @Override
                        public void onDataAvailable(Object data) {


                            // TODO Auto-generated method stub
                            try {
                                progressDialog.dismiss();
                                Log.e("private_game.publish_result ","success " + data.toString());
                                JSONObject response = new JSONObject(data
                                        .toString());
                                if (response.getBoolean("status")) {
                                    if (Utility.isUmpire) {
                                        finish();

                                    }
                                } else {
//                                    Toast.makeText(UmpireScreenActivity.this,
//                                            response.getString("message"),
//                                            Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                Toast.makeText(getApplicationContext(),"error data " + data.toString(),Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }

                        }
                    }, new WebSocketRailsDataCallback() {

                        @Override
                        public void onDataAvailable(Object data) {
                            // TODO Auto-generated method stub
                            progressDialog.dismiss();
                            try{
                                Toast.makeText(getApplicationContext(),"error data " + data.toString(),Toast.LENGTH_LONG).show();
                                Log.e("private_game.publish_result ","success " + data.toString());
                            }catch(Exception e){

                            }
                        }
                    });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // method for umpire prediction
    private void umpireResult(View view) {
        int id = view.getId();
        if (postions.contains(id)) {
            // if selected SWH
            if (Utility.postionValues.get(id, "").equals("SWH")) {
                if (baseHitImageView.isSelected()) {
                    baseHitImageView.setSelected(false);
                }
                if (homeRunImageView.isSelected()) {
                    homeRunImageView.setSelected(false);
                }
            }

            // making current selection true
            view.setSelected(true);

            // if first selection
            if (postionId != 0 && postionId != id) {
                // making old selection false
                findViewById(postionId).setSelected(false);
            }

            // Storing the id
            postionId = id;

            // Storing the value
            positionValue = Utility.postionValues.get(id);
        } else {

            // if base hit is been clicked
            if (view == baseHitImageView) {
                // if "SWH" is been selected
                if (positionValue != null) {
                    // can't select base hit
                    if (positionValue.equals("SWH")) {

                    } else {
                        // if base hit selected
                        if (baseHitImageView.isSelected()
                                && !homeRunImageView.isSelected()) {
                            // turn off base hit
                            baseHitImageView.setSelected(false);
                        } else {

                            // turn on base hit
                            baseHitImageView.setSelected(true);

                        }
                    }
                } else {

                }
            } // if homeselector run is been clicked
            else if (view == homeRunImageView) {

                if (positionValue.equals("SWH")) {
                    findViewById(postionId).setSelected(false);
                }
                // turn on base hit
                baseHitImageView.setSelected(true);
                // turn on homeselector Run
                homeRunImageView.setSelected(true);
                // turn on Strike work out
                runScoreImageView.setSelected(true);
                // Position value
                positionValue = "NA";
            }

            else {
                if (positionValue != null) {
                    if (view.isSelected()) {
                        view.setSelected(false);
                    } else {
                        view.setSelected(true);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(gameReceiver);
        unregisterReceiver(connectionChangeReceiver);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }



    public void initialScreenConditions()
    {
//        gameScreenBackground.setImageResource(R.drawable.baseball_field);

        lfImageView.setSelected(false);
        threeBImageView.setSelected(false);
        ssImageView.setSelected(false);

        rfImageView.setSelected(false);
        twoBImageView.setSelected(false);
        oneBImageView.setSelected(false);

        cfImageView.setSelected(false);
        cfImageView.setSelected(false);
        pImageView.setSelected(false);

        umpireStrikeOutImageView.setSelected(false);
        umpireWalkImageView.setSelected(false);
        hitbypitchumpire.setSelected(false);


        //making all images to off
        runScoreImageView.setSelected(false);
        homeRunImageView.setSelected(false);
        baseHitImageView.setSelected(false);

        //enabling all outside images
        homeRunImageView.setEnabled(true);
        homeRunImageView.setAlpha((float)1.0);

        //enable the runscore
        runScoreImageView.setEnabled(false);
        runScoreImageView.setAlpha((float)0.6);

        //enable the base hit
        baseHitImageView.setEnabled(false);
        baseHitImageView.setAlpha((float)0.6);

        enableSWH();

        submitButton.setEnabled(false);
        submitButton.setAlpha((float)0.6);

        clearButton.setEnabled(false);
        clearButton.setAlpha((float)0.6);





    }
    public void swhFieldPositionConditions()
    {
//        gameScreenBackground.setImageResource(R.drawable.baseball_field);
        hideSelectYourResultLabel();

        lfImageView.setSelected(false);
        threeBImageView.setSelected(false);
        ssImageView.setSelected(false);

        rfImageView.setSelected(false);
        twoBImageView.setSelected(false);
        oneBImageView.setSelected(false);

        cfImageView.setSelected(false);
        cfImageView.setSelected(false);
        pImageView.setSelected(false);

        umpireStrikeOutImageView.setSelected(false);
        umpireWalkImageView.setSelected(false);
        hitbypitchumpire.setSelected(false);


        gameScreenBackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field);
        baseHitImageView.setSelected(false);
        homeRunImageView.setSelected(false);
        //enabling the run score
        runScoreImageView.setEnabled(true);
        runScoreImageView.setAlpha((float)1.0);

        //disabling the home run
        homeRunImageView.setEnabled(false);
        homeRunImageView.setAlpha((float)0.6);

        positionsoff();

        //disabling the basehit
        baseHitImageView.setEnabled(false);
        baseHitImageView.setAlpha((float)0.6);


        enableSWH();

        submitButton.setEnabled(true);
        submitButton.setAlpha((float)1);

        clearButton.setEnabled(true);
        clearButton.setAlpha((float)1);
    }


    private void  enableSWH()
    {

        //enable strikeout
        umpireStrikeOutImageView.setEnabled(true);
        umpireStrikeOutImageView.setAlpha((float) 1.0);

        //enable HitbyPitch
        hitbypitchumpire.setEnabled(true);
        hitbypitchumpire.setAlpha((float)1.0);

        //enable walk
        umpireWalkImageView.setEnabled(true);
        umpireWalkImageView.setAlpha((float)1.0);

        submitButton.setEnabled(true);
        submitButton.setAlpha((float)1);

        clearButton.setEnabled(true);
        clearButton.setAlpha((float)1);


    }

    private void positionsEnable(){

        //disable the positions
        lfImageView.setEnabled(true);
        rfImageView.setEnabled(true);
        cfImageView.setEnabled(true);

        oneBImageView.setEnabled(true);
        twoBImageView.setEnabled(true);
        threeBImageView.setEnabled(true);

        ssImageView.setEnabled(true);
        cImageView.setEnabled(true);
        pImageView.setEnabled(true);


        //decreasing the alpha
        lfImageView.setAlpha((float)1.0);
        rfImageView.setAlpha((float)1.0);
        cfImageView.setAlpha((float)1.0);

        oneBImageView.setAlpha((float)1.0);
        twoBImageView.setAlpha((float)1.0);
        threeBImageView.setAlpha((float)1.0);

        ssImageView.setAlpha((float)1.0);
        cImageView.setAlpha((float)1.0);
        pImageView.setAlpha((float)1.0);

        submitButton.setEnabled(true);
        submitButton.setAlpha((float)1);

        clearButton.setEnabled(true);
        clearButton.setAlpha((float)1);

    }

    public  void enableOutImages() {
        //enabling all outside images
        homeRunImageView.setEnabled(true);
        homeRunImageView.setAlpha((float) 1.0);

        if (!homeRunImageView.isSelected()){

            //enable the runscore
            runScoreImageView.setEnabled(true);
        runScoreImageView.setAlpha((float) 1.0);

        //enable the base hit
        baseHitImageView.setEnabled(true);
        baseHitImageView.setAlpha((float) 1.0);
    }

        submitButton.setEnabled(false);
        submitButton.setAlpha((float)0.6);

        clearButton.setEnabled(false);
        clearButton.setAlpha((float)0.6);

    }




    private void positionsoff()
    {
        gameScreenBackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field);
        positionValue = "NA";

        lfImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_lf_off);
        rfImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_rf_off);
        cfImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_cf_off);

        oneBImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_1b_off);
        twoBImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_2b_off);
        threeBImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_3b_off);

        ssImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_ss_off);
        cImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_c_off);
        pImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_p_off);


        baseHitImageView.setEnabled(true);// enabling base hit and run score becomes true which can be change its state only when when home run is false
        runScoreImageView.setEnabled(true);
    }


    public void  regionSpecificRight()
    {

        enableOutImages();
        hideSelectYourResultLabel();

//        deFocusSwh();
        focusFieldPositions();
        setFieldPositionSelected(true);
        setRegionSelected("Right");
        //setting game right region positions focus and adjacent region paritially prexdictecd positions focused

        gameScreenBackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field_zones_rf_on);
        rfImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.rf_umpire_selected);
        twoBImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.twob_umpire_selected);
        oneBImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.oneb_umpire_selected);

        cfImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.cf_umpire_half_selected);
        pImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.p_umpire_half_selected);
        cImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.c_umpire_half_selected);

        lfImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.lf_umpire_unselected);
        ssImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.ss_umpire_unselected);
        threeBImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.threeb_umpire_unselected);


        if(!homeRunImageView.isSelected()) {
            baseHitImageView.setEnabled(true);
            // enabling base hit and run score becomes true which can be change its state only when when home run is false
            runScoreImageView.setEnabled(true);
        }

        baseHitImageView.setAlpha((float) 1);
        runScoreImageView.setAlpha((float) 1);


        submitButton.setEnabled(true);
        submitButton.setAlpha((float)1);

        clearButton.setAlpha((float)1);
        clearButton.setEnabled(true);





        cfImageView.setSelected(false);
        pImageView.setSelected(false);
        cImageView.setSelected(false);

        lfImageView.setSelected(false);
        ssImageView.setSelected(false);
        threeBImageView.setSelected(false);

        if(rfImageView.isSelected())
        {
            rfImageView.setSelected(true);
        }
        else if (twoBImageView.isSelected())
        {
            twoBImageView.setSelected(true);
        }
        else if (oneBImageView.isSelected())
        {
            oneBImageView.setSelected(true);
        }

        umpireStrikeOutImageView.setSelected(false);
        umpireWalkImageView.setSelected(false);
        hitbypitchumpire.setSelected(false);

    }


    public void regionSpecificCenter()
    {
        enableOutImages();
        hideSelectYourResultLabel();

//        deFocusSwh();
        focusFieldPositions();

        setFieldPositionSelected(true);

        //setting game center region positions focus and adjacent regions and paritially predicted positions focused
        setRegionSelected("Center");
        gameScreenBackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field_zones_cf_on);
        rfImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.rf_umpire_half_selected);
        twoBImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.twob_umpire_half_selected);
        oneBImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.oneb_umpire_half_selected);


        cfImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.cf_umpire_selected);
        pImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.p_umpire_selected);
        cImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.c_umpire_selected);


        lfImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.lf_umpire_half_selected);
        ssImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.ss_umpire_half_selected);
        threeBImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.threeb_umpire_half_selected);


        if(!homeRunImageView.isSelected()) {
            baseHitImageView.setEnabled(true);
            // enabling base hit and run score becomes true which can be change its state only when when home run is false
            runScoreImageView.setEnabled(true);
        }

        baseHitImageView.setAlpha((float) 1);
        runScoreImageView.setAlpha((float) 1);


        submitButton.setEnabled(true);
        submitButton.setAlpha((float)1);

        clearButton.setAlpha((float)1);
        clearButton.setEnabled(true);


        if(cfImageView.isSelected())
        {
            cfImageView.setSelected(true);
        }
        else if (pImageView.isSelected())
        {
            pImageView.setSelected(true);
        }
        else if (cImageView.isSelected())
        {
            cImageView.setSelected(true);
        }


        rfImageView.setSelected(false);
        twoBImageView.setSelected(false);
        oneBImageView.setSelected(false);

        lfImageView.setSelected(false);
        ssImageView.setSelected(false);
        threeBImageView.setSelected(false);

        umpireStrikeOutImageView.setSelected(false);
        umpireWalkImageView.setSelected(false);
        hitbypitchumpire.setSelected(false);



    }






    public void regionSpecificLeft()
    {

        hideSelectYourResultLabel();
        enableOutImages();
//        deFocusSwh();

        focusFieldPositions();
        setFieldPositionSelected(true);

        setRegionSelected("left");

        //setting game left region positions focus and adjacent region paritially prexdictecd positions focused

        gameScreenBackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field_zones_lf_on);

        lfImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.lf_umpire_selected);
        threeBImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.threeb_umpire_selected);
        ssImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.ss_umpire_selected);



        rfImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.rf_umpire_unselected);
        twoBImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.twob_umpire_unselected);
        oneBImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.oneb_umpire_unselected);



        cfImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.cf_umpire_half_selected);
        pImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.p_umpire_half_selected);
        cImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.c_umpire_half_selected);


        if(!homeRunImageView.isSelected()) {
            baseHitImageView.setEnabled(true);
            // enabling base hit and run score becomes true which can be change its state only when when home run is false
            runScoreImageView.setEnabled(true);
        }

        baseHitImageView.setAlpha((float) 1);
        runScoreImageView.setAlpha((float) 1);

        submitButton.setEnabled(true);
        submitButton.setAlpha((float)1);

        clearButton.setAlpha((float)1);
        clearButton.setEnabled(true);


        if(lfImageView.isSelected())
        {
            lfImageView.setSelected(true);
        }
        else if (threeBImageView.isSelected())
        {
            threeBImageView.setSelected(true);
        }
        else if (ssImageView.isSelected())
        {
            ssImageView.setSelected(true);
        }

        rfImageView.setSelected(false);
        twoBImageView.setSelected(false);
        oneBImageView.setSelected(false);

        cfImageView.setSelected(false);
        pImageView.setSelected(false);
        cImageView.setSelected(false);


        umpireStrikeOutImageView.setSelected(false);
        umpireWalkImageView.setSelected(false);
        hitbypitchumpire.setSelected(false);


    }


    private void back() {
//        progressDialog.show();
        SharedPreferences gameid = getSharedPreferences("gameid", MODE_PRIVATE);
        String gameidValue = gameid.getString(Utility.user.getUserName(), "default");
        //        Toast.makeText(getApplicationContext(),gameidValue+"",Toast.LENGTH_SHORT).show();
        if (gameidValue.equals("default")) {
            finish();
        } else {
            leave(gameidValue.split(";")[0]);


        }
    }


    private void leave(String gameidValue)
    {

//        Toast.makeText(getApplicationContext(),gameidValue,Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley
                .newRequestQueue(UmpireScreenActivity.this);



        JsonObjectRequest gameListRequest = new JsonObjectRequest(Request.Method.POST,
                "http://52.88.139.217/api//v2/private_games/"+gameidValue+"/player/leave", null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject responseJsonObject) {
                try {

                    if (responseJsonObject.getBoolean("status")) {

                        if (GameService.dispatcher != null) {
                            if (GameService.dispatcher
                                    .isSubscribed(GameService.chanelName)) {
                                GameService.dispatcher
                                        .unsubscribe(GameService.chanelName);
                                GameService.dispatcher.disconnect();
                            }
                        }
                        if (!Utility.isUmpire) {
                            Utility.clearGame();
                        }
//                        progressDialog.dismiss();
                        SharedPreferences gameidpref=getSharedPreferences("gameid",MODE_PRIVATE);
                        SharedPreferences.Editor gameideditor=gameidpref.edit();
                        gameideditor.putString(Utility.user.getUserName(),"default");
                        gameideditor.commit();
                        Toast.makeText(getApplication(),"Left the game successfully",Toast.LENGTH_SHORT).show();
                        GameService.dispatcher.disconnect();



                    }
                    else
                    {
//                        progressDialog.dismiss();
                        Toast.makeText(getApplication(),responseJsonObject.toString(),Toast.LENGTH_SHORT).show();





                    }
                } catch (JSONException e) {
//                    progressDialog.dismiss();
                    Toast.makeText(getApplication(),"Server Error Occured in Leaving Game",Toast.LENGTH_SHORT).show();

                    e.printStackTrace();
                }
//                progressDialog.dismiss();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
//                progressDialog.dismiss();

                AlertDialog.Builder builder=new AlertDialog.Builder(UmpireScreenActivity.this);
                builder.setMessage(arg0.networkResponse.statusCode+"");
                builder.show();


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization",
                        Utility.getAccessKey(UmpireScreenActivity.this));
                return params;
            }

        };

        queue.add(gameListRequest);
        queue.start();
    }


    private void inviteFriendsMessage() {
        Intent intent = new Intent("android.intent.action.VIEW");

        Uri data = Uri.parse("sms:");

        /** Setting sms uri to the intent */

        intent.setData(data);
        intent.setType("vnd.android-dir/mms-sms");

        intent.putExtra("sms_body",Utility.smsBody1+Utility.user.getUserName()+Utility.smsBody2+ Utility.user.getUserName()+Utility.smsBody3);

        /** Initiates the SMS compose screen, because the activity contain ACTION_VIEW and sms uri */
        startActivity(intent);
    }




    @Override
    public void onBackPressed() {

        forfietDialog.show();

    }

    public void focusFieldPositions()
    {
        rfImageView.setAlpha((float)1);
        twoBImageView.setAlpha((float)1);
        oneBImageView.setAlpha((float)1);

        cfImageView.setAlpha((float)1);
        cImageView.setAlpha((float)1);
        pImageView.setAlpha((float)1);

        lfImageView.setAlpha((float)1);
        ssImageView.setAlpha((float)1);
        threeBImageView.setAlpha((float)1);

    }
    public void deFocusFieldPositions()
    {
        rfImageView.setAlpha((float)0.6);
        twoBImageView.setAlpha((float)0.6);
        oneBImageView.setAlpha((float)0.6);

        cfImageView.setAlpha((float)0.6);
        cImageView.setAlpha((float)0.6);
        pImageView.setAlpha((float)0.6);

        lfImageView.setAlpha((float)0.6);
        ssImageView.setAlpha((float)0.6);
        threeBImageView.setAlpha((float)0.6);
    }

    public void focusSwh()
    {
        hitbypitchumpire.setAlpha((float)1);
        umpireWalkImageView.setAlpha((float)1);
        umpireStrikeOutImageView.setAlpha((float) 1);
    }
    public void deFocusSwh()
    {
        hitbypitchumpire.setAlpha((float)0.6);
        umpireWalkImageView.setAlpha((float)0.6);
        umpireStrikeOutImageView.setAlpha((float) 0.6);
    }

    public void showSelectYourResultLabel()
    {
        selectYourResultLabel.setVisibility(View.VISIBLE);
        currentPlayText.setVisibility(View.GONE);
    }

    public void hideSelectYourResultLabel()
    {
        selectYourResultLabel.setVisibility(View.GONE);
        currentPlayText.setVisibility(View.VISIBLE);
    }



}
