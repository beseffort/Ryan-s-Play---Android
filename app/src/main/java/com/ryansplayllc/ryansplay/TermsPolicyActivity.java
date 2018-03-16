package com.ryansplayllc.ryansplay;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.facebook.FacebookSdk;

/**
 * Created by nimaikrsna on 8/27/2015.
 */
public class TermsPolicyActivity extends Activity {

    WebView termsAndPolicy;
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termspolicy);

        termsAndPolicy = (WebView) findViewById(R.id.policyContainer);
        back = (ImageButton) findViewById(R.id.ibt_back);

        termsAndPolicy.loadUrl("http://www.ryansplay.com");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
