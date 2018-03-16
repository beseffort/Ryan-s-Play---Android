package com.ryansplayllc.ryansplay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
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
import com.ryansplayllc.ryansplay.adapters.JoinPrivateGame2List;
import com.ryansplayllc.ryansplay.models.Game;
import com.ryansplayllc.ryansplay.models.GameList;
import com.ryansplayllc.ryansplay.models.Player;
import com.ryansplayllc.ryansplay.models.SoftKeyboardHandledRelativeLayout;
import com.ryansplayllc.ryansplay.receivers.GameReceiver;
import com.ryansplayllc.ryansplay.services.GameService;

public class JoinPrivateGame2 extends Activity implements OnClickListener {

    //initialising colors list
    private List<String> leftImageColorsList;
    private List<String> rightImageColorsList;

    private ListView gameListView;
    private TextView searchGameIcon;
    private Button searchGameButton;

    private RelativeLayout searchGameLayout;
    private EditText searchGameEditText;
    private RelativeLayout findGameButton;
    private ImageView searchGameCloseIcon;

    private Button allGamesButton;
    private Button popularGameButton;

    private ImageButton backImageButton;

    //footer variables
    public static FrameLayout leaderBoardFooterButton;
    public static FrameLayout profileFooterButton;
    public static FrameLayout homeFooterButton;
    public static FrameLayout settingsFooterButton;
    public static FrameLayout playsFooterButton;
    public static RelativeLayout joinGameFooterLayout;

    private ImageView refershImageButton;
    private RadioGroup umpireWillingRadioGroup;

    public static  SoftKeyboardHandledRelativeLayout mainView;

    // game list
    public static List<Game> gameList;

    // game info popup
    private PopupWindow popupWindow;
    private PopupWindow searchGamepopupWindow;
    private View gameInfolayout;
    private int popUpPosition;
    private ImageView popUpUmpireImageView;
    private int gameListCode = 1; // for updating image in STATIC game list.

    // game info
    private List<Game> showingGame;

    // progress dialog
    public static ProgressDialog progressDialog;

    private String listType = "all";
    private int page = 5;
    private int gameListStart = 0;
    private int gameListEnd = 20;
    private int selectedPostion = -1;
    private boolean isNewAvilable = true;
    private GameReceiver gameReceiver;
    public TextView topSelectGameLabel;

    private TextView dateText;
    // header variables
    private TextView playerNameTextView;
    private TextView noOfPlaysTextView;

    //search game popup variables
    private EditText searchGameName;

    // network
    private AlertDialog networkErrorDialog;
    private NetworkInfo networkInfo;
    private ConnectivityManager connectivityManager;

    //umpirewilling status
    private String umpireWilling;
    private PopupWindow noPlaysPopup;

    public String getUmpireWilling() {
        return umpireWilling;
    }

    public void setUmpireWilling(String umpireWilling) {
        this.umpireWilling = umpireWilling;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.ryansplayllc.ryansplay.R.layout.activity_join_game2);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        GameService.players = new ArrayList<Player>();
        Typeface bebasfont = Typeface.createFromAsset(getAssets(), "BebasNeue Bold.ttf");

        // roll bar
        Rollbar.setIncludeLogcat(true);

        //initial
        page = 1;
        gameListStart = 0;
        gameListEnd = 5;


        listType = "all";
        getGameList(listType);
        gameList = new ArrayList<Game>();

        // network
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        gameListView = (ListView) findViewById(com.ryansplayllc.ryansplay.R.id.joingame2_playerslst_view);
        searchGameIcon = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.joingame2_searchicon);
        searchGameButton = (Button) findViewById(com.ryansplayllc.ryansplay.R.id.joingame2_searchbutton);
        searchGameLayout = (RelativeLayout) findViewById(R.id.search_game_layout);
        searchGameEditText = (EditText) findViewById(R.id.SearchGameNameText);
        findGameButton = (RelativeLayout) findViewById(R.id.find_game_button);
        searchGameCloseIcon = (ImageView) findViewById(R.id.search_game_popup_close);
        topSelectGameLabel = (TextView) findViewById(R.id.select_game_info_label);

        dateText = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.imageView);


        Date currentDate = new Date();
        String dateString = currentDate.toString();

        SimpleDateFormat format = new SimpleDateFormat("EEE MMMM dd yyyy");

        try {
            Date parsed = format.parse(dateString);
            StringBuilder setDate = new StringBuilder(format.format(currentDate));
            setDate.setCharAt(setDate.length() - 5, ',');
            dateText.setText(setDate);
            Log.e(" search private game 3. ", format.format(currentDate));
        } catch (Exception e) {

        }
        Log.e("search private game", format + "");
//        allGamesButton = (Button) findViewById(R.id.join_game_list_bt_all_games);
//        popularGameButton = (Button) findViewById(R.id.join_game_list_bt_popular_games);

        // header finding view
//        playerNameTextView = (TextView) findViewById(R.id.my_tv_player_name);
//        noOfPlaysTextView = (TextView) findViewById(R.id.home_no_of_plays);

        // progress dialog
        progressDialog = new ProgressDialog(JoinPrivateGame2.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        // umpire willing radio button

        backImageButton = (ImageButton) findViewById(com.ryansplayllc.ryansplay.R.id.ibt_back);

        refershImageButton = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.joingame_refreshbutton);

        // footer
        leaderBoardFooterButton = (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board);
        profileFooterButton = (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_account);
        homeFooterButton = (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_home);
        settingsFooterButton = (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_settings);
        playsFooterButton = (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_plays);
        joinGameFooterLayout = (RelativeLayout) findViewById(R.id.joingame_footer_layout);

        //home is selected in footer default
        homeFooterButton.setSelected(true);

        // footer click listeners
        leaderBoardFooterButton.setOnClickListener(this);
        profileFooterButton.setOnClickListener(this);
        homeFooterButton.setOnClickListener(this);
        settingsFooterButton.setOnClickListener(this);
        playsFooterButton.setOnClickListener(this);

        //header icons click listners
        backImageButton.setOnClickListener(this);
        refershImageButton.setOnClickListener(this);
        searchGameIcon.setOnClickListener(this);
        searchGameButton.setOnClickListener(this);


        searchGameButton.setTypeface(bebasfont);

        gameList = new ArrayList<Game>();


        leftImageColorsList = new ArrayList<>();
        rightImageColorsList = new ArrayList<>();


        leftImageColorsList.add("#e81f4b");
        leftImageColorsList.add("#c42038");
        leftImageColorsList.add("#ed6535");
        leftImageColorsList.add("#09328c");
        leftImageColorsList.add("#df4801");
        leftImageColorsList.add("#022f6a");
        leftImageColorsList.add("#004485");
        leftImageColorsList.add("#dd3237");
        leftImageColorsList.add("#d01645");

        rightImageColorsList.add("#2f2b2c");
        rightImageColorsList.add("#013476");
        rightImageColorsList.add("#d11844");
        rightImageColorsList.add("#fdb900");
        rightImageColorsList.add("#0c80b3");
        rightImageColorsList.add("#004377");
        rightImageColorsList.add("#fd7d00");
        rightImageColorsList.add("#1a4d84");
        rightImageColorsList.add("#004996");


        gameListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectedPostion = gameListStart + position;
                showPopup(position);
            }
        });

        populateGameList();

        // broadcast receiver

        IntentFilter filter = new IntentFilter(GameReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        gameReceiver = new GameReceiver();
        registerReceiver(gameReceiver, filter);


        if (gameList.size() > 0) {
            searchGameIcon.setVisibility(View.VISIBLE);
            searchGameButton.setVisibility(View.GONE);
            searchGameButton.bringToFront();

        } else {

            searchGameIcon.setVisibility(View.GONE);
            searchGameButton.setVisibility(View.VISIBLE);
            searchGameButton.bringToFront();
        }


        searchGameEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGameFooterLayout.setVisibility(View.GONE);
            }
        });

         mainView = (SoftKeyboardHandledRelativeLayout) findViewById(R.id.join_game_content_layout);
        mainView.setOnSoftKeyboardVisibilityChangeListener(
                new SoftKeyboardHandledRelativeLayout.SoftKeyboardVisibilityChangeListener() {
                    @Override
                    public void onSoftKeyboardShow() {

                        joinGameFooterLayout.setVisibility(View.GONE);

                    }


                    @Override
                    public void onSoftKeyboardHide() {
                        joinGameFooterLayout.setVisibility(View.VISIBLE);

                    }
                });
    }


    private void showPopup(int postion) {

        LayoutInflater inflater = (LayoutInflater) JoinPrivateGame2.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        gameInfolayout = inflater.inflate(com.ryansplayllc.ryansplay.R.layout.joingame_popup,
                (ViewGroup) findViewById(com.ryansplayllc.ryansplay.R.id.joingame_popup));


        // game info views
//        ImageView umpireImageView = (ImageView) gameInfolayout
//                .findViewById(R.id.game_details_iv_umpire_profile_pic);
        TextView gameNameTextView = (TextView) gameInfolayout
                .findViewById(com.ryansplayllc.ryansplay.R.id.umpire_hit_region);
        TextView gameCreatorTextView = (TextView) gameInfolayout
                .findViewById(com.ryansplayllc.ryansplay.R.id.joingame_popup_creator);
//        TextView umpireNameTextView = (TextView) gameInfolayout
//                .findViewById(R.id.game_details_tv_umpire_name);
        TextView gameLengthTextView = (TextView) gameInfolayout
                .findViewById(com.ryansplayllc.ryansplay.R.id.joingame_popup_game_length);
        TextView noOfPlayersTextView = (TextView) gameInfolayout
                .findViewById(com.ryansplayllc.ryansplay.R.id.joingame_popup_game_length);

        // game info values
        if (selectedPostion > -1 && gameList.size() > 0) {
            //Toast.makeText(getApplicationContext(),"selected position " + selectedPostion+"",Toast.LENGTH_SHORT).show();
            Game game = gameList.get(selectedPostion);

            // umpire image
//            if (game.getUmpire().getUmpireImage() != null) {
//                umpireImageView.setImageBitmap(game.getUmpire().getUmpireImage());
//            } else {
//                gameInfolayout.findViewById(R.id.loadingPanel).setVisibility(
//                        View.VISIBLE);
//                popUpPosition = postion - 1;
//                popUpUmpireImageView = umpireImageView;
//                getumpireImage();
//            }


            // other game info
            gameCreatorTextView.setText(game.getGameCreator().getUsername());
            gameNameTextView.setText(game.getGameName());
//            umpireNameTextView.setText(game.getUmpire().getUserName());
            gameLengthTextView.setText(Integer.toString(game.getLength()));
            noOfPlayersTextView.setText(Integer.toString(game.getLength()) + " Play Duration");

            // showing popup
            popupWindow = new PopupWindow(gameInfolayout,
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
            popupWindow.showAtLocation(gameInfolayout, Gravity.CENTER, 0, 0);
            popupWindow.update();


        } else {
            Toast.makeText(getApplicationContext(), "select Player to Join", Toast.LENGTH_SHORT).show();
        }

    }

    public void popupClose(View view) {


        popupWindow.dismiss();
        enableFooter();
    }

    public void join(View view) {
        try {

            umpireWillingRadioGroup = (RadioGroup) popupWindow.getContentView().findViewById(com.ryansplayllc.ryansplay.R.id.joinstatus_radio);
            switch (umpireWillingRadioGroup.getCheckedRadioButtonId()) {
                case com.ryansplayllc.ryansplay.R.id.joingame_popup_radio_yes:
                    //                Toast.makeText(getApplicationContext(),"willing",Toast.LENGTH_SHORT);
                    setUmpireWilling("willing");
                    break;
                case com.ryansplayllc.ryansplay.R.id.joingame_popup_radio_no:
                    //                Toast.makeText(getApplicationContext(),"unwilling",Toast.LENGTH_SHORT);
                    setUmpireWilling("unwilling");
                    break;
                default:
                    umpireWilling = "unwilling";
                    break;
            }


            //        progressDialog.show();


            umpireWilling = getUmpireWilling();
            Game game = gameList.get(selectedPostion);
            Intent gameService = new Intent(getApplicationContext(),
                    GameService.class);
            gameService.putExtra("action", "join");
            gameService.putExtra("game_id", game.getId());
            Utility.game = game;
            gameService.putExtra("umpireWilling", umpireWilling);


            this.startService(gameService);
            //
            popupWindow.dismiss();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "selected position " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
//        progressDialog.show();
    }


    public void radioTextYesOnClick(View view) {
        Toast.makeText(getBaseContext(), "radioyes", Toast.LENGTH_LONG).show();
        RadioButton radioButtonYes = (RadioButton) popupWindow.getContentView().findViewById(R.id.joingame_popup_radio_yes);

        radioButtonYes.setChecked(true);
        RadioButton radioButtonNo = (RadioButton) popupWindow.getContentView().findViewById(R.id.joingame_popup_radio_no);
        radioButtonNo.setChecked(false);
        Log.e("radio yes", "1");


    }

    public void radioTextNoOnClick(View view) {
        Toast.makeText(getBaseContext(), "radiono", Toast.LENGTH_LONG).show();

        RadioButton radioButtonYes = (RadioButton) view.findViewById(R.id.joingame_popup_radio_yes);
        radioButtonYes.setChecked(false);
        RadioButton radioButtonNo = (RadioButton) view.findViewById(R.id.joingame_popup_radio_no);
        radioButtonNo.setChecked(true);
        Log.e("radio no", "1");

    }

    private void getumpireImage() {
        ImageRequest imageRequest = new ImageRequest(gameList
                .get(popUpPosition).getUmpire().getUmpireURL(),
                new Listener<Bitmap>() {

                    @Override
                    public void onResponse(Bitmap bitmap) {
                        // TODO Auto-generated method stub
                        gameList.get(popUpPosition).getUmpire()
                                .setUmpireImage(bitmap);
                        popUpUmpireImageView.setImageBitmap(bitmap);
                        gameInfolayout.findViewById(com.ryansplayllc.ryansplay.R.id.loadingPanel)
                                .setVisibility(View.GONE);
                    }
                }, 0, 0, null, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }

        });
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(imageRequest);
        queue.start();
    }

    private void getGameList(String listType) {
        RequestQueue queue = Volley.newRequestQueue(JoinPrivateGame2.this);
        Builder url = Uri.parse(Utility.gameList).buildUpon();
        url.appendQueryParameter("game_type", "public_game");
        url.appendQueryParameter("list_type", listType);
        url.appendQueryParameter("page", Integer.toString(page++));
        try {
            JsonObjectRequest gameListRequest = new JsonObjectRequest(Method.GET,
                    url.toString(), null, new Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject responseJsonObject) {
                    try {
//                         AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(JoinGameListActivity.this);
                        if (responseJsonObject.has("player_status")) {
                            if (responseJsonObject.getString("player_status").equalsIgnoreCase("logged_out")) {
                                HandleSessionFail.HandleSessionFail(JoinPrivateGame2.this);

                            }
                        }


                        if (responseJsonObject.getBoolean("status")) {
                            JSONArray gameArray;

                            gameArray = responseJsonObject
                                    .getJSONArray("games");
//                             dialogbuilder.setMessage(gameArray.toString());
//                             dialogbuilder.show();
                            if (gameArray.length() != 0) {
                                for (int i = 0; i < gameArray.length(); i++) {

                                    try {
                                        Game game = new Game();
                                        game.JsonParser((JSONObject) gameArray
                                                .get(i));
                                        gameList.add(game);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            populateGameList();
                        } else {
                            if (responseJsonObject.get("message").equals(
                                    "No Game found")) {
                                isNewAvilable = false;
                            }
                        }
                        progressDialog.dismiss();
                    } catch (JSONException e1) {
                        progressDialog.dismiss();
                        e1.printStackTrace();
                    }
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError arg0) {
                    progressDialog.dismiss();
                }
            })

            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("Authorization",
                            Utility.getAccessKey(JoinPrivateGame2.this));
                    return params;
                }

            };

            queue.add(gameListRequest);
            queue.start();
//            progressDialog.show();
        } catch (Exception e) {
            Log.e("Error at Join GAme List Acttivity gamelist()", e.toString() + "");
        }
    }

    private void populateGameList() {
        if (gameList.size() == 0) {
            getGameList(listType);
            return;
        }
        while (gameListEnd - gameListStart != 5) {
            gameListEnd += 1;
        }

        showingGame = new ArrayList<Game>();
        if (gameListStart < 0) {
            gameListStart = 0;
            gameListEnd = 5;
        }
        if (gameListEnd > gameList.size()) {

            if (isNewAvilable && gameListEnd >= gameList.size() + 5) {
                getGameList(listType);
                return;
            } else {
                if ((gameListEnd + 5) < gameList.size()) {
                    gameListEnd += 5;
                } else {
                    gameListEnd = gameList.size();
                    while (gameListStart > gameListEnd) {
                        gameListStart -= 5;
                    }
                }
                Toast.makeText(getApplicationContext(), "No More games found",
                        Toast.LENGTH_SHORT).show();
            }
        }

        if (gameList.size() > 0) {
            for (int i = gameListStart; i < gameListEnd; i++) {

                showingGame.add(gameList.get(i));
            }
        }

        if (!progressDialog.isShowing()) {
//            progressDialog.show();
        }
        if (gameList.size() > 0) {
            searchGameIcon.setVisibility(View.VISIBLE);
            searchGameButton.setVisibility(View.GONE);
            searchGameButton.bringToFront();

        } else {
            Toast.makeText(getApplicationContext(), "size=0", Toast.LENGTH_SHORT).show();
            searchGameIcon.setVisibility(View.GONE);
            searchGameButton.setVisibility(View.VISIBLE);
            searchGameButton.bringToFront();
        }

        JoinPrivateGame2List gameListAdapter = new JoinPrivateGame2List(showingGame,
                JoinPrivateGame2.this, gameListStart, gameListCode, leftImageColorsList, rightImageColorsList);
        gameListAdapter.notifyDataSetChanged();
        gameListView.setAdapter(gameListAdapter);
        gameListView.setDividerHeight(1);
        Utility.setListViewHeightBasedOnChildren(gameListView);
        progressDialog.dismiss();
    }

    public void listViewNext(View view) {
        gameListStart += 5;
        gameListEnd += 5;
        populateGameList();
    }

    public void listViewBack(View view) {
        gameListStart -= 5;
        gameListEnd -= 5;
        populateGameList();

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
                finish();
                break;


            case com.ryansplayllc.ryansplay.R.id.joingame2_searchicon:
                showSearchGamePopup();
                break;
            case com.ryansplayllc.ryansplay.R.id.joingame2_searchbutton:
                showSearchGamePopup();
                break;


            case com.ryansplayllc.ryansplay.R.id.joingame_refreshbutton:
                page = 1;
                gameListStart = 0;
                gameListEnd = 5;
                getGameList(listType);
                gameList = new ArrayList<Game>();
                break;


            default:
                break;
        }
    }

    private void showSearchGamePopup() {


        //disable footer options
        disableFooter();

        searchGameButton.setVisibility(View.GONE);
        searchGameIcon.setVisibility(View.GONE);

        searchGameButton.setVisibility(View.GONE);

        searchGameLayout.setVisibility(View.VISIBLE);


        searchGameLayout.bringToFront();
        findGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                searchGame();

            }
        });
        searchGameCloseIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout searchGameLabelLayout = (RelativeLayout) findViewById(R.id.searchgame_labels_layout);
                RelativeLayout statusLayout = (RelativeLayout) findViewById(R.id.searchgame_status_layout);

                searchGameLayout.setVisibility(View.GONE);
                if (gameList.size() == 0) {
                    searchGameButton.setVisibility(View.VISIBLE);
                    searchGameIcon.setVisibility(View.GONE);
                } else {
                    searchGameButton.setVisibility(View.GONE);
                    searchGameIcon.setVisibility(View.VISIBLE);
                }

                InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(searchGameEditText.getWindowToken(), 0);
                joinGameFooterLayout.setVisibility(View.VISIBLE);
                searchGameEditText.setText(" ");
                statusLayout.setVisibility(View.GONE);
                statusLayout.clearAnimation();
                searchGameLabelLayout.clearAnimation();
                //disable footer options
                enableFooter();


            }
        });


    }


    public void searchGamePopupCLose(View view) {

        if (gameList.size() == 0) {
            searchGameButton.setVisibility(View.VISIBLE);
            searchGameButton.bringToFront();
            searchGameIcon.setVisibility(View.GONE);

        } else {
            searchGameIcon.setVisibility(View.VISIBLE);
        }
        searchGamepopupWindow.dismiss();
    }

    public void searchGame() {
        RelativeLayout searchGameLabelLayout = (RelativeLayout) findViewById(R.id.searchgame_labels_layout);
        RelativeLayout statusLayout = (RelativeLayout) findViewById(R.id.searchgame_status_layout);


        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(searchGameEditText.getWindowToken(), 0);

        joinGameFooterLayout.setVisibility(View.VISIBLE);

        String creatorName = searchGameEditText.getText().toString().trim();

//        Toast.makeText(getApplicationContext(),creatorName,Toast.LENGTH_SHORT).show();
        if (creatorName.length() == 0) {
            showSearchgameStatus("Enter creator name");

        } else if (creatorName.equalsIgnoreCase(Utility.user.getUserName())) {
            progressDialog.dismiss();
            String statusMessage = "Cannot search your own username";
            showSearchgameStatus(statusMessage);
        } else {
            statusLayout.setVisibility(View.GONE);
            searchGameLabelLayout.clearAnimation();
            statusLayout.clearAnimation();

            gameSearchResult(creatorName);
        }
    }


    public void gameSearchResult(String creator) {
        searchGameIcon.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley
                .newRequestQueue(JoinPrivateGame2.this);

        Builder url = Uri.parse(Utility.searchGame + creator).buildUpon();


        JSONObject params = new JSONObject();
        try {
            params.put("creator_username", creator);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest gameListRequest = new JsonObjectRequest(Method.POST,
                Utility.searchGame, params, new Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject responseJsonObject) {
                try {

                    Log.e("join game response", responseJsonObject.toString());


                    AlertDialog.Builder builder = new AlertDialog.Builder(JoinPrivateGame2.this);
                    builder.setMessage(responseJsonObject.toString());
//                    builder.show();


                    if (responseJsonObject.getBoolean("status")) {

                        if (responseJsonObject.has("player_status")) {
                            if (responseJsonObject.getString("player_status").equalsIgnoreCase("logged_out")) {
                                HandleSessionFail.HandleSessionFail(JoinPrivateGame2.this);

                            }
                        }
                        else {
                            searchGameLayout.setVisibility(View.GONE);
                            topSelectGameLabel.setVisibility(View.VISIBLE);

                            JSONArray gameArray;
                            JSONObject gameObject = new JSONObject();
                            JSONObject playerObject = new JSONObject();


                            gameObject = responseJsonObject.getJSONObject("game");
                            JSONObject privateGame = gameObject.getJSONObject("private_game");
                            playerObject = gameObject.getJSONObject("owner");

                            Player player = new Player();
                            player.setPlayerId(playerObject.getInt("id"));
                            player.setUsername(playerObject.getString("username"));


                            Game game = new Game();
                            game.setId(gameObject.getInt("id"));
                            game.setGameName(gameObject.getString("name"));
                            game.setLength(privateGame.getInt("no_of_plays"));

                            game.setGameCreator(player);


                            GameList gameList1 = new GameList();
                            gameList1.add(game);

                            JoinPrivateGame2.gameList = gameList1;


                            ArrayAdapter adapter = new SearchGameAdapter(gameList1);
                            gameListView.setAdapter(adapter);
                            gameListView.setDividerHeight(1);


                        }
                    }
                        else{

                            builder.setTitle("Error");
                            builder.setMessage(responseJsonObject.getString("message"));
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
//                        builder.show();
                            showSearchgameStatus(responseJsonObject.getString("message"));
                        }
                    }
                catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "some error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                progressDialog.dismiss();
                searchGamepopupWindow.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinPrivateGame2.this);
                //builder.setMessage(arg0.networkResponse.statusCode+"");
                //builder.show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization",
                        Utility.getAccessKey(JoinPrivateGame2.this));
                return params;
            }

        };

        queue.add(gameListRequest);
        queue.start();
        progressDialog.show();
    }


    class SearchGameAdapter extends ArrayAdapter<Game> {
        GameList gamelist;

        public SearchGameAdapter(GameList game) {
            super(JoinPrivateGame2.this, com.ryansplayllc.ryansplay.R.layout.joingame2_playerslist_item, game);
            gamelist = game;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View myView = convertView;
            if (myView == null) {
                //
                myView = (View) getLayoutInflater().inflate(com.ryansplayllc.ryansplay.R.layout.joingame2_playerslist_item, parent, false);
            }
            Game currentGame = gamelist.get(position);
            TextView im = (TextView) myView.findViewById(com.ryansplayllc.ryansplay.R.id.joingame_gamename);
            im.setText(currentGame.getGameName() + "");

            Game game = gameList.get(0);


            return myView;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
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
//            Utility.updateHeaderUI(playerNameTextView, noOfPlaysTextView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(gameReceiver);
    }

    // dialog
    private void showNetworkErrorDialog() {
        AlertDialog.Builder networkErrorBuilder = new AlertDialog.Builder(
                JoinPrivateGame2.this);
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


    public void disableFooter() {
        //disabling footer options
        homeFooterButton.setEnabled(false);
        playsFooterButton.setEnabled(false);
        leaderBoardFooterButton.setEnabled(false);
        settingsFooterButton.setEnabled(false);
        profileFooterButton.setEnabled(false);
    }




    public void showSearchgameStatus(String statusMessage) {
        RelativeLayout searchGameLabelLayout = (RelativeLayout) findViewById(R.id.searchgame_labels_layout);
        RelativeLayout statusLayout = (RelativeLayout) findViewById(R.id.searchgame_status_layout);
        TextView statusText = (TextView) findViewById(R.id.searchgame_status_text);

//            loadin slideup animation
        Animation slideup = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.search_game_popup_slideup);
        Animation fadeAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.home_promo_fadeanim);

        searchGameLabelLayout.clearAnimation();
        statusLayout.clearAnimation();

        statusText.setText(statusMessage);

        slideup.setFillAfter(true);
        fadeAnim.setFillAfter(true);

        searchGameLabelLayout.startAnimation(slideup);
        statusLayout.startAnimation(fadeAnim);
        statusLayout.setVisibility(View.VISIBLE);
        statusLayout.bringToFront();
    }

    public static void enableFooter()
    {
        JoinPrivateGame2.joinGameFooterLayout.bringToFront();
        JoinPrivateGame2.homeFooterButton.setEnabled(true);
        JoinPrivateGame2.leaderBoardFooterButton.setEnabled(true);
        JoinPrivateGame2.playsFooterButton.setEnabled(true);
        JoinPrivateGame2.profileFooterButton.setEnabled(true);
        JoinPrivateGame2.settingsFooterButton.setEnabled(true);
    }


    public  static void mainBringToFront()
    {
        mainView.bringToFront();
    }
}