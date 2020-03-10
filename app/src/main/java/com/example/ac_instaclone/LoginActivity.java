package com.example.ac_instaclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtLoginEmail, edtLoginPassword;
    private Button btnLoginActivity, btnSignUpLoginActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Log In");

        edtLoginEmail = findViewById(R.id.edtLoginEmail);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        //When the user clicks the enter key on the keyboard while password editText is active,
        //it should trigger btnSignUp onClickListener
        edtLoginPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER &&
                        event.getAction() == KeyEvent.ACTION_DOWN) {

                    onClick(btnLoginActivity);

                }
                return false;
            }
        });

        btnLoginActivity = findViewById(R.id.btnLoginActivity);
        btnSignUpLoginActivity = findViewById(R.id.btnSignUpLoginActivity);

        btnLoginActivity.setOnClickListener(this);
        btnSignUpLoginActivity.setOnClickListener(this);

        if (ParseUser.getCurrentUser() != null) {

            // ParseUser.getCurrentUser().logOut();
            transitionToSocialMediaActivity();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnLoginActivity:
                if (edtLoginEmail.getText().toString().equals("") ||
                        (edtLoginPassword.getText().toString().equals(""))){
                    FancyToast.makeText(LoginActivity.this,
                            "Email or Password field cannot be empty",
                            Toast.LENGTH_SHORT, FancyToast.INFO,
                            false).show();
                }
                else{
                    ParseUser.logInInBackground(edtLoginEmail.getText().toString(),
                            edtLoginPassword.getText().toString(),
                            new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {

                                    if (user != null && e == null) {

                                        FancyToast.makeText(LoginActivity.this,
                                                user.getUsername() + " is Logged in successfully",
                                                Toast.LENGTH_SHORT, FancyToast.SUCCESS,
                                                false).show();

                                        transitionToSocialMediaActivity();
                                    }
                                    else{
                                        FancyToast.makeText(LoginActivity.this,
                                                e.getMessage(),
                                                Toast.LENGTH_SHORT, FancyToast.ERROR,
                                                false).show();
                                    }
                                }
                            });
                }

                break;

            case R.id.btnSignUpLoginActivity:
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                break;


        }
    }

    //Dismiss the keyboard when an empty space on root layout is tapped
    public void rootLoginLayout(View view) {
        try {

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    private void transitionToSocialMediaActivity(){
        Intent i = new Intent(this, SocialMediaActivity.class);
        startActivity(i);
    }
}
