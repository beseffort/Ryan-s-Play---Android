package com.ryansplayllc.ryansplay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.*;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.adapters.CropingOptionAdapter;
import com.ryansplayllc.ryansplay.models.User;




public class ChangeProfileInfoActivity extends FragmentActivity implements
        OnClickListener {

    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;


    private Uri mImageCaptureUri;
    private File outPutFile = null;

    private ImageButton backImageButton;
    private FrameLayout homeFooterButton;
    private FrameLayout leaderBoardFooterButton;
    private FrameLayout profileFooterButton;
    private FrameLayout playsFooterButton;
    private FrameLayout settingsFooterButton;
    private EditText usernameEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private TextView emailTextView;
    private ImageView profilePicImageView;
    private ImageView uploadImageButton;
    private Button changeInfoButton;
    private ProgressDialog progressDialog;
    private final int RESULT_LOAD_IMAGE = 22;
    private final int SELECT_PICTURE = 1;
    private final int REQUEST_CODE_CROP_IMAGE = 99;
    private final int REQUEST_CAPTURE_IMAGE = 1;
    private File mFileTemp;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int id = 01;
    private Button seeFullStats;



    private static EditText dateOfBirth;
    private ImageView dateOfBirthTopView;
    private Spinner genderSpinner;
    private Spinner  cityspinner;
    private EditText address;
    private EditText userName;
    private EditText pincode;

    //error message layout
    private RelativeLayout errorMsgLayout;
    private TextView errorMessageText;

    private String genderText;
    private String cityText;



    private String uploadChoice;

    public String getUploadChoice() {
        return uploadChoice;
    }

    public void setUploadChoice(String uploadChoice) {
        this.uploadChoice = uploadChoice;
    }


    private Uri fileUri;

    public int getSelectedGender() {
        return selectedGender;
    }

    public void setSelectedGender(int selectedGender) {
        this.selectedGender = selectedGender;
    }

    private int selectedGender;


    //initialising cancel button
    private TextView cancelButton;

    public String getGenderText() {
        return genderText;
    }

    public void setGenderText(String genderText) {
        this.genderText = genderText;
    }

    public String getCityText() {
        return cityText;
    }

    public void setCityText(String cityText) {
        this.cityText = cityText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_profile_info);

        // roll bar
        Rollbar.setIncludeLogcat(true);
        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        //font face to apply to buttons
        Typeface bebasfont = Typeface.createFromAsset(getAssets(), "BebasNeue Bold.ttf");


        backImageButton = (ImageButton) findViewById(R.id.ibt_back);
        // footer
        leaderBoardFooterButton =   (FrameLayout) findViewById(R.id.fo_ibt_leader_board);
        profileFooterButton =       (FrameLayout) findViewById(R.id.fo_ibt_account);
        playsFooterButton =         (FrameLayout) findViewById(R.id.fo_ibt_plays);
        settingsFooterButton =      (FrameLayout) findViewById(R.id.fo_ibt_settings);
        homeFooterButton =          (FrameLayout) findViewById(R.id.fo_ibt_home);

        //error message status info layout
        errorMessageText = (TextView) findViewById(R.id.ch_profile_err_msg);
        errorMsgLayout   =  (RelativeLayout) findViewById(R.id.ch_profile_err_layout);
        // edit info variable
        usernameEditText = (EditText) findViewById(R.id.change_profile_ed_user_name);
        firstNameEditText = (EditText) findViewById(R.id.change_profile_ed_first_name);
        lastNameEditText = (EditText) findViewById(R.id.change_profile_ed_last_name);
        emailTextView = (TextView) findViewById(R.id.change_profile_tv_email);
        changeInfoButton = (Button) findViewById(R.id.change_profile_bt_update);
        uploadImageButton = (ImageView) findViewById(R.id.change_profile_bt_upload_image);
        profilePicImageView = (ImageView) findViewById(R.id.change_password_iv_profile_pic);
        cancelButton=(TextView) findViewById(R.id.signup_cancel_text_view);

        //adding player info variables
        genderSpinner=(Spinner) findViewById(R.id.edit_profile_gender_spinner);
        cityspinner=(Spinner) findViewById(R.id.edit_profile_city_spinner);
        dateOfBirth=(EditText) findViewById(R.id.edit_profile_dateof_birth);
        dateOfBirthTopView =(ImageView) findViewById(R.id.date_of_birth_topview);
        address = (EditText) findViewById(R.id.ch_prof_address);
        pincode =(EditText) findViewById(R.id.ch_prof_pincod);

        seeFullStats = (Button) findViewById(R.id.seefullstats);
        seeFullStats.setTypeface(bebasfont);
        seeFullStats.setOnClickListener(this);

        // image request

        RequestQueue imageRequestQueue = Volley
                .newRequestQueue(getApplicationContext());
        AlertDialog.Builder builder1=new AlertDialog.Builder(ChangeProfileInfoActivity.this);
        builder1.setMessage(Utility.user.getProfilePicURL()+"");

//        ImageLoader imageLoader = new ImageLoader();
//        imageLoader.imageView = profilePicImageView;
//        imageLoader.user = Utility.user;
//        imageLoader.run();

        UrlImageViewHelper.setUrlDrawable(profilePicImageView, Utility.user.getProfilePicURL());
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        // edit info setting values
        usernameEditText.setText(Utility.user.getUserName());
        firstNameEditText.setText(Utility.user.getFirstName());
        lastNameEditText.setText(Utility.user.getLastName());
        emailTextView.setText(Utility.user.getEmail());

        String customDateFormat[] = Utility.user.getPlayer().getBirthday().split("-");
        try {
            populateSetDate(Integer.valueOf(customDateFormat[0]), Integer.valueOf(customDateFormat[1]), Integer.valueOf(customDateFormat[2]));
        }catch(Exception e){

        }
        address.setText(Utility.user.getPlayer().getState());
        pincode.setText(Utility.user.getPlayer().getZipcode());




        backImageButton.setOnClickListener(this);
        changeInfoButton.setOnClickListener(this);
        uploadImageButton.setOnClickListener(this);

        // footer
        leaderBoardFooterButton.setOnClickListener(this);
        profileFooterButton.setOnClickListener(this);
        playsFooterButton.setOnClickListener(this);
        settingsFooterButton.setOnClickListener(this);
        homeFooterButton.setOnClickListener(this);

        profileFooterButton.setSelected(true);

        // Setting selected state


        // progress settings
        progressDialog = new ProgressDialog(ChangeProfileInfoActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);



        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),
                    TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }


        // notification Manager
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("Picture Upload")
                .setContentText("Upload in progress")
                .setSmallIcon(R.drawable.app_icon);



        //Apply font face to buttons
        changeInfoButton.setTypeface(bebasfont);

        //Array for gender
        ArrayList<String> genderList =new ArrayList<String>();
        genderList.add("select gender");
        genderList.add("male");
        genderList.add("female");

        ArrayList<String> cityList =new ArrayList<String>();
        cityList.add("select");
        cityList.add("UT");
        cityList.add("IN");
        //adding spinners of gender , city and date picker


        //initial spinner structure of gender
        final ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this,
                R.layout.spinnerview, genderList);
        final ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this,
                R.layout.spinnerview, cityList);
        genderSpinner.setAdapter(genderAdapter);
        cityspinner.setAdapter(cityAdapter);



        ArrayList<String> testGenderArray1=new ArrayList<>();
        ArrayList<String> testGenderArray2=new ArrayList<>();

        //populating the gender data according to retrieved from server in spinner
        if(Utility.user.getPlayer().getGender().equals("male"))
        {
            genderList.clear();
            genderList.add("male");
            genderList.add("female");
            final ArrayAdapter<String> genderAdapter1 = new ArrayAdapter<String>(this,
                    R.layout.spinnerview,genderList);
            genderSpinner.setAdapter(genderAdapter1);
        }




        else if(Utility.user.getPlayer().getGender().equals("female"))
        {
            genderList.clear();
            genderList.add("female");
            genderList.add("male");
            final ArrayAdapter<String> genderAdapter1 = new ArrayAdapter<String>(this,
                    R.layout.spinnerview,genderList);
            genderSpinner.setAdapter(genderAdapter1);

        }
        else if (Utility.user.getPlayer().getGender().equals(" "))
        {
            genderList.clear();

            genderList.add("select gender");
            genderList.add("male");
            genderList.add("female");

            final ArrayAdapter<String> genderAdapter1 = new ArrayAdapter<String>(this,
                    R.layout.spinnerview,genderList);
            genderSpinner.setAdapter(genderAdapter1);

        }




        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setGenderText(genderAdapter.getItem(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        cityspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setCityText(cityAdapter.getItem(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dateOfBirthTopView.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

    }





    //select date on click of date birth field
    public void selectDate(View view) {
        DialogFragment newFragment = new SelectDateFragment();
        //fragment for datepicker dialogue
        newFragment.show(getSupportFragmentManager(), "DatePicker");
    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(),3,this, yy, mm, dd);
        }

        @Override
        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            ChangeProfileInfoActivity changeProfileInfoActivity=new ChangeProfileInfoActivity();
            populateSetDate(yy, mm+1, dd);
        }
    }


    public static void populateSetDate(int year, int month, int day) {
        //to set dat on date of birth text field when selected date


        String fullMonth = "";

        switch(day){
            case 1:fullMonth = "st";
                break;
            case 2:fullMonth = "nd";
                break;
            case 3:fullMonth = "rd";
                break;
            case 21:fullMonth = "st";
                break;
            case 22:fullMonth = "nd";
                break;
            case 23:fullMonth = "rd";
                break;
            case 31:fullMonth = "st";
                break;
            default:fullMonth = "th";
                break;
        }
//        dateOfBirth = (EditText)findViewById(com.ryansplayllc.ryansplay.R.id.edit_profile_dateof_birth);
        dateOfBirth.setText(getMonth(month)+" "+ day + ""+fullMonth+", "+year);
    }



    //function for converting int month to month name
    public static String getMonth(int month) {

        return new DateFormatSymbols().getMonths()[month-1];
    }



    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.fo_ibt_home:
                //navigating to GameHomeScreenActivity
                intent = new Intent(getApplicationContext(),
                        GameHomeScreenActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.fo_ibt_leader_board:
                //navigating to LeaderBoardActivity
                intent = new Intent(getApplicationContext(),
                        LeaderBoardActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.seefullstats:
                intent = new Intent(getApplicationContext(),
                        Profile.class);
                startActivity(intent);
                finish();
                break;
            case R.id.fo_ibt_account:

                break;
            case R.id.fo_ibt_plays:
                intent = new Intent(getApplicationContext(), Plays.class);
                startActivity(intent);
                finish();
                break;
            case R.id.fo_ibt_settings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.ibt_back:
                finish();
                break;
            case R.id.date_of_birth_topview :
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(dateOfBirth.getWindowToken(), 0);
                selectDate(v);
                break;

            case R.id.change_profile_bt_update:
                validate();
                break;

            case R.id.signup_cancel_text_view:
                finish();
                break;

            case R.id.change_profile_bt_upload_image:
//
                selectImageOption();
                break;

            default:
                break;



        }
    }





    //update content function is called on succes validation
    private void updateContent()
    {
        hideErrorMessageLayout();
        RequestQueue requestQueue = Volley
                .newRequestQueue(getApplicationContext());
        JSONObject paramsJsonObject = new JSONObject();
        JSONObject playerObject = new JSONObject();
        JSONObject userObject =new JSONObject();

        try {

            if(firstNameEditText.getText().toString().length()==0)
            {
//                firstNameEditText.setError("firstname should not be empty");
                displayError("Firstname should not be empty");
                return;
            }
            if(lastNameEditText.getText().toString().length()==0)
            {
//                lastNameEditText.setError("lastname should not be empty");
                displayError("Lastname should not be empty");
                return;
            }
            if(getGenderText().equals("select gender"))
            {
//                Toast.makeText(getApplicationContext(),
//                        "select gender",
//                        Toast.LENGTH_SHORT).show();
                displayError("Select gender");
                return;
            }
            if(dateOfBirth.getText().toString().length()==0)
            {
//                dateOfBirth.setError("dateofbirth should not be empty");

                displayError("Date of birth should not be empty");
                return;
            }





            if(getGenderText().equals("male"))
            {
                setSelectedGender(1);

            }
            if(getGenderText().equals("female"))
            {
                setSelectedGender(2);

            }

            Log.e("birthday", dateOfBirth.getText().toString());


            userObject.put("first_name",firstNameEditText.getText().toString());
            userObject.put("last_name",lastNameEditText.getText().toString());
            playerObject.put("gender",getSelectedGender());

            playerObject.put("birthday", dateOfBirth.getText().toString());
            userObject.put("player_attributes", playerObject);
            AlertDialog.Builder builder=new AlertDialog.Builder(ChangeProfileInfoActivity.this);
            builder.setMessage(userObject.toString());



        } catch (Exception exception) {
        }
        JsonObjectRequest updateRequest = new JsonObjectRequest(Method.PUT,
                Utility.updateUserProfile, userObject,
                new Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseJsonObject) {
                        try {

                            if(responseJsonObject.has("player_status")) {
                                if (responseJsonObject.getString("player_status").equalsIgnoreCase("logged_out")) {
                                    HandleSessionFail.HandleSessionFail(ChangeProfileInfoActivity.this);

                                }
                            }



                            Log.e("edit profile "," " + responseJsonObject.toString());
                            if (responseJsonObject.getBoolean("status")) {
                                Utility.user.jsonParser(responseJsonObject
                                        .getJSONObject("user"));
                                Toast.makeText(getApplicationContext(),
                                        "Successfully updated",
                                        Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();
                                AlertDialog.Builder builder=new AlertDialog.Builder(ChangeProfileInfoActivity.this);
                                builder.setMessage(responseJsonObject.toString());
//                                    builder.show();
//                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Update failed", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        } catch (JSONException e) {
                        }

                        progressDialog.dismiss();
                    }
                }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                Toast.makeText(getApplicationContext(),
                        "Update failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", Utility
                        .getAccessKey(ChangeProfileInfoActivity.this));
                return params;
            }
        };
        requestQueue.add(updateRequest);
        progressDialog.show();
        requestQueue.start();
    }

    private void uploadImageAsyc() {


        RequestParams requestparams = new RequestParams();
        try {
            requestparams.put("photo", new File(outPutFile.getPath()),"image/jpg");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization",
                Utility.getAccessKey(ChangeProfileInfoActivity.this));
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        client.put(Utility.uploadPhoto, requestparams,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        try {

                            AlertDialog.Builder builder1=new AlertDialog.Builder(ChangeProfileInfoActivity.this);
                            builder1.setMessage(response.toString());

//                            builder1.show();
                            if (response.getBoolean("status")) {
                                Utility.user.jsonParser(response
                                        .getJSONObject("user"));
                                Utility.user.setProfilePic(BitmapFactory
                                        .decodeFile(outPutFile.getPath()));

                                profilePicImageView.setBackgroundResource(0);
                                Bitmap bitmap = BitmapFactory.decodeFile(outPutFile.getPath());
                                Log.e("chbitmap",bitmap.toString()+"");
                                Utility.user.setProfilePic(bitmap);

//                                ImageLoader imageLoader = new ImageLoader();
//                                imageLoader.imageView = profilePicImageView;
//                                imageLoader.user = Utility.user;
//                                imageLoader.run();

                                UrlImageViewHelper.setUrlDrawable(profilePicImageView, Utility.user.getProfilePicURL());

                                Utility.saveProfilePicLocal(getApplicationContext(),BitmapFactory.decodeFile(outPutFile.getPath()));
                                mBuilder.setContentText("Upload completed").setProgress(0, 0, false);
                                mNotifyManager.notify(id, mBuilder.build());
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                            }

                            else {
                                mBuilder.setContentText("Upload failed")
                                        .setProgress(0, 0, false);
                                mNotifyManager.notify(id, mBuilder.build());
                                findViewById(R.id.loadingPanel).setVisibility(
                                        View.GONE);
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            AlertDialog.Builder builder1=new AlertDialog.Builder(ChangeProfileInfoActivity.this);
                            builder1.setMessage(e.toString());


                            builder1.show();

                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers,
                                          Throwable throwable, JSONObject errorResponse) {
                        mBuilder.setContentText("Upload failed").setProgress(0,
                                0, false);
                        mNotifyManager.notify(id, mBuilder.build());
                        findViewById(R.id.loadingPanel)
                                .setVisibility(View.GONE);
                    }

                });
    }




    public void validate()
    {
        if(firstNameEditText.getText().length()!=0)
        {

            if(lastNameEditText.getText().length()!=0)
            {
                if(usernameEditText.getText().length()!=0)
                {
                    //on success validatiob updating data
                    updateContent();

                }
                else
                {
                    displayError("Username should not be empty");
                    return;

                }

            }
            else
            {
                displayError("Lastname should not be empty");
                return;
            }
        }
        else
        {
            displayError("Firstname should not be empty");
            return;
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
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
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


    private void selectImageOption() {
        final CharSequence[] items = { "Capture Photo", "Choose from Gallery", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ChangeProfileInfoActivity.this);
        builder.setTitle("Upload Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Capture Photo")) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp1.jpg");
                    mImageCaptureUri = Uri.fromFile(f);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    startActivityForResult(intent, CAMERA_CODE);

                } else if (items[item].equals("Choose from Gallery")) {

                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, GALLERY_CODE);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK && null != data) {

            mImageCaptureUri = data.getData();
            System.out.println("Gallery Image URI : "+mImageCaptureUri);
            CropingIMG();
        }

        else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {

            System.out.println("Camera Image URI : "+mImageCaptureUri);
            CropingIMG();

        }
        else if (requestCode == CROPING_CODE) {

            try {
                if(outPutFile.exists()){

                    uploadImageAsyc();


                }
                else {
                    Toast.makeText(getApplicationContext(), "Error while save image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void CropingIMG() {

        final ArrayList<CropingOption> cropOptions = new ArrayList<>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

            List list = getPackageManager().queryIntentActivities( intent, 0 );
        int size = list.size();
        if (size == 0) {
               Toast.makeText(this, "Cann't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            //TODO: don't use return-data tag because it's not return large image data and crash not given any message
            //intent.putExtra("return-data", true);

            //Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

            if (size == 1) {
                Intent i   = new Intent(intent);
                ResolveInfo res =(ResolveInfo) list.get(0);

                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            }

            else {
                for (Object res : list) {
                    ResolveInfo res1 =(ResolveInfo) res;

                    final CropingOption co = new CropingOption();

                    co.title  = getPackageManager().getApplicationLabel(res1.activityInfo.applicationInfo);
                    co.icon  = getPackageManager().getApplicationIcon(res1.activityInfo.applicationInfo);
                    co.appIntent= new Intent(intent);
                    co.appIntent.setComponent( new ComponentName(res1.activityInfo.packageName, res1.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int item ) {
                        startActivityForResult( cropOptions.get(item).appIntent, CROPING_CODE);
                    }
                });

                builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel( DialogInterface dialog ) {

                        if (mImageCaptureUri != null ) {
                            getContentResolver().delete(mImageCaptureUri, null, null );
                            mImageCaptureUri = null;
                        }
                    }
                } );

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    @Override
    public void onBackPressed() {



        if(getIntent().getBooleanExtra("settings",false))
        {
            finish();
        }
        else
        {
            Intent homeScreenIntent = new Intent(ChangeProfileInfoActivity.this,GameHomeScreenActivity.class);
            startActivity(homeScreenIntent);
            finish();

        }
    }

    public void displayError(String message)
    {
        errorMessageText.setText(message);
        ScrollView chProfileScrollView=(ScrollView) findViewById(R.id.ch_profile_main_scroll_View);
        ImageView  chProfileErrorIcon=(ImageView) findViewById(R.id.ch_profile_err_icon);
        RelativeLayout mainLayout=(RelativeLayout) findViewById(R.id.ch_profile_main_layout);
        Animation fadeAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.ch_profile_fadeanim);

        Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.ch_profile_anim_slidedown);
        slideDown.setFillAfter(true);
        fadeAnim.setFillAfter(true);

        mainLayout.setAnimation(slideDown);
        chProfileScrollView.setAnimation(slideDown);
        errorMessageText.setAnimation(fadeAnim);
        chProfileErrorIcon.setAnimation(fadeAnim);
        errorMsgLayout.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        p.addRule(RelativeLayout.BELOW, R.id.ch_profile_err_layout);
        chProfileScrollView.setLayoutParams(p);


    }

   public void hideErrorMessageLayout()
   {
       RelativeLayout mainLayout=(RelativeLayout) findViewById(R.id.ch_profile_main_layout);
       errorMsgLayout.setVisibility(View.GONE);
   }
}
