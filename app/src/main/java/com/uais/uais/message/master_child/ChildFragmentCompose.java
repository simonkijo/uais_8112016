package com.uais.uais.message.master_child;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.uais.uais.R;
import com.uais.uais.Utils.UrlProvider;
import com.uais.uais.message.NothingSelectedSpinnerAdapterCompose;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class ChildFragmentCompose extends Fragment {

    private static final String STATUS = "ChildFragmentCompose$Status";
    private static final String CHROLE = "ChildFragmentCompose$ch_role";
    private static final String CHIDNO = "ChildFragmentCompose$ch_id_no";
    private static final String CHCS = "ChildFragmentCompose$ch_cs";
    private static final String CHPG = "ChildFragmentCompose$ch_pg";
    private static final String CHYR = "ChildFragmentCompose$ch_yr";
    private static final String CHSM = "ChildFragmentCompose$ch_sm";
    private static final String SESSION = "ChildFragmentCompose$session";
    private static final String TWONAMES = "ChildFragmentCompose$twoNames";
    private static final String THREENAMES = "ChildFragmentCompose$threeNames";
    private static final String REPLY = "ChildFragmentCompose$reply";
    private static final String SENDER = "ChildFragmentCompose$sender";

    EditText etSubject, etMessage;
    RelativeLayout btnSend;
    Spinner spinnerTo;
    String to = null;
    String sub,sms;
    Bundle cb;
    CharSequence id_ = null; CharSequence chcs_s = null; CharSequence chpg_s = null; CharSequence chyr_s = null; CharSequence chsm_s = null;

    String president[] = {"Prime Minister","Sports Minister","Food Minister","Class Representative"};
    String prime_minister[] = {"President","Sports Minister","Food Minister","Class Representative"};
    String sports_minister[] = {"President","Prime Minister","Food Minister","Class Representative"};
    String food_minister[] = {"President","Prime Minister","Sports Minister","Class Representative"};
    String class_representative[] = {"President","Prime Minister","Sports Minister","Food Minister"};
    String none[] = {"President","Prime Minister","Sports Minister","Food Minister","Class Representative"};

    public static ChildFragmentCompose getStatus(String reply,String sender,String twonames,String threenames,String session, CharSequence status,ArrayList<CharSequence> ch_role,ArrayList<CharSequence> ch_id_no,ArrayList<CharSequence> ch_cs,ArrayList<CharSequence> ch_pg,ArrayList<CharSequence> ch_yr,ArrayList<CharSequence> ch_sm){
        Bundle b = new Bundle();
        b.putString(REPLY,reply);
        b.putString(SENDER,sender);
        b.putString(TWONAMES,twonames);
        b.putString(THREENAMES,threenames);
        b.putString(SESSION,session);
        b.putCharSequence(STATUS,status);
        b.putCharSequenceArrayList(CHROLE,ch_role);
        b.putCharSequenceArrayList(CHIDNO,ch_id_no);
        b.putCharSequenceArrayList(CHCS,ch_cs);
        b.putCharSequenceArrayList(CHPG,ch_pg);
        b.putCharSequenceArrayList(CHYR,ch_yr);
        b.putCharSequenceArrayList(CHSM,ch_sm);
        ChildFragmentCompose cc = new ChildFragmentCompose();
        cc.setArguments(b);
        return cc;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("MNDrawer Master-Child","Child compose created");

        RelativeLayout relativeLayout = (RelativeLayout)inflater.inflate(R.layout.masterchild_compose,container,false);
        spinnerTo = (Spinner)relativeLayout.findViewById(R.id.to_spinner);
        etSubject = (EditText)relativeLayout.findViewById(R.id.et_subject);
        etMessage = (EditText)relativeLayout.findViewById(R.id.et_sms);
        btnSend = (RelativeLayout)relativeLayout.findViewById(R.id.iv_send_container);
        setupSend(btnSend);

        CharSequence testor = getArguments().getCharSequence(STATUS);
        if (testor.equals("None")) {
            setupSpinner(spinnerTo, none);

        } else if (testor.equals("President")) {
            setupSpinner(spinnerTo, president);

        } else if (testor.equals("Prime Minister")) {
            setupSpinner(spinnerTo, prime_minister);

        } else if (testor.equals("Sports Minister")) {
            setupSpinner(spinnerTo, sports_minister);

        } else if (testor.equals("Food Minister")) {
            setupSpinner(spinnerTo, food_minister);

        } else if (testor.equals("Class Representative")) {
            setupSpinner(spinnerTo, class_representative);

        }
        return relativeLayout;
    }

    private void setupSend(RelativeLayout relativeLayout){
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sub = etSubject.getText().toString();
                sms = etMessage.getText().toString();
                cb = getArguments();

                if((cb.getString(REPLY) != null ? (to!=null) : (to==null)) || sms.trim().equals("") || sub.trim().equals("")){
                    Log.d(getClass().getName(),"empty");
                    showAlertDialog(getContext(), getString(R.string.childfragment_compose_error), getString(R.string.childfragment_compose_errorsms), false);
                }else{
                    Log.d(getClass().getName(),"To: "+to+" Subject: "+sub+" sms: "+sms);

                    ArrayList<CharSequence> chrole = cb.getCharSequenceArrayList(CHROLE);
                    ArrayList<CharSequence> chid = cb.getCharSequenceArrayList(CHIDNO);
                    ArrayList<CharSequence> chcs = cb.getCharSequenceArrayList(CHCS);
                    ArrayList<CharSequence> chpg = cb.getCharSequenceArrayList(CHPG);
                    ArrayList<CharSequence> chyr = cb.getCharSequenceArrayList(CHYR);
                    ArrayList<CharSequence> chsm = cb.getCharSequenceArrayList(CHSM);

                    if(cb.getString(REPLY) != null && cb.getString(SENDER) != null){
                        for (int i = 0; i < chid.size(); i++) {
                            if (chid.get(i).equals(cb.getString(SESSION))) {
                                chcs_s = chcs.get(i);
                                chpg_s = chpg.get(i);
                                break;
                            }
                        }
                        id_ = cb.getString(REPLY);
                        to = cb.getString(SENDER);
                    }else if(cb.getString(REPLY) == null && cb.getString(SENDER) == null) {
                        if (to.equals("Class Representative")) {
                            //grab all class representative
                            ArrayList<CharSequence> ids = new ArrayList<>();
                            ArrayList<CharSequence> chcs_ = new ArrayList<>();
                            ArrayList<CharSequence> chpg_ = new ArrayList<>();
                            ArrayList<CharSequence> chyr_ = new ArrayList<>();
                            ArrayList<CharSequence> chsm_ = new ArrayList<>();

                            for (int i = 0; i < chrole.size(); i++) {
                                if (chrole.get(i).equals(to)) {
                                    ids.add(chid.get(i));
                                    chcs_.add(chcs.get(i));
                                    chpg_.add(chpg.get(i));
                                    chyr_.add(chyr.get(i));
                                    chsm_.add(chsm.get(i));
                                }
                            }
                            //Log.d(getClass().getName()," cr id: "+ids.size()+" cs:"+chcs_.size()+" pg: "+chpg_.size()+" yr: "+chyr_.size()+" sm: "+chsm_.size());
                            //grab class representative who study one class with student login
                            for (int i = 0; i < chid.size(); i++) {
                                if (chid.get(i).equals(cb.getString(SESSION))) {
                                    chcs_s = chcs.get(i);
                                    chpg_s = chpg.get(i);
                                    chyr_s = chyr.get(i);
                                    chsm_s = chsm.get(i);
                                    break;
                                }
                            }
                            //Log.d(getClass().getName()," chid size: "+chid.size()+" cs:"+chcs_s+" pg: "+chpg_s+" yr: "+chyr_s+" sm: "+chsm_s);
                            for (int i = 0; i < ids.size(); i++) {
                                if (chcs_.get(i).equals(chcs_s) && chpg_.get(i).equals(chpg_s) && chyr_.get(i).equals(chyr_s) && chsm_.get(i).equals(chsm_s)) {
                                    id_ = ids.get(i);
                                }
                            }
                        } else {
                            for (int i = 0; i < chid.size(); i++) {
                                if (chid.get(i).equals(cb.getString(SESSION))) {
                                    chcs_s = chcs.get(i);
                                    chpg_s = chpg.get(i);
                                    break;
                                }
                            }
                            for (int i = 0; i < chrole.size(); i++) {
                                if (chrole.get(i).equals(to)) {
                                    id_ = chid.get(i);
                                }
                            }
                        }
                    }
                    showAlertDialog(getContext(), "", "sending...", true);
                    Log.d(getClass().getName()," selected is: "+id_+" cs: "+chcs_s+" pg: "+chpg_s+" to: "+to);
                    sendMessage(id_,sub,sms,to,cb.getString(TWONAMES)+", "+chcs_s+", "+chpg_s,cb.getString(SESSION),cb.getString(THREENAMES));
                }
            }
        });
    }
    public void setupSpinner(Spinner spinner,final String[] arrayList){
        cb = getArguments();
        ArrayAdapter<String> aa = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, arrayList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        NothingSelectedSpinnerAdapterCompose nssa = new NothingSelectedSpinnerAdapterCompose(aa,R.layout.spinner_row_nothing_selected_compose,getContext());
        if(cb.getString(REPLY) != null && cb.getString(SENDER) != null) {
            CharSequence reply_role = new MasterFragmentInbox().filterRole(cb.getCharSequenceArrayList(CHROLE),cb.getCharSequenceArrayList(CHIDNO),cb.getString(REPLY));
            if(reply_role == null){
                nssa.setTxt(cb.getString(SENDER));
            }else if(reply_role.equals("None")){  //reply_role.equals("None")
                nssa.setTxt(cb.getString(SENDER));
            }else {
                nssa.setTxt(reply_role);
            }
            Log.d(getClass().getName()," reply id: "+cb.getString(REPLY)+" reply role: "+reply_role);
        }else if(cb.getString(REPLY) == null && cb.getString(SENDER) == null){
            nssa.setTxt("To");
        }
        spinner.setAdapter(nssa);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(id == -1){
                    //do nothing
                }else{
                    to = arrayList[(int)id];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void sendMessage(CharSequence table, String subject, String message, String to, String from, String reply, String sender){
        RequestParams params = new RequestParams();
        params.put("table",table);
        params.put("subject",subject);
        params.put("message",message);
        params.put("to",to);
        params.put("from",from);
        params.put("reply",reply);
        params.put("sender",sender);

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(UrlProvider.SEND_MESSAGE, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(getClass().getName()," statusCode: "+statusCode);

                String status = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsondata = response.getJSONObject(i);
                        status = jsondata.getString("success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(getClass().getName()," sms: "+status);
                if(status.equals("sent")){
                    etSubject.setText(""); etMessage.setText("");
                    //showAlertDialog(getContext(), "", "Message Sent", true);
                }else if(status.equals("fail")){
                    //showAlertDialog(getContext(), "", "Please send again", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(getClass().getName()," statusCode: "+statusCode);
            }
        });
    }
    //alert dialog
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        // Setting Dialog Title
        alertDialog.setTitle(Html.fromHtml("<font color='#000'>"+title+"</font>"));  //Html.fromHtml("<font color='#000'>"+title+"</font>")
        // Setting Dialog Message
        alertDialog.setMessage(Html.fromHtml("<font color='#000'>"+message+"</font>"));  //Html.fromHtml("<font color='#000'>"+message+"</font>")
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
