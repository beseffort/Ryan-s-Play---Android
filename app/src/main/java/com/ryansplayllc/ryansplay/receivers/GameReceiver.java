package com.ryansplayllc.ryansplay.receivers;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.FinalLeaderBoardActivity;
import com.ryansplayllc.ryansplay.GameHomeScreenActivity;
import com.ryansplayllc.ryansplay.GameLobbyActivity;
import com.ryansplayllc.ryansplay.GameScreenActivity;
import com.ryansplayllc.ryansplay.HandleSessionFail;
import com.ryansplayllc.ryansplay.JoinPrivateGame2;
import com.ryansplayllc.ryansplay.Plays;
import com.ryansplayllc.ryansplay.R;
import com.ryansplayllc.ryansplay.UmpireScreenActivity;
import com.ryansplayllc.ryansplay.Utility;
import com.ryansplayllc.ryansplay.adapters.GameLobbyPlayerList;
import com.ryansplayllc.ryansplay.models.Creator;
import com.ryansplayllc.ryansplay.models.Player;
import com.ryansplayllc.ryansplay.models.Umpire;
import com.ryansplayllc.ryansplay.services.GameService;

import java.util.List;

public class GameReceiver extends BroadcastReceiver {
    public static final String ACTION_RESP = "com.ryansplayllc.ryansplay.intent.action.GAME_ACTION";
    public static String playerLoginStatus="";
    public static boolean gameJoined = false;
    public Context globalContext = null;

    @Override
    public void onReceive(Context context, Intent intent)
    {

        // roll bar
        globalContext = context;
        Rollbar.setIncludeLogcat(true);

        JSONObject responseJsonString = null;
        try {
            responseJsonString = new JSONObject(intent.getStringExtra("value"));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        String action = intent.getStringExtra("action");

        boolean error = intent.getBooleanExtra("error", false);

        Log.e("join trigger test initial "," " + context.getClass());
        // Log.e("testing web sockets boolean error","new player was joined " + error);
        if (!error) {
            if (action.equals("join")) {
                try {
                    Log.e("receiver join respponse",responseJsonString.toString());

                    if (responseJsonString.getBoolean("status")) {

                        if (responseJsonString.has("player_status")) {
                            if (responseJsonString.getString("player_status").equalsIgnoreCase("logged_out")) {
                                HandleSessionFail.HandleSessionFail((Activity) context);

                            }
                        }

                    else{

                        Utility.game.JsonParser(responseJsonString
                                .getJSONObject("game"));
                        JSONObject creatorJsonObject = responseJsonString.getJSONObject("game").getJSONObject("owner");
                        Creator creator = new Creator();

                        creator.jsonParser(creatorJsonObject);

//						JoinGameListActivity.progressDialog.dismiss();

                        //if the game is still in playing state, navigate to game screen
                        if (Utility.gameStatus.equals("inprogress")) {

                            //initializes the service
                            Intent gameService = new Intent(context, GameService.class);
                            gameService.putExtra("action", "gameLobby");
                            context.startService(gameService);

                            //navigate to game screen.
                            Intent newIntent = new Intent(context,
                                    GameScreenActivity.class);

                            newIntent.putExtra("gameStatus", "inprogress");
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(newIntent);

                            Utility.gameStatus = "";

                        } else {
                            Intent newIntent = new Intent(context,
                                    GameLobbyActivity.class);
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(newIntent);
                        }

                    }
                    }
                    else if(Integer.parseInt(Utility.user.getTotalNoOfPlays())<Utility.game.getLength())
                    {
                        Log.e("noplays popup1","");
                        showNoPlaysPopup();
                    }

                    else {

//						JoinGameListActivity.progressDialog.dismiss();
                        try{
                            Toast.makeText(context,
                                    responseJsonString.getString("message"),
                                    Toast.LENGTH_SHORT).show();

                        }
                        catch (Exception e)
                        {
                            Log.e("Error joingame",e.toString());
                        }
                    }
//                    JoinGameListActivity.progressDialog.dismiss();
//                    Intent newIntent = new Intent(context,
//                            GameLobbyActivity.class);
//                    newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//                            | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(newIntent);  //for development purpose o disabling check for player already in another game.....

                }
                catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(context, "Error Occured"+e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();

                }

            } else if (action.equals("leave")) {
                try {
                    if (responseJsonString.getBoolean("status")) {
                        Intent newIntent = new Intent(context,
                                GameHomeScreenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(newIntent);
                    } else {
                        Toast.makeText(context,
                                responseJsonString.getString("message"),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (action.equals("new_player_joined")) {

                Log.e("join trigger test initial final","1 " + context.getClass());
                try{
                     updateLeaderBoard();

                }catch (Exception e){
                }
                try {
                    if (responseJsonString != null && gameJoined
                            && responseJsonString.getJSONObject("player").has("username")) {
                        if (responseJsonString.getJSONObject("player").getLong("id") == Utility.user
                                .getId()) {
                            return;
                        }
                        for (int i = 0; i < GameService.players.size(); i++) {
                            Player player = GameService.players.get(i);
                            if (player.getUserId() == responseJsonString.getJSONObject("player").getLong("id")) {
                                return;
                            }
                        }
                        Player player = new Player();
                        player.jsonParser(responseJsonString.getJSONObject("player"));


                        GameService.players.add(player);


//                        GameLobbyPlayerList gameLobbyPlayerListAdapter=new GameLobbyPlayerList(GameService.players,(Activity) context);
//                        GameLobbyActivity.gameLobbyListView
//                                .setAdapter(gameLobbyPlayerListAdapter);

                        context.sendBroadcast(new Intent(GameLobbyActivity.PLAYER_JOINED_RESPONSE));


                        GameLobbyActivity.gameLobbyPlayersSize.setText("("+GameService.players.size()+")");
                        Utility.setListViewHeightBasedOnChildren(GameLobbyActivity.gameLobbyListView);

                        Log.e("testing web sockets","new player was joined " + GameService.players.size());

                        Toast.makeText(context,
                                player.getUsername() + " Joined game",
                                Toast.LENGTH_SHORT).show();
                        gameJoined = false;

                        GameLobbyActivity.inviteFriends.setVisibility(View.VISIBLE);

                        Log.e("Umpire status1",Utility.isUmpire+"="+Utility.game.getUmpire().getId()+"");

                        GameLobbyActivity.emptyGameLobbyLayout.setVisibility(View.GONE);
                        GameLobbyActivity.fullgameLobbyLayout.setVisibility(View.VISIBLE);
                        if(Utility.isUmpire){

                            GameLobbyActivity.launchGameButton.setVisibility(View.VISIBLE);
                            GameLobbyActivity.changeUmpireButton.setVisibility(View.VISIBLE);

                        }else{

                            GameLobbyActivity.launchGameButton.setVisibility(View.GONE);
                            GameLobbyActivity.changeUmpireButton.setVisibility(View.GONE);
                        }



                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    Log.e("testing the game lobby error ","new player was joined error" + e.getMessage());
                    e.printStackTrace();
                }
            } else if (action.equals("player_left")) {
                try {

                    responseJsonString = responseJsonString.getJSONObject("player");
                    long id = responseJsonString.getLong("id");
                    int possition = -1;
                    Player player = null;
                    for (int i = 0; i < GameService.players.size(); i++) {
                        if (GameService.players.get(i).getUserId() == id) {
                            player = GameService.players.get(i);
                            possition = i;
                        }
                    }

                    if (possition != -1) {
                        try{

                            GameService.players.remove(possition);

                            context.sendBroadcast(new Intent(GameLobbyActivity.PLAYER_JOINED_RESPONSE));

                            GameLobbyActivity.gameLobbyListView.setDividerHeight(1);

                            if (Utility.user.getId() != player.getUserId()) {
                                Toast.makeText(context, player.getUsername() + " left the game",Toast.LENGTH_SHORT).show();
                            }
                            if(GameService.players.size()==1) {


                                GameLobbyActivity.emptyGameLobbyLayout.setVisibility(View.VISIBLE);
                                GameLobbyActivity.playerWaitInfoLabel.setVisibility(View.GONE);
                                GameLobbyActivity.fullgameLobbyLayout.setVisibility(View.GONE);
                                GameLobbyActivity.launchGameButton.setVisibility(View.GONE);
                                GameLobbyActivity.changeUmpireButton.setVisibility(View.GONE);
                            }

                        }
                        catch (Exception e)
                        {
                            Log.e("left exception Game receiver",e.toString());
                        }

                    }

                    updateLeaderBoard();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Log.e("player left error",e.getMessage()+"");
                    e.printStackTrace();
                }
            }
            else if (action.equals("umpire_changed")) {
                changeGameLobbyRecieve();
                Log.e("change umpire receive ","1");
                Umpire umpire = new Umpire();
                Toast.makeText(context,umpire.getUserName()+"before change",Toast.LENGTH_SHORT).show();
                umpire.jsonParser(responseJsonString);
                Utility.game.setUmpire(umpire);
                Toast.makeText(context,umpire.getUserName()+"after Change",Toast.LENGTH_SHORT).show();



                if (Utility.getUserId(context) == umpire.getId()) {
                }
            } else if (action.equals("abandoned")) {
                try {
                    if (responseJsonString.getBoolean("status")) {

                        Utility.enterdGameLobbyStatus =false;
                        if (!Utility.isUmpire) {
                            Toast.makeText(context, "Game has been abandoned",
                                    Toast.LENGTH_SHORT).show();
                        }

                        //context.startActivity(newIntent);
                        gameAbandoned();

                        // clearing game variable
                        Utility.clearGame();
                    } else {
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else if (action.equals("started")) {
                try {
                    if (responseJsonString.getBoolean("status"))
                    {
                        if (Utility.isUmpire)
                        {
                            GameLobbyActivity.progressDialog.dismiss();
                            Log.e("umpire status",Utility.isUmpire+"");
                        }

                       // game finished flag
                        Utility.isGameFinished = false;
                      // Toast.makeText(context, "Game Started",Toast.LENGTH_SHORT).show();
                        Intent newIntent = new Intent(context,
                                GameScreenActivity.class);

                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        context.startActivity(newIntent);
                        GameLobbyActivity gameLobbyActivity=new GameLobbyActivity();
                        gameLobbyActivity.finish();

                    }
                    else
                    {

                    }
                } catch (JSONException e) {

                    e.printStackTrace();

                }
            } else if (action.equals("finished")) {
                try {
                    if (responseJsonString.getBoolean("status")) {
                        JSONObject gameJsonObject = responseJsonString
                                .getJSONObject("game");

                        // game finished flag
                        Utility.isGameFinished = true;

                        Toast.makeText(context, "Game Finished",
                                Toast.LENGTH_SHORT).show();

//						newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
//								| Intent.FLAG_ACTIVITY_NEW_TASK);
                        Utility.clearGame();
//




                    } else {
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {

                if (action.equals("join")) {

                }
                Toast.makeText(context, "Error Occured", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void gameAbandoned() {

        try{
            ActivityManager am = (ActivityManager) globalContext.getSystemService(globalContext.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            Log.e("current activity name", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName()+"   Package Name :  "+componentInfo.getPackageName());

            if(taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase("com.ryansplayllc.ryansplay.GameScreenActivity")){
                Intent newIntent = new Intent(globalContext,
                        FinalLeaderBoardActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                globalContext.startActivity(newIntent);
            }else{
                Intent newIntent = new Intent(globalContext,
                        GameHomeScreenActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                globalContext.startActivity(newIntent);
            }
            // connection change service

            globalContext.sendBroadcast(new Intent(GameScreenActivity.PLAY_LEFT_RECIEVER));
        }catch (Exception e){
            Log.e("current activity name", "CURRENT Activity :: exception " + e.getMessage());
        }


    }

    private void updateLeaderBoard() {
        try{
            ActivityManager am = (ActivityManager) globalContext.getSystemService(globalContext.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            Log.e("current activity name", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName()+"   Package Name :  "+componentInfo.getPackageName());

            // connection change service

            globalContext.sendBroadcast(new Intent(GameScreenActivity.PLAY_LEFT_RECIEVER));
            if(Utility.isUmpire)
            {
                globalContext.sendBroadcast(new Intent(UmpireScreenActivity.PLAY_LEFT_RECIEVER));
            }


        }catch (Exception e){
            Log.e("current activity name", "CURRENT Activity :: exception " + e.getMessage());
        }
    }

    public void changeGameLobbyRecieve()
    {
        Log.e("change umpire receive ","2");

        GameLobbyActivity.gameLobbyListView.setAdapter(new GameLobbyPlayerList(GameService.players,
                (Activity) globalContext));
        GameLobbyActivity.gameLobbyListView.setDividerHeight(1);
        Utility.setListViewHeightBasedOnChildren(GameLobbyActivity.gameLobbyListView);


    }

    public void showNoPlaysPopup()
    {
        Log.e("noplays popup2","");
        final PopupWindow noPlaysPopup;
        LayoutInflater layoutInflater  = (LayoutInflater)globalContext.getSystemService(globalContext.LAYOUT_INFLATER_SERVICE);
        Log.e("noplays popup3","");
        final View popupView = layoutInflater.inflate(R.layout.no_plays_popup, null);
        Log.e("noplays popup4","");
        noPlaysPopup = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        noPlaysPopup.showAtLocation(popupView, Gravity.CENTER,0,0);
        Log.e("noplays popup5","");
        ImageView noPlaysPopupCloseIcon = (ImageView) popupView.findViewById(R.id.noplayspopup_close);


        noPlaysPopupCloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noPlaysPopup.dismiss();
                JoinPrivateGame2.enableFooter();


            }
        });


        Button addPlays=(Button) popupView.findViewById(R.id.noplayspopup_addPlays);
        addPlays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playsIntent =new Intent(globalContext,Plays.class);
                playsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                globalContext.startActivity(playsIntent);
            }
        });

    }


}
