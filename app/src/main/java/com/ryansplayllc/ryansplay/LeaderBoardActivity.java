package com.ryansplayllc.ryansplay;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.adapters.LeaderBoardList;
import com.ryansplayllc.ryansplay.models.User;
import com.ryansplayllc.ryansplay.models.WorldPlayer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeaderBoardActivity extends Activity implements OnClickListener {

    private ImageButton backImageButton;
    //footer variables
    private FrameLayout leaderBoardFooterButton;
    private FrameLayout profileFooterButton;
    private FrameLayout homeFooterButton;
    private FrameLayout settingsFooterButton;
    private FrameLayout playsFooterButton;

    public static ProgressDialog progressDialog;
    //intilising topbar buttons of game leaderbord
    private LinearLayout topBarPoints;
    private LinearLayout topBarAccuracy;
    private RelativeLayout topBarDate;

    private TextView currentPlayerName,currentPlayerPts,currentPlayerRank ,btmPtsLabel;

    // The current offset index of data you have loaded
    private int currentPage = 1;
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;

    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    public static String sortBy = "world_rank" ;

    private ListView leaderboardList;
    private ImageView currentPlayerProfilePic;
    ArrayList<WorldPlayer> playerList = new ArrayList<WorldPlayer>();

    public void initialSettings(){
        currentPage = 1;
        visibleThreshold = 0;

        previousTotalItemCount = 0;
        loading = true;
        startingPageIndex = 0;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ryansplayllc.ryansplay.R.layout.activity_leader_board);
        // roll bar
        Rollbar.setIncludeLogcat(true);

        backImageButton = (ImageButton) findViewById(com.ryansplayllc.ryansplay.R.id.ibt_back);

        // footer
        leaderBoardFooterButton =    (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board);
        profileFooterButton =        (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_account);
        homeFooterButton =           (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_home);
        settingsFooterButton =       (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_settings);
        playsFooterButton =          (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_plays);

        //topbar assigning variables
        topBarPoints=   (LinearLayout) findViewById(com.ryansplayllc.ryansplay.R.id.top_bar_points);
        topBarAccuracy= (LinearLayout) findViewById(com.ryansplayllc.ryansplay.R.id.top_bar_games_played);
        topBarDate=     (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.top_bar_date);


        currentPlayerName = (TextView) findViewById(R.id.currentPlayerName);
        currentPlayerPts = (TextView) findViewById(R.id.currentPlayerPts);
        currentPlayerRank = (TextView) findViewById(R.id.currentPlayerRank);
        currentPlayerProfilePic = (ImageView) findViewById(R.id.lb_current_playerPic);
        btmPtsLabel = (TextView) findViewById(R.id.lb_pts_btm_label);
        //loading current player profile image
        Log.e("userimageurl", Utility.user.getProfilePicURL() + "");
        if (Utility.user.getProfilePic() != null) {
            currentPlayerProfilePic.setImageBitmap(Utility.user.getProfilePic());
        } else {
            ImageLoader imageLoader = new ImageLoader();
            imageLoader.imageView = currentPlayerProfilePic;
            imageLoader.player = Utility.user;
            imageLoader.run();
        }

        // footer click listeners
        leaderBoardFooterButton.setOnClickListener(this);
        profileFooterButton.setOnClickListener(this);
        homeFooterButton.setOnClickListener(this);
        settingsFooterButton.setOnClickListener(this);
        playsFooterButton.setOnClickListener(this);

        backImageButton.setVisibility(View.GONE);
        // Setting selected state
        leaderBoardFooterButton.setSelected(true);




        //applying click events for tobar
        topBarPoints.setOnClickListener(this);
        topBarDate.setOnClickListener(this);
        topBarAccuracy.setOnClickListener(this);

        //default points tab is on hovered
        topBarPoints.setSelected(true);
        sortBy = "world_rank";
        loadLeaderBoard(1,20);
        initialSettings();


        leaderboardList=(ListView) findViewById(com.ryansplayllc.ryansplay.R.id.leaderboardlist);
        leaderboardList.setOnScrollListener( new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                Log.e("progress dialog ","showing page number " + currentPage);
                //progressDialog.show();
                loadLeaderBoard(currentPage,20);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });



    }

    private void loadLeaderBoard(int page,int per_page) {

        // progress bar
        progressDialog = new ProgressDialog(LeaderBoardActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);



        RequestQueue requestQueue = Volley
                .newRequestQueue(getApplicationContext());

        JsonObjectRequest playerProfileJsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, Utility.worldRank+"?page="+page+"&per_page="+per_page+"&sort_by="+sortBy, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseJsonObject) {
                        try {

                            Log.e("world leader board",responseJsonObject.toString());

                            if(responseJsonObject.has("player_status")) {
                                if (responseJsonObject.getString("player_status").equalsIgnoreCase("logged_out")) {
                                    HandleSessionFail.HandleSessionFail(LeaderBoardActivity.this);

                                }
                            }

                            if (responseJsonObject.getBoolean("status")) {
                                // array of json object




                                JSONArray playerJsonObject = responseJsonObject.getJSONArray("players");

                                //GameService.players = new ArrayList<Player>();
                                //parsing and adding of player object in list
                                for (int i = 0; i < playerJsonObject.length(); i++) {

                                    WorldPlayer player = new WorldPlayer();

                                    JSONObject playerJSON = (JSONObject) playerJsonObject
                                            .getJSONObject(i);

                                    player.jsonParser(playerJSON);

                                    playerList.add(player);

                                    Log.e("current user " + Utility.user.getId()," loop user " + player.getId());
                                    /*if(Utility.user.getId() == player.getId()){
                                        currentPlayerName.setText(player.getUserName());
                                        currentPlayerRank.setText("#"+player.getPointsRank()+" Overall");
                                        currentPlayerPts.setText(""+player.getPoints());
                                    }*/
                                }

                                Log.e("world players list length",playerList.size()+"");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        leaderboardList.setFastScrollEnabled(false);
                                        leaderboardList.setSmoothScrollbarEnabled(false);
                                        leaderboardList.setOverScrollMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
                                        leaderboardList.setAdapter(new LeaderBoardList(LeaderBoardActivity.this,playerList));

                                        progressDialog.dismiss();

                                    }
                                });

                                if(currentPage != 1){
                                    leaderboardList.setSelection((currentPage*20-26));
                                }else{
                                    currentPlayerName.setText(responseJsonObject.getJSONObject("current_user").getString("username"));

                                    if(sortBy.equals("world_rank")) {
                                        currentPlayerRank.setText("#" + responseJsonObject.getJSONObject("current_user").getInt("points_rank") + " Overall");
                                        currentPlayerPts.setText("" + responseJsonObject.getJSONObject("current_user").getInt("points"));
                                        btmPtsLabel.setText("PTS");
                                    }
                                    if(sortBy.equals("plays_rank"))
                                    {

                                        currentPlayerRank.setText("#" + responseJsonObject.getJSONObject("current_user").getInt("plays_rank") + " Overall");
                                        currentPlayerPts.setText("" + responseJsonObject.getJSONObject("current_user").getInt("play_coins"));
                                        btmPtsLabel.setText("PLAYS");

                                    }
                                    if(sortBy.equals("biggest_game_won_rank"))
                                    {
                                        currentPlayerRank.setText("#" + responseJsonObject.getJSONObject("current_user").getInt("biggest_game_won_rank") + " Overall");
                                        currentPlayerPts.setText("" + responseJsonObject.getJSONObject("current_user").getInt("biggest_game_won_players_count"));
                                       btmPtsLabel.setText("PLAYERS");

                                    }
                                }

                            }
                        } catch (Exception e) {


                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "error in server call ", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                Log.e("progress dialog ","dismiss");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization",
                        Utility.getAccessKey(LeaderBoardActivity.this));
                return params;
            }
        };

        progressDialog.show();



        requestQueue.add(playerProfileJsonObjectRequest);
        requestQueue.start();
    }

    public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {


        public EndlessScrollListener() {
        }

        public EndlessScrollListener(int visibleThreshold) {
            visibleThreshold = visibleThreshold;
        }

        public EndlessScrollListener(int visibleThreshold, int startPage) {
            visibleThreshold = visibleThreshold;
            startingPageIndex = startPage;
            currentPage = startPage;
        }

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.
        @Override
        public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,int totalItemCount)
        {
            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state
            if (totalItemCount < previousTotalItemCount) {
                currentPage = startingPageIndex;
                previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) { loading = true; }
            }
            // If it’s still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
                currentPage++;
            }

            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            if (!loading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + visibleThreshold)) {
                onLoadMore(currentPage + 1, totalItemCount);
                loading = true;
            }
        }

        // Defines the process for actually loading more data based on page
        public abstract void onLoadMore(int page, int totalItemsCount);

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Don't take any action on changed
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_home:
                intent = new Intent(getApplicationContext(),
                        GameHomeScreenActivity.class);
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
            case com.ryansplayllc.ryansplay.R.id.top_bar_points:

                playerList = new ArrayList<WorldPlayer>();
                topBarPoints.setSelected(true);
                topBarAccuracy.setSelected(false);
                topBarDate.setSelected(false);
                sortBy = "world_rank";
                loadLeaderBoard(1,20);

                topBarPoints.setEnabled(false);
                topBarAccuracy.setEnabled(true);
                topBarDate.setEnabled(true);

                initialSettings();
                break;
            case com.ryansplayllc.ryansplay.R.id.top_bar_games_played:
                playerList = new ArrayList<WorldPlayer>();
                topBarAccuracy.setSelected(true);
                topBarDate.setSelected(false);
                topBarPoints.setSelected(false);
                sortBy = "plays_rank";
                loadLeaderBoard(1,20);

                topBarPoints.setEnabled(true);
                topBarAccuracy.setEnabled(false);
                topBarDate.setEnabled(true);
                initialSettings();
                break;
            case com.ryansplayllc.ryansplay.R.id.top_bar_date:
                playerList = new ArrayList<WorldPlayer>();
                topBarDate.setSelected(true);
                topBarPoints.setSelected(false);
                topBarAccuracy.setSelected(false);
                sortBy = "biggest_game_won_rank";
                loadLeaderBoard(1,20);

                topBarPoints.setEnabled(true);
                topBarAccuracy.setEnabled(true);
                topBarDate.setEnabled(false);
                initialSettings();
                break;


            case com.ryansplayllc.ryansplay.R.id.ibt_back:
                finish();
                break;
            default:
                break;
        }
    }

    public class ImageLoader implements Runnable {
        public User player;
        public ImageView imageView;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            ImageRequest imageRequest = new ImageRequest(
                    player.getProfilePicURL(), new Response.Listener<Bitmap>() {

                @Override
                public void onResponse(Bitmap bitmap) {
                   Bitmap resizedbitmap=Bitmap.createScaledBitmap(bitmap,50,50,true);
                    player.setProfilePic(resizedbitmap);

                    imageView.setImageBitmap(player.getProfilePic());
                }
            }, 0, 0, null, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                    // TODO Auto-generated method stub
                }
            });
            queue.add(imageRequest);
            queue.start();
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeScreenIntent = new Intent(LeaderBoardActivity.this,GameHomeScreenActivity.class);

        startActivity(homeScreenIntent);
        finish();
    }
}
