package com.uais.uais.message.master_child;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.uais.uais.R;
import com.uais.uais.Utils.FilterData;
import com.uais.uais.Utils.UrlProvider;
import com.uais.uais.detectinternetconnection.ConnectionDetector;
import com.uais.uais.message.DataConstructor;
import com.uais.uais.message.RecyclerAdapterReadMaster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import it.neokree.materialnavdrawer.MaterialNavigationDrawer;

import static com.uais.uais.Utils.duplicateRemoval.dupSender;

/**
 * Created by HP on 10/25/2016.
 */

public class MasterFragmentRead extends Fragment {
    private static final String SESSIONID = "MasterFragmentRead$session_id";
    private static final String TWONAMES = "MasterFragmentRead$twoNames";
    private static final String THREENAMES = "MasterFragmentRead$threeNames";
    public static View.OnClickListener onClickListener;

    RelativeLayout rl;
    // flag for Network connection status
    Boolean isNetworkPresent = false;

    String session_Id;
    ArrayList<String> from,subject,sms,time,date,reply,sender,sms_id;
    ArrayList<CharSequence> st_role,st_id_no,st_cs,st_pg,st_yr,st_sm;
    RecyclerView recyclerView;
    ImageButton composeBtn;
    Bundle bi;
    RecyclerAdapterReadMaster recyclerAdapter;

    public static MasterFragmentRead getSessionId(String sessionId,String twoNames,String threeNames){
        Bundle bundle = new Bundle();
        bundle.putString(SESSIONID,sessionId);
        bundle.putString(TWONAMES,twoNames);
        bundle.putString(THREENAMES,threeNames);
        MasterFragmentRead mfr = new MasterFragmentRead();
        mfr.setArguments(bundle);
        return mfr;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("MNDrawer Master-Child", "Master created");
        // get Network status
        isNetworkPresent = ConnectionDetector.isNetworkAvailable(getContext());

        RelativeLayout relativeLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_recyclerview,container,false);
        recyclerView = (RecyclerView)relativeLayout.findViewById(R.id.recyclerView);
        rl = (RelativeLayout)relativeLayout.findViewById(R.id.sms_pro_loading);
        composeBtn = (ImageButton)relativeLayout.findViewById(R.id.compose);

        onClickListener = new MyOnClickListener(getActivity());

        if(savedInstanceState == null) {
            showProgress(true, false);
            loadSms();
        }

        setupFAB(composeBtn);

        return relativeLayout;
    }
    private void setupFAB(ImageButton imageButton){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MaterialNavigationDrawer)getActivity()).setFragmentChild(ChildFragmentCompose.getStatus(null,null,bi.getString(TWONAMES),bi.getString(THREENAMES),session_Id,filterRole(st_role, st_id_no, session_Id),st_role,st_id_no,st_cs,st_pg,st_yr,st_sm),"Compose");
            }
        });
    }
    private void setupRecyclerView(RecyclerView recyclerView,ArrayList<String> sender) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new RecyclerAdapterReadMaster(createItemList(sender));
        recyclerView.setAdapter(recyclerAdapter);
    }
    private List<DataConstructor> createItemList(ArrayList<String> sender) {
        List<DataConstructor> itemList = new ArrayList<>();
        if (sender.size() == 0) {
            itemList.add(new DataConstructor("url", "       No Message"));
        } else {
            for (int i = 0; i < dupSender(sender).size(); i++) {
                itemList.add(new DataConstructor(UrlProvider.getImageUrl(underScoreAdder(dupSender(sender)).get(i)), dupSender(sender).get(i)));
            }
        }
        return itemList;
    }
    public class MyOnClickListener implements View.OnClickListener{
        Activity act_;
        public MyOnClickListener(Activity act){
            act_ = act;
        }
        @Override
        public void onClick(View v) {
            int sIP = recyclerView.getChildLayoutPosition(v);
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(sIP);
            TextView tv_sender = (TextView) viewHolder.itemView.findViewById(R.id.sender_name);
            String name = tv_sender.getText().toString();

            Log.d(getClass().getName()," clicked: "+name+" clicked at: "+sIP);

            if(sender.size() == 0){
                //do nothing
            }else{
                ((MaterialNavigationDrawer) act_).setFragmentChild(ChildFragmentReadMaster.getAllDataRead(
                        FilterData.replyId(reply,sender,name),FilterData.senderPN(sender,name),FilterData.subjectPN(subject,sender,name),
                        FilterData.fromPN(from,sender,name),FilterData.datePN(date,sender,name),FilterData.timePN(time,sender,name),
                        FilterData.smsPN(sms,sender,name),FilterData.smsIdPN(sms_id,sender,name),
                        session_Id,bi.getString(TWONAMES),bi.getString(THREENAMES),
                        st_role,st_id_no,st_cs,st_pg,st_yr,st_sm
                ), "Message");
            }
        }
    }

    private void loadSms(){
        bi = getArguments();
        session_Id = bi.getString(SESSIONID);
        if(isNetworkPresent) {
            final RequestParams params = new RequestParams();
            params.put("stId", session_Id);

            final AsyncHttpClient client = new AsyncHttpClient();
            client.post(UrlProvider.LOAD_ROLE, params, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d(getClass().getName()," statusCode: "+statusCode+" role loaded");

                    if (isAdded() && getActivity() != null) {  //this isAdded() removes IllegalStateException if user interrupt background process
                        st_role = new ArrayList<>(); st_id_no = new ArrayList<>(); st_cs = new ArrayList<>();
                        st_pg = new ArrayList<>(); st_yr = new ArrayList<>(); st_sm = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsondata = response.getJSONObject(i);
                                st_role.add(jsondata.getString("role"));
                                st_id_no.add(jsondata.getString("id_no"));
                                st_cs.add(jsondata.getString("cs"));
                                st_pg.add(jsondata.getString("programme"));
                                st_yr.add(jsondata.getString("year"));
                                st_sm.add(jsondata.getString("semester"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Log.d(getClass().getName(), " role: " + filterRole(st_role, st_id_no, session_Id));

                        client.post(UrlProvider.LOAD_READ, params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray response_) {
                                super.onSuccess(statusCode, headers, response_);
                                Log.d(getClass().getName(), " statusCode: " + statusCode + " sms loaded");

                                try {
                                    showProgress(false, true); //remove loading spinner
                                    from = new ArrayList<>();
                                    subject = new ArrayList<>();
                                    sms = new ArrayList<>();
                                    time = new ArrayList<>();
                                    date = new ArrayList<>();
                                    reply = new ArrayList<>();
                                    sender = new ArrayList<>();
                                    sms_id = new ArrayList<>();

                                    for (int i = 0; i < response_.length(); i++) {
                                        try {
                                            JSONObject jsondata = response_.getJSONObject(i);
                                            from.add(jsondata.getString("from_"));
                                            subject.add(jsondata.getString("subject"));
                                            sms.add(jsondata.getString("sms"));
                                            time.add(jsondata.getString("time"));
                                            date.add(jsondata.getString("date_"));
                                            reply.add(jsondata.getString("reply"));
                                            sender.add(jsondata.getString("sender"));
                                            sms_id.add(jsondata.getString("inbox_pk"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    setupRecyclerView(recyclerView,sender);
                                    if (from.size() == 0) {
                                        Log.d(getClass().getName(), " no sms in inbox");
                                    } else {
                                        Log.d(getClass().getName(), " sms in inbox");
                                    }

                                }catch(IllegalStateException ise){ise.printStackTrace();}
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse_) {
                                super.onFailure(statusCode, headers, throwable, errorResponse_);
                                showProgress(false, false);  //remove loading spinner
                            }
                        });
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d(getClass().getName()," statusCode: "+statusCode);
                    showProgress(false,false); //remove loading spinner
                }
            });
        }else{
            showAlertDialog(getContext(),getResources().getString(R.string.no_connection),getResources().getString(R.string.no_connection_sms),false);
        }

    }
    public ArrayList<String> underScoreAdder(ArrayList<String> list){
        ArrayList<String> newList = new ArrayList<>();
        for(int i=0;i<list.size();i++){
            newList.add(list.get(i).replace(" ","_"));
        }
        return newList;
    }
    public CharSequence filterRole(ArrayList<CharSequence> role, ArrayList<CharSequence> id, CharSequence compareId){
        CharSequence role_ = null;
        for(int i=0;i<role.size();i++){
            if(id.get(i).equals(compareId)){
                role_ = role.get(i);
            }
        }
        return role_;
    }
    //Shows the progress UI and hides the other layout.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show, boolean fab) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            composeBtn.setVisibility(fab ? View.VISIBLE : View.GONE);
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            rl.setVisibility(show ? View.VISIBLE : View.GONE);
            rl.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rl.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            composeBtn.setVisibility(fab ? View.VISIBLE : View.GONE);
            rl.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    //alert dialog
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(Html.fromHtml("<font color='#000'>"+title+"</font>"));

        // Setting Dialog Message
        alertDialog.setMessage(Html.fromHtml("<font color='#000'>"+message+"</font>"));

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
