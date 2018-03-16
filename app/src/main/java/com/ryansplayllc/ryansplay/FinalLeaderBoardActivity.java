package com.ryansplayllc.ryansplay;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
//import com.facebook.Request;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.model.GraphUser;
//import com.facebook.widget.WebDialog;
import com.facebook.*;
//import com.facebook.login.LoginClient;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.ryansplayllc.ryansplay.adapters.GameLeaderBoardListNew;
import android.view.View.OnClickListener;

import com.ryansplayllc.ryansplay.models.Creator;
import com.ryansplayllc.ryansplay.models.Player;
import com.ryansplayllc.ryansplay.models.User;
import com.ryansplayllc.ryansplay.services.GameService;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bolts.AppLinks;
import br.net.bmobile.websocketrails.WebSocketRailsDataCallback;
import io.fabric.sdk.android.Fabric;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;


public class FinalLeaderBoardActivity extends ActionBarActivity implements OnClickListener {

    ListView gameleaderboardList;
    TextView creatorName,gamename, gameLength,playerName, playerTotalPlays,playerPoints,playerRank;
    ImageView back, userProfileImage;
    LinearLayout footer;
    private Button ReturntoHomeButton;

    //footer variables
    private FrameLayout leaderBoardFooterButton,profileFooterButton,homeFooterButton,settingsFooterButton,playsFooterButton;

    //social share options
    private TextView fbShareText,twitterShareText;

    private static List<String> permissions;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(com.ryansplayllc.ryansplay.R.layout.game_leader_board);
        Fabric.with(this, new TweetComposer());

        final Typeface bebasfont = Typeface.createFromAsset(getAssets(), "BebasNeue Bold.ttf");
        callbackManager = CallbackManager.Factory.create();


        // footer
        leaderBoardFooterButton =    (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board);
        profileFooterButton =        (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_account);
        homeFooterButton =           (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_home);
        settingsFooterButton =       (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_settings);
        playsFooterButton =          (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_plays);


        footer = (LinearLayout) findViewById(com.ryansplayllc.ryansplay.R.id.footer);
        footer.bringToFront();


        gamename = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.gl_game_name);
        creatorName = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.gl_username);
        gameLength = (TextView) findViewById(R.id.fgl_game_length);
        playerName = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.my_tv_player_name);
        playerTotalPlays = (TextView) findViewById(R.id.fgl_player_total_plays);
        playerPoints = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.playerPoints);
        playerRank =(TextView) findViewById(com.ryansplayllc.ryansplay.R.id.playerRank);
        userProfileImage = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.creatorProfileImage);
        ReturntoHomeButton = (Button) findViewById(R.id.lb_returntohome);

        fbShareText  = (TextView)  findViewById(R.id.fl_fbshare_text);
        twitterShareText = (TextView) findViewById(R.id.fl_twiittershare_text);

        back = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.ibt_back);

        back.setVisibility(View.INVISIBLE);
        // footer click listeners
        leaderBoardFooterButton.setOnClickListener(this);
        profileFooterButton.setOnClickListener(this);
        homeFooterButton.setOnClickListener(this);
        settingsFooterButton.setOnClickListener(this);
        playsFooterButton.setOnClickListener(this);

        homeFooterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent  intent = new Intent(FinalLeaderBoardActivity.this,
                        GameHomeScreenActivity.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                Utility.enterdGameLobbyStatus = false;
                Intent i = new Intent(FinalLeaderBoardActivity.this, GameHomeScreenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });


        ReturntoHomeButton.setTypeface(bebasfont);
        ReturntoHomeButton.setOnClickListener(this);

        /***** FB Permissions *****/

        permissions = new ArrayList<String>();
        permissions.add("email");
        permissions.add("user_location");
        permissions.add("user_birthday");


        //sharing result with facebook

        //creating share dialog
        shareDialog = new ShareDialog(this);

        fbShareText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    //logging into facebook
                    LoginManager.getInstance().logInWithReadPermissions(FinalLeaderBoardActivity.this, Arrays.asList("user_birthday", "email", "public_profile", "user_friends", "user_location"));
                }
                    catch (Exception e)
                    {
                  Log.e("fbshare exception",e.toString());
                }
            }
        });


        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onSuccess(LoginResult loginResult)
            {

               Log.e("loginresult",loginResult.getRecentlyGrantedPermissions().toString()+"recently denied"+loginResult.getRecentlyDeniedPermissions());

               if (ShareDialog.canShow(ShareLinkContent.class))
                {

                    Uri targetUrl =AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), getIntent());

                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("I got "+Utility.user.getLastGamePoints()+" points playing RP Baseball. Download the game today. #RPBaseball @ryansplay")
                    .setContentDescription("Rank " + "#" + Utility.user.getUserRank())
                    .setImageUrl(Uri.parse("http://52.88.139.217/images/rpa_fb_invite_banner.jpg"))
                    .setContentUrl(Uri.parse("https://fb.me/1656445941266582"))
                    .build();

                    shareDialog.show(linkContent);
//                    Toast.makeText(getApplicationContext(),"Posted on your wall Successfully",Toast.LENGTH_SHORT).show();
//                    ShareApi.share(linkContent, null);

                }


                if(!Utility.user.getProvoider().equals("facebook"))
                {
                    LoginManager.getInstance().logOut();
               }

            }

            @Override
            public void onCancel() {
                if(!Utility.user.getProvoider().equals("facebook"))
                    LoginManager.getInstance().logOut();
            }

            @Override
            public void onError(FacebookException e)
            {

            }
        } );


        //sharing result with twitter
        twitterShareText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    TweetComposer.Builder builder = new TweetComposer.Builder(FinalLeaderBoardActivity.this)
                            .text("I got "+Utility.user.getLastGamePoints()+" points playing RP Baseball. Download the game today. #RPBaseball @ryansplay")
                            .image(getLocalBitmapUri())
                            .url(new URL("https://play.google.com/apps/testing/com.ryansplayllc.ryansplay"))
                            ;
                    builder.show();


                    File file = new File(getLocalBitmapUri()+"");
                             file.delete();

                }
                catch (Exception e)
                {

                }
            }
        });


        try {
            gameleaderboardList = (ListView) findViewById(com.ryansplayllc.ryansplay.R.id.glb_ll_leader_board);

            gamename.setText(Utility.gameName);
            creatorName.setText(Creator.getUserName());
            gameLength.setText(Utility.user.getLastGamePlays() + " Plays");
            playerRank.setText("#" + Utility.user.getUserRank());
            playerTotalPlays.setText((Integer.parseInt(Utility.user.getTotalNoOfPlays()) - (Integer.parseInt(Utility.user.getLastGamePlays()))) + "");
            playerName.setText(Utility.user.getUserName());
            playerPoints.setText(Utility.user.getLastGamePoints());


            Creator creator = new Creator();


            //loading current player profile image
            Log.e("userimageurl", Utility.user.getProfilePicURL() + "");
            if (Utility.user.getProfilePic() != null) {
                userProfileImage.setImageBitmap(Utility.user.getProfilePic());
            } else {
                ImageLoader imageLoader = new ImageLoader();
                imageLoader.imageView = userProfileImage;
                imageLoader.user = Utility.user;
                imageLoader.run();
            }


            showGameLeaderBoard();
        }
        catch (Exception e)
        {

        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Save current session
        super.onSaveInstanceState(outState);

    }

    /********** Activity Methods **********/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }






    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.lb_returntohome:
                finish();
                Utility.enterdGameLobbyStatus = false;
                Intent i = new Intent(FinalLeaderBoardActivity.this, GameHomeScreenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_home:

                intent = new Intent(FinalLeaderBoardActivity.this,
                        GameHomeScreenActivity.class);
                startActivity(intent);
                finish();
                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board:
                intent = new Intent(FinalLeaderBoardActivity.this,
                        LeaderBoardActivity.class);
                startActivity(intent);
                finish();
                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_account:
                intent = new Intent(FinalLeaderBoardActivity.this,
                        ChangeProfileInfoActivity.class);
                startActivity(intent);
                finish();
                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_plays:
                intent = new Intent(FinalLeaderBoardActivity.this, Plays.class);
                startActivity(intent);
                finish();
                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_settings:
                intent = new Intent(FinalLeaderBoardActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
    }


    public class ImageLoader implements Runnable {
        public User user;
        public ImageView imageView;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            ImageRequest imageRequest = new ImageRequest(
                    user.getProfilePicURL(), new Response.Listener<Bitmap>() {

                @Override
                public void onResponse(Bitmap bitmap) {
                    user.setProfilePic(bitmap);
                    imageView.setImageBitmap(user.getProfilePic());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.ryansplayllc.ryansplay.R.menu.menu_game_leader_board_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.ryansplayllc.ryansplay.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showGameLeaderBoard(){
        long gameId = Utility.game.getId();
        Log.e("final leader board ",Utility.game.getId()+"");
        if(gameId == 0)
            gameId = GameService.staticGameId;

        LeaderBoardInput leaderBoardInput = new LeaderBoardInput();
        leaderBoardInput.setGame_id(gameId);
        leaderBoardInput.setPer_page(50);
        leaderBoardInput.setPage(1);


        GameService.dispatcher.trigger("private_game.get_leaderboard",
                leaderBoardInput, new WebSocketRailsDataCallback() {

                    @Override
                    public void onDataAvailable(Object data) {


                        // TODO Auto-generated method stub
                        try {

                            List<Player> playersList = new ArrayList<Player>();

                            Log.e("final leader board1 ", data.toString());

                            JSONObject playersObject = new JSONObject(data.toString());
                            JSONArray playersArray = playersObject.getJSONArray("players");

                            SharedPreferences useridpref= getSharedPreferences("userid",MODE_PRIVATE);
                            Integer userId = Integer.parseInt(useridpref.getString("userid","0"));

                            for(int i=0;i<playersArray.length();i++){
                                Player player = new Player();
                                player.jsonParser((JSONObject) playersArray.get(i));

                                playersList.add(player);
                            }

                            final List<Player> playersListtemp =playersList;
                            //   playersList;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

//stuff that updates ui
                                    gameleaderboardList.setAdapter( new GameLeaderBoardListNew(FinalLeaderBoardActivity.this,playersListtemp));
                                    gameleaderboardList.setDividerHeight(1);

                                }
                            });

                            Log.e("--player size after gameleaderboardList.add ",playersList.size()+"");


                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            // Toast.makeText(getApplicationContext(),"error data " + data.toString(),Toast.LENGTH_LONG).show();
                            Log.e("--player size IN showGameLeaderBoard()",e.getMessage()+"");
                            Log.e("final leader board1 catch ", data.toString());
                            e.printStackTrace();
                        }

                    }
                }, new WebSocketRailsDataCallback() {

                    @Override
                    public void onDataAvailable(Object data) {
                        // TODO Auto-generated method stub
                        //progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "error data " + data.toString(), Toast.LENGTH_LONG).show();
                        Log.e("private_game.publish_result ","success " + data.toString());
                        Log.e("final leader board1 err resp ", data.toString());
                    }
                });
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

            leave(gameidValue);

//            finish();

        }
    }


    private void leave(final String gameidValue) {

        RequestQueue queue = Volley
                .newRequestQueue(FinalLeaderBoardActivity.this);


        JsonObjectRequest gameListRequest = new JsonObjectRequest(Request.Method.POST,
                "http://52.88.139.217/api//v2/private_games/" + gameidValue + "/player/leave", null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject responseJsonObject) {
                try {

                    if (responseJsonObject.getBoolean("status")) {


                        SharedPreferences gameidpref = getSharedPreferences("gameid", MODE_PRIVATE);
                        SharedPreferences.Editor gameideditor = gameidpref.edit();
                        gameideditor.putString(Utility.user.getUserName(), "default");
                        gameideditor.commit();
                        Toast.makeText(getApplication(), "Left the game successfully", Toast.LENGTH_SHORT).show();



                    } else {

                        Toast.makeText(getApplication(), responseJsonObject.toString(), Toast.LENGTH_SHORT).show();



                    }
                } catch (JSONException e) {

                    Toast.makeText(getApplication(), "Server Error Occured in Leaving Game", Toast.LENGTH_SHORT).show();
//                    finish();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {


                AlertDialog.Builder builder = new AlertDialog.Builder(FinalLeaderBoardActivity.this);
                builder.setMessage("Error Response" +gameidValue+ "");
//                builder.show();
                finish();


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization",
                        Utility.getAccessKey(FinalLeaderBoardActivity.this));
                return params;
            }

        };

        queue.add(gameListRequest);
        queue.start();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Uri getLocalBitmapUri() {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = getResources().getDrawable(R.drawable.rp_icon3);
        Bitmap bmp = null;
//        bmp =Utility.user.getProfilePic();

        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) getResources().getDrawable(R.drawable.rp_icon3)).getBitmap();        } else {
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
    public void onBackPressed() {
        finish();
        Utility.enterdGameLobbyStatus = false;
        Intent i = new Intent(FinalLeaderBoardActivity.this, GameHomeScreenActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void uploadPic(File file, String message,Twitter twitter) throws Exception  {
        try{
            StatusUpdate status = new StatusUpdate(message);
            status.setMedia(file);
            twitter.updateStatus(status);}
        catch(TwitterException e){
            Log.d("TAG", "Pic Upload error" + e+"");
            throw e;
        }
    }

//        - See more at: http://www.theappguruz.com/blog/share-image-and-text-on-twitter-in-android-using-native-code#sthash.57mgSZKI.dpuf


        }

