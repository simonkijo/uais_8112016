package com.uais.uais;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.uais.uais.Utils.UrlProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class Registration extends AppCompatActivity {

    EditText mFname,mMname,mSname,mPhone,mEmail,mNationality,mUname,mPwd,mRepwd;
    RadioButton mRadioValue;
    RadioGroup mRadioGroup;
    Button mRegister;
    TextView mLogin;

    String mFname_,mMname_,mSname_,mPhone_,mEmail_,mNationality_,mUname_,mPwd_,mRepwd_,mRadioValue_;
    View mProgressView;
    View mRegistrationFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mFname = (EditText)findViewById(R.id.fname);
        mMname = (EditText)findViewById(R.id.mname);
        mSname = (EditText)findViewById(R.id.sname);
        mPhone = (EditText)findViewById(R.id.phoneno);
        mEmail = (EditText)findViewById(R.id.email);
        mNationality = (EditText)findViewById(R.id.nationality);
        mUname = (EditText)findViewById(R.id.uname);
        mPwd = (EditText)findViewById(R.id.pwd);
        mRepwd = (EditText)findViewById(R.id.repwd);
        mRadioGroup = (RadioGroup)findViewById(R.id.gender);

        mLogin = (TextView) findViewById(R.id.tv_login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent it = new Intent(Registration.this,LoginActivity.class);
                startActivity(it);
                finish();*/
                onBackPressed();
            }
        });

        mRepwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.reg_ || id == EditorInfo.IME_NULL) {
                    //do validation and register
                    attemptRegistration();
                    //forces hide soft keyboard.
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mRepwd.getWindowToken(),0);
                    return true;
                }
                return false;
            }
        });

        mRegister = (Button) findViewById(R.id.btn_register);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do validation and register
                attemptRegistration();
                //forces hide soft keyboard.
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mRepwd.getWindowToken(),0);
            }
        });

        mRegistrationFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }
    //get values from fields
    private void getValues(){
        mFname_ = mFname.getText().toString();
        mMname_ = mMname.getText().toString();
        mSname_ = mSname.getText().toString();
        mPhone_ = mPhone.getText().toString();
        mEmail_ = mEmail.getText().toString();
        mNationality_ = mNationality.getText().toString();
        mUname_ = mUname.getText().toString();
        mPwd_ = mPwd.getText().toString();
        mRepwd_ = mRepwd.getText().toString();

        mRadioValue = (RadioButton)findViewById(mRadioGroup.getCheckedRadioButtonId());
        mRadioValue_ = mRadioValue.getText().toString();
    }
    //attempt to register and check all validations
    private void attemptRegistration(){
        //reset errors
        mFname.setError(null); mMname.setError(null); mSname.setError(null); mPhone.setError(null); mEmail.setError(null);
        mNationality.setError(null); mUname.setError(null); mPwd.setError(null); mRepwd.setError(null);

        getValues();

        boolean cancel = false;
        View focusView = null;

        //check for validations
        if(TextUtils.isEmpty(mFname_.trim())){
            mFname.setError(getString(R.string.error_field_required));
            focusView = mFname;
            cancel = true;
        }else if(!isNameValid(mFname_)){
            mFname.setError(getString(R.string.error_name_invalid));
            focusView = mFname;
            cancel = true;
        }
        if(TextUtils.isEmpty(mMname_.trim())){
            mMname.setError(getString(R.string.error_field_required));
            focusView = mMname;
            cancel = true;
        }else if(!isNameValid(mMname_)){
            mMname.setError(getString(R.string.error_name_invalid));
            focusView = mMname;
            cancel = true;
        }
        if(TextUtils.isEmpty(mSname_.trim())){
            mSname.setError(getString(R.string.error_field_required));
            focusView = mSname;
            cancel = true;
        }else if(!isNameValid(mSname_)){
            mSname.setError(getString(R.string.error_name_invalid));
            focusView = mSname;
            cancel = true;
        }
        if(TextUtils.isEmpty(mPhone_.trim())){
            mPhone.setError(getString(R.string.error_field_required));
            focusView = mPhone;
            cancel = true;
        }else if(!isPhoneValid(mPhone_)){
            mPhone.setError(getString(R.string.error_phone_invalid));
            focusView = mPhone;
            cancel = true;
        }
        if(TextUtils.isEmpty(mEmail_.trim())){
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        }else if(!isEmailValid(mEmail_)){
            mEmail.setError(getString(R.string.error_email_invalid));
            focusView = mEmail;
            cancel = true;
        }
        if(TextUtils.isEmpty(mNationality_.trim())){
            mNationality.setError(getString(R.string.error_field_required));
            focusView = mNationality;
            cancel = true;
        }else if(!isNationalityValid(mNationality_)){
            mNationality.setError(getString(R.string.error_nat_invalid));
            focusView = mNationality;
            cancel = true;
        }
        if(TextUtils.isEmpty(mUname_.trim())){
            mUname.setError(getString(R.string.error_field_required));
            focusView = mUname;
            cancel = true;
        }
        if(TextUtils.isEmpty(mPwd_.trim())){
            mPwd.setError(getString(R.string.error_field_required));
            focusView = mPwd;
            cancel = true;
        }else if(!isPasswordValid(mPwd_)){
            mPwd.setError(getString(R.string.error_invalid_password));
            focusView = mPwd;
            cancel = true;
        }
        if(TextUtils.isEmpty(mRepwd_.trim())){
            mRepwd.setError(getString(R.string.error_field_required));
            focusView = mRepwd;
            cancel = true;
        }else if(!isPasswordMatch(mPwd_,mRepwd_)){
            mRepwd.setError(getString(R.string.error_invalid_pwd_match));
            focusView = mRepwd;
            cancel = true;
        }
        if(cancel){
            focusView.requestFocus();
        }else{
            //show progress spinner
            showProgress(true);
            //send data to server
            UserRegisterData(mFname_,mMname_,mSname_,mPhone_,mEmail_,mRadioValue_,mNationality_,mUname_,mPwd_);
        }
    }
    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }
    private boolean isPasswordMatch(String pwd, String repwd) {
        return pwd.equals(repwd);
    }
    private boolean isEmailValid(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isNameValid(String name){
        String NAME = "^[A-Za-z]+$";
        return Pattern.compile(NAME).matcher(name).matches();
    }
    private boolean isNationalityValid(String nat){
        String NAT = "^[A-Za-z ]+$";
        return Pattern.compile(NAT).matcher(nat).matches();
    }
    private boolean isPhoneValid(String ph){
        String PH = "^\\+?([0-9]{3})\\)?[-. ]?([0-9]{3})[-. ]?([0-9]{6})$";
        return Pattern.compile(PH).matcher(ph).matches();
    }
    //Shows the progress UI and hides the registration form.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegistrationFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegistrationFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //Represents an asynchronous registration task.
    public void UserRegisterData(String fname, String mname,String sname, String phone,String email,String gender,String nat,String uname,String pwd){
        RequestParams params = new RequestParams();
        params.put("fname",fname);
        params.put("mname",mname);
        params.put("sname",sname);
        params.put("phone",phone);
        params.put("email",email);
        params.put("gender",gender);
        params.put("nat",nat);
        params.put("uname",uname);
        params.put("pwd",pwd);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(UrlProvider.REGISTER,params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                showProgress(false);
                String id = null;
                String status = null;
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject jsondata = response.getJSONObject(i);
                        id = jsondata.getString("id_no");
                        status = jsondata.getString("status");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(id.equals("0") && status.equals("success")){
                    Intent lg = new Intent(Registration.this,LoginActivity.class);
                    lg.putExtra("success_tag","success");
                    startActivity(lg);

                    finish();
                }else if(id.equals("1") && status.equals("fail")){
                    showAlertDialog(Registration.this,getResources().getString(R.string.db_error),getResources().getString(R.string.db_error_sms),false);
                }else if(id.equals("false") && status.equals("false")){
                    showAlertDialog(Registration.this,getResources().getString(R.string.not_inSystem),getResources().getString(R.string.not_inSystem_sms),false);
                }else if(id.equals("2") && status.equals("fail")){
                    showAlertDialog(Registration.this,getString(R.string.info),getString(R.string.info_lecturer),false);
                }else if(id.equals("3") && status.equals("registered")){
                    showAlertDialog(Registration.this,getString(R.string.info),getString(R.string.registered),false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                showProgress(false);

            }
        });
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
}
