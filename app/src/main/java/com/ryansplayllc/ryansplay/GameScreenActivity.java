package com.ryansplayllc.ryansplay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.fabric.sdk.android.Fabric;
import play.prediction.PredictionAndResult;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import br.net.bmobile.websocketrails.WebSocketRailsDataCallback;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.adapters.GameScreenLeaderBoardList;
import com.ryansplayllc.ryansplay.adapters.PlayByPlayAdapter;
import com.ryansplayllc.ryansplay.models.Creator;
import com.ryansplayllc.ryansplay.models.NetworkUtils;
import com.ryansplayllc.ryansplay.models.PlayByPlay;
import com.ryansplayllc.ryansplay.models.Player;
import com.ryansplayllc.ryansplay.models.Umpire;
import com.ryansplayllc.ryansplay.models.User;
import com.ryansplayllc.ryansplay.receivers.GameReceiver;
import com.ryansplayllc.ryansplay.services.GameService;
import com.ryansplayllc.ryansplay.util.IabHelper;
import com.ryansplayllc.ryansplay.util.IabResult;
import com.ryansplayllc.ryansplay.util.Inventory;
import com.ryansplayllc.ryansplay.util.Purchase;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

public class GameScreenActivity extends Activity implements
        OnClickListener {


    /*********************************************************************variables declarations*******************************************************/
    private static List<String> permissions;
    //get region of selected position
    private String fieldSelected;
    private ArrayList<String> playByPlayList;
    private ArrayList<String> playByPlayNo;
    public static ArrayList<String> pointsList = new ArrayList<>();
    public static ArrayList<String> getGamePoints;

    private List<Integer> postions = new ArrayList<Integer>(Arrays.asList(
            R.id.umpire_imageView_1b,    R.id.umpire_imageView_3b,
            R.id.umpire_imageView_2b,    R.id.umpire_imageView_c,
            R.id.umpire_imageView_cf,    R.id.umpire_imageView_lf,
            R.id.umpire_imageView_p,     R.id.umpire_imageView_rf,
            R.id.game_screen_textView_ss,R.id.umpire_imageView_swh));

    private String gamePoints;
    private String userNoOfPlays;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private String gameStatus = "";
    public static String screenStatus = "";
    public static String currentGamePoints;
    // player prediction
    private String postionValue = "NA";
    private int postionId = 0;

    // Variables for game
    public static int currentPlay = 1;
    public static int lastPlayPointValue = 0;
    public static String lastPlayPositionValue = "";
    private boolean isConnectedToGame = true;
    private boolean positionSelcted;

    // count down timer
    CountDownTimer countDownTimer = null;

    private boolean isSelectable = true;

    //variable for checking which of the top menu selected
    private boolean playByPlayMenuSelected;

    private boolean purchasing_flag = false;

    /***************Setters and Getters for the game screen variables*******************/

    public boolean isPlayByPlayMenuSelected() {
        return playByPlayMenuSelected;
    }

    public void setPlayByPlayMenuSelected(boolean playByPlayMenuSelected) {
        this.playByPlayMenuSelected = playByPlayMenuSelected;
    }

    // waiting for result flag
    private boolean isWaitingForPlayResult = false;

    public String getUserNoOfPlays() {
        return userNoOfPlays;
    }

    public void setUserNoOfPlays(String userNoOfPlays) {
        this.userNoOfPlays = userNoOfPlays;
    }

    public String getGamePoints() {
        return gamePoints;
    }

    public void setGamePoints(String gamePoints) {
        this.gamePoints = gamePoints;
    }

    public boolean isPositionSelcted() {
        return positionSelcted;
    }

    public void setPositionSelcted(boolean positionSelcted) {
        this.positionSelcted = positionSelcted;
    }

    // The helper object
    IabHelper mHelper;
    static String product_id = "gas";

    /****************************************************views declarations*****************************************************************/

    // header views
    private TextView gameNameTextView,gsUserRank;
    private ImageView gsAddPlaysIcon;
    //number of plays
    public TextView noOfPlaysInHeader, noOfPlaysInAddPlays;

    private RelativeLayout addMorePlayLayout;
    private ImageButton addPlaysCloseButton;
    //top select your position label view
    public TextView selectYourPosLabel;
    ProgressBar taskProgressInner;

    //result pop view
    RelativeLayout predictionResultViewLayout;
    TextView resultViewPlayNoText,resultViewLastPlayPointText,resultViewHitRegion;
    LinearLayout lastPlayContr;


    // game info views
    private RelativeLayout timeInfoLinearLayout,playStartLinearLayout,gsOpenPlayImage,closePlayButton;

    // game info views
    private TextView countDownTextView,currentPlayTextView,lastPlayPostionValueTextView,lastPlayPointValueTextView;

    // game play views
    private RelativeLayout homeRunImageView,baseHitImageView,swhImageView,runScore,rfTextView,cfTextView,lfTextView,oneBTextView,twoBTextView,
            threeBTextView,ssTextView,pTextView,cTextView;
    private TextView leftFieldPoints,rightFieldpoints,centerFieldPoints;

    //badges for selected position button
    ImageView lfLabel, cfLabel, rfLabel, ssLabel, b1Label, b2Label, b3Label, pLabel, cLabel, swhLabel, rsLabel, hitLabel, homeRunLabel;
    TextView possiblePoints, playerGuessComment;
    private RelativeLayout timeContainer;

    RelativeLayout ballinPlayView;
    private TextView yourGuessesLabel;
    //final result
    private RelativeLayout finalLeaderBoard;
    private TextView finalName, finalRank, finalPoints;

    //social sharing options
    private ImageView fbShareImageView,tweetImageVIew;

    //game leaderboard listview variables intilisation
    private ListView gameleaderboardList;
    private RelativeLayout leaderboardSelect,playByPlaySelect;
    private ImageView gameScreenBackground;

    //more list variable initialistaion
    private RelativeLayout moreListContent;
    private ImageButton moreIconImage;
    //More options
    private FrameLayout gsMoreExitButton,gsMoreAddMorePlays;
    private FrameLayout inviteFrns;
    private ImageView gsMoreCloseIcon;

    //currentplayer info variables
    private TextView currentPlayerName,currentPlayerPoints;
    private ImageView currentPlayerProfileImage;
    private RelativeLayout currentPlayerLayout;

    // progress dialog
    public static ProgressDialog progressDialog;
    // game screen pop up
    private PopupWindow popupWindow = new PopupWindow();

    // Forfeit dialog
    private AlertDialog leaveGameDialog;

    // rejoin dialog
    private AlertDialog rejoinDialog;


    // intent service
    Intent gameService;

    Notification notification;
    NotificationManager notificationManager;

    // game prediction
    public static Prediction predictionObject = new GameScreenActivity().new Prediction();

    // game receiver
    private GameReceiver gameReceiver;



/******************************************************Broadcast receivers***********************************************************/
    /** Game Actions For Receivers**/

    // game result change
    public static final String GAME_RESULT_ACTION_RESP = "com.ryansplayllc.ryansplay.intent.action.GAME_RESULT";

    // play start action
    public static final String PLAY_START_ACTION_RESP = "com.ryansplayllc.ryansplay.intent.action.PLAY_START";

    // internet connection change
    public static final String CONNECTION_ACTION_RESP = "com.ryansplayllc.ryansplay.intent.action.CONNECTION";

    // player left
    public static final String PLAY_LEFT_RECIEVER = "com.ryansplayllc.ryansplay.intent.action.PLAYLEFT";


    /***************** connection receiver*****************/


    private BroadcastReceiver connectionChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra("connected", false);

//            String status = NetworkUtils.getConnectivityStatusString(context);

//            Toast.makeText(context, status, Toast.LENGTH_LONG).show();

            if (!isConnected) {
                runOnUiThread(


                        new Runnable() {

                            @Override
                            public void run() {
                                if (isConnectedToGame)
                                {
                                    String message = "Your internet connection is been disconnected. please connect to the internet.";
//
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                    isConnectedToGame = false;
                                }
                            }
                        });
                return;
            }
            if (isConnected) {
                popupWindow.dismiss();

//                Toast.makeText(getApplicationContext(), "Internet Connected again", Toast.LENGTH_SHORT).show();
//                Intent i=new Intent(getApplicationContext(),FirstSplashScreen.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(i);


            }

            if (GameService.dispatcher == null) {
                GameService.dispatcher.connect();

                GameService.dispatcher.bind("disconnected",
                        new WebSocketRailsDataCallback() {

                            @Override
                            public void onDataAvailable(Object data) {
                                try {
                                    if (data != null) {
                                        JSONObject responseJsonObject = new JSONObject(
                                                data.toString());
                                        if (responseJsonObject
                                                .getBoolean("status"))
                                        {
                                            Utility.game
                                                    .JsonParser(responseJsonObject
                                                            .getJSONObject("game"));
                                            if (Utility.game
                                                    .getPlaysCompleted() > 0) {
                                                JSONObject lastPlayResultJsonObject = responseJsonObject
                                                        .getJSONObject("last_play_result");
                                                GameScreenActivity.currentPlay = lastPlayResultJsonObject
                                                        .getInt("play_number");
                                                PredictionAndResult result = new PredictionAndResult();
                                                result.jsonParser(lastPlayResultJsonObject
                                                        .getJSONObject("result"));

                                                // for play by play implementation extracting comments value from result jcson object
                                                playByPlayList.add(lastPlayResultJsonObject.getString("comments"));
                                                playByPlayNo.add(currentPlay + "");


                                            }
                                            if (!isConnectedToGame
                                                    && (rejoinDialog == null || !rejoinDialog
                                                    .isShowing())) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        showRejoinAlert();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
            }
        }
    };


    /************* umpire change receiver***************/

    public BroadcastReceiver umpireChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            JSONObject responseJsonString = null;
            try {
                JSONObject tempJSON = new JSONObject(intent.getStringExtra("value"));
                responseJsonString = tempJSON.getJSONObject("user");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            boolean error = intent.getBooleanExtra("error", false);
            if (!error) {
                Umpire umpire = new Umpire();
                umpire.jsonParser(responseJsonString);
                Utility.game.setUmpire(umpire);
                if (Utility.getUserId(context) == umpire.getId()) {
                    Toast.makeText(context, "You are the new Umpire", Toast.LENGTH_SHORT).show();

                    Utility.isUmpire = true;
                    showUmpireOptions();
                    hideBallinPlayView();
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    if (isWaitingForPlayResult) {
                        Intent umpireActivityIntent = new Intent(
                                GameScreenActivity.this,
                                UmpireScreenActivity.class);
                        startActivity(umpireActivityIntent);
                    }

                }
                else {

                    Toast.makeText(context,
                            umpire.getUserName() + " is the new Umpire",
                            Toast.LENGTH_SHORT).show();
                    Utility.isUmpire = false;
                    showPlayerOptions();
                }
            } else {
            }
        }
    };


    /****************************** play start receiver***************************/

    BroadcastReceiver playStartReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            try {
                JSONObject responseObject = new JSONObject(
                        intent.getStringExtra("value"));
                Log.e("play start response",responseObject.toString());

                if (!gameStatus.equalsIgnoreCase("inprogress") || Utility.isUmpire) {
                    if (responseObject.getBoolean("status")) {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }

                        timmer();
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    /**************Play Left Receiver************************/

    BroadcastReceiver playLeftReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showGameLeaderBoard();
        }
    };


    /******************Play Result Receiver**************************/
    BroadcastReceiver playResultReceiver = new BroadcastReceiver()
    {


        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                //if the player joins in the middle of the game
                yourGuessesLabel.setVisibility(View.VISIBLE);
                hideTopInfoLabel();
                if (gameStatus.equalsIgnoreCase("inprogress")) {
                    hideBallinPlayView();
                    currentPlayTextView.setText("Play " + (Utility.disconnectedGame.getNo_of_plays_completed() + 2) + "/"
                            + Utility.game.getLength());
                    showPlayByPlay();

                    showGameLeaderBoard();
                    checkCurrentPlay((Utility.disconnectedGame.getNo_of_plays_completed() + 2));


                    if ((Utility.disconnectedGame.getNo_of_plays_completed() + 2) > Utility.disconnectedGame.getNoOfPlays()) {
                        RelativeLayout gameOver = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.final_game_sceen);
                        gameOver.setVisibility(View.VISIBLE);
                        playerStatsView();
                        gameOver.bringToFront();
                        currentPlay = 1;


                        finalLeaderBoard.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                finish();
                                Utility.enterdGameLobbyStatus = false;
                                Intent i = new Intent(GameScreenActivity.this, FinalLeaderBoardActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.putExtra("game_id", Utility.disconnectedGame.getId());

                                startActivity(i);
                            }
                        });
                    }

                }

                if (screenStatus.equalsIgnoreCase("pause") && Utility.isNotificationsEnable)
                    sendNotificationLocalToUser();

                gameStatus = "";
                JSONObject responseObject = new JSONObject(
                        intent.getStringExtra("value"));
                if (responseObject.getBoolean("status")) {
                    isWaitingForPlayResult = false;
                    ++currentPlay;

                    // play result JSON object
                    JSONObject resultObject = responseObject.getJSONObject(
                            "umpire_play").getJSONObject("result");

                    // calculate points of last play
                    lastPlayPointValue = new Utility().calculatePoints(
                            resultObject, predictionObject);

                    // adding last play point to game points
                    Utility.gamePoint += lastPlayPointValue;
                    lastPlayPositionValue = resultObject.getString("position");

                    // setting play number
                    if (currentPlay <= Utility.game.getLength()) {
                        currentPlayTextView.setText("Play " + currentPlay + "/"
                                + Utility.game.getLength());
                        checkCurrentPlay(currentPlay);

                    }
                    // game play info
                    lastPlayPostionValueTextView.setText("Hit to " + lastPlayPositionValue + "  ");

                    pointsHistoryForPlayByPlay(lastPlayPointValue + "");
                    if (lastPlayPointValue > 0) {

                        lastPlayPointValueTextView.setText(" +" + Integer
                                .toString(lastPlayPointValue) + " PTS");
                    } else {
                        lastPlayPointValueTextView.setText(" " + Integer
                                .toString(lastPlayPointValue) + " PTS");

                    }
                    positionsoff();
                    // setting total game points


                    // resting selection
                    if (postionId != 0) {
                        findViewById(postionId).setSelected(false);
                    }
                    baseHitImageView.setSelected(false);
                    homeRunImageView.setSelected(false);
                    runScore.setSelected(false);
                    postionId = 0;
                    postionValue = "NA";
                    // show result pop up and hide ballin play
                    hideBallinPlayView();

                    predictionResultView();
//                    disableGameViews();


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // to stop timer executing until "game finished"
                            // event
                            // callback received
                            // toasting current play number
                            isSelectable = true;
                            hideResultView();
                            initialScreenConditions();


                            if (currentPlay <= Utility.game.getLength()) {
                                // hiding time view
                                if (Utility.isUmpire) {
                                    showUmpireOptions();
                                } else {
                                    showPlayerOptions();
                                }
                            } else {
                                RelativeLayout gameOver = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.final_game_sceen);
                                gameOver.setVisibility(View.VISIBLE);
                                playerStatsView();
                                gameOver.bringToFront();
                                currentPlay = 1;


                                finalLeaderBoard.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        finish();
                                        Utility.enterdGameLobbyStatus = false;
                                        Intent i = new Intent(GameScreenActivity.this, FinalLeaderBoardActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                    }
                                });

                            }
                        }
                    }, 5000);

                    initialScreenConditions();
                    disableGameViews();
//                    showGameLeaderBoard();
                    showPlayByPlay();  //calling this function to update lastposition value in bottom of game field


                    if (isPlayByPlayMenuSelected()) {
                        showPlayByPlay();
                    } else {
                        showGameLeaderBoard();
                    }
                    PlayByPlay.setPlayByPlayGamePoints(lastPlayPointValue + "");


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };



    //input that was passed as  a bean object to get the leader board
    public class LeaderBoardInput {

        private long game_id;
        private int page;
        private int per_page;

        public void setGame_id(long game_id) {
            this.game_id = game_id;
        }

        public long getGame_id() {
            return this.game_id;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPer_page() {
            return per_page;
        }

        public void setPer_page(int per_page) {
            this.per_page = per_page;
        }
    }

    SharedPreferences notifPref;
    SharedPreferences.Editor notifEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notification = new Notification(com.ryansplayllc.ryansplay.R.drawable.app_icon, "New Message", System.currentTimeMillis());
        callbackManager = CallbackManager.Factory.create();

        setContentView(com.ryansplayllc.ryansplay.R.layout.game_screen_land);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // roll bar
        Rollbar.setIncludeLogcat(true);
        Fabric.with(this, new TweetComposer());

        lastPlayContr = (LinearLayout) findViewById(R.id.gs_lastplay_container);
        moreListContent = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.gs_more_list_content);
        moreIconImage = (ImageButton) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_more_menu);
        yourGuessesLabel = (TextView) findViewById(R.id.your_guesses);
        selectYourPosLabel =(TextView) findViewById(R.id.selectyourpos_label);

        timeContainer = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.timeContainer);
        timeContainer.setVisibility(View.VISIBLE);

        ballinPlayView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.ball_in_play_screen);

        possiblePoints = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.pointsValue);
        playerGuessComment = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.player_guess_comment);
        //badges TextView lfLabel,cfLabel,rfLabel,ssLabel,b1Label,b2Label,b3Label,pLabel,cLabel,swhLabel,rsLabel,hitLabel,homeRunLabel
        lfLabel = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.lfLabel);
        cfLabel = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.cfLabel);
        rfLabel = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.rfLabel);
        ssLabel = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.ssLabel);
        b1Label = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.B1Label);
        b2Label = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.B2Label);
        b3Label = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.B3Label);
        pLabel = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.pLabel);
        cLabel = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.cLabel);
        swhLabel = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.swhLabel);
        rsLabel = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.rsLabel);
        hitLabel = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.hitLabel);
        homeRunLabel = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.homeRunLabel);

        noOfPlaysInHeader = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.textView29);
        gsUserRank = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.gs_user_rank);
        noOfPlaysInAddPlays = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.balancelabel1);

//
        gameNameTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_tv_game_name);

        // play views
        timeInfoLinearLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_play_info_ll_time_info);
        playStartLinearLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_play_info_ll_start);
        closePlayButton = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_play_info_bt_close_play);
        gsOpenPlayImage = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.gs_open_play_image);

        //moreOptions Initialistaions
        gsMoreAddMorePlays = (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.top_menu_add_more_plays);

        gsMoreAddMorePlays.setOnClickListener(this);

        inviteFrns = (FrameLayout) findViewById(R.id.inviteFrns);
        inviteFrns.setOnClickListener(this);

        addMorePlayLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.add_more_plays_screen);
        addPlaysCloseButton = (ImageButton) findViewById(com.ryansplayllc.ryansplay.R.id.add_more_plays_close_button);
        addPlaysCloseButton.setOnClickListener(this);
        //hiding add more plays default
        addMorePlayLayout.setVisibility(View.GONE);
        showTimer();

        //Toast.makeText(getApplicationContext(),"current play "+currentPlay+"",Toast.LENGTH_LONG).show();
        gameNameTextView.setText(Utility.game.getGameName());
        // game info views
        countDownTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_tv_count_down);
        currentPlayTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.textView19);
        lastPlayPostionValueTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_textView_last_play_postion_value);
        lastPlayPointValueTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_textView_last_play_point_value);
        leftFieldPoints = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.left_field_points);
        rightFieldpoints = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.right_field_points);
        centerFieldPoints = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.center_field_points);

        //currentplayer info views intialisations

        currentPlayerLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.gs_current_playerinfo_layout);
        currentPlayerName = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.gs_current_player_name);
        currentPlayerPoints = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.gs_current_player_points);
        currentPlayerProfileImage = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.gs_current_player_profileimage);
        currentPlayTextView.setText("Play "+currentPlay +"/"+Utility.game.getLength());
        showTopInfoLabel();


        final Typeface font = Typeface.createFromAsset(getAssets(), "BebasNeue Bold.ttf");
        lastPlayPostionValueTextView.setTypeface(font);
        lastPlayPointValueTextView.setTypeface(font);

        // game Screen positions initialization
        homeRunImageView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_imageView_home_run);
        baseHitImageView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_imageView_base_hit);
        swhImageView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_imageView_swh);
        runScore = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_imageView_run_score);
        rfTextView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_textView_rf);
        cfTextView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_textView_cf);
        lfTextView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_textView_lf);
        oneBTextView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_textView_1b);
        twoBTextView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_textView_2b);
        threeBTextView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_textView_3b);
        ssTextView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_textView_ss);
        pTextView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_textView_p);
        cTextView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_textView_c);

        //game screen leader board variables
        leaderboardSelect = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.gs_leaderboard_select);
        gsAddPlaysIcon = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.gs_add_plays_icon);
        playByPlaySelect = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.gs_play_by_plays_select);
        gsMoreExitButton = (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_exit_select);
        gsMoreCloseIcon = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.gs_more_close_icon);

        //current player layout variables
        currentPlayerLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.gs_current_playerinfo_layout);
        //result pop views initialisations
        predictionResultViewLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.result_Screen);
        resultViewPlayNoText = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.pop_up_umpire_result_no_of_plays);
        resultViewLastPlayPointText = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.pop_up_umpire_result_total_points);
        resultViewHitRegion = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.pop_up_umpire_result_hitregion);

        //social network sharing views
        fbShareImageView = (ImageView) findViewById(R.id.gs_fb_share_image);
        tweetImageVIew = (ImageView) findViewById(R.id.gs_tweet_image);

//        Utility.game.setLength(1);
//

        hideAllFieldPosBadges();

        try {

            Intent i = getIntent();
            String gameStats = i.getStringExtra("gameStatus");

            if (gameStats.equalsIgnoreCase("inprogress")) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timeContainer.setVisibility(View.GONE);
                        yourGuessesLabel.setVisibility(View.GONE);
                        playerGuessComment.setText("You will join the game next play.");

                        gameStatus = "inprogress";

                        if(Utility.user.getId() == Utility.game.getUmpire().getId()) {
                            hideBallinPlayView();
                            gameStatus = "";
                        }
                        else {
                            showBallinPlayView();
                        }
                        showPlayByPlay();
                        showGameLeaderBoard();

                        currentPlay = 0;
                        currentPlay = Utility.disconnectedGame.getNo_of_plays_completed() + 1;



                        Utility.game.setLength(Utility.disconnectedGame.getNoOfPlays());
                        currentPlayTextView.setText("Play " + currentPlay + "/"
                                + Utility.game.getLength());

                        if (currentPlay==1)
                        {
                            showTopInfoLabel();
                        }
                        else
                        {
                            hideTopInfoLabel();
                        }
                        checkCurrentPlay(currentPlay);



                    }

                }, 500);


            }
        } catch (Exception e) {

        }
        /*if(Utility.isUmpire)
            checkForPlayingGames();*/



        Utility.user.setLastGameName(Utility.game.getGameName());

        /***** FB Permissions *****/
        permissions = new ArrayList<String>();
        permissions.add("email");
        permissions.add("user_location");
        permissions.add("user_birthday");

        shareDialog = new ShareDialog(this);


        finalLeaderBoard = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.finalLeaderBoard);

        checkCurrentPlay(currentPlay);
        if (lastPlayPositionValue.equals("")) {
            lastPlayPostionValueTextView.setText("NA ");
        } else {
            lastPlayPostionValueTextView.setText("HIT TO " + lastPlayPositionValue);
        }
        lastPlayPointValueTextView
                .setText(Integer.toString(lastPlayPointValue) + " PTS");


        finalLeaderBoard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                Utility.enterdGameLobbyStatus = false;
                Intent i = new Intent(GameScreenActivity.this, FinalLeaderBoardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });

        fbShareImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    LoginManager.getInstance().logInWithReadPermissions(GameScreenActivity.this, Arrays.asList("user_birthday", "email", "public_profile", "user_friends", "user_location"));


                } catch (Exception e) {
                }
            }
        });


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (ShareDialog.canShow(ShareLinkContent.class))
                {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle("I got " + Utility.user.getLastGamePoints() + " points playing RP Baseball. Download the game today. #RPBaseball @ryansplay")
                            .setContentDescription("Rank " + "#" + Utility.user.getUserRank())
                            .setImageUrl(Uri.parse("http://52.88.139.217/images/rpa_fb_invite_banner.jpg"))
                            .setContentUrl(Uri.parse("https://fb.me/1656445941266582"))
                            .build();

                    shareDialog.show(linkContent);
                }
                if (!Utility.user.getProvoider().equals("facebook"))
                {
                    LoginManager.getInstance().logOut();
                }

            }

            @Override
            public void onCancel() {
                if (!Utility.user.getProvoider().equals("facebook"))
                    LoginManager.getInstance().logOut();
            }

            @Override
            public void onError(FacebookException e) {

            }
        });


        tweetImageVIew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    File myImageFile = new File("file:///C:/Users/nimaikrsna/Desktop/fbimage.jpg");
                    Uri myImageUri = Uri.fromFile(myImageFile);

                    TweetComposer.Builder builder = new TweetComposer.Builder(GameScreenActivity.this)
                            .text("I got "+Utility.user.getLastGamePoints()+" points playing RP Baseball. Download the game today. #RPBaseball @ryansplay")
                            .image(getLocalBitmapUri())
                            .url(new URL("https://play.google.com/apps/testing/com.ryansplayllc.ryansplay"))
                            ;

                    builder.show();
                } catch (Exception e) {

                }

            }
        });




        // play views
        closePlayButton.setOnClickListener(this);


        // game screen listener
        homeRunImageView.setOnClickListener(this);
        baseHitImageView.setOnClickListener(this);
        swhImageView.setOnClickListener(this);
        runScore.setOnClickListener(this);

        rfTextView.setOnClickListener(this);
        cfTextView.setOnClickListener(this);
        lfTextView.setOnClickListener(this);
        oneBTextView.setOnClickListener(this);
        twoBTextView.setOnClickListener(this);
        threeBTextView.setOnClickListener(this);
        ssTextView.setOnClickListener(this);
        pTextView.setOnClickListener(this);
        cTextView.setOnClickListener(this);


        //making selected fieldpoints invisible in initial state

        leftFieldPoints.setVisibility(View.GONE);
        rightFieldpoints.setVisibility(View.GONE);
        centerFieldPoints.setVisibility(View.GONE);
        leaderboardSelect.setSelected(true);


        //filling list view in game screen leaderboard

        gameleaderboardList = (ListView) findViewById(com.ryansplayllc.ryansplay.R.id.game_screen_leader_board_listVIew);
        //gameleaderboardList.setAdapter(new GameScreenLeaderBoardList(this));
        try {
            showGameLeaderBoard();
        } catch (NullPointerException ne) {
            //registerReceiver(gameReceiver, filter);
            try{
                unregisterReceiver(gameReceiver);
            }catch (Exception e){

            }
//            Intent i = new Intent(GameScreenActivity.this, FirstSplashScreen.class);
//            startActivity(i);

            //restarting the app on crash....
            finish();
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }


        //appplying hovers for gamescreen leaderboard toptab
        leaderboardSelect.setOnClickListener(this);
//        chatListSelect.setOnClickListener(this);
        playByPlaySelect.setOnClickListener(this);

        gsAddPlaysIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addMorePlayLayout.setVisibility(View.VISIBLE);
                addMorePlayLayout.bringToFront();

                initialize_inAppSetting();
                disableGameViews();

            }
        });

        //more icon click listener
        moreIconImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                moreListContent.setVisibility(View.VISIBLE);
                moreListContent.bringToFront();
                addMorePlayLayout.setVisibility(View.GONE);
//                hideResultView();
                RelativeLayout predictionResultView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.result_Screen);
                predictionResultView.setVisibility(View.GONE);

                disableGameViews();

            }
        });

        gsMoreCloseIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                moreListContent.setVisibility(View.GONE);

                moreIconImage.setBackgroundResource(Color.TRANSPARENT);
                moreIconImage.setImageResource(com.ryansplayllc.ryansplay.R.drawable.more);
                enableGameViews();
                positionsEnable();
                swhImageView.setEnabled(true);

            }
        });

        gsMoreExitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//=                Intent exitscreen new Intent(getBaseContext(),GameHomeScreenActivity.class);
//                startActivity(exitscreen);
                leaveGameDialog.show();
            }
        });


        gameScreenBackground = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.umpirescreenfield);
        gameScreenBackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field);

        if(Utility.user.getId() == Utility.game.getUmpire().getId())
        {
            Utility.isUmpire = true;

            showUmpireOptions();

        }
        else {
            showPlayerOptions();
        }


        initialScreenConditions();
        closePlayButton.setAlpha((float)0.6);
        closePlayButton.setEnabled(false);

        // listners for changing baseball region backgrounds ..


        homeRunImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (homeRunImageView.isSelected()) {
                    homeRunImageView.setSelected(false);
                    homeRunLabel.setVisibility(View.GONE);
                }
                else
                {
                    homeRunImageView.setEnabled(true);
                    homeRunImageView.setSelected(true);
                    homeRunLabel.setVisibility(View.VISIBLE);
                }

                setPrediction();


            }
        });
//
        swhImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                gameScreenBackground = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.umpirescreenfield);
                gameScreenBackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field);



               /* if (swhImageView.isSelected()) {
                    postionValue = "NA";


                    positionsEnable();
                    positionsoff();
                    initialScreenConditions();

                    //enable swh
                    swhImageView.setSelected(false);
                    swhImageView.setEnabled(true);
                    swhImageView.setAlpha((float) 1.0);

                    swhLabel.setVisibility(View.GONE);


                }*/

//                else {

                postionValue = "SWH";

                //close guesses enabling
                closePlayButton.setAlpha((float)1);
                closePlayButton.setEnabled(true);

                //disable positions
                positionsoff();
                swhFieldConditions();

                //swh on
                swhImageView.setSelected(true);
                swhImageView.setAlpha((float)1.0);



                //enable rs
                runScore.setEnabled(true);
                runScore.setAlpha((float) 1.0);

                //disable bh
                baseHitImageView.setSelected(false);
                baseHitImageView.setEnabled(false);
                baseHitImageView.setAlpha((float) 0.6);

                //hr enable
                homeRunImageView.setEnabled(true);
                homeRunImageView.setAlpha((float) 1.0);

                swhLabel.setVisibility(View.VISIBLE);

                hideAllFieldPosBadges();
                hitLabel.setVisibility(View.GONE);

//                }

                leftFieldPoints.setVisibility(View.GONE);
                rightFieldpoints.setVisibility(View.GONE);
                centerFieldPoints.setVisibility(View.GONE);


                setPrediction();



            }
        });

        runScore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (runScore.isSelected())
                {
                    runScore.setSelected(false);
                    rsLabel.setVisibility(View.GONE);
                }

                else{
                    runScore.setEnabled(true);
                    runScore.setSelected(true);
                    rsLabel.setVisibility(View.VISIBLE);
                }


                setPrediction();

            }
        });

        baseHitImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //enable positions
                positionsEnable();

                //disable swh
                swhLabel.setVisibility(View.GONE);
                swhImageView.setSelected(false);

//                swhImageView.setEnabled(false);

//                swhImageView.setAlpha((float) 0.6);

                //enable rs
                runScore.setEnabled(true);
                runScore.setAlpha((float) 1.0);

                //enable home run
                homeRunImageView.setEnabled(true);
                homeRunImageView.setAlpha((float) 1.0);

                //toggling base hit
                if (baseHitImageView.isSelected()) {
                    baseHitImageView.setSelected(false);
                    hitLabel.setVisibility(View.GONE);
                } else {

                    baseHitImageView.setSelected(true);
                    hitLabel.setVisibility(View.VISIBLE);
                }
                setPrediction();

            }
        });


        //click listeners for positions to change focus on click
        setPositionSelcted(false);

        rfTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                //if (rfTextView.isSelected()) {
                //rf is off
//                    rfTextView.setSelected(false);
//
//                    //off positions
//                    positionsoff();
//                    initialScreenConditions();
//
//                    rfLabel.setVisibility(View.GONE);
//
//                    postionValue = "NA";
//
//                }
//            else {
                hideAllFieldPosBadges();
                swhLabel.setVisibility(View.GONE);
                //rf is on
                rfTextView.setSelected(true);
                focusFieldPositions();

                rfLabel.setVisibility(View.VISIBLE);
                //making right field on
                rightRegionSelections();

                //two b off
                twoBTextView.setSelected(false);

                //one b off
                oneBTextView.setSelected(false);

                enableOutImages();

                //setting position vale to "RF"
                postionValue = "RF";


//                }

                setPrediction();


            }
        });

        twoBTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (twoBTextView.isSelected()) {

                    twoBTextView.setSelected(false);

                    b2Label.setVisibility(View.GONE);

                    //setting  screen to initial state
                    positionsoff();
                    initialScreenConditions();

                    postionValue = "NA";
                } */
//                else {
                hideAllFieldPosBadges();
                swhLabel.setVisibility(View.GONE);
                //two b is on
                twoBTextView.setSelected(true);
                focusFieldPositions();


                b2Label.setVisibility(View.VISIBLE);

                //setting right field on
                rightRegionSelections();

                rfTextView.setSelected(false);
                twoBTextView.setSelected(true);
                oneBTextView.setSelected(false);
                //enabling home run runscopre and base hit
                enableOutImages();

                postionValue = "B2";

//                }


                setPrediction();


            }
        });
        oneBTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


              /*  if (oneBTextView.isSelected()) {

                    //one b is off
                    oneBTextView.setSelected(false);

                    b1Label.setVisibility(View.GONE);

                    //making all positions to intial off
                    positionsoff();
                    initialScreenConditions();

                    postionValue = "NA";


                } */
//                else {

                hideAllFieldPosBadges();
                swhLabel.setVisibility(View.GONE);
                //setting  right filed to on
                rightRegionSelections();

                b1Label.setVisibility(View.VISIBLE);
                focusFieldPositions();

                // making rf to off
                rfTextView.setSelected(false);
                //making 2b to off
                twoBTextView.setSelected(false);
                //makuing 1b to off
                oneBTextView.setSelected(true);

                //enable homerun basehit and runscore
                enableOutImages();


                postionValue = "B1";
//                }


                setPrediction();


            }
        });

        cfTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (cfTextView.isSelected()) {
                    cfTextView.setSelected(false);

                    positionsoff();
                    initialScreenConditions();

                    cfLabel.setVisibility(View.GONE);

                    postionValue = "NA";
                }*/
//                else {

                hideAllFieldPosBadges();
                swhLabel.setVisibility(View.GONE);


                centerRegionSelections();

                focusFieldPositions();
                cfLabel.setVisibility(View.VISIBLE);


                cfTextView.setSelected(true);
                pTextView.setSelected(false);
                cTextView.setSelected(false);

                enableOutImages();

                postionValue = "CF";

//                }


                setPrediction();


            }
        });
        pTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (pTextView.isSelected()) {
                    pTextView.setSelected(false);
                    positionsoff();
                    initialScreenConditions();
                    postionValue = "NA";

                    pLabel.setVisibility(View.GONE);

                } */
//                else {


                hideAllFieldPosBadges();
                swhLabel.setVisibility(View.GONE);

                centerRegionSelections();
                pLabel.setVisibility(View.VISIBLE);
                focusFieldPositions();

                pTextView.setSelected(true);
                cfTextView.setSelected(false);
                cTextView.setSelected(false);

                enableOutImages();

                postionValue = "P";
//                }


                setPrediction();

            }
        });

        cTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (cTextView.isSelected()) {
                    cTextView.setSelected(false);
                    positionsoff();

                    initialScreenConditions();

                    postionValue = "NA";

                    cLabel.setVisibility(View.GONE);

                } */
//                else {

                centerRegionSelections();


                hideAllFieldPosBadges();
                swhLabel.setVisibility(View.GONE);

                cLabel.setVisibility(View.VISIBLE);
                focusFieldPositions();

                pTextView.setSelected(false);
                cfTextView.setSelected(false);
                cTextView.setSelected(true);
                enableOutImages();

                postionValue = "C";
//                }


                setPrediction();

            }
        });
        lfTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (lfTextView.isSelected()) {
                    lfTextView.setSelected(false);

                    positionsoff();
                    initialScreenConditions();
                    postionValue = "NA";

                    lfLabel.setVisibility(View.GONE);

                }*/
//                else {
                leftRegionSelections();

                hideAllFieldPosBadges();
                swhLabel.setVisibility(View.GONE);
                focusFieldPositions();

                lfTextView.setSelected(true);
                ssTextView.setSelected(false);
                threeBTextView.setSelected(false);
                enableOutImages();

                postionValue = "LF";

                lfLabel.setVisibility(View.VISIBLE);
//                }


                setPrediction();


            }
        });

        ssTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (ssTextView.isSelected()) {
                    ssTextView.setSelected(false);

                    positionsoff();

                    initialScreenConditions();

                    postionValue = "NA";

                    ssLabel.setVisibility(View.GONE);
                } */
//                else {
                leftRegionSelections();

                hideAllFieldPosBadges();
                swhLabel.setVisibility(View.GONE);

                ssLabel.setVisibility(View.VISIBLE);
                focusFieldPositions();

                lfTextView.setSelected(false);
                ssTextView.setSelected(true);
                threeBTextView.setSelected(false);
                enableOutImages();

                postionValue = "SS";

//                }

                setPrediction();


            }
        });
        threeBTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (threeBTextView.isSelected()) {
                    threeBTextView.setSelected(false);
                    positionsoff();

                    initialScreenConditions();

                    postionValue = "NA";

                    b3Label.setVisibility(View.GONE);
                } */
//                else {
                leftRegionSelections();
                hideAllFieldPosBadges();
                swhLabel.setVisibility(View.GONE);

                focusFieldPositions();

                lfTextView.setSelected(false);
                ssTextView.setSelected(false);
                threeBTextView.setSelected(true);

                enableOutImages();

                postionValue = "B3";

                b3Label.setVisibility(View.VISIBLE);
//                }


                setPrediction();

            }
        });


        // progress dialog
        progressDialog = new ProgressDialog(GameScreenActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        // dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(
                GameScreenActivity.this);
        builder.setMessage(Html.fromHtml("<font color='#1191F0'>Are you sure you want to exit the game?</font>")).setTitle("");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {

                    SharedPreferences gameObjectSp =getSharedPreferences("gameobject",MODE_PRIVATE);
                    SharedPreferences.Editor gameObjectEditor = gameObjectSp.edit();
                    gameObjectEditor.putString("gameobject"," ");
                    gameObjectEditor.commit();
                    back();
                    finish();
                    Utility.enterdGameLobbyStatus = false;
                    Intent i = new Intent(GameScreenActivity.this, GameHomeScreenActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                catch (Exception e)
                {

                }

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        leaveGameDialog = builder.create();
        hideSystemUI();

        countDownTextView.setText("3");

        // broadcast receiver

        IntentFilter filter = new IntentFilter(GameReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        gameReceiver = new GameReceiver();
        registerReceiver(gameReceiver, filter);


        registerReceiver(playLeftReceiver,new IntentFilter(GameScreenActivity.PLAY_LEFT_RECIEVER));
        // play start service
        IntentFilter gameChangefilter = new IntentFilter(
                GameScreenActivity.GAME_RESULT_ACTION_RESP);
        gameChangefilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(playResultReceiver, gameChangefilter);

        // play start service
        IntentFilter playStartfilter = new IntentFilter(
                GameScreenActivity.PLAY_START_ACTION_RESP);
        playStartfilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(playStartReceiver, playStartfilter);

        // connection change service
        IntentFilter connectionFilter = new IntentFilter(
                GameScreenActivity.CONNECTION_ACTION_RESP);
        connectionFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(connectionChangeReceiver, connectionFilter);

        // umpire change service
        IntentFilter umpireFilter = new IntentFilter(
                Utility.UMPIRE_CHANGE_ACTION_RESP);
        umpireFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(umpireChangeReceiver, umpireFilter);

        // reset values
        postionId = 0;
        postionValue = "NA";
        currentPlay = 1;
        lastPlayPointValue = 0;
        lastPlayPositionValue = "";

        initialScreenConditions();



    }



    public void showGameLeaderBoard() {
        final Player player = new Player();
        final User userObject = new User();

        RelativeLayout playByPlayTopTabView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.playbyplay_toptab3);
        RelativeLayout leaderBoardTopTab = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.toptab3);
        final TextView playerListSize = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.players_list_size);

        currentPlayerLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.gs_current_playerinfo_layout);
        currentPlayerName = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.gs_current_player_name);
        currentPlayerPoints = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.gs_current_player_points);

        currentPlayerLayout.setVisibility(View.VISIBLE);
        playByPlayTopTabView.setVisibility(View.GONE);
        leaderBoardTopTab.setVisibility(View.VISIBLE);

        //loading current player profile image

        UrlImageViewHelper.setUrlDrawable(currentPlayerProfileImage, Utility.user.getProfilePicURL());

        LayoutParams Listparams = (LayoutParams) gameleaderboardList.getLayoutParams();
        Listparams.addRule(RelativeLayout.BELOW, com.ryansplayllc.ryansplay.R.id.toptab3);

        gameleaderboardList.setLayoutParams(Listparams);

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

                            Utility.gameName = Utility.game.getGameName();
                            Log.e("show leader board","game name ---> " + Utility.game.getGameName());
                            final List<Player> playersList = new ArrayList<Player>();


                            JSONObject playersObject = new JSONObject(data.toString());
                            JSONArray playersArray = playersObject.getJSONArray("players");

                            SharedPreferences useridpref = getSharedPreferences("userid", MODE_PRIVATE);
                            Integer userId = Integer.parseInt(useridpref.getString("userid", "0"));

                            for (int i = 0; i < playersArray.length(); i++) {
                                Player player = new Player();
                                player.jsonParser((JSONObject) playersArray.get(i));

                                playersList.add(player);


                                if (userId == (player.getUserId())) {

                                    setGamePoints(player.getGamepoints() + "");
                                    setUserNoOfPlays(player.getNoOfPlays() + "");
                                    //updating user lastgame details
                                    userObject.setLastGameCreator(Creator.getUserName());
                                    userObject.setUserRank(player.getWorldRank() + "");
                                    userObject.setLastGameName(Utility.game.getGameName());
                                    userObject.setLastGamePoints(player.getTotalPoints() + "");
                                    userObject.setLastGamePlays((Utility.game.getLength()) + "");

                                    Log.e("user game length",userObject.getLastGamePlays()+"");
                                    Log.e("user plays left",userObject.getTotalNoOfPlays()+"");
                                    Log.e("user plays game name",userObject.getLastGameName()+"");


//                                    updating current player info
                                    currentGamePoints = player.getPointsRank();

                                }
                            }

                            final List<Player> playersListtemp = playersList;
                            //   playersList;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    //stuff that updates leaderboard
                                    PlayByPlay playByPlay = new PlayByPlay();
                                    gameleaderboardList.setAdapter(new GameScreenLeaderBoardList(GameScreenActivity.this, playersListtemp));
                                    gameleaderboardList.setDividerHeight(1);
                                    currentPlayerName.setText(Utility.user.getUserName());
                                    currentPlayerPoints.setText(getGamePoints());
                                    playerListSize.setText("(" + playersList.size() + ")");
                                    gsUserRank.setText("#" + userObject.getUserRank());

                                    noOfPlaysInHeader.setText(getUserNoOfPlays() + "");
                                    noOfPlaysInAddPlays.setText("Balance: " + getUserNoOfPlays() + " plays");

                                }
                            });




                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            // Toast.makeText(getApplicationContext(),"error data " + data.toString(),Toast.LENGTH_LONG).show();


                            e.printStackTrace();
                        }

                    }
                }, new WebSocketRailsDataCallback() {

                    @Override
                    public void onDataAvailable(Object data) {
                        // TODO Auto-generated method stub

                        Toast.makeText(getApplicationContext(), "error data " + data.toString(), Toast.LENGTH_LONG).show();

                    }
                });
    }


    public void showPlayByPlay() {
        RelativeLayout playByPlayTopTabView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.playbyplay_toptab3);
        RelativeLayout leaderBoardTopTab = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.toptab3);
        currentPlayerLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.gs_current_playerinfo_layout);

        final SharedPreferences pointsSharedPreference = getSharedPreferences("playpoints", MODE_PRIVATE);


        currentPlayerLayout.setVisibility(View.GONE);
        playByPlayTopTabView.setVisibility(View.VISIBLE);
        leaderBoardTopTab.setVisibility(View.GONE);


        LayoutParams Listparams = (LayoutParams) gameleaderboardList.getLayoutParams();
        Listparams.addRule(RelativeLayout.BELOW, com.ryansplayllc.ryansplay.R.id.playbyplay_toptab3);

        gameleaderboardList.setLayoutParams(Listparams);

        LeaderBoardInput leaderBoardInput = new LeaderBoardInput();
        leaderBoardInput.setGame_id(Utility.game.getId());
        leaderBoardInput.setPer_page(50);
        leaderBoardInput.setPage(1);



        GameService.dispatcher.trigger("private_game.get_play_by_play",
                leaderBoardInput, new WebSocketRailsDataCallback() {

                    @Override
                    public void onDataAvailable(Object data) {

                        try {


                            final ArrayList<PlayByPlay> playByPlayArrayList = new ArrayList<PlayByPlay>();

                            JSONObject playByPlayJson = new JSONObject(data.toString());
                            JSONArray playbyPlayJsonArray = playByPlayJson.getJSONArray("play_by_play");
                            for (int i = 0; i < playbyPlayJsonArray.length(); i++) {
                                PlayByPlay playByPlayObject = new PlayByPlay();
                                playByPlayObject.jsonParser((JSONObject) playbyPlayJsonArray.get(i));
                                playByPlayArrayList.add(playByPlayObject);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //thread to update play by play list
                                    gameleaderboardList.setAdapter(new PlayByPlayAdapter(GameScreenActivity.this, playByPlayArrayList, pointsSharedPreference.getString(Utility.game.getId() + "", "0")));
                                    gameleaderboardList.setDividerHeight(1);
                                    try {

                                        lastPlayPostionValueTextView.setText(playByPlayArrayList.get(0).getPlaybyPlayComments());
                                        resultViewHitRegion.setText(playByPlayArrayList.get(0).getPlaybyPlayComments());

                                    } catch (Exception e) {
                                        lastPlayPostionValueTextView.setText("err +NA");
                                    }

                                }
                            });
                        } catch (Exception e) {

                        }
                    }
                },
                new WebSocketRailsDataCallback() {

                    @Override
                    public void onDataAvailable(Object data) {
                        // TODO Auto-generated method stub
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "error data " + data.toString(), Toast.LENGTH_LONG).show();

                    }
                });


    }


    // sends a local notification if the play is changed in game.
    private void sendNotificationLocalToUser() {

        Intent notificationIntent = new Intent(this, GameScreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setAutoCancel(false);

        if ((currentPlay + 1)<= Utility.game.getLength()) {
//            notification.setLatestEventInfo(GameScreenActivity.this, "Game in progress", "Play " + (currentPlay + 1) + " started get your guess in.", pendingIntent);

//            builder.setTicker("this is ticker text");
            builder.setContentTitle("Game in progress");
            builder.setContentText("Play " + (currentPlay + 1) + " started get your guess in.");
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setContentIntent(pendingIntent);
            builder.setOngoing(true);
//            builder.setSubText("This is subtext...");   //API level 16
            builder.setNumber(100);
            builder.build();

        }else{
            notificationIntent = new Intent(this,FinalLeaderBoardActivity.class);
            pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
//            notification.setLatestEventInfo(GameScreenActivity.this, "Game completed", "Your game is complete. Check the final leaderboard.", pendingIntent);

//            builder.setTicker("this is ticker text");
            builder.setContentTitle("Game completed");
            builder.setContentText("Your game is complete. Check the final leaderboard.");
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setContentIntent(pendingIntent);
            builder.setOngoing(true);
//            builder.setSubText("This is subtext...");   //API level 16
            builder.setNumber(100);
            builder.build();

        }

        notification = builder.getNotification();
        notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;

        notificationManager.notify(9999, notification);
    }


    public void enableOutImages() {

        //enabling the basehit
        baseHitImageView.setEnabled(true);
        baseHitImageView.setAlpha((float) 1.0);

        //enabling home run
        homeRunImageView.setEnabled(true);
        homeRunImageView.setAlpha((float) 1.0);

        //enabling runscore
        runScore.setEnabled(true);
        runScore.setAlpha((float) 1.0);

    }


    private void positionsDisable() {

        //disable the positions
        lfTextView.setEnabled(false);
        rfTextView.setEnabled(false);
        cfTextView.setEnabled(false);

        oneBTextView.setEnabled(false);
        twoBTextView.setEnabled(false);
        threeBTextView.setEnabled(false);

        ssTextView.setEnabled(false);
        cTextView.setEnabled(false);
        pTextView.setEnabled(false);


        //decreasing the alpha
        lfTextView.setAlpha((float) 0.6);
        rfTextView.setAlpha((float) 0.6);
        cfTextView.setAlpha((float) 0.6);

        oneBTextView.setAlpha((float) 0.6);
        twoBTextView.setAlpha((float) 0.6);
        threeBTextView.setAlpha((float) 0.6);

        ssTextView.setAlpha((float) 0.6);
        cTextView.setAlpha((float) 0.6);
        pTextView.setAlpha((float) 0.6);
    }

    private void positionsEnable() {

        //disable the positions
        lfTextView.setEnabled(true);
        rfTextView.setEnabled(true);
        cfTextView.setEnabled(true);

        oneBTextView.setEnabled(true);
        twoBTextView.setEnabled(true);
        threeBTextView.setEnabled(true);

        ssTextView.setEnabled(true);
        cTextView.setEnabled(true);
        pTextView.setEnabled(true);


        //decreasing the alpha
        lfTextView.setAlpha((float) 1.0);
        rfTextView.setAlpha((float) 1.0);
        cfTextView.setAlpha((float) 1.0);

        oneBTextView.setAlpha((float) 1.0);
        twoBTextView.setAlpha((float) 1.0);
        threeBTextView.setAlpha((float) 1.0);

        ssTextView.setAlpha((float) 1.0);
        cTextView.setAlpha((float) 1.0);
        pTextView.setAlpha((float) 1.0);
    }

    private void positionsoff() {
        lfTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_lf_off);
        rfTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_rf_off);
        cfTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_cf_off);

        oneBTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_1b_off);
        twoBTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_2b_off);
        threeBTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_3b_off);

        ssTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_ss_off);
        cTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_c_off);
        pTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.position_p_off);

        leftFieldPoints.setVisibility(View.GONE);
        rightFieldpoints.setVisibility(View.GONE);
        centerFieldPoints.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        ImageView gamescreenbackground = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.umpirescreenfield);

        switch (view.getId()) {


            case com.ryansplayllc.ryansplay.R.id.top_menu_add_more_plays:
                addMorePlayLayout.setVisibility(View.VISIBLE);
                addMorePlayLayout.bringToFront();
                initialize_inAppSetting();
                moreListContent.setVisibility(View.GONE);
                moreIconImage.setBackgroundResource(Color.TRANSPARENT);
                moreIconImage.setImageResource(com.ryansplayllc.ryansplay.R.drawable.more);


                break;
            case com.ryansplayllc.ryansplay.R.id.add_more_plays_close_button:
                addMorePlayLayout.setVisibility(View.GONE);
                enableGameViews();
                positionsEnable();
                swhImageView.setEnabled(true);

                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_home:
                intent = new Intent(getApplicationContext(),
                        GameHomeScreenActivity.class);
                startActivity(intent);
                finish();
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
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                finish();
                break;
            case com.ryansplayllc.ryansplay.R.id.ibt_back:
                leaveGameDialog.show();
                break;

            case com.ryansplayllc.ryansplay.R.id.game_screen_play_info_bt_close_play:

                closePlay();
                break;


            case com.ryansplayllc.ryansplay.R.id.gs_leaderboard_select:
                leaderboardSelect.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.game_tab_hover);
                playByPlaySelect.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.game_tab);
                setPlayByPlayMenuSelected(false);
                showGameLeaderBoard();

                break;
//
            case com.ryansplayllc.ryansplay.R.id.gs_play_by_plays_select:
                playByPlaySelect.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.game_tab_hover);
                leaderboardSelect.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.game_tab);
                setPlayByPlayMenuSelected(true);

                showPlayByPlay();

                break;

            case R.id.inviteFrns:
                intent = new Intent("android.intent.action.VIEW");

                Uri data = Uri.parse("sms:");

                /** Setting sms uri to the intent */

                intent.setData(data);
                intent.setType("vnd.android-dir/mms-sms");
                intent.putExtra("sms_body",Utility.smsBody1+Utility.user.getUserName()+Utility.smsBody2+ Utility.user.getUserName()+Utility.smsBody3+Utility.smsBody4);

                /** Initiates the SMS compose screen, because the activity contain ACTION_VIEW and sms uri */
                startActivity(intent);

                break;

            default:
                if (isSelectable) {
//                    playerPrediction(view);

                }
                break;

        }
    }

    // close play
    private void closePlay() {



        // object for "game.start_next_play" event
        Message message = new Message();
        message.setGame_id(Utility.game.getId());
        message.setPlay_number(currentPlay);


        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        // Triggering game start event
        GameService.dispatcher.trigger("private_game.close_guessing_period", message,
                new WebSocketRailsDataCallback() {

                    @Override
                    public void onDataAvailable(Object data) {
                        try
                        {
                            JSONObject responseJsonObject = new JSONObject(data
                                    .toString());
                            if (responseJsonObject.getBoolean("status")) {
                                progressDialog.dismiss();


                            } else {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Toast.makeText(GameScreenActivity.this,
                                                "Error Occured 1",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });

                            Toast.makeText(GameScreenActivity.this, "error occured 2" + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                    }
                }, new WebSocketRailsDataCallback() {

                    @Override
                    public void onDataAvailable(Object data) {


                        progressDialog.dismiss();
                        final Object finalData = data;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GameScreenActivity.this,
                                        "Error Occured 3 " + finalData.toString(), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                    }
                });
    }


    // method for count down timer
    private void timmer() {
        showTimmerOptions();



        final int val = 500;
        taskProgressInner = (ProgressBar) findViewById(com.ryansplayllc.ryansplay.R.id.TaskProgress_Inner);
        new Thread(new Runnable() {
            public void run() {
                int tmp = (val / 100);

                for (int incr = 0; incr <= val; incr++) {

                    try {
                        taskProgressInner.setProgress(incr);
                        if ((incr % 100) == 0) {
                            tmp--;
                        }
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // intilaizating count down timer object
        progressDialog.dismiss();
        countDownTimer = new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                countDownTextView.setText(Long
                        .toString(millisUntilFinished / 1000));


            }

            public void onFinish() {

                countDownTextView.setText("0");


                isSelectable = false;


                // changing text view if player is not umpire
                if (!Utility.isUmpire) {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            String message = "Waiting for the Umpire result.";
//                            showWaitingPopUp(message);
                            showBallinPlayView();
                            final ImageView gamescreenbackground = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.umpirescreenfield);
                            gamescreenbackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field);


                        }
                    });
                }
                // calling method to submit result
                submitPlayerPrediction();
            }
        };
        isSelectable = true;
        countDownTimer.start();

    }



    @Override
    public void onBackPressed() {
        if(!purchasing_flag) {
            leaveGameDialog.show();
        }
        super.onBackPressed();
    }


    public void setPrediction() {
        try {
            JSONObject resultObject = new JSONObject();

            if (postionValue.equals("NA"))
                resultObject.put("position", "");
            else if (postionValue.equals("SWH"))
                resultObject.put("position", "SO");
            else
                resultObject.put("position", postionValue);

            resultObject.put("basehit", baseHitImageView.isSelected());
            resultObject.put("runscored", runScore.isSelected());
            resultObject.put("homerun", homeRunImageView.isSelected());

            predictionObject.setPosition(postionValue);
            predictionObject.setBasehit(baseHitImageView.isSelected());
            predictionObject.setHomerun(homeRunImageView.isSelected());
            predictionObject.setRunscored(runScore.isSelected());

            int positivePoints = new Utility().calculatePoints(resultObject, predictionObject);

            resultObject.put("position", "");
            resultObject.put("basehit", false);
            resultObject.put("runscored", false);
            resultObject.put("homerun", false);
            int negativePoints = new Utility().calculatePoints(resultObject, predictionObject);
            //int points = new Utility().calculatePoints(predictionObject,resultObject);
            possiblePoints.setText(positivePoints + " / " + negativePoints);

            hideTopInfoLabel();
        } catch (Exception e) {

        }
    }


    private void submitPlayerPrediction() {
        try {
            // predication class
            predictionObject.setPosition(postionValue);
            predictionObject.setBasehit(baseHitImageView.isSelected());
            predictionObject.setHomerun(homeRunImageView.isSelected());
            predictionObject.setRunscored(runScore.isSelected());

            //showing the comments of ball in play.
            JSONObject playerGuessObject = new JSONObject();
            playerGuessObject.put("position", postionValue);
            playerGuessObject.put("baseHit", baseHitImageView.isSelected());
            playerGuessObject.put("homeRun", homeRunImageView.isSelected());
            playerGuessObject.put("runScored", runScore.isSelected());

//            Toast.makeText(getApplicationContext(),Utility.showPlayerGuess(playerGuessObject),Toast.LENGTH_LONG).show();
            if(Utility.showPlayerGuess(playerGuessObject).equalsIgnoreCase("ball hit to NA")){
                playerGuessComment.setText("You didn't get a guess in!");
            }else {
                playerGuessComment.setText(Utility.showPlayerGuess(playerGuessObject));
            }
            // player prediction object for sending
            PlayerPrediction playerPrediction = new PlayerPrediction();
            playerPrediction.setgame_id(Utility.game.getId());
            playerPrediction.setplay_number(currentPlay);
            playerPrediction.setPrediction(predictionObject);


            GameService.dispatcher.trigger("private_game.submit_prediction",
                    playerPrediction, new WebSocketRailsDataCallback() {

                        @Override
                        public void onDataAvailable(Object data) {
                            try {


                                JSONObject response = new JSONObject(data
                                        .toString());
                                if (response.getBoolean("status"))
                                {
                                    isWaitingForPlayResult = true;
                                    if (Utility.isUmpire) {
                                        Intent intent = new Intent(
                                                getBaseContext(),
                                                UmpireScreenActivity.class);
                                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);


                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new WebSocketRailsDataCallback() {

                        @Override
                        public void onDataAvailable(Object data) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    private void showRejoinAlert() {
        // dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(
                GameScreenActivity.this);
        builder.setMessage("Are you want to rejoin the game?").setTitle(
                "Exit the game");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                rejoinRequest();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // starting homeselector screens
                Intent newIntent = new Intent(GameScreenActivity.this,
                        GameHomeScreenActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newIntent);
            }
        });
        rejoinDialog = builder.create();
        rejoinDialog.show();
    }

    private void rejoinRequest() {
        RequestQueue queue = Volley.newRequestQueue(GameScreenActivity.this);
        String url = Utility.playforfiet + Utility.game.getId()
                + "/players/rejoin";
        JSONObject params = new JSONObject();
        try {
            params.put("willing_to_umpire", "unwilling");
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Method.POST, url, params, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject responseJsonObject) {
                progressDialog.dismiss();
                try {
                    if (responseJsonObject.getBoolean("status")) {
                        // game service
                        gameService = new Intent(
                                getApplicationContext(),
                                GameService.class);
                        gameService.putExtra("action", "gameLobby");
                        GameScreenActivity.this
                                .startService(gameService);
                        isConnectedToGame = true;
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        Utility.isUmpire = false;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(GameScreenActivity.this,
                            "Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization",
                        Utility.getAccessKey(GameScreenActivity.this));
                return params;
            }

        };
        queue.add(jsonObjectRequest);
        queue.start();
        progressDialog.show();
    }

    private void showUmpireOptions() {
        // start game options
        timeInfoLinearLayout.setVisibility(View.INVISIBLE);
        gsOpenPlayImage.setVisibility(View.GONE);
        playStartLinearLayout.setVisibility(View.VISIBLE);

        if(!postionValue.equals("NA"))
        {
            closePlayButton.setAlpha((float)1);
            closePlayButton.setEnabled(true);
        }
        else
        {
            closePlayButton.setAlpha((float)0.6);
            closePlayButton.setEnabled(false);
        }


    }

    private void showTimmerOptions() {
        // start game options
        timeInfoLinearLayout.setVisibility(View.VISIBLE);
//		selectPostionLinearLayout.setVisibility(View.INVISIBLE);
        playStartLinearLayout.setVisibility(View.INVISIBLE);
        gsOpenPlayImage.setVisibility(View.GONE);
//        changeUmpireButton.setEnabled(false);


    }

    private void showPlayerOptions() {
        // start game options
//		selectPostionLinearLayout.setVisibility(View.VISIBLE);
        playStartLinearLayout.setVisibility(View.INVISIBLE);
        timeInfoLinearLayout.setVisibility(View.INVISIBLE);
        gsOpenPlayImage.setVisibility(View.VISIBLE);


    }

    @Override
    protected void onDestroy() {
        if(!purchasing_flag)
        {
            //if(mHelper != null) mHelper.dispose();
            mHelper = null;
        }
        super.onDestroy();
        unregisterReceiver(playResultReceiver);

        unregisterReceiver(gameReceiver);
        unregisterReceiver(playStartReceiver);
        unregisterReceiver(connectionChangeReceiver);
        unregisterReceiver(umpireChangeReceiver);
        postionId = 0;
        postionValue = "NA";
        //currentPlay = 1;
        lastPlayPointValue = 0;
        lastPlayPositionValue = "";
        currentPlay = 1;
    }

    private class Message {
        private long game_id;

        //in v1
        //private int plays_completed;

        private int play_number;

        public long getGame_id() {
            return game_id;
        }

        public void setGame_id(long game_id) {
            this.game_id = game_id;
        }

        public int getPlay_number() {
            //in v1
            //return plays_completed;

            return play_number;
        }

        public void setPlay_number(int play_number) {

            //in v1
            //this.plays_completed = plays_completed;

            this.play_number = play_number;
        }
    }

    public class PlayerPrediction {
        private long game_id;
        private int play_number;
        private Prediction prediction;

        public long getgame_id() {
            return game_id;
        }

        public void setgame_id(long gameId) {
            this.game_id = gameId;
        }

        public int getplay_number() {
            return play_number;
        }

        public void setplay_number(int play_number) {
            this.play_number = play_number;
        }

        public Prediction getPrediction() {
            return prediction;
        }

        public void setPrediction(Prediction prediction) {
            this.prediction = prediction;
        }
    }

    public class UmpireResult {
        private long game_id;
        private int play_number;
        private Result result;

        public long getgame_id() {
            return game_id;
        }

        public void setgame_id(long gameId) {
            this.game_id = gameId;
        }

        public int getplay_number() {
            return play_number;
        }

        public void setplay_number(int play_number) {
            this.play_number = play_number;
        }

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }
    }

    public class Result {
        private String position;
        private boolean basehit;
        private boolean homerun;
        private boolean runscored;

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public boolean isBasehit() {
            return basehit;
        }

        public void setBasehit(boolean basehit) {
            this.basehit = basehit;
        }

        public boolean isHomerun() {
            return homerun;
        }

        public void setHomerun(boolean homerun) {
            this.homerun = homerun;
        }

        public boolean isRunscored() {
            return runscored;
        }

        public void setRunscored(boolean runscored) {
            this.runscored = runscored;
        }
    }

    public class Prediction {
        private String position;
        private boolean basehit;
        private boolean homerun;
        private boolean runscored;

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public boolean isBasehit() {
            return basehit;
        }

        public void setBasehit(boolean basehit) {
            this.basehit = basehit;
        }

        public boolean isHomerun() {
            return homerun;
        }

        public void setHomerun(boolean homerun) {
            this.homerun = homerun;
        }

        public boolean isRunscored() {
            return runscored;
        }

        public void setRunscored(boolean runscored) {
            this.runscored = runscored;
        }
    }

    private class GameObject {
        private long game_id;

        public long getGame_id() {
            return game_id;
        }

        public void setId(long id) {
            this.game_id = id;
        }
    }


    public void hideAllFieldPosBadges() {
        lfLabel.setVisibility(View.GONE);
        cfLabel.setVisibility(View.GONE);
        rfLabel.setVisibility(View.GONE);
        ssLabel.setVisibility(View.GONE);
        b1Label.setVisibility(View.GONE);
        b2Label.setVisibility(View.GONE);
        b3Label.setVisibility(View.GONE);
        pLabel.setVisibility(View.GONE);
        cLabel.setVisibility(View.GONE);

    }

    private void initialScreenConditions() {


        gameScreenBackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field);
        closePlayButton.setAlpha((float)0.6);
        closePlayButton.setEnabled(false);

        swhImageView.setSelected(false);
        baseHitImageView.setSelected(false);
        homeRunImageView.setSelected(false);
        runScore.setSelected(false);

        lfTextView.setSelected(false);
        rfTextView.setSelected(false);
        cfTextView.setSelected(false);
        cTextView.setSelected(false);
        pTextView.setSelected(false);
        oneBTextView.setSelected(false);
        twoBTextView.setSelected(false);
        threeBTextView.setSelected(false);
        ssTextView.setSelected(false);

        leftFieldPoints.setVisibility(View.GONE);
        rightFieldpoints.setVisibility(View.GONE);
        centerFieldPoints.setVisibility(View.GONE);

        homeRunLabel.setVisibility(View.GONE);
        hitLabel.setVisibility(View.GONE);
        rsLabel.setVisibility(View.GONE);
        swhLabel.setVisibility(View.GONE);

        hideAllFieldPosBadges();
        possiblePoints.setText("0 / 0");
        baseHitImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.base_hit);

        positionsEnable();

        //disable basehit
        baseHitImageView.setEnabled(false);
        baseHitImageView.setAlpha((float) 0.6);

        //disable home run
        homeRunImageView.setEnabled(false);
        homeRunImageView.setAlpha((float) 0.6);

        //disable unscore
        runScore.setEnabled(false);
        runScore.setAlpha((float) 0.6);

        //enable swh
        swhImageView.setEnabled(true);
        swhImageView.setAlpha((float) 1.0);


    }


    /*------------------------function called when selected position is swh --------------------*/

    private void swhFieldConditions() {

        gameScreenBackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field);

        swhImageView.setSelected(false);
        baseHitImageView.setSelected(false);

        lfTextView.setSelected(false);
        rfTextView.setSelected(false);
        cfTextView.setSelected(false);
        cTextView.setSelected(false);
        pTextView.setSelected(false);
        oneBTextView.setSelected(false);
        twoBTextView.setSelected(false);
        threeBTextView.setSelected(false);
        ssTextView.setSelected(false);

        leftFieldPoints.setVisibility(View.GONE);
        rightFieldpoints.setVisibility(View.GONE);
        centerFieldPoints.setVisibility(View.GONE);

        hideAllFieldPosBadges();
        swhLabel.setVisibility(View.GONE);

        homeRunImageView.setEnabled(true);
        runScore.setEnabled(true);
        possiblePoints.setText("0 / 0");
        baseHitImageView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.base_hit);

        positionsEnable();

        //disable basehit
        baseHitImageView.setEnabled(false);
        baseHitImageView.setAlpha((float) 0.6);

        //enable swh
        swhImageView.setEnabled(true);
        swhImageView.setAlpha((float) 1.0);


    }

/*------------------------function called when selected position is from right region--------------------*/

    public void rightRegionSelections() {
        setPositionSelcted(true);

        //setting gamescreen background as per region of position selected
        gameScreenBackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field_zones_rf_on);

        //changing position image sources as adjacent to half selected and far positons to empty backgrounds to position buttons
        rfTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.rf_player_selected);
        twoBTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.twob_player_selected);
        oneBTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.oneb_player_selected);

        cfTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.cf_player_half_selected);
        pTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.p_player_half_selected);
        cTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.c_player_half_selected);

        lfTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.lf_player_unselected);
        ssTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.ss_player_unselected);
        threeBTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.threeb_player_unselected);


        cfTextView.setSelected(false);
        pTextView.setSelected(false);
        cTextView.setSelected(false);

        lfTextView.setSelected(false);
        ssTextView.setSelected(false);
        threeBTextView.setSelected(false);

        if (rfTextView.isSelected()) {
            rfTextView.setSelected(true);
            oneBTextView.setSelected(false);
            oneBTextView.setSelected(false);

        } else if (twoBTextView.isSelected()) {
            twoBTextView.setSelected(true);
            oneBTextView.setSelected(false);

        } else if (oneBTextView.isSelected()) {
            oneBTextView.setSelected(true);
        }

        //setting top field points visibility as per positions selections
        rightFieldpoints.setVisibility(View.VISIBLE);
        leftFieldPoints.setVisibility(View.VISIBLE);
        centerFieldPoints.setVisibility(View.VISIBLE);

        //setting top field points  as per selections
        rightFieldpoints.setText("+20");
        leftFieldPoints.setText("+5");
        centerFieldPoints.setText("+10");

        //changing top field points text color as per selections
        rightFieldpoints.setTextColor(Color.parseColor("#ffffff"));
        centerFieldPoints.setTextColor(Color.parseColor("#b5c3ae"));
        leftFieldPoints.setTextColor(Color.parseColor("#b5c3ae"));

        //swh is disable
        swhImageView.setSelected(false);
        swhImageView.setEnabled(true);

        //close guesses enabling
        closePlayButton.setAlpha((float)1);
        closePlayButton.setEnabled(true);

    }


/*------------------------function called when selected position is from left region--------------------*/

    public void leftRegionSelections() {

        setPositionSelcted(true);

        //setting gamescreen background as per region of position selected
        gameScreenBackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field_zones_lf_on);

/**
 **changing position image sources as selected reion positins to selected backrounds
 **adjacent to half selected and
 **far positons to empty backgrounds to position buttons
 **/

        lfTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.lf_player_selected);
        threeBTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.threeb_player_selected);
        ssTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.ss_player_selected);

        rfTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.rf_player_unselected);
        twoBTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.twob_player_unselected);
        oneBTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.oneb_player_unselected);

        cfTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.cf_player_half_selected);
        pTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.p_player_half_selected);
        cTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.c_player_half_selected);

        if (lfTextView.isSelected()) {
            lfTextView.setSelected(true);
        } else if (threeBTextView.isSelected()) {
            threeBTextView.setSelected(true);
        } else if (ssTextView.isSelected()) {
            ssTextView.setSelected(true);
        }

        rfTextView.setSelected(false);
        twoBTextView.setSelected(false);
        oneBTextView.setSelected(false);

        cfTextView.setSelected(false);
        pTextView.setSelected(false);
        cTextView.setSelected(false);

        rightFieldpoints.setVisibility(View.VISIBLE);
        leftFieldPoints.setVisibility(View.VISIBLE);
        centerFieldPoints.setVisibility(View.VISIBLE);

        rightFieldpoints.setText("+5");
        leftFieldPoints.setText("+20");
        centerFieldPoints.setText("+10");

        rightFieldpoints.setTextColor(Color.parseColor("#b5c3ae"));
        centerFieldPoints.setTextColor(Color.parseColor("#b5c3ae"));
        leftFieldPoints.setTextColor(Color.parseColor("#ffffff"));

        //swh is disable
        swhImageView.setSelected(false);
        swhImageView.setEnabled(true);

        //close guesses enabling
        closePlayButton.setAlpha((float)1);
        closePlayButton.setEnabled(true);

    }

    /*------------------------function called when selected position is from center region--------------------*/

    public void centerRegionSelections() {
        setPositionSelcted(true);

        //setting gamescreen background as per region of position selected
        gameScreenBackground.setImageResource(com.ryansplayllc.ryansplay.R.drawable.baseball_field_zones_cf_on);

/**
 **changing position image sources as selected reion positins to selected backrounds
 **adjacent to half selected and
 **far positons to empty backgrounds to position buttons
 **/
        rfTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.rf_player_half_selected);
        twoBTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.twob_player_half_selected);
        oneBTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.oneb_player_half_selected);

        cfTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.cf_player_selected);
        pTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.p_player_selected);
        cTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.c_player_selected);

        lfTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.lf_player_half_selected);
        ssTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.ss_player_half_selected);
        threeBTextView.setBackgroundResource(com.ryansplayllc.ryansplay.R.drawable.threeb_player_half_selected);


        if (cfTextView.isSelected()) {
            cfTextView.setSelected(true);
        } else if (pTextView.isSelected()) {
            pTextView.setSelected(true);
        } else if (cTextView.isSelected()) {
            cTextView.setSelected(true);
        }


        rfTextView.setSelected(false);
        twoBTextView.setSelected(false);
        oneBTextView.setSelected(false);

        lfTextView.setSelected(false);
        ssTextView.setSelected(false);
        threeBTextView.setSelected(false);


        rightFieldpoints.setVisibility(View.VISIBLE);
        leftFieldPoints.setVisibility(View.VISIBLE);
        centerFieldPoints.setVisibility(View.VISIBLE);


        rightFieldpoints.setText("+10");
        leftFieldPoints.setText("+10");
        centerFieldPoints.setText("+20");

        rightFieldpoints.setTextColor(Color.parseColor("#b5c3ae"));
        centerFieldPoints.setTextColor(Color.parseColor("#ffffff"));
        leftFieldPoints.setTextColor(Color.parseColor("#b5c3ae"));

        //swh is disable
        swhImageView.setSelected(false);
        swhImageView.setEnabled(true);

        //close guesses enabling
        closePlayButton.setAlpha((float)1);
        closePlayButton.setEnabled(true);


    }

    @Override
    protected void onResume() {
        super.onResume();
        screenStatus = "resume";
    }

    @Override
    protected void onPause() {
        super.onPause();
        screenStatus = "pause";
    }


    public void showTimer() {

        timeContainer.bringToFront();

        new CountDownTimer(4000, 1000) {
            TextView timer = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.time);
            RelativeLayout timerLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.timeContainer);

            public void onTick(long millisUntilFinished) {
                timer.setText("" + millisUntilFinished / 1000);
                disableGameViews();
                disableLeftViews();

            }

            public void onFinish() {
                timerLayout.setVisibility(View.GONE);
                enableGameViews();
                enableLeftViews();
                swhImageView.setEnabled(true);
            }
        }.start();
    }



    public void showBallinPlayView() {

        ballinPlayView.setVisibility(View.VISIBLE);
        ballinPlayView.bringToFront();

        disableGameViews();
        disableLeftViews();


    }

    public void predictionResultView() {
        predictionResultViewLayout.setVisibility(View.VISIBLE);
        predictionResultViewLayout.bringToFront();


        if (lastPlayPointValue > 0) {
            resultViewLastPlayPointText.setText("+"+Integer.toString(lastPlayPointValue));
        } else {

            resultViewLastPlayPointText.setText(Integer.toString(lastPlayPointValue));

        }

        resultViewPlayNoText.setText("Play #" + (currentPlay - 1));




    }

    public void hideBallinPlayView() {


        RelativeLayout ballinPlayView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.ball_in_play_screen);
        ballinPlayView.setVisibility(View.GONE);
//        enableGameViews();
        enableLeftViews();
    }

    public void hideResultView() {
        RelativeLayout predictionResultView = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.result_Screen);
        predictionResultView.setVisibility(View.GONE);

        enableGameViews();
//        enableLeftViews();

    }

    public void disableGameViews()
    {
        positionsDisable();

        swhImageView.setEnabled(false);
        homeRunImageView.setEnabled(false);
        runScore.setEnabled(false);
        baseHitImageView.setEnabled(false);

        closePlayButton.setEnabled(false);
    }


    public void disableLeftViews() {
        moreIconImage.setEnabled(false);
        gsAddPlaysIcon.setEnabled(false);

        leaderboardSelect.setEnabled(false);
        playByPlaySelect.setEnabled(false);

    }


    public void enableGameViews()
    {
        positionsEnable();
        if(swhImageView.isSelected())
        {
            homeRunImageView.setEnabled(true);
            runScore.setEnabled(true);
            baseHitImageView.setEnabled(false);


        }
        if(isPositionSelcted())
        {

            homeRunImageView.setEnabled(true);
            runScore.setEnabled(true);
            baseHitImageView.setEnabled(true);


        }
        if(swhImageView.isSelected())
        {
            positionsDisable();
            swhImageView.setEnabled(true);
        }
        if(isPositionSelcted())
        {

            swhImageView.setEnabled(false);
            positionsEnable();

        }

        if(postionValue.equals("NA"))
        {
            closePlayButton.setAlpha((float)0.6);
            closePlayButton.setEnabled(false);
        }
        else
        {
            closePlayButton.setAlpha((float)1);
            closePlayButton.setEnabled(true);
        }



    }

    public void enableLeftViews() {
        moreIconImage.setEnabled(true);
        gsAddPlaysIcon.setEnabled(true);

        leaderboardSelect.setEnabled(true);
        playByPlaySelect.setEnabled(true);
    }


    private void back() {
//        progressDialog.show();
        SharedPreferences gameid = getSharedPreferences("gameid", MODE_PRIVATE);
        String gameidValue = gameid.getString(Utility.user.getUserName(), "default");
        //        Toast.makeText(getApplicationContext(),gameidValue+"",Toast.LENGTH_SHORT).show();
        if (gameidValue.equals("default")) {

        } else {

            SharedPreferences pointsSharedPreference = getSharedPreferences("playpoints", MODE_PRIVATE);

            SharedPreferences.Editor pointsEditor = pointsSharedPreference.edit();
            pointsEditor.putString(Utility.game.getId() + "", null);
            pointsEditor.commit();

            leaveGameRequest(gameidValue.split(";")[0]);

//            finish();

        }
    }

    // player leaving out of the game
    private void leaveGameRequest(final String gameidValue) {
        RequestQueue queue = Volley.newRequestQueue(GameScreenActivity.this);

        JsonObjectRequest leaveGameRequest = new JsonObjectRequest(Method.POST,
                "http://52.88.139.217/api//v2/private_games/" + gameidValue + "/player/leave", null, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject responseJsonObject) {
                try {
                    AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(GameScreenActivity.this);
                    dialogbuilder.setMessage(responseJsonObject.toString());
//                            dialogbuilder.show();
                    if (responseJsonObject.getBoolean("status")) {
                        if (countDownTimer != null) {
                            countDownTimer.cancel(); // Stopping timer
                        }
                        progressDialog.dismiss();
                        //un subscribing channel
                        if (GameService.dispatcher != null) {
                            if (GameService.dispatcher.isSubscribed(GameService.chanelName))
                            {
                                GameService.dispatcher.unsubscribe(GameService.chanelName);
                                GameService.dispatcher.disconnect();

                            }
                        }
                        if (!Utility.isUmpire) {
                            Utility.clearGame();
                        }

                        progressDialog.dismiss();
                        SharedPreferences gameidpref = getSharedPreferences("gameid", MODE_PRIVATE);
                        SharedPreferences.Editor gameideditor = gameidpref.edit();
                        gameideditor.putString(Utility.user.getUserName(), "default");
                        gameideditor.commit();
                        Toast.makeText(getApplication(), "Left the game successfully", Toast.LENGTH_SHORT).show();
                        currentPlay = 1;

                        // starting homeselector screens
                        Intent newIntent = new Intent(
                                GameScreenActivity.this,
                                GameHomeScreenActivity.class);
                        newIntent
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(newIntent);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(GameScreenActivity.this,
                            "Error Occured 4", Toast.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization",
                        Utility.getAccessKey(GameScreenActivity.this));
                return params;
            }

        };
        queue.add(leaveGameRequest);
        queue.start();
    }

    public void pointsHistoryForPlayByPlay(String lastPlayPoints) {
        PlayerPrediction prediction = new PlayerPrediction();

        SharedPreferences pointsSharedPreference = getSharedPreferences("playpoints", MODE_PRIVATE);
        String historyPoints = pointsSharedPreference.getString(Utility.game.getId() + "", "0");

        historyPoints += "," + lastPlayPoints;

        SharedPreferences.Editor pointsEditor = pointsSharedPreference.edit();
        pointsEditor.putString(Utility.game.getId() + "", historyPoints);
        pointsEditor.commit();


    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        try
        {
            notificationManager.cancel(9999);

        }
        catch(Exception e)
        {
            Log.e("gs notification err",e.toString()+"");
        }
    }

    public void playerStatsView()
    {
        finalLeaderBoard = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.finalLeaderBoard);
        finalName = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.final_game_name);
        finalRank = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.final_player_rank);
        finalPoints = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.final_total_points);
        TextView rankSuffix = (TextView) findViewById(R.id.rank_sup);
        finalName.setText(Utility.game.getGameName());

        String ending = "";
        int  ones = (Integer.parseInt(Utility.user.getUserRank())) % 10;
        int tens = (Integer.parseInt(Utility.user.getUserRank()) / 10);
        tens = tens % 10;
        if(tens == 1){
            ending = "th";
        }else {
            switch (ones) {
                case 1:
                    ending = "st";
                    break;
                case 2:
                    ending = "nd";
                    break;
                case 3:
                    ending = "rd";
                    break;
                default:
                    ending = "th";
                    break;
            }
        }


        finalRank.setText("YOU CAME IN " + Utility.user.getUserRank());
        rankSuffix.setText(ending);
        finalPoints.setText(getGamePoints() + "");
    }

    public void checkCurrentPlay(int currentPlay)
    {
        if(currentPlay==1)
        {
            lastPlayContr.setVisibility(View.INVISIBLE);
        }
        else
        {
            lastPlayContr.setVisibility(View.VISIBLE);
        }
    }

    public void focusFieldPositions()
    {
        lfTextView.setAlpha((float)1);
        ssTextView.setAlpha((float)1);
        threeBTextView.setAlpha((float)1);

        rfTextView.setAlpha((float)1);
        twoBTextView.setAlpha((float)1);
        oneBTextView.setAlpha((float)1);

        cfTextView.setAlpha((float)1);
        cTextView.setAlpha((float)1);
        pTextView.setAlpha((float)1);
    }

    public void showTopInfoLabel()
    {
        currentPlayTextView.setVisibility(View.GONE);
        selectYourPosLabel.setVisibility(View.VISIBLE);
    }

    public void hideTopInfoLabel()
    {
        currentPlayTextView.setVisibility(View.VISIBLE);
        selectYourPosLabel.setVisibility(View.GONE);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }

    void complain(String message) {
        alert("Error: " + message);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Uri getLocalBitmapUri() {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = getResources().getDrawable(R.drawable.rp_icon3);
        Bitmap bmp = null;
//        bmp =Utility.user.getProfilePic();

        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) getResources().getDrawable(R.drawable.rp_icon3)).getBitmap();
        }
        else
        {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {

            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
        }
    }

    public void initialize_inAppSetting()
    {
        //in app purchasing initialization started
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjaLlrsD9+iPbSuj1pt49gLh/s4vkZ0NAFuXhwPFJ/VxlriklfZs4Xzki9/SS1JeMAFvwfaIQ2KXXxcAh2FziUJnLdMJFglH1PWOqMgHPt2FO2NVz4JF4oCWUC5h4o5GRm4SoXvGir4YlvJwovc3Cz1V3PZYSU2Gi1dMkTpYo1E8AH2UYeZ6LeVmmSMksTLkejfzTUcqIbvGD2iQJiaHltswjrLrDhUrAJ0yGDtXbTRWkpeID5qQXs1Jgfo7XgzAcYP0O6F+tRASNvYu6Jc7SdV9n/ZuimPI+6b3Eed9yJtCAtJyMeuJQwpQIwCoipZb5fWH9ZN+pB6fDXA7xdVUzawIDAQAB";

        // Create the helper, passing it our context and the public key to verify signatures with
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        //mHelper.enableDebugLogging(false);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {

                if (!result.isSuccess()) {
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;
                mHelper.queryInventoryAsync(mGotInventoryListener);

            }
        });
        //in app purchasing initialization completed
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */



            Purchase gasPurchase = inventory.getPurchase("plays20");
            if (gasPurchase != null && gasPurchase.getDeveloperPayload().equals("add more plays")){
                mHelper.consumeAsync(inventory.getPurchase("plays20"), mConsumeFinishedListener);
                return;
            }
            gasPurchase = inventory.getPurchase("plays50");
            if (gasPurchase != null && gasPurchase.getDeveloperPayload().equals("add more plays")){
                mHelper.consumeAsync(inventory.getPurchase("plays50"), mConsumeFinishedListener);
                return;
            }
            gasPurchase = inventory.getPurchase("plays200");
            if (gasPurchase != null && gasPurchase.getDeveloperPayload().equals("add more plays")){
                mHelper.consumeAsync(inventory.getPurchase("plays200"), mConsumeFinishedListener);
                return;
            }
            gasPurchase = inventory.getPurchase("plays500");
            if (gasPurchase != null && gasPurchase.getDeveloperPayload().equals("add more plays")){
                mHelper.consumeAsync(inventory.getPurchase("plays500"), mConsumeFinishedListener);
                return;
            }
            gasPurchase = inventory.getPurchase("plays1000");
            if (gasPurchase != null && gasPurchase.getDeveloperPayload().equals("add more plays")){
                mHelper.consumeAsync(inventory.getPurchase("plays1000"), mConsumeFinishedListener);
                return;
            }
        }
    };

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            //Toast.makeText(getApplicationContext(),"You Purchased " + purchase.getSku(),Toast.LENGTH_LONG).show();
            // if we were disposed of in the meantime, quit.
            purchasing_flag = false;
            if (mHelper == null) return;

            if (result.isFailure()) {
                //complain("Error purchasing: " + result);
                return;
            }
            if (!purchase.getDeveloperPayload().equals("add more plays") ) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }


            if (purchase.getSku().equals(product_id)) {
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);

            }

        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit

                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String transaction_id = purchase.getOrderId();
                transaction_id = transaction_id.replaceAll("[^\\d]", "");
                String transaction_details = purchase.getSku()+"##"+Utility.user.getUserName()+"##"+purchase.getToken()+"##"+date+"##"+transaction_id;

                int add_play_num = 0;
                if(purchase.getSku().equals("plays20"))
                    add_play_num = 20;
                else if(purchase.getSku().equals("plays50"))
                    add_play_num = 50;
                else if(purchase.getSku().equals("plays200"))
                    add_play_num = 200;
                else if(purchase.getSku().equals("plays500"))
                    add_play_num = 500;
                else if(purchase.getSku().equals("plays1000"))
                    add_play_num = 1000;
                if(add_play_num!=0)
                    update_plays(add_play_num, transaction_details);

            }
            else {
                complain("Error while consuming: " + result);
            }

        }
    };

    public void update_plays(final int additional_plays, String transaction_details){

        RequestQueue queue = Volley.newRequestQueue(GameScreenActivity.this);


        final JSONObject params = new JSONObject();
        try {
            params.put("play_coins", additional_plays);
            params.put("transaction_details", transaction_details);
            params.put("push_token", Utility.pushToken);
            params.put("uuid", Utility.uuid);

        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Utility.updateUserPlays, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject responseJsonObject) {

                try {

                    Log.e("update plays","parameters---> "+params.toString()+"response----> "+responseJsonObject.toString());
                    if (responseJsonObject.getBoolean("status")) {
                        noOfPlaysInHeader.setText(String.valueOf(Integer.parseInt(GameHomeScreenActivity.staticNop) + additional_plays));
                        noOfPlaysInAddPlays.setText(String.valueOf(Integer.parseInt(GameHomeScreenActivity.staticNop) + additional_plays));
                    }
                    else
                    {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(GameScreenActivity.this,
                            "Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("update play error","err");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization",
                        Utility.getAccessKey(GameScreenActivity.this));
                return params;
            }

        };
        queue.add(jsonObjectRequest);
        queue.start();
    }

    public void addMorePlays(View currentView) {

        Toast.makeText(getApplicationContext(),currentView.getTag().toString(),Toast.LENGTH_LONG).show();

        product_id = currentView.getTag().toString();
        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "add more plays";

        purchasing_flag = true;
        mHelper.launchPurchaseFlow(this, product_id, 8008,
                mPurchaseFinishedListener, payload);
    }

}


