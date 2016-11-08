package com.uais.uais;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.uais.uais.detectinternetconnection.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class PasswordRecovery extends AppCompatActivity {

    // UI references.
    private EditText mEmailView, mUnameView;
    private View mProgressView;
    private View mRecoverFormView;

    // flag for Network connection status
    Boolean isNetworkPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        // get Network status
        isNetworkPresent = ConnectionDetector.isNetworkAvailable(getApplicationContext());

        // Set up the login form.
        mUnameView = (EditText) findViewById(R.id.uname_r);

        mEmailView = (EditText) findViewById(R.id.email_r);
        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.recover || id == EditorInfo.IME_NULL) {
                    if(isNetworkPresent) {
                        attemptRecover();
                    }else{
                        showAlertDialog(PasswordRecovery.this,getResources().getString(R.string.no_connection),getResources().getString(R.string.no_connection_sms),false);
                    }
                    //forces hide soft keyboard.
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mEmailView.getWindowToken(),0);
                    return true;
                }
                return false;
            }
        });

        final TextView mRecover = (TextView)findViewById(R.id.tv_login_r);
        mRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(PasswordRecovery.this,LoginActivity.class);
                startActivity(i);*/
                onBackPressed();
            }
        });

        Button mBtnRecover = (Button) findViewById(R.id.btn_recover);
        mBtnRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkPresent) {
                    attemptRecover();
                }else{
                    showAlertDialog(PasswordRecovery.this,getResources().getString(R.string.no_connection),getResources().getString(R.string.no_connection_sms),false);
                }
                //forces hide soft keyboard.
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mEmailView.getWindowToken(),0);
            }
        });

        mRecoverFormView = findViewById(R.id.recover_form);
        mProgressView = findViewById(R.id.recover_progress);
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
     * Attempts to recover password
     * If there are form errors, the
     * errors are presented and no actual recovery password attempt is made.
     */
    private void attemptRecover() {
        // Reset errors.
        mUnameView.setError(null);
        mEmailView.setError(null);
        // Store values at the time of the login attempt.
        String uname = mUnameView.getText().toString();
        String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(email.trim())){
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }else if(!isEmailValid(email)){
            mEmailView.setError(getString(R.string.error_email_invalid));
            focusView = mEmailView;
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
            UserRecoverData(uname,email);
            Log.d(getClass().getName(),uname+" : "+email);
        }
    }

    private boolean isEmailValid(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

            mRecoverFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRecoverFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRecoverFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRecoverFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous recover password task
     */
    public void UserRecoverData(String uname,String email){
        RequestParams params = new RequestParams();
        params.put("uname",uname);
        params.put("email",email);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(UrlProvider.RECOVER, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(getClass().getName()," statusCode: "+statusCode);

                showProgress(false);

                String status = null;
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject jsondata = response.getJSONObject(i);
                        status = jsondata.getString("success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(getClass().getName()," status: "+status);

                if(status.equals("success")){
                    mUnameView.setText(""); //empty the input username
                    mEmailView.setText("");  //empty the input email
                    showAlertDialog(PasswordRecovery.this,getString(R.string.successful),getString(R.string.pwd_recover_sms),true);
                }else if(status.equals("fail")){
                    showAlertDialog(PasswordRecovery.this,getString(R.string.error),getString(R.string.recover_error_sms),false);
                }else if(status.equals("invalid")){
                    showAlertDialog(PasswordRecovery.this,getString(R.string.error),getString(R.string.invalid_ue),false);
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
