package com.ryansplayllc.ryansplay.services;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import br.net.bmobile.websocketrails.WebSocketRailsChannel;
import br.net.bmobile.websocketrails.WebSocketRailsDataCallback;
import br.net.bmobile.websocketrails.WebSocketRailsDispatcher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.GameLobbyActivity;
import com.ryansplayllc.ryansplay.GameScreenActivity;
import com.ryansplayllc.ryansplay.HandleSessionFail;
import com.ryansplayllc.ryansplay.JoinPrivateGame2;
import com.ryansplayllc.ryansplay.LoginActivity;
import com.ryansplayllc.ryansplay.Plays;
import com.ryansplayllc.ryansplay.R;
import com.ryansplayllc.ryansplay.Utility;
import com.ryansplayllc.ryansplay.models.Player;
import com.ryansplayllc.ryansplay.receivers.GameReceiver;

public class
        GameService extends IntentService {

    public long gameId;
    private String umpireWilling;
    public static String chanelName;
    public static WebSocketRailsDispatcher dispatcher;
    public static WebSocketRailsChannel gameChannel = null;
    public static List<Player> players;
    public static long staticGameId = 0;

    public GameService() {
        super("Game");
        // roll bar
        Rollbar.setIncludeLogcat(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getStringExtra("action");
        if (action.equals("leave")) {
            leave();
        } else if (action.equals("join")) {
            gameId = intent.getLongExtra("game_id", 0);
            SharedPreferences gameidpref=getSharedPreferences("gameid",MODE_PRIVATE);
            SharedPreferences.Editor gameideditor=gameidpref.edit();
            gameideditor.putString(Utility.user.getUserName(),gameId+";"+Utility.user.getUserName());
            gameideditor.commit();
            umpireWilling = intent.getStringExtra("umpireWilling");
            join();
        } else if (action.equals("gameLobby")) {
            gameLobby();
        }else if(action.equals("rejoin")){


            gameId = intent.getLongExtra("game_id", 0);
            SharedPreferences gameidpref=getSharedPreferences("gameid",MODE_PRIVATE);
            SharedPreferences.Editor gameideditor=gameidpref.edit();
            gameideditor.putString(Utility.user.getUserName(),gameId+";"+Utility.user.getUserName());
            gameideditor.commit();

            umpireWilling = intent.getStringExtra("umpireWilling");
            rejoin();
        }
    }

    public void gameLobby() {
        // TODO Auto-generated method stub

        try {

            Log.e("testing disconnected issues23 ","23");
            dispatcher = new WebSocketRailsDispatcher(new URL(
                    Utility.socketBaseURL + "?access_token="
                            + Utility.getAccessKey(getApplicationContext())));
            dispatcher.connect();

            Log.e("final leader board game id ","" + Utility.game.getId());
            staticGameId = Utility.game.getId();
            chanelName = "private_game_" + Utility.game.getId() + "_"
                    + Utility.game.getGameName();
            gameChannel = dispatcher.subscribe(chanelName);

            gameChannel.bind("joined", new WebSocketRailsDataCallback() {

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
            gameChannel.bind("left", new WebSocketRailsDataCallback() {

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
            gameChannel.bind("disconnected", new WebSocketRailsDataCallback() {

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
            gameChannel.bind("umpire_changed",
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
            gameChannel.bind("abandoned", new WebSocketRailsDataCallback() {

                @Override
                public void onDataAvailable(Object data) {
                    // TODO Auto-generated method stub
                    try {
                        JSONObject jsonObject = new JSONObject(data.toString());
                        if (jsonObject.getBoolean("status")) {
                            if (dispatcher != null) {
                                if (dispatcher.isSubscribed(chanelName)) {
                                    dispatcher.unsubscribe(chanelName);
                                    Log.e("channel subscribe status","channel unsubscribed"+dispatcher.isSubscribed(chanelName));
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
            gameChannel.bind("game_started", new WebSocketRailsDataCallback() {

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

            gameChannel.bind("result_published", new WebSocketRailsDataCallback() {

                @Override
                public void onDataAvailable(Object data) {
                    // TODO Auto-generated method stub
                    Log.e("private_game.publish_result 1","success 1" + data.toString());
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(GameScreenActivity.GAME_RESULT_ACTION_RESP);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra("action", "play_result");
                    broadcastIntent.putExtra("value", data.toString());
                    broadcastIntent.putExtra("error", false);
                    sendBroadcast(broadcastIntent);
                }
            });

            gameChannel.bind("finished", new WebSocketRailsDataCallback() {

                @Override
                public void onDataAvailable(Object data) {
                    // TODO Auto-generated method stub
                    try {

                        Log.e("123456789very important"," " + data);
                        JSONObject jsonObject = new JSONObject(data.toString());
                        if (jsonObject.getBoolean("status")) {
                            if (dispatcher != null) {
                                if (dispatcher.isSubscribed(chanelName)) {
                                    dispatcher.unsubscribe(chanelName);

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
            gameChannel.bind("guessing_period_closed",
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

            Log.e("testing disconnected issues24 ","24");
            JSONObject message = new JSONObject();
            message.put("game_id", Utility.game.getId());
            gameChannel.dispatch("joined", message);
            Log.e("testing disconnected issues25 ","25");
            GameObject gameObject = new GameObject();
            gameObject.setId(Utility.game.getId());


            Log.e("status of game"," "+Utility.gameStatus);
            if(Utility.gameStatus.equalsIgnoreCase("waiting")){
                Log.e("testing disconnected issues26","26");

            }else{
                dispatcher.trigger("private_game.join_game", gameObject);
                Log.e("testing disconnected issues27 ","27");
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("testing disconnected issues21 ","21");
            e.printStackTrace();
        }
    }

    public void joinOnRefresh(){
        join();
    }
    private void join() {
        // TODO Auto-generated method stub

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = Utility.baseURL + "/v2/private_games/" + gameId + "/player/join";

        JSONObject params = new JSONObject();
        try {
            params.put("umpiring_interest", umpireWilling);
        } catch (JSONException e) {
            Log.e("** Game service first try block error ", gameId+" -->game id"+e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Method.POST, url, params, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject responseJsonObject) {


                try{

                    Log.e("join response",responseJsonObject.toString());
                    if(responseJsonObject.has("player_status")) {
                        if (responseJsonObject.getString("player_status").equalsIgnoreCase("logged_out"))
                        {
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(GameReceiver.ACTION_RESP);
                            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            broadcastIntent.putExtra("action", "join");
                            broadcastIntent.putExtra("value",
                                    responseJsonObject.toString());
                            broadcastIntent.putExtra("error", false);
                            sendBroadcast(broadcastIntent);
                        }
                    }

                    if (responseJsonObject.getBoolean("status")) {
                        // array of json object


                        JSONObject gameJsonObject = responseJsonObject.getJSONObject("game");

                        JSONArray playerJsonObject = gameJsonObject
                                .getJSONArray("players");

                        //GameService.players = new ArrayList<Player>();
                        // parsing and adding of player object in list
                        for (int i = 0; i < playerJsonObject.length(); i++) {

                            Player player = new Player();

                            JSONObject playerJSON = (JSONObject) playerJsonObject
                                    .getJSONObject(i);

                            player.jsonParser(playerJSON);

                            GameService.players.add(player);

                        }

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(GameReceiver.ACTION_RESP);
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        broadcastIntent.putExtra("action", "join");
                        broadcastIntent.putExtra("value",
                                responseJsonObject.toString());
                        broadcastIntent.putExtra("error", false);
                        sendBroadcast(broadcastIntent);
                    }

                    else
                    {
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(GameReceiver.ACTION_RESP);
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        broadcastIntent.putExtra("action", "join");
                        broadcastIntent.putExtra("value",
                                responseJsonObject.toString());
                        broadcastIntent.putExtra("error", false);
                        sendBroadcast(broadcastIntent);
                    }


                }catch(Exception e){
                    //Log.e("** Game service", e.);
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("** error in game service ",volleyError.networkResponse.statusCode+"");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization",
                        Utility.getAccessKey(getApplicationContext()));
                return params;
            }
        };
        queue.add(jsonObjectRequest);
        queue.start();
    }


    private void rejoin() {
        // TODO Auto-generated method stub

        Log.e("testing disconnected issues15 ","15");

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = Utility.baseURL + "/v2/private_games/" + gameId + "/player/rejoin";

        /*JSONObject params = new JSONObject();
        try {
            //params.put("umpiring_interest", umpireWilling);
        } catch (JSONException e) {
            Log.e("rejoin ** Game service first try block error ", gameId+" -->game id"+e.getMessage());
        }*/
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Method.POST, url, null, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject responseJsonObject) {


                try{
                    if (responseJsonObject.getBoolean("status")) {
                        // array of json object


                        JSONObject gameJsonObject = responseJsonObject.getJSONObject("game");

                        JSONArray playerJsonObject = gameJsonObject
                                .getJSONArray("players");

                        GameService.players = new ArrayList<Player>();
                        //GameService.players = new ArrayList<Player>();
                        // parsing and adding of player object in list
                        for (int i = 0; i < playerJsonObject.length(); i++) {

                            Player player = new Player();

                            JSONObject playerJSON = (JSONObject) playerJsonObject
                                    .getJSONObject(i);

                            player.jsonParser(playerJSON);

                            GameService.players.add(player);

                        }

                        IntentFilter filter = new IntentFilter(GameReceiver.ACTION_RESP);
                        filter.addCategory(Intent.CATEGORY_DEFAULT);
                        GameReceiver gameReceiver = new GameReceiver();
                        registerReceiver(gameReceiver, filter);

                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(GameReceiver.ACTION_RESP);
                        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                        broadcastIntent.putExtra("action", "join");
                        broadcastIntent.putExtra("value",responseJsonObject.toString());
                        broadcastIntent.putExtra("error", false);
                        sendBroadcast(broadcastIntent);

                    }else{
                        Toast.makeText(getApplicationContext(),responseJsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e){
                    Log.e("rejoin ^^^ game service error", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("** error in game service ",volleyError.networkResponse.statusCode+"");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization",
                        Utility.getAccessKey(getApplicationContext()));
                return params;
            }
        };
        queue.add(jsonObjectRequest);
        queue.start();
    }

    private void leave() {
        // TODO Auto-generated method stub
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "http://52.88.139.217/api//v2/private_games/" + gameId + "/player/leave";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST,url
                , null, new Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject responseJsonObject) {
                Intent broadcastIntent = new Intent();
                try {
                    if (responseJsonObject.getBoolean("status")) {
                        if (dispatcher != null) {
                            if (dispatcher.isSubscribed(chanelName)) {
                                dispatcher.unsubscribe(chanelName);
                            }
                        }
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                IntentFilter filter = new IntentFilter(GameReceiver.ACTION_RESP);
                filter.addCategory(Intent.CATEGORY_DEFAULT);
                GameReceiver gameReceiver = new GameReceiver();
                registerReceiver(gameReceiver, filter);

                broadcastIntent.setAction(GameReceiver.ACTION_RESP);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra("action", "leave");
                broadcastIntent.putExtra("value",
                        responseJsonObject.toString());
                broadcastIntent.putExtra("error", false);
                sendBroadcast(broadcastIntent);

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization",
                        Utility.getAccessKey(getApplicationContext()));
                return params;
            }
        };
        queue.add(jsonObjectRequest);
        queue.start();
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
}
