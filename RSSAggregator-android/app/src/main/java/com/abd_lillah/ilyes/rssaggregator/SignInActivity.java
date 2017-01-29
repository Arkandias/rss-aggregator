package com.abd_lillah.ilyes.rssaggregator;

/**
 * Created by ilyes on 29/01/2017.
 */
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SignInActivity extends Activity
{
    Button btnSignIn,btnSignUp;
    LoginDataBaseAdapter loginDataBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // create a instance of SQLite Database
        loginDataBaseAdapter=new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();

        // Get The Reference Of Buttons
        btnSignIn=(Button)findViewById(R.id.buttonSignIn);
        btnSignUp=(Button)findViewById(R.id.buttonSignUP);

        // Set OnClick Listener on SignIn button
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
               signIn(v);
            }
        });

        // Set OnClick Listener on SignUp button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub

                /// Create Intent for SignUpActivity  abd Start The Activity
                Intent intentSignUP=new Intent(getApplicationContext(),SignUPActivity.class);
                startActivity(intentSignUP);
            }
        });
    }
    // Methods to handleClick Event of Sign In Button
    public void signIn(View V)
    {
        // get the References of views
        final  EditText editTextUserName=(EditText)findViewById(R.id.editTextUserNameToLogin);
        final  EditText editTextPassword=(EditText)findViewById(R.id.editTextPasswordToLogin);

        Button btnSignIn=(Button) findViewById(R.id.buttonSignIn);

        // Set On ClickListener
        btnSignIn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // get The User name and Password
                Log.v("Trigger", "Passed");
                String userName=editTextUserName.getText().toString();
                String password=editTextPassword.getText().toString();

                // fetch the Password form database for respective user name
                String storedPassword=loginDataBaseAdapter.getSingleEntry(userName);
                String storedUserID=loginDataBaseAdapter.getSingleID(userName);
                Log.v("USER_ID", storedUserID);
                // check if the Stored password matches with  Password entered by user
                if(password.equals(storedPassword))
                {
                    Snackbar.make(v
                            , "Login Successful, Welcome " + userName, Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .show();
                    Intent intentSuccess = new Intent(SignInActivity.this ,MainActivity.class);
                    intentSuccess.putExtra("user_id", storedUserID);
                    startActivity(intentSuccess);
                }
                else
                {
                    Snackbar.make(v
                            , "Credentials does not match, please retry", Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        loginDataBaseAdapter.close();
    }
}