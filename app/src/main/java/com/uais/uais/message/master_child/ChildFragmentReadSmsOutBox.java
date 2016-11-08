package com.uais.uais.message.master_child;

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

import com.uais.uais.R;

import java.util.ArrayList;

/**
 * Created by HP on 10/27/2016.
 */

public class ChildFragmentReadSmsOutBox extends Fragment {
    private static final String SUB = "ChildFragmentReadSmsOutBox$sub";
    private static final String FROM = "ChildFragmentReadSmsOutBox$from";
    private static final String TIME = "ChildFragmentReadSmsOutBox$time";
    private static final String SMS = "ChildFragmentReadSmsOutBox$sms";
    private static final String ROLE = "ChildFragmentReadSmsOutBox$role";
    private static final String SMSID = "ChildFragmentReadSmsOutBox$smsId";
    private static final String SESSIONID_ = "ChildFragmentReadSmsOutBox$session_Id";

    private static final String CHROLE = "ChildFragmentReadSmsOutBox$ch_role";
    private static final String CHIDNO = "ChildFragmentReadSmsOutBox$ch_id_no";
    private static final String CHCS = "ChildFragmentReadSmsOutBox$ch_cs";
    private static final String CHPG = "ChildFragmentReadSmsOutBox$ch_pg";
    private static final String CHYR = "ChildFragmentReadSmsOutBox$ch_yr";
    private static final String CHSM = "ChildFragmentReadSmsOutBox$ch_sm";
    private static final String TWONAMES = "ChildFragmentReadSmsOutBox$twoNames";
    private static final String THREENAMES = "ChildFragmentReadSmsOutBox$threeNames";

    private static final String REPLY = "ChildFragmentReadSmsOutBox$reply";
    private static final String SENDER = "ChildFragmentReadSmsOutBox$sender";

    TextView subject_read, time_read, from_read, message_read;
    RelativeLayout btn_reply;
    Bundle b;

    public static ChildFragmentReadSmsOutBox grabValues(CharSequence reply, CharSequence sender, String twonames, String threenames, CharSequence sub, CharSequence from, CharSequence time, CharSequence sms, CharSequence smsID, CharSequence role, String sessionID, ArrayList<CharSequence> ch_role, ArrayList<CharSequence> ch_id_no, ArrayList<CharSequence> ch_cs, ArrayList<CharSequence> ch_pg, ArrayList<CharSequence> ch_yr, ArrayList<CharSequence> ch_sm){
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
        ChildFragmentReadSmsOutBox cfr = new ChildFragmentReadSmsOutBox();
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
        btn_reply.setVisibility(View.GONE);

        messageText(subject_read, from_read, time_read, message_read);

        return relativeLayout;
    }

    public void messageText(TextView sub, TextView from, TextView time, TextView sms){
        b = getArguments();
        sub.setText(b.getString(SUB));
        from.setText("From: "+b.getString(FROM));
        time.setText(b.getString(TIME));
        sms.setText(Html.fromHtml(b.getString(SMS)));
    }
}
