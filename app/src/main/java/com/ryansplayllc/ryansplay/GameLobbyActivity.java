package com.ryansplayllc.ryansplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.adapters.GameLobbyPlayerList;
import com.ryansplayllc.ryansplay.models.Player;
import com.ryansplayllc.ryansplay.models.Umpire;
import com.ryansplayllc.ryansplay.receivers.GameReceiver;
import com.ryansplayllc.ryansplay.services.GameService;

import br.net.bmobile.websocketrails.WebSocketRailsDataCallback;

public class GameLobbyActivity extends Activity implements
        OnClickListener {


    //Strings for Game lobby
    public  String creatorStatus;

    public String getCreatorStatus() {
        return creatorStatus;
    }

    public void setCreatorStatus(String creatorStatus) {
        this.creatorStatus = creatorStatus;
    }

    public static Button launchGameButton;
    public static Button changeUmpireButton;
    private ImageView refershImageView; // Refresh button
    private AlertDialog dialog;
    private GameReceiver gameReceiver;

    // player list view

    public static ListView gameLobbyListView;
    // players list
    public static List<Player> players;

    // progress dialog
    public static ProgressDialog progressDialog;

    //gamelobby player list adapter variable
    public static GameLobbyPlayerList gameLobbyPlayerListAdapter;

    // footer variables
    private FrameLayout leaderBoardFooterButton;
    private FrameLayout profileFooterButton;
    private FrameLayout homeFooterButton;
    private FrameLayout settingsFooterButton;
    private FrameLayout playsFooterButton;
    // header variables
    private ImageButton backImageButton;
    private TextView playerNameTextView;
    private TextView noOfPlaysTextView;
    public static String creatorName;

    public static TextView gameLobbyPlayersSize;

    private RelativeLayout startGameButton;



    // game info
    private TextView gameNameTextView;
    private TextView gameUmpireTextView = null;
    private TextView gameLenghtTextView;
    public static TextView playerWaitInfoLabel;

    // public profileselector
    private PopupWindow popupWindow;
    private View publicProfilelayout;
    private ImageView profilePicImageView;
    private TextView playersLength;

    // intent service
    Intent gameService;


    //header invite friends variable
    public static   TextView inviteFriends;

    //intialising empty players game lobby and  filled players game lobby
    public static RelativeLayout emptyGameLobbyLayout,inviteFriendsButton;
    public static RelativeLayout fullgameLobbyLayout;


    // umpire change
    public BroadcastReceiver umpireChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            JSONObject tempJSON = null;
            JSONObject responseJsonString = null;
            try {
                tempJSON = new JSONObject(
                        intent.getStringExtra("value"));
                Log.e("umpire change activity 1",intent.getStringExtra("value"));
                responseJsonString = tempJSON.getJSONObject("user");
                Log.e("umpire change activity 2",intent.getStringExtra("value"));

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            boolean error = intent.getBooleanExtra("error", false);
            if (!error) {
                Log.e("umpire change activity 3",responseJsonString.toString());

                Umpire umpire = new Umpire();
                umpire.jsonParser(responseJsonString);
                Utility.game.setUmpire(umpire);
                Log.e("umpire change activity ",Utility.getUserId(context) + " == " + umpire.getId());
                if (Utility.getUserId(context) == umpire.getId()) {
                    Toast.makeText(context, "You are the new Umpire",
                            Toast.LENGTH_SHORT).show();
                    populatePlayerList();
                    showUmpireOptions();
                    Utility.isUmpire = true;
                } else {
                    Toast.makeText(context,
                            "Umpire changed",
                            Toast.LENGTH_SHORT).show();
                    populatePlayerList();
                    hideUmpireOptions();
                    Utility.isUmpire = false;
                }
                gameUmpireTextView.setText(creatorName);
            } else {
            }
        }
    };
 public static final String PLAYER_JOINED_RESPONSE = "com.ryansplayllc.ryansplay.intent.action.PLAYER_JOINED_ACTION";


    public BroadcastReceiver playerJoinedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            refreshGameLobbyPlayersList();
        }
    };
public  static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ryansplayllc.ryansplay.R.layout.game_lobby_new);
        context=getApplicationContext();
        //setting creator Status
        setCreatorStatus(getIntent().getStringExtra("creator"));
//        Toast.makeText(getApplicationContext(),getIntent().getBooleanExtra("creator",false)+"",Toast.LENGTH_SHORT).show();


        //making status true for user on entering game lobby using for footer navigations
        Utility.enterdGameLobbyStatus = true;

        Typeface bebasfont = Typeface.createFromAsset(getAssets(), "BebasNeue Bold.ttf");
        SharedPreferences gameidpref=getSharedPreferences("gameid",MODE_PRIVATE);
        SharedPreferences.Editor gameideditor=gameidpref.edit();
        gameideditor.putString(Utility.user.getUserName(),Utility.game.getId()+";"+Utility.user.getUserName());
        gameideditor.commit();


        // roll bar
        Rollbar.setIncludeLogcat(true);

        playersLength = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.playersLength);
        emptyGameLobbyLayout  =(RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.empty_gamelobby_layout);
        fullgameLobbyLayout   =(RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.full_game_lobby);

        inviteFriends=(TextView) findViewById(com.ryansplayllc.ryansplay.R.id.inviteFriends_header);
        inviteFriendsButton = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.invite_friends_button);


        inviteFriendsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteFriendsMessage();
            }
        });
        inviteFriends.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteFriendsMessage();
            }
        });

        launchGameButton =  (Button) findViewById(com.ryansplayllc.ryansplay.R.id.game_lobby_bt_launch_game);
        changeUmpireButton =(Button) findViewById(com.ryansplayllc.ryansplay.R.id.gl_bt_change_umpire);
        playerWaitInfoLabel =(TextView) findViewById(R.id.player_wait_info_label);

        launchGameButton.setTypeface(bebasfont);
        changeUmpireButton.setTypeface(bebasfont);
        changeUmpireButton.setVisibility(View.VISIBLE);

        startGameButton = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.start_game_button);

        refershImageView = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.refreshButton);
        backImageButton = (ImageButton) findViewById(com.ryansplayllc.ryansplay.R.id.ibt_back);

        // player list view
        gameLobbyListView = (ListView) findViewById(com.ryansplayllc.ryansplay.R.id.gl_lv_gome_loby);

        // game info
        gameNameTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.game_lobby_tv_game_name);
        gameUmpireTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.game_lobby_tv_game_umpire);
        gameLenghtTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.game_lobby_tv_game_length);
        gameLobbyPlayersSize = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.gamelobby_players_list_size);

        creatorName = Utility.creator.getUserName()+"";


        // game info value set
        gameNameTextView.setText(Utility.game.getGameName());
        gameUmpireTextView.setText(creatorName);
        gameLenghtTextView.setText(Utility.game.getLength()+"");
        // showing game launch button

        // progress bar
        progressDialog = new ProgressDialog(GameLobbyActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        // footer
        leaderBoardFooterButton =    (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board);
        profileFooterButton =        (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_account);
        homeFooterButton =           (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_home);
        settingsFooterButton =       (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_settings);
        playsFooterButton =          (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_plays);

        homeFooterButton.setSelected(true);

        // footer
        leaderBoardFooterButton.setOnClickListener(this);
        profileFooterButton.setOnClickListener(this);
        homeFooterButton.setOnClickListener(this);
        settingsFooterButton.setOnClickListener(this);
        playsFooterButton.setOnClickListener(this);

        startGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Utility.enterdGameLobbyStatus = false;
                Utility.isUmpire=true;

                GameObject gameObject = new GameObject();
                gameObject.setId(Utility.game.getId());


                GameService.dispatcher.trigger("private_game.start_game", gameObject);
                progressDialog.show();

                SharedPreferences gameObjectSp =getSharedPreferences("gameobject",MODE_PRIVATE);
                SharedPreferences.Editor gameObjectEditor = gameObjectSp.edit();
                gameObjectEditor.putString("gameobject"," ");
                gameObjectEditor.commit();



                Log.e("game lobby start game",Utility.game.getLength()+"");

            }
        });

        // player request

        players = new ArrayList<Player>();

        populatePlayerList();



        // back button
        backImageButton.setOnClickListener(this);

//

        // Listeners
        launchGameButton.setOnClickListener(this);
        changeUmpireButton.setOnClickListener(this);

        // dialog

        AlertDialog.Builder builder = new AlertDialog.Builder(
                GameLobbyActivity.this);
        builder.setMessage(Html.fromHtml("<font color='#1191F0'>Are you sure you want to exit the game?</font>")).setTitle("");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                SharedPreferences gameObjectSp =getSharedPreferences("gameobject",MODE_PRIVATE);
                SharedPreferences.Editor gameObjectEditor = gameObjectSp.edit();
                gameObjectEditor.putString("gameobject"," ");
                gameObjectEditor.commit();

                Utility.enterdGameLobbyStatus = false;
                back();
                finish();
                Intent i = new Intent(GameLobbyActivity.this, GameHomeScreenActivity.class);
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
        dialog = builder.create();

        // listview onclick listener for public profileselector
        gameLobbyListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                long userId = players.get((position)).getUserId();

                RequestQueue queue = Volley
                        .newRequestQueue(GameLobbyActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Method.GET, Utility.playerPublicProfile + userId
                        + "/show", null, new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseJsonObject) {
                        // TODO Auto-generated method stub
//								showPublicProfile(responseJsonObject);
                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        // TODO Auto-generated method stub
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                "Error Occured", Toast.LENGTH_SHORT)
                                .show();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders()
                            throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("Authorization",
                                Utility.getAccessKey(GameLobbyActivity.this));
                        return params;
                    }

                };

            }
        });

        // broadcast receiver

        IntentFilter filter = new IntentFilter(GameReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        gameReceiver = new GameReceiver();
        registerReceiver(gameReceiver, filter);

        IntentFilter playerJoinedFilter =new IntentFilter(GameLobbyActivity.PLAYER_JOINED_RESPONSE);
        playerJoinedFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(playerJoinedReceiver,playerJoinedFilter);


        // game service
//        Toast.makeText(getApplicationContext(),"action kept game lobby",Toast.LENGTH_SHORT).show();

        if(!Utility.gameStatus.equals("waiting")) {
            gameService = new Intent(getApplicationContext(), GameService.class);
            gameService.putExtra("action", "gameLobby");
            this.startService(gameService);
            Utility.gameStatus = "";
        }else{
            getPlayers();
            Utility.gameStatus = "";
        }
        // umpire change service
        IntentFilter umpireFilter = new IntentFilter(
                Utility.UMPIRE_CHANGE_ACTION_RESP);
        umpireFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(umpireChangeReceiver, umpireFilter);


        progressDialog.dismiss();

    }

    public void getPlayers(){

        Log.e("status of game 1"," "+Utility.gameStatus + "game id " + Utility.game.getId());
        ListOfPlayersInput listOfPlayersInput = new ListOfPlayersInput();
        listOfPlayersInput.setGame_id(Utility.game.getId());
        listOfPlayersInput.setPer_page(50);
        listOfPlayersInput.setPage(1);

        try{
            GameService.dispatcher.trigger("private_game.get_players_list",
                    listOfPlayersInput, new WebSocketRailsDataCallback() {

                        @Override
                        public void onDataAvailable(Object data) {

                            Log.e("status of game 2"," "+data.toString());
                            // TODO Auto-generated method stub
                            try {

                                final List<Player> playersList = new ArrayList<Player>();



                                JSONObject playersObject = new JSONObject(data.toString());
                                JSONArray playersArray = playersObject.getJSONArray("players");

                                for (int i = 0; i < playersObject.getJSONArray("players").length(); i++) {
                                    Player player = new Player();
                                    player.jsonParser((JSONObject) playersObject.getJSONArray("players").get(i));

                                    playersList.add(player);
                                    Log.e("players List", playersList.get(i).getUsername());



                                }
                                GameService.players = playersList;
                                populatePlayerList();






                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                Log.e("status of game 3 exception "," "+e.getMessage());

                                e.printStackTrace();
                            }

                        }
                    }, new WebSocketRailsDataCallback() {

                        @Override
                        public void onDataAvailable(Object data) {
                            // TODO Auto-generated method stub
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "error data " + data.toString(), Toast.LENGTH_LONG).show();

                        }
                    });
        }catch (Exception e){


            finish();
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    //input that was passed as  a bean object to get the leader board
    public class ListOfPlayersInput {

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

    public void popupClose(View view) {
        popupWindow.dismiss();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_home:
                intent = new Intent(getApplicationContext(),
                        GameHomeScreenActivity.class);
                startActivity(intent);

                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board:
                intent = new Intent(getApplicationContext(),
                        LeaderBoardActivity.class);
                startActivity(intent);

                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_account:
                intent = new Intent(getApplicationContext(),
                        ChangeProfileInfoActivity.class);
                startActivity(intent);

                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_plays:
                intent = new Intent(getApplicationContext(), Plays.class);
                startActivity(intent);

                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);

                break;
            case com.ryansplayllc.ryansplay.R.id.ibt_back:
                dialog.show();
//                finish();
                break;
            case com.ryansplayllc.ryansplay.R.id.game_lobby_bt_launch_game:
                Utility.enterdGameLobbyStatus = false;
                Utility.isUmpire=true;

                GameObject gameObject = new GameObject();
                gameObject.setId(Utility.game.getId());


                GameService.dispatcher.trigger("private_game.start_game", gameObject);
                progressDialog.show();

                SharedPreferences gameObjectSp =getSharedPreferences("gameobject",MODE_PRIVATE);
                SharedPreferences.Editor gameObjectEditor = gameObjectSp.edit();
                gameObjectEditor.putString("gameobject"," ");
                gameObjectEditor.commit();



                Log.e("game lobby start game",Utility.game.getLength()+"");


                break;
            case com.ryansplayllc.ryansplay.R.id.gl_bt_change_umpire:
                intent = new Intent(getApplicationContext(),
                        ChangeUmpireActivity.class);
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


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //Utility.updateHeaderUI(playerNameTextView, noOfPlaysTextView);
        if (gameUmpireTextView != null) {
            gameUmpireTextView.setText(creatorName);
        }
    }

    private void hideUmpireOptions()
    {
        launchGameButton.setVisibility(View.GONE);
        changeUmpireButton.setVisibility(View.GONE);
        if(players.size()>1) {
            playerWaitInfoLabel.setVisibility(View.VISIBLE);
        }
    }

    private void showUmpireOptions()
    {
        launchGameButton.setVisibility(View.VISIBLE);
        changeUmpireButton.setVisibility(View.VISIBLE);
        playerWaitInfoLabel.setVisibility(View.GONE);
    }

    public void populatePlayerList() {

        try {

            players = null;
            players = GameService.players;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    gameLobbyPlayersSize.setText("("+players.size()+")");
                    gameUmpireTextView.setText(creatorName);

                   gameLobbyPlayerListAdapter=new GameLobbyPlayerList(players, GameLobbyActivity.this);

                    gameLobbyListView.setAdapter(gameLobbyPlayerListAdapter);
                    gameLobbyListView.setDividerHeight(1);

                    playersLength.setText("Players");
                    if(players.size()<=1){
                        emptyGameLobbyLayout.setVisibility(View.VISIBLE);
                        playerWaitInfoLabel.setVisibility(View.GONE);
                        launchGameButton.setVisibility(View.GONE);
                        inviteFriends.setVisibility(View.GONE);
                    }else{
                        emptyGameLobbyLayout.setVisibility(View.GONE);
                        launchGameButton.setVisibility(View.VISIBLE);
                        playerWaitInfoLabel.setVisibility(View.GONE);


                    }



                    //to show or hide the umpire options

                    try{

                        Log.e("Umpire status2",Utility.isUmpire+"="+Utility.game.getUmpire().getId()+"");
                        if ((Utility.user.getId()==Utility.game.getUmpire().getId()) && players.size()>1) {
                            showUmpireOptions();

                            Utility.isUmpire = true;
                        } else {
                            hideUmpireOptions();
                        }
                    }catch(Exception e){
                        hideUmpireOptions();
                    }

                }
            });
        }catch (Exception e){
            getPlayers();
            e.printStackTrace();
        }
//
    }

    public void listViewNext(View view) {
    }

    public void listViewBack(View view) {
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

    private void back() {
        try {
            SharedPreferences gameid = getSharedPreferences("gameid", MODE_PRIVATE);
            String gameidValue = gameid.getString(Utility.user.getUserName(), "default");
            Log.e("gameid",gameidValue.toString());
            if (gameidValue.equals("default")) {

            } else {
                leave(gameidValue.split(";")[0]);
            }
        } catch (Exception e) {
            Log.e("Game lobby catch", e.toString());
        }

    }

    private void leave(String gameidValue) {

        RequestQueue queue = Volley
                .newRequestQueue(GameLobbyActivity.this);


        JsonObjectRequest gameListRequest = new JsonObjectRequest(Method.POST,
                "http://52.88.139.217/api//v2/private_games/" + gameidValue + "/player/leave", null, new Listener<JSONObject>() {

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

                        progressDialog.dismiss();
                        SharedPreferences gameidpref = getSharedPreferences("gameid", MODE_PRIVATE);
                        SharedPreferences.Editor gameideditor = gameidpref.edit();
                        gameideditor.putString(Utility.user.getUserName(), "default");
                        gameideditor.commit();
                        Toast.makeText(getApplication(), "Left the game successfully", Toast.LENGTH_SHORT).show();


                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(getApplication(), "game leave Error reponse"+responseJsonObject.toString(), Toast.LENGTH_SHORT).show();


                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplication(), "Server Error Occured in Leaving Game", Toast.LENGTH_SHORT).show();
//                    finish();
                    e.printStackTrace();
                }
                progressDialog.dismiss();

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                progressDialog.dismiss();

                AlertDialog.Builder builder = new AlertDialog.Builder(GameLobbyActivity.this);
                builder.setMessage(arg0.networkResponse.statusCode + "");
                builder.show();
                finish();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization",
                        Utility.getAccessKey(GameLobbyActivity.this));
                return params;
            }

        };

        queue.add(gameListRequest);
        queue.start();
    }


    @Override
    protected void onPause() {
        super.onPause();

        Log.e("gamelobby", "paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("gamelobby","stopped");

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        SharedPreferences gameObjectSp =getSharedPreferences("gameobject",MODE_PRIVATE);

        try {
            long newUmpireId =0;
            //storing umpire id before parsing to restore umpire id after change umpire
            newUmpireId=Utility.game.getUmpire().getId();

            JSONObject gameObject = new JSONObject(gameObjectSp.getString("gameobject","default"));

            Utility.game.JsonParser(gameObject);
            Utility.game.JsonParser(gameObject.getJSONObject("private_game"));

            if (newUmpireId!=0)
            {
                Log.e("new umpire id",Utility.game.getUmpire().getId()+"");
                Utility.game.getUmpire().setId(newUmpireId);
            }

            if ((Utility.user.getId()==Utility.game.getUmpire().getId()))
            {
                Utility.isUmpire=true;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        gameUmpireTextView.setText(creatorName);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.e("gamelobby","onpostresumed");
        try {
            unregisterReceiver(gameReceiver);
            unregisterReceiver(umpireChangeReceiver);
            unregisterReceiver(playerJoinedReceiver);

        }catch(Exception e){
            Log.e("Game lobby activity error in onDestroy method ",""+e.getMessage());
        }
    }



    public  void refreshGameLobbyPlayersList()
    {
        GameLobbyPlayerList gameLobbyPlayerListAdapter=new GameLobbyPlayerList(GameService.players,GameLobbyActivity.this);
        gameLobbyPlayerListAdapter.notifyDataSetChanged();
        GameLobbyActivity.gameLobbyListView.setAdapter(gameLobbyPlayerListAdapter);

    }



}

