package com.ryansplayllc.ryansplay;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.adapters.UmpireChangeList;
import com.ryansplayllc.ryansplay.models.Umpire;
import com.ryansplayllc.ryansplay.services.GameService;

import br.net.bmobile.websocketrails.WebSocketRailsDataCallback;

public class ChangeUmpireActivity extends Activity implements OnClickListener {

	// back button
	private ImageButton backImageButton;

	// footer variables
    private FrameLayout homeFooterButton;
    private FrameLayout leaderBoardFooterButton;
    private FrameLayout profileFooterButton;
    private FrameLayout playsFooterButton;
    private FrameLayout settingsFooterButton;

	//confirm change umpire
	private RelativeLayout selectedUmpireDetails,noPlayersLayout,playerListContainer;
	private TextView switchTo,confimation,newUmpireName;
	private ImageView closeButton;
	private Button changeConfirmation,returnLobbyEmpty;
	private TextView currentGameName;

	// player list view
	public ListView playerListView;

	// players list
	public List<Umpire> umpires;

	// header variables
	private TextView creatorNameTextView;
	private TextView chUmpireGameLength;

	// progress dialog
	public static ProgressDialog progressDialog;

	// change umpire
	private Button changeUmpireButton;

	//change umpire views
	private  TextView changeUmpireStatustext;
	private  ImageView changeUmpireStausImage;
	private  RelativeLayout changeUmpireStatusLayout;
	private  RelativeLayout playersListLayout;


	// selected position
	int selectedPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.ryansplayllc.ryansplay.R.layout.activity_change_umpire);
		Typeface bebasfont = Typeface.createFromAsset(getAssets(), "BebasNeue Bold.ttf");

		// roll bar
		Rollbar.setIncludeLogcat(true);



		//confirm change umpire
		playerListContainer = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.playerListContainer);
		noPlayersLayout = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.noPlayersLayout);
		selectedUmpireDetails = (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.selectedUmpireDetails);
		switchTo = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.switchTo);
		confimation = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.confirmation);
		newUmpireName = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.newUmpireName);
		changeConfirmation = (Button) findViewById(com.ryansplayllc.ryansplay.R.id.change_confirmation);
		returnLobbyEmpty = (Button) findViewById(com.ryansplayllc.ryansplay.R.id.returnLobbyEmpty);

		returnLobbyEmpty.setTypeface(bebasfont);
		returnLobbyEmpty.setOnClickListener(this);

		closeButton = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.confirm_popup_close_button);
		currentGameName = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.my_tv_game_name);




		changeConfirmation.setOnClickListener(this);
		changeConfirmation.setTypeface(bebasfont);

		closeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectedUmpireDetails.setVisibility(View.GONE);
                playerListView.setEnabled(true);
			}
		});

		selectedUmpireDetails.setVisibility(View.GONE);
		// header finding view
		creatorNameTextView = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.ch_umpire_creatorname);
		chUmpireGameLength = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.ch_umpire_game_length);


        creatorNameTextView.setText(GameLobbyActivity.creatorName);
		currentGameName.setText(Utility.game.getGameName());
		chUmpireGameLength.setText(Utility.game.getLength() + " Plays");

		// player list view
		playerListView = (ListView) findViewById(com.ryansplayllc.ryansplay.R.id.change_umpire_lv_player_list);

		// footer
        leaderBoardFooterButton =   (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board);
        profileFooterButton =       (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_account);
        playsFooterButton =         (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_plays);
        settingsFooterButton =      (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_settings);
        homeFooterButton =          (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_home);
                // footer
        leaderBoardFooterButton.setOnClickListener(this);
        profileFooterButton.setOnClickListener(this);
        playsFooterButton.setOnClickListener(this);
        settingsFooterButton.setOnClickListener(this);
        homeFooterButton.setOnClickListener(this);

		// back button
		backImageButton = (ImageButton) findViewById(com.ryansplayllc.ryansplay.R.id.ch_umpire_back_button);
        findViewById(R.id.ibt_back).setVisibility(View.GONE);

		// change button
		changeUmpireButton = (Button) findViewById(com.ryansplayllc.ryansplay.R.id.ch_umpire_return_gameLobby);

		//change umpire status views
		changeUmpireStatustext = (TextView) findViewById(com.ryansplayllc.ryansplay.R.id.ch_umpire_status_messagetext);
		changeUmpireStausImage = (ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.ch_umpire_status_image);
		changeUmpireStatusLayout= (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.ch_umpire_status_layout);
		playersListLayout =       (RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.change_umpire_list_layout);


		// back button
		backImageButton.setOnClickListener(this);

		// change button
		changeUmpireButton.setOnClickListener(this);
		changeUmpireButton.setTypeface(bebasfont);


		// progress bar
		progressDialog = new ProgressDialog(ChangeUmpireActivity.this);
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(false);

		// player request
		umpires = new ArrayList<Umpire>();
		populatePlayerList();


            Toast.makeText(getApplicationContext(),"Select a new umpire",Toast.LENGTH_SHORT).show();


		playerListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				selectedPosition = position;
                if(selectedPosition>-1)
                {
                    changeUmpireButton.setAlpha(1);
                }

				try{
                    playerListView.setEnabled(false);
					Umpire umpire = umpires.get(selectedPosition);
					newUmpireName.setText(umpire.getUserName());
					selectedUmpireDetails.setVisibility(View.VISIBLE);
					selectedUmpireDetails.bringToFront();
				}catch (Exception e){
					Log.e("umpire change activity error",e.getMessage());
				}
			}
		});

	}

	private void toggleContainers(){
		if(umpires.size() >= 1 ) {
			noPlayersLayout.setVisibility(View.GONE);
			playerListContainer.setVisibility(View.VISIBLE);
			changeUmpireButton.setVisibility(View.VISIBLE);
		}else{
			noPlayersLayout.setVisibility(View.VISIBLE);
			playerListContainer.setVisibility(View.GONE);
			changeUmpireButton.setVisibility(View.GONE);
		}
	}
	//input that was passed as  a bean object to get the umpire willing list
	public class UmpireWillingListInput{

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

	private void populatePlayerList() {

		RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

		// params
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("page", "1");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		UmpireWillingListInput umpireWillingListInput = new UmpireWillingListInput();
		umpireWillingListInput.setGame_id(Utility.game.getId());
		umpireWillingListInput.setPer_page(50);
		umpireWillingListInput.setPage(1);

		GameService.dispatcher.trigger("private_game.get_willing_umpires",
				umpireWillingListInput, new WebSocketRailsDataCallback() {

					@Override
					public void onDataAvailable(Object data) {

						try {
							Log.e("umpire willing list  arraylist",data.toString()+"");

							JSONObject responseJsonObject = new JSONObject(data.toString());
							if (responseJsonObject.getBoolean("status")) {
								// array of json object
								JSONArray umpireJsonObject = responseJsonObject
										.getJSONArray("players");

								// parsing and adding of player object in list
								for (int i = 0; i < umpireJsonObject.length(); i++) {
									Umpire umpire = new Umpire();
									JSONObject umpireJSON = (JSONObject) umpireJsonObject
											.get(i);
									umpire.jsonParser(umpireJSON);
									umpires.add(umpire);
								}


								final List<Umpire> finalUmpires = umpires;
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										playerListView.setAdapter(new UmpireChangeList(
												finalUmpires, ChangeUmpireActivity.this ,Utility.game.getUmpire().getUserName()));
										playerListView.setDividerHeight(1);
										Utility.setListViewHeightBasedOnChildren(playerListView);
										progressDialog.dismiss();

										toggleContainers();
									}
								});

							} else {
								playerListView.setVisibility(View.GONE);
								changeUmpireButton.setVisibility(View.GONE);
								findViewById(com.ryansplayllc.ryansplay.R.id.change_umpire_ll_no_umpire)
										.setVisibility(View.VISIBLE);
							}
							progressDialog.dismiss();

						}
						catch (Exception e)
						{
							Log.e("umpire willing list in catch", "after trigger in onDataAvailable trigegr" +e.getMessage());

						}
					}
				},
				new WebSocketRailsDataCallback() {

					@Override
					public void onDataAvailable(Object data) {
						// TODO Auto-generated method stub
						progressDialog.dismiss();
						Toast.makeText(getApplicationContext(),"error data " + data.toString(),Toast.LENGTH_LONG).show();
						Log.e("umpire willing list  error" , "after trigger in onDataAvailable trigegr");                    }
				});


	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
            case com.ryansplayllc.ryansplay.R.id.fo_ibt_home:
                intent = new Intent(getApplicationContext(),
                        GameHomeScreenActivity.class);
                startActivity(intent);
                finish();
                break;
			case com.ryansplayllc.ryansplay.R.id.change_confirmation:
				changeUmpire();
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
		case com.ryansplayllc.ryansplay.R.id.ch_umpire_back_button:
			finish();
			break;
		case com.ryansplayllc.ryansplay.R.id.ch_umpire_return_gameLobby:
			finish();
			break;
		case com.ryansplayllc.ryansplay.R.id.returnLobbyEmpty:
			finish();
			break;
		default:
			break;
		}
	}

	private void setSelectable() {
		for (int i = 0; i < umpires.size(); i++) {
			if (!umpires.get(i).getStatus()) {
			}
		}
	}

	//input that was passed as  a bean object to get the new umpire
	public class NewUmpire{

		private long game_id;
		private long new_umpire_id;
		//private int per_page;

		public void setGame_id(long game_id){
			this.game_id = game_id;
		}
		public long getGame_id(){
			return this.game_id;
		}


		public long getNew_umpire_id(){
			return new_umpire_id;
		}
		public void setNew_umpire_id(long new_umpire_id){
			this.new_umpire_id = new_umpire_id;
		}
	}

	private void changeUmpire() {
		playerListView.setOnItemClickListener(null);
		selectedUmpireDetails.setVisibility(View.GONE);


        //RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        //JSONObject params = new JSONObject();
        if (selectedPosition > -1) {
			RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

			// params
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("page", "1");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			NewUmpire newUmpire = new NewUmpire();
			newUmpire.setGame_id(Utility.game.getId());
			newUmpire.setNew_umpire_id(umpires.get(selectedPosition).getId());
			//newUmpire.setPage(1);

			GameService.dispatcher.trigger("private_game.change_umpire",
					newUmpire, new WebSocketRailsDataCallback() {

						@Override
						public void onDataAvailable(Object data) {


							try {


                                Utility.game.getUmpire().setId(umpires.get(selectedPosition).getId());


								final JSONObject statusJson= new JSONObject(data.toString());

								Log.e("umpire change list  arraylist", data.toString() + "");

								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										try {
											Animation slidedown = AnimationUtils.loadAnimation(getApplicationContext(), com.ryansplayllc.ryansplay.R.anim.anim_slidedown);

											changeUmpireStatusLayout.setVisibility(View.VISIBLE);
											changeUmpireStatustext.setTextColor(Color.GREEN);


											Log.e("umpire message = ",newUmpireName + "is now umpire.");
											changeUmpireStatustext.setText(newUmpireName.getText() + " is now umpire.");
											changeUmpireStatustext.setTextColor(Color.parseColor("#FF779824"));
											playersListLayout.startAnimation(slidedown);
											slidedown.setFillAfter(true);

											playerListView.setAdapter(new UmpireChangeList(
													umpires, ChangeUmpireActivity.this,Utility.game.getUmpire().getUserName()));
											playerListView.setDividerHeight(1);

                                            Log.e("umpire change list  arraylist old umpire id --> ",Utility.user.getId() +" new umpire id---> " + umpires.get(selectedPosition).getId());
										} catch (Exception e) {
											Log.e("change umpire error", e.toString());
										}
									}
								});







							}
							catch (Exception e)
							{
								Log.e("umpire willing list in catch", "after trigger in onDataAvailable trigegr" +e.getMessage());

							}
						}
					},
					new WebSocketRailsDataCallback() {

						@Override
						public void onDataAvailable(Object data) {
							// TODO Auto-generated method stub
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(),"error data " + data.toString(),Toast.LENGTH_LONG).show();
							Log.e("umpire willing list  error" , "after trigger in onDataAvailable trigegr");                    }
					});

		}else{
			Toast.makeText(getApplicationContext(),"Select a new umpire",Toast.LENGTH_SHORT).show();
		}

	}


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
