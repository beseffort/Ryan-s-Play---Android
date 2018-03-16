package com.ryansplayllc.ryansplay;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;
import android.widget.AbsListView.LayoutParams;

import org.json.JSONObject;

import play.prediction.PredictionAndResult;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ryansplayllc.ryansplay.GameScreenActivity.Prediction;
import com.ryansplayllc.ryansplay.models.Creator;
import com.ryansplayllc.ryansplay.models.DisconnectedGame;
import com.ryansplayllc.ryansplay.models.Game;
import com.ryansplayllc.ryansplay.models.User;

public class Utility {

    //push notifications keys and numbers
    public static String projectNumber = "598239096189";
    public static String uuid = "";
    public static String pushToken = "";
    public static String osType = "android";
    public static boolean isNotificationsEnable =true;
    // roll bar
    public static String rollbarKey = "9cff681bb8054be692a2c0f27a47b98a";
    public static String rollbarEnviroinment = "production";

    public static boolean fbSignUpStatus = false;
    public static boolean fbSignUpStatus1 = false;
    public static boolean signUpStatus=false;

    public static User user = new User();
    public static Game game = new Game();
    public static Creator creator = new Creator();

    public static DisconnectedGame disconnectedGame = new DisconnectedGame();
    public static DisconnectedGame playingGame = new DisconnectedGame();
    // game finished flag
    public static boolean isGameFinished = false;

    //game status
    public static String gameStatus = "";

    public static String gameName = "";
    //sms body
    public static String smsBody1 = "Download RP Baseball with this link Ryansplay.com/app At sign up, enter promocode: ";
    public static String smsBody2 = " to get 10 free  plays for you and me. Search for game creator: ";
    public static String smsBody3 = " to join my game";
    public static String smsBody4 = " https://play.google.com/apps/testing/com.ryansplayllc.ryansplay";
    //
    //public static final String baseURL =  "http://52.88.139.217/api/";
    //public static final String socketBaseURL = "http://52.88.139.217:3001/websocket";
    public static final String baseURL =  "http://rpbalance.com/api/";
    public static final String socketBaseURL = "http://rpbalance.com:3001/websocket";

    // user operations

    public static final String loginURL = baseURL+"/v2/session/login";
    public static String signUpURL =baseURL+"/v2/registration/normal";
    public static final String fbSignUpURL =baseURL+"/v2/registration/facebook";
    public static final String logoutURL = baseURL+"/v2/session/logout";
    public static final String userProfile = baseURL+"/v2/players/public_profile?user_id=";
    public static final String privateProfile=baseURL+"/v2/players/private_profile";
    public static final String playerProfile = baseURL+"/v2/user/player/show";
    public static final String updateUserProfile = baseURL+"/v2/players/edit_profile_information";
    public static final String updateUserPlays = baseURL+"/v2/players/update_plays";
    public static final String uploadPhoto =baseURL+"/v2/players/upload_profile_picture";
    public static final String forgotPassword = baseURL+ "/v2/registration/forgot_password";
    public static final String resetPassword =baseURL+"/v2/players/change_password";

    //promo code
    public static final String redeemPromoCode = baseURL+"/v2/players/promo_code/redeem_promo_code";
    public static final String signUpPromoCode = baseURL+"/v2/players/promo_code/signup_promo_code";

    //world rank
    public static final String worldRank=baseURL+"/v2/players/world_leaderboard";

    //current or disconnect games
    public static final String connectOrDisconnect = baseURL+"/v2/players/current_or_disconnected_game";

    // game list url
    public static final String createNewGame =baseURL+"/v2/private_games/create";
    public static final String gameList = baseURL+"/v2/games/index";

    // game url
    public static final String gamePlayerList =baseURL+"/v2/game/";
    public static final String playforfiet = baseURL+"/v2/game/";
    public static final String leaderBoard = baseURL+"/v2/game/";
    public static final String possibleUmpireList = baseURL+"/v2/game/";
    public static final String changeUmpire =baseURL+"/v2/game/";
    public static  final  String joinGame = baseURL+"/v2/private_games/{game_id}/player/join";
    public static final String searchGame=baseURL+"/v2/private_games/search?creator_username=";


    // player URL
    public static final String playerPublicProfile =baseURL+"/v2/players/public_profile";


    // preference
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    public static final String PREFS_NAME = "ryansPlayPreff";
    public static final String PREFS_KEY_AUTHORIZATION_KEY = "Authorization_key";
    public static final String PREFS_KEY_USER_ID = "USER_ID";
    public static final String PREFS_KEY_USER_PLAYS = "PLAYS";

    // broadcast receiver
    public static final String UMPIRE_CHANGE_ACTION_RESP = "com.ryansplayllc.ryansplay.intent.action.UMPIRE_CHANGE";

    // game play
    public static boolean isUmpire = false;
    public static int gamePoint = 0;

    // result calculation
    private static final ArrayList<String> LEFT_POSITION = new ArrayList<String>(
            Arrays.asList("B3", "SS", "LF"));
    private static final ArrayList<String> CENTER_POSITION = new ArrayList<String>(
            Arrays.asList("C", "P", "CF"));
    private static final ArrayList<String> RIGHT_POSITION = new ArrayList<String>(
            Arrays.asList("B1", "B2", "RF"));
    private int points = 0;
    private String playerRegion;
    private String resultRegion;

    public static String promoCode = "ABCD";
    public static boolean enterdGameLobbyStatus = false;

    // player prediction values
    public static SparseArray<String> postionValues = new SparseArray<String>();

    static {
        postionValues.put(com.ryansplayllc.ryansplay.R.id.game_screen_textView_1b, "B1");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.game_screen_textView_2b, "B2");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.game_screen_textView_3b, "B3");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.game_screen_textView_p, "P");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.game_screen_textView_c, "C");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.game_screen_textView_ss, "SS");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.game_screen_textView_lf, "LF");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.game_screen_textView_cf, "CF");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.game_screen_textView_rf, "RF");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.game_screen_imageView_swh, "SWH");
    }

    // player prediction values
    public static SparseArray<String> umpirePostionValues = new SparseArray<String>();

    static {
        postionValues.put(com.ryansplayllc.ryansplay.R.id.umpire_imageView_3b, "B1");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.umpire_imageView_3b, "B2");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.umpire_imageView_2b, "B3");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.umpire_imageView_p, "P");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.umpire_imageView_c, "C");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.umpire_imageView_ss, "SS");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.umpire_imageView_lf, "LF");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.umpire_imageView_cf, "CF");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.umpire_imageView_rf, "RF");
        postionValues.put(com.ryansplayllc.ryansplay.R.id.umpire_imageView_swh, "SWH");
    }

    // METHODS------------------
    // for list view dynamic height
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            listView.setVisibility(View.GONE);
            return;
        }
        int listWidth = listView.getMeasuredWidth();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            listItem.measure(
                    MeasureSpec.makeMeasureSpec(listWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            // listItem.measure(listWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static void saveProfilePicLocal(Context context, Bitmap bitmap) {
        File cacheDir = context.getCacheDir();
        File f = new File(cacheDir, "profile_pic");

        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // clearing cache
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static void updateHeaderUI(TextView playerNameTextView,
                                      TextView noOfPlaysTextView) {
        playerNameTextView.setText(Utility.user.getUserName()+"");
        //noOfPlaysTextView.setText(Integer.toString(Utility.user.getPlayer().getNoOfPlays()));

    }

    public static long getUserId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Utility.PREFS_NAME, Context.MODE_PRIVATE);
        long userId = preferences.getLong(Utility.PREFS_KEY_USER_ID, -1L);
        return userId;
    }

    public static void setUserId(Context context, long userId) {
        preferences = context.getSharedPreferences(Utility.PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putLong(Utility.PREFS_KEY_USER_ID, userId);
        editor.commit();

    }
    public static int getUserPlays(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Utility.PREFS_NAME, Context.MODE_PRIVATE);
        int plays = preferences.getInt(Utility.PREFS_KEY_USER_PLAYS, -1);
        return plays;
    }

    public static void setUserPlays(Context context, int plays) {
        preferences = context.getSharedPreferences(Utility.PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putInt(Utility.PREFS_KEY_USER_PLAYS, plays);
        editor.commit();
    }
    public static String getAccessKey(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                Utility.PREFS_NAME, Context.MODE_PRIVATE);
        String accessKey = preferences.getString(
                Utility.PREFS_KEY_AUTHORIZATION_KEY, "");
        return accessKey;
    }

    public static void setAccessKey(Context context, String accessKey) {
        preferences = context.getSharedPreferences(Utility.PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(Utility.PREFS_KEY_AUTHORIZATION_KEY, accessKey);
        editor.commit();

    }


    //show the guesses
    public static String showPlayerGuess(JSONObject prediction)

    {

        String selectedPosition= "";

        try{
            boolean isHomeRun = prediction.getBoolean("homeRun");
            boolean isRunScore = prediction.getBoolean("runScored");
            boolean isBaseHit = prediction.getBoolean("baseHit");

            String position = prediction.getString("position");

            switch (position) {


                case "SO":

                    selectedPosition = "Strike Out";
                    break;

                case "WLK":

                    selectedPosition = "Walk";
                    break;


                case "HBP":

                    selectedPosition = "Hit By Pitch";
                    break;

                case "B1":

                    position = "1B";
                    break;


                case "B2":

                    position = "2B";
                    break;


                case "B3":

                    position = "3B";
                    break;


                default:
                    break;
            }


            if (isHomeRun) {

                selectedPosition = "Home Run! ";

            }


            if (!position.equals("SO") && !position.equals("WLK") && !position.equals("HBP"))

            {
                if(position.equals("SWH")){
                    selectedPosition += "Strike Out, Walk, or Hit By Pitch";
                }else if (isBaseHit) {
                          if( isHomeRun) {
                              if(position.equals("NA")){
                                  selectedPosition += "Base hit";

                              }
                              else
                              selectedPosition += "Base hit " + position;
                          }
                          else
                               selectedPosition += "Base hit to " + position;

                } else

                {

                    selectedPosition += "Ball hit to " + position;

                }

            } else if (isBaseHit) {

                selectedPosition += "base hit";

            }

            if(isRunScore){

                selectedPosition += ", Run Scored ";

            }






        }catch (Exception e){

        }



        return selectedPosition;



    }

    // game play points calculations---------------

    public int calculatePoints(JSONObject resultJsonObject,
                               Prediction playerPrediction) {
        points = 0;
        PredictionAndResult result = new PredictionAndResult();
        result.jsonParser(resultJsonObject);

        // points based on position

        if(playerPrediction.getPosition().equalsIgnoreCase("NA"))
        {
            points = 0;
        }

        else if (result.getPostion().equals(playerPrediction.getPosition())) {
            // same position
            points += 25;
        }
        else {
            playerRegion = getRegion(playerPrediction.getPosition());
            resultRegion = getRegion(result.getPostion());
            if (playerRegion.equals(resultRegion) && !playerPrediction.getPosition().equals("SWH")) {
                // if both are same region
                points += 20;
            }
            else {

                if (isAdjecent()) {
                    points += 10;
                } else  if(isAwayZone()){
                    points += 5;
                }
            }
        }


        Log.e("calculate points 1",points+"");
        //Check if user predicted SWH and result is one of the three SWH options
        if (playerPrediction.getPosition().equals("SWH")) {
            if(result.getPostion().equals("SO") || result.getPostion().equals("WLK") || result.getPostion().equals("HBP")) {
                points += 25;
            }else if(result.getPostion() != "" && !result.isBaseHit()){
                points += 3;
            }
            else if(result.getPostion() != "" && result.isBaseHit()){
                points += 1;
            }
        }else if((result.getPostion().equals("SO") || result.getPostion().equals("WLK") || result.getPostion().equals("HBP")) && !playerPrediction.getPosition().equals("NA")){
               points += 3;
        }


        Log.e("calculate points 2",points+"");
        //checking SWH and player out
        /*if (playerPrediction.getPosition().equals("SWH") && !result.isBaseHit()) {
            points += 1;
        }*/

        Log.e("calculate points 3",points+"");
        // points for basehit
        if (playerPrediction.isBasehit()) {
            // if player prediction selected basehit
            if (result.isBaseHit()) {
                // if result also basehit
                points += 25;
            } else {
                // if result is not basehit
                points -= 2;
            }
        }

        Log.e("calculate points 4",points+"");
        // points homeselector run
        if (playerPrediction.isHomerun()) {
            // if player prediction selected homeselector run
            if (result.isHomeRun()) {
                // if result also homeselector run
                points += 50;
            } else {
                // if result is not homeselector run
                points -= 2;
            }
        }

        Log.e("calculate points 5",points+"");
        // points run scored
        if (playerPrediction.isRunscored()) {
            // if player prediction selected run scored
            if (result.isRunScored()) {
                // if result also run scored
                points += 20;
            } else {
                // if result is not run scored
                points -= 2;
            }
        }

        Log.e("calculate points 6",points+"");
        return points;
    }

    private String getRegion(String position) {
        String region = "";
        if (LEFT_POSITION.contains(position)) {
            return region = "left";
        } else if (CENTER_POSITION.contains(position)) {
            return region = "center";
        } else if (RIGHT_POSITION.contains(position)) {
            return region = "right";
        }
        return position;
    }

    //if player selection is adjacent

    private boolean isAdjecent() {
        if ((playerRegion.equals("left") && resultRegion.equals("center"))
                || (playerRegion.equals("right") && resultRegion
                .equals("center")) || (playerRegion.equals("center") && resultRegion.equals("left")) || (playerRegion.equals("center") && resultRegion.equals("right"))) {
            return true;
        }
        return false;
    }

    private boolean isAwayZone(){
        if(playerRegion.equals("left") && resultRegion.equals("right")){
            return true;
        }else if(playerRegion.equals("right") && resultRegion.equals("left")){
            return true;
        }
        return false;
    }

    public static void clearGame() {
        isUmpire = false;
        game = new Game();
        gamePoint = 0;
        GameScreenActivity.currentPlay=1;
        Log.e("Utility.java  -","Game cleared");

    }
}
