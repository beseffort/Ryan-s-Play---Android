package com.ryansplayllc.ryansplay;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rollbar.android.Rollbar;

public class ChangePasswordActivity extends Activity implements OnClickListener {

    private ImageButton backImageButton;

    //footer variables
    private FrameLayout leaderBoardFooterButton;
    private FrameLayout profileFooterButton;
    private FrameLayout homeFooterButton;
    private FrameLayout settingsFooterButton;
    private FrameLayout playsFooterButton;


    private Button changePasswordButton;
    private EditText currentPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confromPassword;
    private ProgressDialog progressDialog;

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private AlertDialog networkErrorDialog;

    private TextView playerNameTextView;
    private TextView noOfPlaysTextView;


    //for changepassword status view
    private RelativeLayout statusLayout;
    private TextView statusMessage;
    private LinearLayout changePasswordContent;
    private ImageView statusIcon;
    private TextView cancelTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ryansplayllc.ryansplay.R.layout.activity_change_password);
        Typeface bebasfont = Typeface.createFromAsset(getAssets(), "BebasNeue Bold.ttf");

        // roll bar
        Rollbar.setIncludeLogcat(true);

        backImageButton = (ImageButton) findViewById(com.ryansplayllc.ryansplay.R.id.ibt_back);
        // footer
        leaderBoardFooterButton =    (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_leader_board);
        profileFooterButton =        (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_account);
        homeFooterButton =           (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_home);
        settingsFooterButton =       (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_settings);
        playsFooterButton =          (FrameLayout) findViewById(com.ryansplayllc.ryansplay.R.id.fo_ibt_plays);

        // password details
        currentPasswordEditText = (EditText) findViewById(com.ryansplayllc.ryansplay.R.id.change_password_et_current_password);
        newPasswordEditText = (EditText) findViewById(com.ryansplayllc.ryansplay.R.id.change_password_et_new_password);
        confromPassword = (EditText) findViewById(com.ryansplayllc.ryansplay.R.id.change_password_et_confrom_password);

        changePasswordButton = (Button) findViewById(com.ryansplayllc.ryansplay.R.id.change_password_bt_change);
        //Applying fontface to change password button
        changePasswordButton.setTypeface(bebasfont);


        //button for cancel in Change bassword to go to  profileselector Screen
        cancelTextView =(TextView) findViewById(com.ryansplayllc.ryansplay.R.id.cancelbtn);
        //applying click listener to cancel button

        cancelTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilescreen = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(profilescreen);

            }
        });

        // playerNameTextView = (TextView) findViewById(R.id.my_tv_player_name);
        // noOfPlaysTextView = (TextView) findViewById(R.id.my_tv_no_of_plays);

        backImageButton.setOnClickListener(this);
        changePasswordButton.setOnClickListener(this);



        // footer click listeners
        leaderBoardFooterButton.setOnClickListener(this);
        profileFooterButton.setOnClickListener(this);
        homeFooterButton.setOnClickListener(this);
        settingsFooterButton.setOnClickListener(this);
        playsFooterButton.setOnClickListener(this);
        // Setting selected state
        settingsFooterButton.setSelected(true);

        // progress settings
        progressDialog = new ProgressDialog(ChangePasswordActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        //Change password content to translate to close status message bloc on tap event
        changePasswordContent=(LinearLayout) findViewById(com.ryansplayllc.ryansplay.R.id.changepasswordcontent);
        statusLayout=(RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.changepasswordstatusview);

        changePasswordContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                statusLayout.setVisibility(View.GONE);
                return false;
            }
        });


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
            case com.ryansplayllc.ryansplay.R.id.change_password_bt_change:
                statusLayout=(RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.changepasswordstatusview);
                statusMessage=(TextView) findViewById(com.ryansplayllc.ryansplay.R.id.errormessage);
                statusIcon=(ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.erroricon);

                if(currentPasswordEditText.getText().length()!=0) {
                    updatePassword();
                }
                else
                {
                    statusLayout.setVisibility(View.VISIBLE);
                    statusMessage.setText("Old password should not be empty");
                    statusMessage.setTextColor(Color.parseColor("#FFD64541"));
                    statusIcon.setImageResource(com.ryansplayllc.ryansplay.R.drawable.erow);


                }
                break;
            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
            showNetworkErrorDialog();
        } else {
            //  Utility.updateHeaderUI(playerNameTextView, noOfPlaysTextView);
        }
    }

    private void showNetworkErrorDialog() {
        AlertDialog.Builder networkErrorBuilder = new AlertDialog.Builder(
                ChangePasswordActivity.this);
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




    public void validate()
    {
        if(currentPasswordEditText.length()!=0)
        {
            if(newPasswordEditText.length()!=0)
            {

            }
            else
            {
                newPasswordEditText.setError("New Password should not be empty");

            }
        }
        else
        {
            currentPasswordEditText.setError("Current password should not be empty");
        }


    }


    //update password function is called on success

    public void updatePassword()
    {
        //for change password status block making visible
        statusLayout=(RelativeLayout) findViewById(com.ryansplayllc.ryansplay.R.id.changepasswordstatusview);
        statusMessage=(TextView) findViewById(com.ryansplayllc.ryansplay.R.id.errormessage);
        changePasswordContent=(LinearLayout) findViewById(com.ryansplayllc.ryansplay.R.id.changepasswordcontent);
        statusIcon=(ImageView) findViewById(com.ryansplayllc.ryansplay.R.id.erroricon);

        if(newPasswordEditText.getText().length()!=0) {
            if (newPasswordEditText.getText().toString()
                    .equals(confromPassword.getText().toString())) {
                RequestQueue requestQueue = Volley
                        .newRequestQueue(getApplicationContext());
                JSONObject paramsJsonObject = new JSONObject();
                try {
                    paramsJsonObject.put("current_password",
                            currentPasswordEditText.getText().toString());
                    paramsJsonObject.put("new_password", newPasswordEditText
                            .getText().toString());
                } catch (Exception exception) {
                }
                JsonObjectRequest updateRequest = new JsonObjectRequest(
                        Method.PUT, Utility.resetPassword,
                        paramsJsonObject, new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseJsonObject) {
                        try {

                            if(responseJsonObject.has("player_status")) {
                                if (responseJsonObject.getString("player_status").equalsIgnoreCase("logged_out")) {
                                    HandleSessionFail.HandleSessionFail(ChangePasswordActivity.this);

                                }
                            }

                            if (responseJsonObject.getBoolean("status")) {
                                Utility.user.jsonParser(responseJsonObject.getJSONObject("user"));

                                progressDialog.dismiss();

                                statusLayout.setVisibility(View.VISIBLE);
                                statusIcon.setImageResource(com.ryansplayllc.ryansplay.R.drawable.susse);


                                statusMessage.setText("Successfully updated");
                                statusMessage.setTextColor(Color.parseColor("#FF779824"));


                            } else {

                                statusLayout.setVisibility(View.VISIBLE);


                                statusMessage.setText(responseJsonObject
                                        .getString("message"));


                                statusMessage.setTextColor(Color.parseColor("#FFD64541"));
                                statusIcon.setImageResource(com.ryansplayllc.ryansplay.R.drawable.erow);                            }
                        } catch (JSONException e) {
                        }

                        progressDialog.dismiss();
                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        Toast.makeText(getApplicationContext(), "Update failed internal server error" + "error code: ", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders()
                            throws AuthFailureError {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("Authorization",
                                Utility.getAccessKey(ChangePasswordActivity.this));
                        return params;
                    }
                };
                requestQueue.add(updateRequest);
                progressDialog.show();
                requestQueue.start();
            } else {
                statusLayout.setVisibility(View.VISIBLE);
                statusMessage.setText("Password doesn't match");
                statusMessage.setTextColor(Color.parseColor("#FFD64541"));
                statusIcon.setImageResource(com.ryansplayllc.ryansplay.R.drawable.erow);
            }
        }
        else
        {
            statusLayout.setVisibility(View.VISIBLE);
            statusMessage.setText("New password should not be empty");
            statusMessage.setTextColor(Color.parseColor("#FFD64541"));
            statusIcon.setImageResource(com.ryansplayllc.ryansplay.R.drawable.erow);

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
