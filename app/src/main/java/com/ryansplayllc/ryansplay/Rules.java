package com.ryansplayllc.ryansplay;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;


public class Rules extends ActionBarActivity implements View.OnClickListener {

    private ImageButton backImageButton;

    private FrameLayout leaderBoardFooterButton;
    private FrameLayout profileFooterButton;
    private FrameLayout homeFooterButton;
    private FrameLayout settingsFooterButton;
    private FrameLayout playsFooterButton;
    private Button playBallButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules);

        // footer

        leaderBoardFooterButton =    (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board);
        profileFooterButton =        (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_account);
        homeFooterButton =           (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_home);
        settingsFooterButton =       (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_settings);
        playsFooterButton =          (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_plays);

        playBallButton = (Button) findViewById(R.id.rules_play_info);

        // footer listener
        leaderBoardFooterButton.setOnClickListener(this);
        profileFooterButton.setOnClickListener(this);
        homeFooterButton.setOnClickListener(this);
        settingsFooterButton.setOnClickListener(this);
        playsFooterButton.setOnClickListener(this);

        //back button click listener
        backImageButton = (ImageButton) findViewById(com.ryansplayllc.ryansplay.R.id.ibt_back);
        backImageButton.setOnClickListener(this);

        homeFooterButton.setSelected(true);

        WebView rulesWebView = (WebView) findViewById(R.id.rules_webView);
        WebSettings webSettings = rulesWebView.getSettings();
        rulesWebView.setWebViewClient(new MyWebViewClient());
        rulesWebView.getSettings().setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        rulesWebView.loadUrl("file:///android_asset/rules.html");
            if (Utility.signUpStatus) {

                Utility.signUpStatus=false;

                //hide back button on entering rules screen after  signup
                backImageButton.setVisibility(View.GONE);

              //making playball button visible on normal navigations to rules screen
                playBallButton.setVisibility(View.VISIBLE);

                //navigating to home screen on click of play ball button
                playBallButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Rules.this, GameHomeScreenActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }




    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

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
                intent = new Intent(getApplicationContext(), GameHomeScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
    }

    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.ryansplayllc.ryansplay.R.menu.menu_rules, menu);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeScreenIntent = new Intent(Rules.this,GameHomeScreenActivity.class);
        homeScreenIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeScreenIntent);
    }
}
