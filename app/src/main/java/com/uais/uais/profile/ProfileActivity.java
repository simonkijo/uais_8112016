package com.uais.uais.profile;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.uais.uais.R;
import com.uais.uais.Utils.UrlProvider;
import com.uais.uais.detectinternetconnection.ConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

import static com.uais.uais.academicMaterials.AcademicActivity.setTransparent;

public class ProfileActivity extends AppCompatActivity {

    ImageView photoIv, coverIv;
    String student_id, student_pic;
    RelativeLayout mContainer, mLoading;
    TextView fullname,gender,nat;
    EditText phone_number,email,uname,pwd,repwd;
    Button save_changes;
    // flag for Network connection status
    Boolean isNetworkPresent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // get Network status
        isNetworkPresent = ConnectionDetector.isNetworkAvailable(this);

        student_id = getIntent().getExtras().getString("st_Id");
        student_pic = getIntent().getExtras().getString("prof_photo");
        Log.d(getClass().getName()," id: "+student_id+" pic: "+student_pic);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Profile");
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle("My Profile");

        photoIv = (ImageView)findViewById(R.id.profile_photo);
        coverIv = (ImageView)findViewById(R.id.image_cover);
        coverIv.setImageResource(R.drawable.mat3);
        //photoIv.setImageDrawable(new CircularImageView(getResources(),R.drawable.photo).getCircularPhoto());
        Picasso.with(this)
                .load(UrlProvider.getImageUrl(student_pic))
                .placeholder(R.drawable.empty_photo)
                .error(R.drawable.empty_photo)
                .resize(200, 200)
                .centerCrop()
                .transform(new CropCircleTransformation())
                .into(photoIv);
        mContainer = (RelativeLayout)findViewById(R.id.main_container);
        mLoading = (RelativeLayout)findViewById(R.id.loading_bg_spinner_profile);
        fullname = (TextView)findViewById(R.id.fullname);
        gender = (TextView)findViewById(R.id.gender);
        nat = (TextView)findViewById(R.id.nat);
        phone_number = (EditText)findViewById(R.id.phone_number);
        email = (EditText)findViewById(R.id.email);
        uname = (EditText)findViewById(R.id.uname);
        pwd = (EditText)findViewById(R.id.pwd);
        repwd = (EditText)findViewById(R.id.repwd);
        save_changes = (Button)findViewById(R.id.save_changes);
        sendChanges(save_changes);
        //set statusBar transparent
        setTransparent(this);
        //load profile data background
        showProgress(true,false);
        loadProfile(student_id);
    }

    private void sendChanges(Button schanges){
        schanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(getClass().getName(),"clicked");
                grabChanges();
            }
        });
    }
    private void grabChanges(){
        // Reset errors.
        phone_number.setError(null); email.setError(null); uname.setError(null); pwd.setError(null);
        repwd.setError(null);
        // Store values at the time of the upload profile changes attempt.
        String phone_d = phone_number.getText().toString();
        String email_d = email.getText().toString();
        String uname_d = uname.getText().toString();
        String pwd_d = pwd.getText().toString();
        String repwd_d = repwd.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(phone_d.trim())){
            phone_number.setError(getString(R.string.error_field_required));
            focusView = phone_number;
            cancel = true;
        }else if(!isPhoneValid(phone_d)){
            phone_number.setError(getString(R.string.error_phone_invalid));
            focusView = phone_number;
            cancel = true;
        }
        if(TextUtils.isEmpty(email_d.trim())){
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        }else if(!isEmailValid(email_d)){
            email.setError(getString(R.string.error_email_invalid));
            focusView = email;
            cancel = true;
        }
        if (TextUtils.isEmpty(uname_d.trim())) {
            uname.setError(getString(R.string.error_field_required));
            focusView = uname;
            cancel = true;
        }
        if(TextUtils.isEmpty(pwd_d.trim())){
            pwd.setError(getString(R.string.error_field_required));
            focusView = pwd;
            cancel = true;
        }else if(!isPasswordValid(pwd_d)){
            pwd.setError(getString(R.string.error_invalid_password));
            focusView = pwd;
            cancel = true;
        }
        if(TextUtils.isEmpty(repwd_d.trim())){
            repwd.setError(getString(R.string.error_field_required));
            focusView = repwd;
            cancel = true;
        }else if(!isPasswordMatch(pwd_d,repwd_d)){
            repwd.setError(getString(R.string.error_invalid_pwd_match));
            focusView = repwd;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt upload and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true,false);
            uploadProfileChanges(phone_d,email_d,uname_d,pwd_d,student_id);

            Log.d(getClass().getName()," phone_no: "+phone_d+" email: "+email_d+" username: "+uname_d+" password: "+pwd_d);
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
    private boolean isPhoneValid(String ph){
        String PH = "^\\+?([0-9]{3})\\)?[-. ]?([0-9]{3})[-. ]?([0-9]{6})$";
        return Pattern.compile(PH).matcher(ph).matches();
    }

    public class CropCircleTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            Bitmap result = getCroppedBitmapDrawable(source);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() { return "square()"; }
        //convert bitmap to circle
        Bitmap getCroppedBitmapDrawable(Bitmap bitmap) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                    bitmap.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }
    }
    //get data run background
    private void loadProfile(String studentId){
        if(isNetworkPresent) {
            RequestParams params = new RequestParams();
            params.put("stId", studentId);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(UrlProvider.STUDENT_PROFILE_DATA, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d(getClass().getName()," statusCode: "+statusCode+" profile data loaded");

                    showProgress(false,true);
                    String p_fname=null, p_mname=null , p_sname=null , p_phone=null , p_email=null , p_gender=null , p_nat=null , p_uname=null;
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsondata = response.getJSONObject(i);
                            p_fname = jsondata.getString("fname");
                            p_mname = jsondata.getString("mname");
                            p_sname = jsondata.getString("sname");
                            p_phone = jsondata.getString("phone_no");
                            p_email = jsondata.getString("email");
                            p_gender = jsondata.getString("gender");
                            p_nat = jsondata.getString("nationality");
                            p_uname = jsondata.getString("username");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //set data to ui
                    fullname.setText(p_fname+" "+p_mname+" "+p_sname);
                    phone_number.setText(p_phone);
                    email.setText(p_email);
                    gender.setText(p_gender);
                    nat.setText(p_nat);
                    uname.setText(p_uname);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d(getClass().getName()," statusCode: "+statusCode);
                    showProgress(false,false);
                }
            });
        }else{
            showAlertDialog(ProfileActivity.this,getResources().getString(R.string.no_connection),getResources().getString(R.string.no_connection_sms),false);
        }
    }
    //upload profile changes to db
    private void uploadProfileChanges(String phone_,String email_,String uname_,String pwd_,String id){
        if(isNetworkPresent) {
            RequestParams params = new RequestParams();
            params.put("phone", phone_);
            params.put("email", email_);
            params.put("uname", uname_);
            params.put("pwd", pwd_);
            params.put("stId",id);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(UrlProvider.STUDENT_UPLOAD_PROF_CHANGES, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d(getClass().getName()," statusCode: "+statusCode+" profile data uploaded");

                    showProgress(false,true);
                    String p_fname=null, p_mname=null , p_sname=null , p_phone=null , p_email=null , p_gender=null , p_nat=null , p_uname=null;
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsondata = response.getJSONObject(i);
                            p_fname = jsondata.getString("fname");
                            p_mname = jsondata.getString("mname");
                            p_sname = jsondata.getString("sname");
                            p_phone = jsondata.getString("phone_no");
                            p_email = jsondata.getString("email");
                            p_gender = jsondata.getString("gender");
                            p_nat = jsondata.getString("nationality");
                            p_uname = jsondata.getString("username");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //set data to ui
                    fullname.setText(p_fname+" "+p_mname+" "+p_sname);
                    phone_number.setText(p_phone);
                    email.setText(p_email);
                    gender.setText(p_gender);
                    nat.setText(p_nat);
                    uname.setText(p_uname);
                    pwd.setText("");  //set empty text
                    repwd.setText("");  //set empty text
                    //success status
                    showAlertDialog(ProfileActivity.this,getString(R.string.success),getString(R.string.profile_success_change_sms),true);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d(getClass().getName()," statusCode: "+statusCode);
                    showProgress(false,false);
                }
            });
        }else{
            showAlertDialog(ProfileActivity.this,getResources().getString(R.string.no_connection),getResources().getString(R.string.no_connection_sms),false);
        }
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
    //Shows the progress UI and hides the other layout.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show, final boolean container) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            mContainer.setVisibility(container ? View.VISIBLE : View.GONE);

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mLoading.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoading.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoading.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mContainer.setVisibility(container ? View.VISIBLE : View.GONE);
            mLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
