package com.ryansplayllc.ryansplay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.rollbar.android.Rollbar;
import com.ryansplayllc.ryansplay.receivers.UserReciver;
import com.ryansplayllc.ryansplay.services.UserService;

public class SignUpActivity extends FragmentActivity {

	private EditText firstNameEditText;
	private EditText lastNameEditText;
	private EditText emailEditText;
	private EditText passwordEditText;
	private EditText confromPasswordEditText;
    private EditText usernameEditText;
    private TextView rpCodeLabel;
    private EditText rpCodeText;
    private EditText dateOfBirth;
    private Spinner  genderSpinner;
    private Spinner  cityspinner;
    public static String userPromoCode="";

    Calendar signupCalender;



	private TextView loginTextView;
	private Button signoutButton;
    private Button signupButton;
    private TextView cancelTextView;



	
	// user broadcast receiver
	private UserReciver userReciver;
	
	public static ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(com.ryansplayllc.ryansplay.R.layout.activity_sign_up);
        Typeface bebasfont = Typeface.createFromAsset(getAssets(), "BebasNeue Bold.ttf");

		// roll bar
		Rollbar.setIncludeLogcat(true);

		Utility.deleteCache(getApplicationContext());
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		// progress bar
		progressDialog = new ProgressDialog(SignUpActivity.this);
		progressDialog.setMessage("Loading...");
		progressDialog.setCancelable(false);
		// sign_up views
		firstNameEditText = (EditText) findViewById(com.ryansplayllc.ryansplay.R.id.su_et_first_name);
		lastNameEditText = (EditText) findViewById(com.ryansplayllc.ryansplay.R.id.su_et_last_name);
		emailEditText = (EditText) findViewById(com.ryansplayllc.ryansplay.R.id.su_et_email);
		passwordEditText = (EditText) findViewById(com.ryansplayllc.ryansplay.R.id.su_et_password);
		confromPasswordEditText = (EditText) findViewById(com.ryansplayllc.ryansplay.R.id.su_et_cnfm_password);
        signupButton=(Button) findViewById(com.ryansplayllc.ryansplay.R.id.lg_bt_login);
        cancelTextView=(TextView) findViewById(com.ryansplayllc.ryansplay.R.id.signup_cancel_text_view);
        usernameEditText=(EditText) findViewById(com.ryansplayllc.ryansplay.R.id.signup_username);
        rpCodeText = (EditText) findViewById(R.id.signup_promo_code);
        rpCodeLabel=(TextView) findViewById(R.id.signup_rpcode_label);




        //applying font style to signup button
        signupButton.setTypeface(bebasfont);
		TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(usernameEditText.getText().toString().length()!=0)
                {
                    rpCodeLabel.setVisibility(View.GONE);
                }
                else
                {
                    rpCodeLabel.setVisibility(View.VISIBLE);
                }
            }

            private boolean filterLongEnough() {
                return usernameEditText.getText().toString().trim().length() > 2;
            }
        };
        usernameEditText.addTextChangedListener(fieldValidatorTextWatcher);




		// facebook sign_up button on click listener
		findViewById(com.ryansplayllc.ryansplay.R.id.authButton).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });


       cancelTextView.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               finish();
               return false;
           }
       });




		// user receiver
		IntentFilter filter = new IntentFilter(UserReciver.ACTION_RESP);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		userReciver = new UserReciver();
		registerReceiver(userReciver, filter);


        //Array for gender
        ArrayList<String> genderList =new ArrayList<String>();
        genderList.add("Male");
        genderList.add("Female");

        ArrayList<String> cityList =new ArrayList<String>();
        cityList.add("UT");
        cityList.add("IN");








	}


	public void signUp(View view) {
		final String firstName = firstNameEditText.getText().toString().trim();
		final String lastName = lastNameEditText.getText().toString().trim();
		final String email = emailEditText.getText().toString().trim();
		final String password = passwordEditText.getText().toString();
        final String username=usernameEditText.getText().toString().trim();
		final String confromPassword = confromPasswordEditText.getText()
				.toString();

        final String rpCode = rpCodeText.getText().toString();

        userPromoCode = rpCode;

		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
//		String NAME_PATTERN = "[a-zA-z]+([ '-][a-zA-Z]+)*";

//		Pattern namePattern = Pattern.compile(NAME_PATTERN);
		Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);

//		Matcher fistNameMatcher = namePattern.matcher(firstName);
//		Matcher lastNameMatcher = namePattern.matcher(lastName);
		Matcher emailMatcher = emailPattern.matcher(email);

		if (firstName.length()==0) {
			firstNameEditText.setError("Firstname should not be  empty");
			return;
		}

		if (lastName.length()==0) {
			lastNameEditText.setError("Lastname should not be  empty");
			return;
		}
        if(email.length()==0)
        {
            emailEditText.setError("Email id should not be  empty");
            return;

        }
		if (!emailMatcher.matches()) {
			emailEditText.setError("Not a valid e mail");
			return;
		}

        if(username.length()==0)
        {
            usernameEditText.setError("Username should not be empty");
            return;
        }

        if( !(username.length() >= 3 && username.length() <=20 ))
        {
            usernameEditText.setError("Username should contain atleast 3-20 characters");
            return;
        }

        if(isUsernameUppercaseFound())
        {

            usernameEditText.setError("Username should not contain uppercase characters");
            return;
        }


        Log.e("username contains (.)",username.contains(".")+"");

        if(username.contains("."))
        {
            char a;
            int n=0;
            for(int i=0;i<username.length();i++)
            {
                a=username.charAt(i);
                if(a == '.') {
                 n++;
                }
            }
            if(n>1)
            {
                usernameEditText.setError("username can contain maximum of one period(.)");
            }
        }

		if (password.length() == 0) {
			passwordEditText.setError("Password should not be empty");
			return;
		}

		if (!password.equals(confromPassword)) {
			confromPasswordEditText.setError("Password does not match");
			return;
		}


        Utility.signUpURL = Utility.baseURL+"/v2/registration/normal";

		Utility.user.setFirstName(firstName);
        Utility.user.setLastName(lastName);
        Utility.user.setEmail(email);
        Utility.user.setPassword(password);
        Utility.user.setUserName(username);
        Intent userService = new Intent(getApplicationContext(),
                UserService.class);
        userService.putExtra("action", "signup");
        this.startService(userService);
		SignUpActivity.progressDialog.show();
	}

    private boolean isUsernameUppercaseFound() {
        boolean isCharUpper =false;
        for(int i=0;i<usernameEditText.getText().toString().length();i++)
        {
            Character c = usernameEditText.getText().toString().charAt(i);
            if(c.isUpperCase(c))
            {
            isCharUpper=true;
                break;
            }
            Log.e("uppercase status",c.isUpperCase(c)+"");
        }
        return isCharUpper;
    }


    @Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();


	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}



}
