package com.ryansplayllc.ryansplay;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.ryansplayllc.ryansplay.ChangeProfileInfoActivity;
import com.ryansplayllc.ryansplay.GameHomeScreenActivity;
import com.ryansplayllc.ryansplay.Plays;
import com.ryansplayllc.ryansplay.R;


public class Faq extends ActionBarActivity implements View.OnClickListener {

    private ImageButton backImageButton;
    //footer variables
    private FrameLayout leaderBoardFooterButton;
    private FrameLayout profileFooterButton;
    private FrameLayout homeFooterButton;
    private FrameLayout settingsFooterButton;
    private FrameLayout playsFooterButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        backImageButton = (ImageButton) findViewById(R.id.ibt_back);

        // footer
        leaderBoardFooterButton =    (FrameLayout) findViewById(R.id.fo_ibt_leader_board);
        profileFooterButton =        (FrameLayout) findViewById(R.id.fo_ibt_account);
        homeFooterButton =           (FrameLayout) findViewById(R.id.fo_ibt_home);
        playsFooterButton =          (FrameLayout) findViewById(R.id.fo_ibt_plays);
        settingsFooterButton =        (FrameLayout) findViewById(R.id.fo_ibt_settings);

        // footer click listeners
        leaderBoardFooterButton.setOnClickListener(this);
        profileFooterButton.setOnClickListener(this);
        homeFooterButton.setOnClickListener(this);
        settingsFooterButton.setOnClickListener(this);
        playsFooterButton.setOnClickListener(this);
        backImageButton.setOnClickListener(this);
//        backImageButton.setVisibility(View.GONE);

        settingsFooterButton.setSelected(true);


    }


    @Override
    public void onClick(View v) {

        Intent intent;
        switch (v.getId()) {
            case R.id.fo_ibt_home:
                intent = new Intent(getApplicationContext(),
                        GameHomeScreenActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.fo_ibt_account:
                intent = new Intent(getApplicationContext(),
                        ChangeProfileInfoActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.fo_ibt_leader_board:
                intent = new Intent(getApplicationContext(),
                        LeaderBoardActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.fo_ibt_plays:
                intent = new Intent(getApplicationContext(), Plays.class);
                startActivity(intent);
                finish();
                break;
            case R.id.ibt_back:

                finish();
                break;
          
            case R.id.fo_ibt_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                finish();

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
