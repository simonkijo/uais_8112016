package com.uais.uais;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.uais.uais.Utils.UrlProvider;
import com.uais.uais.academicMaterials.AcademicActivity;
import com.uais.uais.detectinternetconnection.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mPwdView, mUnameView;
    private View mProgressView;
    private View mLoginFormView;

    // flag for Network connection status
    Boolean isNetworkPresent = false;

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //check if user didn't log out then log in automatic
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String s_id = sharedPrefs.getString("student_id","");
        if(!s_id.equals("")){
            Intent i = new Intent(LoginActivity.this,AcademicActivity.class);
            startActivity(i);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // get Network status
        isNetworkPresent = ConnectionDetector.isNetworkAvailable(getApplicationContext());

        //after registration intent 'success' sent here and user can login
        try {
            String b = getIntent().getExtras().getString("success_tag");
            if (b != null && b.equals("success")) {
                showAlertDialog(LoginActivity.this,getResources().getString(R.string.reg_success),getResources().getString(R.string.reg_success_sms),true);
            }
        }catch (NullPointerException e){e.printStackTrace();}
        
        // Set up the login form.
        mUnameView = (EditText) findViewById(R.id.uname);

        mPwdView = (EditText) findViewById(R.id.pwd);
        mPwdView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if(isNetworkPresent) {
                        attemptLogin();
                    }else{
                        showAlertDialog(LoginActivity.this,getResources().getString(R.string.no_connection),getResources().getString(R.string.no_connection_sms),false);
                    }
                    //forces hide soft keyboard.
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mPwdView.getWindowToken(),0);
                    return true;
                }
                return false;
            }
        });

        TextView mRecover = (TextView)findViewById(R.id.tv_recover);
        mRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,PasswordRecovery.class);
                startActivity(i);
            }
        });
        final TextView mRegister = (TextView)findViewById(R.id.tv_register);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,Registration.class);
                startActivity(i);
            }
        });
        Button mBtnSignIn = (Button) findViewById(R.id.btn_sign_in);
        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkPresent) {
                    attemptLogin();
                }else{
                    showAlertDialog(LoginActivity.this,getResources().getString(R.string.no_connection),getResources().getString(R.string.no_connection_sms),false);
                }
                //forces hide soft keyboard.
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mPwdView.getWindowToken(),0);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    //alert dialog
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.mipmap.ic_check_circle_black_18dp : R.mipmap.ic_error_black_18dp);

        // Setting OK Button
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }
    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors, the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mUnameView.setError(null);
        mPwdView.setError(null);
        // Store values at the time of the login attempt.
        String uname = mUnameView.getText().toString();
        String password = mPwdView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPwdView.setError(getString(R.string.error_field_required));
            focusView = mPwdView;
            cancel = true;
        }else if(!isPasswordValid(password)){
            mPwdView.setError(getString(R.string.error_invalid_password));
            focusView = mPwdView;
            cancel = true;
        }

        // Check for a valid user name.
        if (TextUtils.isEmpty(uname.trim())) {
            mUnameView.setError(getString(R.string.error_field_required));
            focusView = mUnameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            UserAuthData(uname,password);
            Log.d(getClass().getName(),uname+" : "+password);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login task used to authenticate
     * the user.
     */
    public void UserAuthData(String uname,String pwd){
        RequestParams params = new RequestParams();
        params.put("uname",uname);
        params.put("pwd",pwd);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(UrlProvider.LOGIN, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(getClass().getName()," statusCode: "+statusCode);

                showProgress(false);
                String id = null;
                String status = null;  String fname = null; String sname = null; String mname = null;
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject jsondata = response.getJSONObject(i);
                        id = jsondata.getString("id_no");
                        status = jsondata.getString("status");
                        fname = jsondata.getString("fname");
                        sname = jsondata.getString("sname");
                        mname = jsondata.getString("mname");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.d(getClass().getName()," status: "+status+" fname: "+fname+" mname: "+mname+" sname: "+sname+" id: "+id);
                if(status.equals("student")){
                    //put Auth credentials to SharedPreferences, second time will be logged in automatic if user didn't log out
                    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor se = sharedPrefs.edit();
                    se.putString("fname",fname);
                    se.putString("mname",mname);
                    se.putString("sname",sname);
                    se.putString("student_id",id);
                    se.putString("for_service",id);
                    se.apply();

                    //login success go to Academic Activity
                    Intent i = new Intent(LoginActivity.this,AcademicActivity.class);
                    startActivity(i);

                    finish();
                }else if(id.equals("false") && status.equals("false")){
                    showAlertDialog(LoginActivity.this,getResources().getString(R.string.not_registered),getResources().getString(R.string.not_registered_sms),false);
                }else if(status.equals("lecturer")){
                    showAlertDialog(LoginActivity.this,getString(R.string.info),getString(R.string.info_lecturer),false);
                }else if(status.equals("admin")){
                    showAlertDialog(LoginActivity.this,getString(R.string.info),getString(R.string.info_admin),false);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(getClass().getName()," statusCode: "+statusCode);
                showProgress(false);

            }
        });
    }
}
