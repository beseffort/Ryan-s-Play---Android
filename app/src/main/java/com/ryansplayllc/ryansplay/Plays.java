//Utility.user.getPlayer().getNoOfPlays()

package com.ryansplayllc.ryansplay;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ryansplayllc.ryansplay.util.IabHelper;
import com.ryansplayllc.ryansplay.util.IabResult;
import com.ryansplayllc.ryansplay.util.Inventory;
import com.ryansplayllc.ryansplay.util.Purchase;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


public class Plays extends ActionBarActivity implements View.OnClickListener {
    private ImageButton backImageButton;
    //footer variables
    private FrameLayout leaderBoardFooterButton;
    private FrameLayout profileFooterButton;
    private FrameLayout homeFooterButton;
    private FrameLayout settingsFooterButton;
    private FrameLayout inviteFrns;

    //Balance plays Text intialising variables;
    private TextView balancePlays;

    private FrameLayout playsFooterButton;

    /*************variables for inapp purchasing****************/
    // Debug tag, for logging
    static final String TAG = "TrivialDrive";

    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
    static String product_id = "gas";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    boolean purchasing_flag = false;

    // The helper object
    IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plays);
        backImageButton = (ImageButton) findViewById(R.id.ibt_back);
        backImageButton.setVisibility(View.GONE);


        //in app purchasing initialization started
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjaLlrsD9+iPbSuj1pt49gLh/s4vkZ0NAFuXhwPFJ/VxlriklfZs4Xzki9/SS1JeMAFvwfaIQ2KXXxcAh2FziUJnLdMJFglH1PWOqMgHPt2FO2NVz4JF4oCWUC5h4o5GRm4SoXvGir4YlvJwovc3Cz1V3PZYSU2Gi1dMkTpYo1E8AH2UYeZ6LeVmmSMksTLkejfzTUcqIbvGD2iQJiaHltswjrLrDhUrAJ0yGDtXbTRWkpeID5qQXs1Jgfo7XgzAcYP0O6F+tRASNvYu6Jc7SdV9n/ZuimPI+6b3Eed9yJtCAtJyMeuJQwpQIwCoipZb5fWH9ZN+pB6fDXA7xdVUzawIDAQAB";

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        /*
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String str = "GPA1357-9128-5488-65370";
        str = str.replaceAll("[^\\d]", "");
        String transaction_details = "plays20##"+Utility.user.getUserName()+"##"+"2403403252353280jfdifjpjopewhg"+"##"+date+"##"+str;

        update_plays(20, transaction_details);*/
        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(false);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.e(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.e(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
        //in app purchasing initialization completed


        // footer
        leaderBoardFooterButton =    (FrameLayout) findViewById(R.id.fo_ibt_leader_board);
        profileFooterButton =        (FrameLayout) findViewById(R.id.fo_ibt_account);
        homeFooterButton =           (FrameLayout) findViewById(R.id.fo_ibt_home);
        settingsFooterButton =       (FrameLayout) findViewById(R.id.fo_ibt_settings);
        playsFooterButton =          (FrameLayout) findViewById(R.id.fo_ibt_plays);
        inviteFrns = (FrameLayout) findViewById(R.id.inviteFrns);

        //balanceplays text assigning to variables
        balancePlays=  (TextView) findViewById(R.id.addplays_plays_in_hand);
        balancePlays.setText(GameHomeScreenActivity.staticNop);

        RequestQueue requestQueue = Volley
                .newRequestQueue(getApplicationContext());
        JsonObjectRequest userProfileRequest = new JsonObjectRequest(
                Request.Method.GET, Utility.userProfile+Utility.user.getId(), null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseJsonObject) {

                        try {
                            if (responseJsonObject.getBoolean("status")) {

                                JSONObject userInfo = responseJsonObject.getJSONObject("user");
                                balancePlays.setText(userInfo.getString("play_coins"));

                            }
                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return null;
            }
        };



// footer click listeners
        leaderBoardFooterButton.setOnClickListener(this);
        profileFooterButton.setOnClickListener(this);
        homeFooterButton.setOnClickListener(this);
        settingsFooterButton.setOnClickListener(this);
        playsFooterButton.setOnClickListener(this);

        inviteFrns.setOnClickListener(this);
        playsFooterButton.setSelected(true);



    }




    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */



            Purchase gasPurchase = inventory.getPurchase("plays20");
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                mHelper.consumeAsync(inventory.getPurchase("plays20"), mConsumeFinishedListener);
                return;
            }
            gasPurchase = inventory.getPurchase("plays50");
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                mHelper.consumeAsync(inventory.getPurchase("plays50"), mConsumeFinishedListener);
                return;
            }
            gasPurchase = inventory.getPurchase("plays200");
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                mHelper.consumeAsync(inventory.getPurchase("plays200"), mConsumeFinishedListener);
                return;
            }
            gasPurchase = inventory.getPurchase("plays500");
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                mHelper.consumeAsync(inventory.getPurchase("plays500"), mConsumeFinishedListener);
                return;
            }
            gasPurchase = inventory.getPurchase("plays1000");
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                mHelper.consumeAsync(inventory.getPurchase("plays1000"), mConsumeFinishedListener);
                return;
            }
        }
    };

    // User clicked the "Buy Gas" button
    public void buySomePlays(View currentView) {
        Log.d(TAG, "Buy plays button clicked.");

        // launch the gas purchase UI flow.
        // We will be notified of completion via mPurchaseFinishedListener

       // Toast.makeText(getApplicationContext(),currentView.getTag().toString(),Toast.LENGTH_LONG).show();

        product_id = currentView.getTag().toString();
        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "buy more plays";

        purchasing_flag = true;
        mHelper.launchPurchaseFlow(this, product_id, RC_REQUEST,
                mPurchaseFinishedListener, payload);
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.fo_ibt_home:
                intent = new Intent(getApplicationContext(),
                        GameHomeScreenActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.fo_ibt_leader_board:
                intent = new Intent(getApplicationContext(),
                        LeaderBoardActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.fo_ibt_account:
                intent = new Intent(getApplicationContext(),
                        ChangeProfileInfoActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.fo_ibt_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.inviteFrns:
                intent = new Intent("android.intent.action.VIEW");

                Uri data = Uri.parse("sms:");

                /** Setting sms uri to the intent */

                intent.setData(data);
                intent.setType("vnd.android-dir/mms-sms");
                intent.putExtra("sms_body",Utility.smsBody1+Utility.user.getUserName()+Utility.smsBody2+ Utility.user.getUserName()+Utility.smsBody3+Utility.smsBody4);

                /** Initiates the SMS compose screen, because the activity contain ACTION_VIEW and sms uri */
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        if(payload.equals("buy more plays"))
            return true;
        else
            return false;
        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            purchasing_flag = false;
            //Toast.makeText(getApplicationContext(),"You Purchased " + purchase.getSku(),Toast.LENGTH_LONG).show();
            //alert("You Purchased" + purchase.getSku());

            // if we were disposed of in the meantime, quit.
            if (mHelper == null)
            {
                //alert("mHelper==null");
                return;
            }

            if (result.isFailure()) {
                //complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(product_id)) {
                //alert("Starting consumption");
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase is gas. Starting gas consumption.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }

        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);
            //alert("Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit

                String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                String transaction_id = purchase.getOrderId();
                transaction_id = transaction_id.replaceAll("[^\\d]", "");
                String transaction_details = purchase.getSku()+"##"+Utility.user.getUserName()+"##"+purchase.getToken()+"##"+date+"##"+transaction_id;

                int add_play_num = 0;
                if(purchase.getSku().equals("plays20"))
                    add_play_num = 20;
                else if(purchase.getSku().equals("plays50"))
                    add_play_num = 50;
                else if(purchase.getSku().equals("plays200"))
                    add_play_num = 200;
                else if(purchase.getSku().equals("plays500"))
                    add_play_num = 500;
                else if(purchase.getSku().equals("plays1000"))
                    add_play_num = 1000;
                if(add_play_num!=0)
                    update_plays(add_play_num, transaction_details);
                Log.d(TAG, "Consumption successful. Provisioning.");

            }
            else {
                complain("Error while consuming: " + result);
            }


        }
    };

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    public void update_plays(final int additional_plays, String transaction_details){


        RequestQueue queue = Volley.newRequestQueue(Plays.this);


        final JSONObject params = new JSONObject();
        try {
            params.put("play_coins", additional_plays);
            params.put("transaction_details", transaction_details);
            params.put("push_token", Utility.pushToken);
            params.put("uuid", Utility.uuid);

        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, Utility.updateUserPlays, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject responseJsonObject) {

                try {
                    Log.e("update plays","parameters---> "+params.toString()+"response----> "+responseJsonObject.toString());
                    if (responseJsonObject.getBoolean("status")) {
                        balancePlays.setText(String.valueOf(Integer.parseInt(GameHomeScreenActivity.staticNop) + additional_plays));
                    }
                    else
                    {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Plays.this,
                            "Error Occured", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("update play error","err");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization",
                        Utility.getAccessKey(Plays.this));
                return params;
            }

        };
        queue.add(jsonObjectRequest);
        queue.start();
    }


    @Override
    public void onBackPressed() {
        if(!purchasing_flag) {
            Intent homeScreenIntent = new Intent(Plays.this, GameHomeScreenActivity.class);
            startActivity(homeScreenIntent);
            finish();
        }
        super.onBackPressed();
    }

    public void onDestroy() {
        if(!purchasing_flag)
        {
            //if(mHelper != null) mHelper.dispose();
            mHelper = null;
        }
        super.onDestroy();
    }
}
