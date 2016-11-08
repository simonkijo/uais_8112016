package com.uais.uais.message;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.uais.uais.LoginActivity;
import com.uais.uais.R;
import com.uais.uais.Utils.UrlProvider;
import com.uais.uais.detectinternetconnection.ConnectionDetector;
import com.uais.uais.message.master_child.MasterFragmentInbox;
import com.uais.uais.message.master_child.MasterFragmentOutBox;
import com.uais.uais.message.master_child.MasterFragmentRead;
import com.uais.uais.message.master_child.MasterFragmentTrash;

import java.util.ArrayList;

import it.neokree.materialnavdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavdrawer.elements.MaterialAccount;
import it.neokree.materialnavdrawer.elements.listeners.MaterialAccountListener;


public class Accounts extends MaterialNavigationDrawer implements MaterialAccountListener {
    // flag for Network connection status
    Boolean isNetworkPresent = false;
    ArrayList<String> read,trash,reply;
    String sessionName, photo_name, studentId, two_names, three_names;
    ArrayList<Integer> inboxCount,readCount,outboxCount,trashCount;
    int ib,rb,ob,tb;
    SharedPreferences sharedPrefs;

    @Override
    public void init(Bundle savedInstanceState) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sid = sharedPrefs.getString("student_id","");
        String fname_ = sharedPrefs.getString("fname","");
        String mname_ = sharedPrefs.getString("mname","");
        String sname_ = sharedPrefs.getString("sname","");
        // get Network status
        isNetworkPresent = ConnectionDetector.isNetworkAvailable(this);

        sessionName = fname_ + " " + sname_;
        photo_name = fname_ + "_" + mname_ + "_" + sname_;
        two_names = fname_ + " " + sname_;
        three_names = fname_ + " " + mname_ + " " + sname_;
        studentId = sid;

        if(sid.equals("")){
            //if user logout and receive a message will be directed to login first
            Intent i = new Intent(Accounts.this,LoginActivity.class);
            startActivity(i);
            // add accounts
            MaterialAccount account = new MaterialAccount(this.getResources(), sessionName, "", UrlProvider.getImageUrl(photo_name), R.drawable.mat3);
            this.addAccount(account);
            // create sections
            this.addSection(newSection("Inbox", R.mipmap.ic_inbox_black_18dp, MasterFragmentInbox.getSessionId(studentId, two_names, three_names)).setSectionColor(Color.parseColor("#558b2f")));
            this.addSection(newSection("Read",R.mipmap.ic_mail_outline_black_18dp, MasterFragmentRead.getSessionId(studentId, two_names, three_names)).setSectionColor(Color.parseColor("#558b2f")));
            this.addSection(newSection("Outbox",R.mipmap.ic_mail_black_18dp,MasterFragmentOutBox.getSessionId(studentId, two_names, three_names)).setSectionColor(Color.parseColor("#558b2f")));
            this.addSection(newSection("Trash",R.mipmap.ic_delete_black_18dp, MasterFragmentTrash.getSessionId(studentId, two_names, three_names)).setSectionColor(Color.parseColor("#558b2f")));
        }else {
            // add accounts
            MaterialAccount account = new MaterialAccount(this.getResources(), sessionName, "", UrlProvider.getImageUrl(photo_name), R.drawable.mat3);
            this.addAccount(account);
            // create sections
            this.addSection(newSection("Inbox", R.mipmap.ic_inbox_black_18dp, MasterFragmentInbox.getSessionId(studentId, two_names, three_names)).setSectionColor(Color.parseColor("#558b2f")));
            this.addSection(newSection("Read",R.mipmap.ic_mail_outline_black_18dp,MasterFragmentRead.getSessionId(studentId, two_names, three_names)).setSectionColor(Color.parseColor("#558b2f")));
            this.addSection(newSection("Outbox",R.mipmap.ic_mail_black_18dp, MasterFragmentOutBox.getSessionId(studentId, two_names, three_names)).setSectionColor(Color.parseColor("#558b2f")));
            this.addSection(newSection("Trash",R.mipmap.ic_delete_black_18dp,MasterFragmentTrash.getSessionId(studentId, two_names, three_names)).setSectionColor(Color.parseColor("#558b2f")));
        }
    }

    @Override
    public void onAccountOpening(MaterialAccount account) {

    }

    @Override
    public void onChangeAccount(MaterialAccount newAccount) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        // set the indicator for child fragments
        // N.B. call this method AFTER the init() to leave the time to instantiate the ActionBarDrawerToggle
        this.setHomeAsUpIndicator(R.mipmap.ic_arrow_back_white_24dp);
    }

    @Override
    public void onHomeAsUpSelected() {
        // when the back arrow is selected this method is called
    }
    //load and count inbox, read, outbox and trash
    /*private void loadCount(String sessionId){
        if(isNetworkPresent) {
            RequestParams params = new RequestParams();
            params.put("stId", sessionId);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post("http://www.uais.co.nf/mobile/loadCountSms", params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d(getClass().getName()," statusCode: "+statusCode+" data loaded for count");

                    read =new ArrayList<String>(); trash =new ArrayList<String>(); reply =new ArrayList<String>();
                    inboxCount = new ArrayList<>(); readCount = new ArrayList<>(); outboxCount = new ArrayList<>(); trashCount = new ArrayList<>();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsondata = response.getJSONObject(i);
                            read.add(jsondata.getString("read_"));
                            trash.add(jsondata.getString("trash"));
                            reply.add(jsondata.getString("reply"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    //set data to ui
                    Log.d(getClass().getName()," readCount: "+read.size()+" trashCount: "+trash.size()+" replyCount: "+reply.size());
                    for(int i=0;i<read.size();i++){
                        //count inbox
                        if(read.get(i).equals("unread") && trash.get(i).equals("untrash") && !reply.get(i).equals(studentId)){
                            inboxCount.add(i);
                        }
                        //read count
                        if(read.get(i).equals("read") && trash.get(i).equals("untrash") && !reply.get(i).equals(studentId)){
                            readCount.add(i);
                        }
                        //outbox count
                        if(read.get(i).equals("unread") && trash.get(i).equals("untrash") && reply.get(i).equals(studentId)){
                            outboxCount.add(i);
                        }
                        //trash count
                        if((read.get(i).equals("read") && trash.get(i).equals("trash") && !reply.get(i).equals(studentId)) || (read.get(i).equals("unread") && trash.get(i).equals("trash") && reply.get(i).equals(studentId))){
                            trashCount.add(i);
                        }
                    }

                    if(inboxCount.size() == 0){
                        ib = 0;
                    }else{
                        ib = inboxCount.size();
                    }

                    if(readCount.size() == 0){
                        rb = 0;
                    }else{
                        rb = readCount.size();
                    }

                    if(outboxCount.size() == 0){
                        ob = 0;
                    }else {
                        ob = outboxCount.size();
                    }

                    if(trashCount.size() == 0){
                        tb = 0;
                    }else{
                        tb = trashCount.size();
                    }
                    Log.d(getClass().getName()," inboxCount: "+ib+" readCount: "+rb+" outboxCount: "+ob+" trashCount: "+tb);

                   }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d(getClass().getName()," statusCode: "+statusCode);

                }
            });
        }else{
            new ProfileActivity().showAlertDialog(Accounts.this,getResources().getString(R.string.no_connection),getResources().getString(R.string.no_connection_sms),false);
        }

    }*/

}
