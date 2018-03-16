package com.ryansplayllc.ryansplay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.graphics.Color;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.models.Creator;
import com.ryansplayllc.ryansplay.models.Player;
import com.ryansplayllc.ryansplay.services.GameService;

public class CreateNewGameActivity extends Activity implements
        OnClickListener {

    private Button createGameButton;
    private ImageButton backImageButton;

    private FrameLayout leaderBoardFooterButton;
    private FrameLayout profileFooterButton;
    private FrameLayout homeFooterButton;
    private FrameLayout settingsFooterButton;
    private FrameLayout playsFooterButton;
    private EditText gameNameEditText;
    private RadioGroup gameLengthRadioGroup;
    public ProgressDialog progressDialog;
    public PopupWindow noPlaysPopup;

    // header variables
    private EditText creatorNameTextView;
    //private TextView noOfPlaysTextView;

    // network
    private AlertDialog networkErrorDialog;
    private NetworkInfo networkInfo;
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ryansplayllc.ryansplay.R.layout.activity_create_new_game);

        // roll bar
        Rollbar.setIncludeLogcat(true);
        Typeface bebasfont = Typeface.createFromAsset(getAssets(), "BebasNeue Bold.ttf");


        // network
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        createGameButton = (Button) findViewById(com.ryansplayllc.ryansplay.R.id.cg_bt_create_game);

        // header finding view
        creatorNameTextView = (EditText) findViewById(com.ryansplayllc.ryansplay.R.id.game_create_tv_player_name);


        backImageButton = (ImageButton) findViewById(com.ryansplayllc.ryansplay.R.id.ibt_back);

        //
        gameNameEditText = (EditText) findViewById(com.ryansplayllc.ryansplay.R.id.cg_et_game_name);
        gameNameEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Log.d("%s", gameNameEditText.getText().toString());
                if(gameNameEditText.getText().toString()!="") {
                    createGameButton.setBackgroundResource(R.drawable.signup_bar);//"@drawable/signup_bar");
                    createGameButton.setTextColor(Color.WHITE);
                }
                if(gameNameEditText.getText().toString().trim().length() == 0)
                {
                    createGameButton.setBackgroundResource(R.drawable.button_background);//"@drawable/button_background");
                    createGameButton.setTextColor(Color.GRAY);
                }

            }
        });

        gameLengthRadioGroup = (RadioGroup) findViewById(com.ryansplayllc.ryansplay.R.id.plays);

        // footer
        leaderBoardFooterButton =    (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board);
        profileFooterButton =        (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_account);
        homeFooterButton =           (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_home);
        settingsFooterButton =       (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_settings);
        playsFooterButton =          (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_plays);


        // footer
        leaderBoardFooterButton.setOnClickListener(this);
        profileFooterButton.setOnClickListener(this);
        homeFooterButton.setOnClickListener(this);
        settingsFooterButton.setOnClickListener(this);
        playsFooterButton.setOnClickListener(this);

        backImageButton.setOnClickListener(this);

        createGameButton.setOnClickListener(this);

        // progress baat
        progressDialog = new ProgressDialog(CreateNewGameActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        //setting creator Name
        creatorNameTextView.setText(Utility.user.getUserName());

        homeFooterButton.setSelected(true);

        createGameButton.setTypeface(bebasfont);


    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            showNetworkErrorDialog();
        } else {
            //Utility.updateHeaderUI(playerNameTextView, noOfPlaysTextView);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case com.ryansplayllc.ryansplay.R.id.fo_ibt_home:
                intent = new Intent(getApplicationContext(), CreateNewGameActivity.class);
//                startActivity(intent);
//                finish();
                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board:
                intent = new Intent(getApplicationContext(),
                        LeaderBoardActivity.class);
                intent.putExtra("homestatus","creategame");

                startActivity(intent);
                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_account:
                intent = new Intent(getApplicationContext(),
                        ChangeProfileInfoActivity.class);
                intent.putExtra("homestatus","creategame");
                startActivity(intent);
                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_plays:
                intent = new Intent(getApplicationContext(), Plays.class);
                intent.putExtra("homestatus","creategame");
                startActivity(intent);
                break;
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra("homestatus","creategame");
                startActivity(intent);
                break;
            case com.ryansplayllc.ryansplay.R.id.ibt_back:
                back();
//                finish();
                break;
            case com.ryansplayllc.ryansplay.R.id.cg_bt_create_game:

                InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

                GameService.players = new ArrayList<Player>();
                String gameName = gameNameEditText.getText().toString();


                if (gameName.length() == 0) {
                    gameNameEditText.setError("Required");
                    break;
                }
                int gameLength = 0;
                if (gameLengthRadioGroup.getCheckedRadioButtonId() == com.ryansplayllc.ryansplay.R.id.game_length_10) {
                    gameLength = 5;
                } else if (gameLengthRadioGroup.getCheckedRadioButtonId() == com.ryansplayllc.ryansplay.R.id.game_length_20) {
                    gameLength = 10;
                } else if (gameLengthRadioGroup.getCheckedRadioButtonId() == com.ryansplayllc.ryansplay.R.id.game_length_30) {
                    gameLength = 20;
                }
                final  int finalGameLength=gameLength;
                Utility.game.setLength(gameLength);

                RequestQueue queue = Volley
                        .newRequestQueue(CreateNewGameActivity.this);
                JSONObject params = new JSONObject();
                try {
                    params.put("game_name", gameName);
                    params.put("no_of_plays", gameLength);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                JsonObjectRequest request = new JsonObjectRequest(Method.POST,
                        Utility.createNewGame, params, new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseJsonObject) {
                        try {

                            Log.e("create game response",responseJsonObject.toString());

                            if (responseJsonObject.getBoolean("status")) {

                                if (responseJsonObject.has("player_status")) {
                                    if (responseJsonObject.getString("player_status").equalsIgnoreCase("logged_out")) {
                                        HandleSessionFail.HandleSessionFail(CreateNewGameActivity.this);

                                    }
                                } else {

                                    JSONObject gameObject = new JSONObject();
                                    JSONObject privateGame = new JSONObject();
                                    gameObject = responseJsonObject.getJSONObject("game");
                                    privateGame = gameObject.getJSONObject("private_game");

                                    JSONObject playerJsonObject = responseJsonObject.getJSONObject("game")
                                            .getJSONObject("umpire");
                                    JSONObject creatorJsonObject = responseJsonObject.getJSONObject("game").getJSONObject("owner");

                                    Player player = new Player();
                                    Creator creator = new Creator();

                                    SharedPreferences gameObjectSp = getSharedPreferences("gameobject", MODE_PRIVATE);
                                    SharedPreferences.Editor gameObjectEditor = gameObjectSp.edit();
                                    gameObjectEditor.putString("gameobject", gameObject.toString());
                                    gameObjectEditor.commit();


                                    player.jsonParser(playerJsonObject);
                                    player.setNoOfPlays(Integer.parseInt(Utility.user.getTotalNoOfPlays()));

                                    GameService.players.add(player);


                                    Utility.game.JsonParser(responseJsonObject
                                            .getJSONObject("game"));
                                    creator.jsonParser(creatorJsonObject);


                                    SharedPreferences gameidpref = getSharedPreferences("gameid", MODE_PRIVATE);
                                    SharedPreferences.Editor gameideditor = gameidpref.edit();
                                    gameideditor.putString(Utility.user.getUserName(), Utility.game.getId() + ";" + Utility.user.getUserName());
                                    gameideditor.commit();
                                    progressDialog.dismiss();

                                    Log.e("Utility is umpire 1", "true");
                                    Utility.isUmpire = true;
                                    Intent intent = new Intent(
                                            CreateNewGameActivity.this,
                                            GameLobbyActivity.class);
                                    intent.putExtra("creator", true);
                                    intent.putExtra("gamelength", privateGame.getInt("no_of_plays") + "");
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewGameActivity.this);
                                    builder.setMessage(responseJsonObject.toString());
//                                builder.show();
                                    finish();
                                }
                            }
                            else if(Integer.parseInt(Utility.user.getTotalNoOfPlays())<finalGameLength)
                            {
                                progressDialog.dismiss();
                                showNoPlaysPopup();
                            }

                            else {
                                progressDialog.dismiss();
                                Toast.makeText(
                                        CreateNewGameActivity.this,
                                        responseJsonObject
                                                .getString("message"),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Error " + e.getMessage() + " ",Toast.LENGTH_LONG).show();
                        }

                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        progressDialog.dismiss();
/*                        Toast.makeText(CreateNewGameActivity.this,
                                "Error Occoured " + arg0.getMessage() + " " + arg0.networkResponse.statusCode, Toast.LENGTH_SHORT).show();*/
                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("Authorization",
                                Utility.getAccessKey(CreateNewGameActivity.this));
                        return params;
                    }
                };
                queue.add(request);
                queue.start();
                progressDialog.show();


                break;
            default:
                break;
        }
    }

    private void back()
    {
//        progressDialog.show();
        SharedPreferences gameid=getSharedPreferences("gameid",MODE_PRIVATE);
        String gameidValue=gameid.getString(Utility.user.getUserName(),"default");
        //        Toast.makeText(getApplicationContext(),gameidValue+"",Toast.LENGTH_SHORT).show();
        if(gameidValue.equals("default")) {
            finish();
        }
        else
        {
            leave(gameidValue.split(";")[0]);
//            finish();

        }

    }

    private void leave(String gameidValue)
    {

//        Toast.makeText(getApplicationContext(),gameidValue,Toast.LENGTH_SHORT).show();
        RequestQueue queue = Volley
                .newRequestQueue(CreateNewGameActivity.this);



        JsonObjectRequest gameListRequest = new JsonObjectRequest(Method.POST,
                "http://52.88.139.217/api//v2/private_games/"+gameidValue+"/player/leave", null, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject responseJsonObject) {
                try {

                    Log.e("create game response left",responseJsonObject.toString());

                    if(responseJsonObject.has("player_status")) {
                        if (responseJsonObject.getString("player_status").equalsIgnoreCase("logged_out")) {
                            HandleSessionFail.HandleSessionFail(CreateNewGameActivity.this);

                        }
                    }


//                    AlertDialog.Builder builder=new AlertDialog.Builder(CreateNewGameActivity.this);
//
//                        builder.setMessage(responseJsonObject.toString());builder.show();


                    if (responseJsonObject.getBoolean("status")) {

                        progressDialog.dismiss();
                        SharedPreferences gameidpref=getSharedPreferences("gameid",MODE_PRIVATE);
                        SharedPreferences.Editor gameideditor=gameidpref.edit();
                        gameideditor.putString(Utility.user.getUserName(),"default");
                        gameideditor.commit();
//                        Toast.makeText(getApplication(),"Exited from Game Successfully",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else
                    {
                        progressDialog.dismiss();
//                        Toast.makeText(getApplication(),responseJsonObject.toString(),Toast.LENGTH_SHORT).show();
                        finish();
//                        builder.setMessage(responseJsonObject.toString());
//                        builder.show();
                    }
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplication(),"Server Error Occured in Leaving Game",Toast.LENGTH_SHORT).show();
                    finish();
                    e.printStackTrace();
                }
                progressDialog.dismiss();

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                progressDialog.dismiss();

                AlertDialog.Builder builder=new AlertDialog.Builder(CreateNewGameActivity.this);
                builder.setMessage(arg0.networkResponse.statusCode+"");
                builder.show();
                finish();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization",
                        Utility.getAccessKey(CreateNewGameActivity.this));
                return params;
            }

        };

        queue.add(gameListRequest);
        queue.start();
    }

    // dialog
    private void showNetworkErrorDialog() {
        AlertDialog.Builder networkErrorBuilder = new AlertDialog.Builder(
                CreateNewGameActivity.this);
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
                        finish();
                    }
                });
        networkErrorDialog = networkErrorBuilder.create();
        networkErrorDialog.show();
    }

    @Override
    public void onBackPressed() {
        back();
    }


         public void showNoPlaysPopup()
          {

            InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            LayoutInflater layoutInflater  = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);

            final View popupView = layoutInflater.inflate(R.layout.no_plays_popup, null);
            noPlaysPopup = new PopupWindow(popupView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            noPlaysPopup.showAtLocation(popupView, Gravity.CENTER,0,0);

            ImageView noPlaysPopupCloseIcon = (ImageView) popupView.findViewById(R.id.noplayspopup_close);
            noPlaysPopupCloseIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    noPlaysPopup.dismiss();
                }
            });


            Button addPlays=(Button) popupView.findViewById(R.id.noplayspopup_addPlays);
            addPlays.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent playsIntent =new Intent(CreateNewGameActivity.this,Plays.class);
                    finish();
                    startActivity(playsIntent);
                }
            });

        }


}
