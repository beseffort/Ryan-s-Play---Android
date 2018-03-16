package com.ryansplayllc.ryansplay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rollbar.android.Rollbar;

public class Profile extends ActionBarActivity implements
        OnClickListener {

    private ImageButton backImageButton;
    private TextView addPlaysTextView;
    //footer variables
    private FrameLayout leaderBoardFooterButton;
    private FrameLayout profileFooterButton;
    private FrameLayout homeFooterButton;
    private FrameLayout settingsFooterButton;
    private FrameLayout playsFooterButton;

    private Button changePasswordButton;
    private Button changeInfoButton;
    // header variables
    private TextView playerNameTextView;
    private TextView noOfPlaysTextView;
    // profileselector variables
    private TextView userNameTextView;
    private TextView worldRankTextView;
    private TextView totalPlaysTextView;
    private TextView totalPointsTextView;
    private TextView playsCompletedTextView;
    private TextView homeRumTextView;
    private TextView baseHitTextView;
    private TextView strikeOutTextView;
    private TextView nameTextView;
    private TextView emailTextView;
    // private  TextView ringsTextview;
    private  TextView accuracyTextview;
    private  TextView pointsRank;
    //private  TextView ringsRank;
    private  TextView accuracyRank;
    private TextView  hitsRank;
    private   TextView homeRunRank;
    private  TextView runScoreRank;
    private  TextView gameRank;
    private  TextView playsRank;



    private ImageView profilePicImageView;
    public boolean forwardFlag = false; // for forward intent
    private ProgressDialog progressDialog;

    private TextView editButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ryansplayllc.ryansplay.R.layout.activity_my_account);

        // roll bar
        Rollbar.setIncludeLogcat(true);

        // header finding view
        playerNameTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.my_tv_player_name);
        noOfPlaysTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.home_no_of_plays);


        Bundle bundle = getIntent().getExtras();
        try {
            backImageButton = (ImageButton) findViewById(com.ryansplayllc.ryansplay.R.id.ibt_back);
            backImageButton.setOnClickListener(this);//


            Log.e("***profile page***", "before sign up");
            Boolean bool = bundle.getBoolean("sign up");

            if (bundle.getBoolean("sign up")) {
                forwardFlag = true; // making forward intent flag true
                addPlaysTextView.setText(""); // removing add plays text
                addPlaysTextView.setRotation(180);
                addPlaysTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        com.ryansplayllc.ryansplay.R.drawable.ic_back, 0); // setting forward icon
                //backImageButton.setVisibility(View.INVISIBLE);
                View footer = findViewById(com.ryansplayllc.ryansplay.R.id.my_in_footer);
                footer.setVisibility(View.GONE);
                addPlaysTextView.setOnClickListener(this);
            } else {
                addPlaysTextView.setVisibility(View.INVISIBLE);

            }
        } catch (Exception exception) {

        }

        // footer references
        leaderBoardFooterButton =    (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board);
        profileFooterButton =        (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_account);
        homeFooterButton =           (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_home);
        settingsFooterButton =       (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_settings);
        playsFooterButton =          (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_plays);

        // header references
        playerNameTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.my_tv_player_name);
        editButton=(TextView) findViewById(com.ryansplayllc.ryansplay.R.id.editbutton);



        // profile screen body references

        //references for text in the circles
        totalPlaysTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.my_tv_total_plays);
        totalPointsTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.my_tv_total_points);
        playsCompletedTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.gl_plays_for_points);
        homeRumTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.my_tv_hr);
        baseHitTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.my_tv_base_hit);
        strikeOutTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.my_tv_strike_out);
//        ringsTextview = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.profile_rings_text);
        accuracyTextview = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.profile_accuracy_text);

        //references for profile picture
        profilePicImageView = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.my_iv_profile_pic);

        //ranks
        pointsRank  =   (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.profile_points_rank);
//        ringsRank   =   (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.profile_rings_rank);
        accuracyRank=   (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.profile_accuracy_rank);
        hitsRank    =   (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.hits_rank);
        homeRunRank =   (TextView)  findViewById(com.ryansplayllc.ryansplay.R.id.home_runs_rank);
        runScoreRank=   (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.run_score_rank);
        gameRank    =   (TextView)  findViewById(com.ryansplayllc.ryansplay.R.id.game_rank);
        playsRank   =   (TextView)  findViewById(com.ryansplayllc.ryansplay.R.id.plays_rank);


        editButton.setOnClickListener(this);

        // progress settings
        progressDialog = new ProgressDialog(Profile.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);






        RequestQueue requestQueue = Volley
                .newRequestQueue(getApplicationContext());

        JsonObjectRequest playerProfileJsonObjectRequest = new JsonObjectRequest(
                Method.GET, Utility.userProfile+Utility.user.getId(), null,
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseJSONObject) {
                        try {

                            if(responseJSONObject.has("player_status")) {
                                if (responseJSONObject.getString("player_status").equalsIgnoreCase("logged_out")) {
                                    HandleSessionFail.HandleSessionFail(Profile.this);

                                }
                            }


//                            AlertDialog.Builder builder=new AlertDialog.Builder(Profile.this);
//                            builder.setMessage(responseJSONObject.toString());
//                            builder.show();

                            Log.e("profilestats",responseJSONObject.toString());
                            if (responseJSONObject.getBoolean("status")) {



                                // profileselector value setting


                                JSONObject userInfo = responseJSONObject.getJSONObject("user");
                                JSONObject playerInfo = userInfo.getJSONObject("player");

                                //sets the text of all the ranks
//                                Utility.user.getPlayer().setPointsRank(playerInfo.getString("points_rank"));
//                                Utility.user.getPlayer().setAccuracyRank(playerInfo.getString("points_rank"));
//                                Utility.user.getPlayer().setPointsRank(playerInfo.getString("points_rank"));

                                pointsRank.setText("#" + playerInfo.getString("points_rank") + " overall");
                                //ringsRank.setText("#" + playerInfo.getString("rings_count") + " overall");
                                accuracyRank.setText("#" + playerInfo.getString("biggest_game_won_rank") + " overall");
                                hitsRank.setText("#" + playerInfo.getString("basehit_rank") + " overall");
                                homeRunRank.setText("#" + playerInfo.getString("homerun_rank") + " overall");
                                runScoreRank.setText("#" + playerInfo.getString("runscore_rank") + " overall");
                                gameRank.setText("#" + playerInfo.getString("games_rank") + " overall");
                                playsRank.setText("#" + playerInfo.getString("plays_rank") + " overall");


                                //sets the counts

                                totalPointsTextView.setText(userInfo.getString("points"));
                                //ringsTextview.setText(playerInfo.getString("rings_count"));
                                accuracyTextview.setText(""+playerInfo.getString("biggest_game_won_players_count"));
                                baseHitTextView.setText(playerInfo.getString("basehit_count"));
                                homeRumTextView.setText(playerInfo.getString("homerun_count"));
                                strikeOutTextView.setText(playerInfo.getString("runscored_count"));
                                playsCompletedTextView.setText(playerInfo.getString("games_count"));
                                totalPlaysTextView.setText(userInfo.getString("play_coins"));


                                updateUI();
                                progressDialog.dismiss();

                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            progressDialog.dismiss();
                            e.printStackTrace();
//                            AlertDialog.Builder builder=new AlertDialog.Builder(Profile.this);
//                            builder.setMessage(e.toString());
//                            builder.show();


                        }
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(),"error in server call " ,Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization",
                        Utility.getAccessKey(Profile.this));
                return params;
            }
        };

        progressDialog.show();



        requestQueue.add(playerProfileJsonObjectRequest);
        requestQueue.start();


        // image request

        RequestQueue imageRequestQueue = Volley
                .newRequestQueue(getApplicationContext());
        ImageRequest imageRequest = new ImageRequest(
                Utility.user.getProfilePicURL(), new Listener<Bitmap>() {

            @Override
            public void onResponse(Bitmap bitmap) {
                Utility.user.setProfilePic(bitmap);
                profilePicImageView.setImageBitmap(Utility.user
                        .getProfilePic());
                Utility.saveProfilePicLocal(getApplicationContext(),
                        bitmap);
                // image prgressbar
                findViewById(com.ryansplayllc.ryansplay.R.id.loadingPanel)
                        .setVisibility(View.GONE);
            }
        }, 0, 0, null, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub

            }
        });

//        changePasswordButton.setOnClickListener(this);
//       changeInfoButton.setOnClickListener(this);

        // footer click listeners
        leaderBoardFooterButton.setOnClickListener(this);
        profileFooterButton.setOnClickListener(this);
        homeFooterButton.setOnClickListener(this);
        settingsFooterButton.setOnClickListener(this);
        playsFooterButton.setOnClickListener(this);

        // Setting selected state
        profileFooterButton.setSelected(true);

        // bitmap Operation
        File cacheDir = getBaseContext().getCacheDir();
        File f = new File(cacheDir, "profile_pic");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            Utility.user.setProfilePic(bitmap);
            // image prgressbar
            findViewById(com.ryansplayllc.ryansplay.R.id.loadingPanel).setVisibility(View.GONE);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            // image prgressbar
            findViewById(com.ryansplayllc.ryansplay.R.id.loadingPanel).setVisibility(View.GONE);
            imageRequestQueue.add(imageRequest);
            imageRequestQueue.start();



        }

    }

    private void updateUI() {

//        Toast.makeText(getApplicationContext(),"updateUI",Toast.LENGTH_SHORT).show();
        playerNameTextView.setText(Utility.user.getUserName());
        // noOfPlaysTextView.setText(Integer.toString(Utility.user.getPlayer()
        //         .getNoOfPlays()));
        // userNameTextView.setText(Utility.user.getUserName());
//        worldRankTextView.setText(Integer.toString(Utility.user.getPlayer()
//                .getWorldRank()));
        /*totalPlaysTextView.setText(Integer.toString(Utility.user.getPlayer()
                .getPlayCoins() ));
        totalPointsTextView.setText(Integer.toString(Utility.user.getPlayer()
                .getTotalPoints()));
        playsCompletedTextView.setText(Integer.toString(Utility.user
                .getPlayer().getPlaysCompleted()));
        homeRumTextView.setText(Integer.toString(Utility.user.getPlayer()
                .getHr()));
        baseHitTextView.setText(Integer.toString(Utility.user.getPlayer()
                .getBasehits()));
        strikeOutTextView.setText(Integer.toString(Utility.user.getPlayer()
                .getStrikeout()));
        ringsTextview.setText(Integer.toString(Utility.user.getPlayer()
                .getRings()));
        pointsRank.setText(Utility.user.getPlayer()
                .getPointsRank());
        ringsRank.setText(Utility.user.getPlayer()
                .getRingsRank());
        accuracyRank.setText(Utility.user.getPlayer()
                .getAccuracyRank());
        hitsRank.setText(Utility.user.getPlayer()
                .getHitsRank());
        homeRunRank.setText(Utility.user.getPlayer()
                .getHomeRunsRank());*/

//        AlertDialog.Builder builder=new AlertDialog.Builder(MyAccountActivity.this);
//        builder.setMessage(Utility.user.getPlayer().getRings()+" ; "+Utility.user.getPlayer().getAccuracy());
//                        builder.show();
        // nameTextView.setText(Utility.user.getFirstName() + " "
        //        + Utility.user.getLastName());
        // emailTextView.setText(Utility.user.getEmail());
        if (Utility.user.getProfilePic() != null) {
            profilePicImageView.setImageBitmap(Utility.user.getProfilePic());
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();


        updateUI();

        // header values
        //Utility.updateHeaderUI(playerNameTextView, noOfPlaysTextView);
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
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board:
                intent = new Intent(getApplicationContext(),
                        LeaderBoardActivity.class);
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
            case com.ryansplayllc.ryansplay.R.id.editbutton :
                intent = new Intent(getApplicationContext(), ChangeProfileInfoActivity.class);
                startActivity(intent);
                finish();
                break;
            case com.ryansplayllc.ryansplay.R.id.ibt_back:
                finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
