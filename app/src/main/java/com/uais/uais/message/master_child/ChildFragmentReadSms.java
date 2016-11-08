package com.uais.uais.message.master_child;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uais.uais.LocalService;
import com.uais.uais.R;

import java.util.ArrayList;

import it.neokree.materialnavdrawer.MaterialNavigationDrawer;


public class ChildFragmentReadSms extends Fragment {
    private static final String SUB = "ChildFragmentReadSms$sub";
    private static final String FROM = "ChildFragmentReadSms$from";
    private static final String TIME = "ChildFragmentReadSms$time";
    private static final String SMS = "ChildFragmentReadSms$sms";
    private static final String ROLE = "ChildFragmentReadSms$role";
    private static final String SMSID = "ChildFragmentReadSms$smsId";
    private static final String SESSIONID_ = "ChildFragmentReadSms$session_Id";

    private static final String CHROLE = "ChildFragmentReadSms$ch_role";
    private static final String CHIDNO = "ChildFragmentReadSms$ch_id_no";
    private static final String CHCS = "ChildFragmentReadSms$ch_cs";
    private static final String CHPG = "ChildFragmentReadSms$ch_pg";
    private static final String CHYR = "ChildFragmentReadSms$ch_yr";
    private static final String CHSM = "ChildFragmentReadSms$ch_sm";
    private static final String TWONAMES = "ChildFragmentReadSms$twoNames";
    private static final String THREENAMES = "ChildFragmentReadSms$threeNames";

    private static final String REPLY = "ChildFragmentReadSms$reply";
    private static final String SENDER = "ChildFragmentReadSms$sender";

    TextView subject_read, time_read, from_read, message_read;
    RelativeLayout btn_reply;
    Bundle b;
    private LocalService s;
    volatile boolean stop = true;

    public static ChildFragmentReadSms grabValues(CharSequence reply,CharSequence sender,String twonames,String threenames,CharSequence sub, CharSequence from, String time, CharSequence sms, CharSequence smsID, CharSequence role, String sessionID, ArrayList<CharSequence> ch_role, ArrayList<CharSequence> ch_id_no, ArrayList<CharSequence> ch_cs, ArrayList<CharSequence> ch_pg, ArrayList<CharSequence> ch_yr, ArrayList<CharSequence> ch_sm){
        Bundle b = new Bundle();
        b.putCharSequence(REPLY,reply);
        b.putCharSequence(SENDER,sender);
        b.putString(TWONAMES,twonames);
        b.putString(THREENAMES,threenames);
        b.putCharSequence(SUB,sub);
        b.putCharSequence(FROM,from);
        b.putCharSequence(TIME,time);
        b.putCharSequence(SMS,sms);
        b.putCharSequence(ROLE,role);
        b.putCharSequence(SMSID,smsID);
        b.putString(SESSIONID_,sessionID);
        b.putCharSequenceArrayList(CHROLE,ch_role);
        b.putCharSequenceArrayList(CHIDNO,ch_id_no);
        b.putCharSequenceArrayList(CHCS,ch_cs);
        b.putCharSequenceArrayList(CHPG,ch_pg);
        b.putCharSequenceArrayList(CHYR,ch_yr);
        b.putCharSequenceArrayList(CHSM,ch_sm);
        ChildFragmentReadSms cfr = new ChildFragmentReadSms();
        cfr.setArguments(b);
        return cfr;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("MNDrawer Master-Child","Child read sms created");

        RelativeLayout relativeLayout = (RelativeLayout)inflater.inflate(R.layout.masterchild_readsms,container,false);
        subject_read = (TextView)relativeLayout.findViewById(R.id.tv_subject);
        time_read = (TextView)relativeLayout.findViewById(R.id.tv_time);
        from_read = (TextView)relativeLayout.findViewById(R.id.tv_from);
        message_read = (TextView)relativeLayout.findViewById(R.id.tv_message);
        btn_reply = (RelativeLayout)relativeLayout.findViewById(R.id.iv_reply_container);

        messageText(subject_read, from_read, time_read, message_read);
        setupReply(btn_reply);
        return relativeLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSmsRead();
    }

    public void messageText(TextView sub, TextView from, TextView time, TextView sms){
        b = getArguments();
        sub.setText(b.getCharSequence(SUB));
        from.setText("From: "+b.getCharSequence(FROM));
        time.setText(b.getCharSequence(TIME));
        sms.setText(Html.fromHtml(b.getString(SMS)));
    }
    private void setupReply(RelativeLayout relativeLayout){
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MaterialNavigationDrawer)getActivity()).setFragmentChild(ChildFragmentCompose.getStatus(b.getString(REPLY),b.getString(SENDER),b.getString(TWONAMES),b.getString(THREENAMES),b.getString(SESSIONID_),b.getCharSequence(ROLE),b.getCharSequenceArrayList(CHROLE),b.getCharSequenceArrayList(CHIDNO),b.getCharSequenceArrayList(CHCS),b.getCharSequenceArrayList(CHPG),b.getCharSequenceArrayList(CHYR),b.getCharSequenceArrayList(CHSM)),"Compose");
            }
        });
    }
    private void setSmsRead(){
        //delay for 6 sec
        Thread t = new Thread(){
            public void run() {
                try {
                    sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if(stop) {
                        //Log.d(getClass().getName()," smsid:cfrs "+b.getString(SMSID)+" sessionid:cfrs "+b.getString(SESSIONID_));
                        s.setRead(b.getString(SMSID), b.getString(SESSIONID_));
                        stop = false;
                    }
                }
            }
        };
        t.start();
    }
    @Override
    public void onResume() {
        super.onResume();
        Context context = getContext();
        Intent intent= new Intent(context, LocalService.class);
        context.bindService(intent, mConnection,Context.BIND_AUTO_CREATE);
    }
    @Override
    public void onPause() {
        super.onPause();
        //getContext().unbindService(mConnection);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            LocalService.MyBinder b = (LocalService.MyBinder) binder;
            s = b.getService();
            Log.d("onResume : ","connected to service");
        }

        public void onServiceDisconnected(ComponentName className) {
            s = null;
        }
    };
}
