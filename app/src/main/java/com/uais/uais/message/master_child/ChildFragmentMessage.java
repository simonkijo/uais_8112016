package com.uais.uais.message.master_child;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uais.uais.R;
import com.uais.uais.message.DataConstructor;
import com.uais.uais.message.RecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialnavdrawer.MaterialNavigationDrawer;

/**
 * Created by HP on 11/7/2016.
 */

public class ChildFragmentMessage extends Fragment {
    private static final String SESSIONID = "ChildFragmentMessage$session_id";
    private static final String TWONAMES = "ChildFragmentMessage$twoNames";
    private static final String THREENAMES = "ChildFragmentMessage$threeNames";
    private static final String CHROLE = "ChildFragmentMessage$ch_role";
    private static final String CHIDNO = "ChildFragmentMessage$ch_id_no";
    private static final String CHCS = "ChildFragmentMessage$ch_cs";
    private static final String CHPG = "ChildFragmentMessage$ch_pg";
    private static final String CHYR = "ChildFragmentMessage$ch_yr";
    private static final String CHSM = "ChildFragmentMessage$ch_sm";
    private static final String REPLY = "ChildFragmentMessage$reply";
    private static final String SENDER = "ChildFragmentMessage$sender";
    private static final String SUBJECT = "ChildFragmentMessage$subject";
    private static final String FROM = "ChildFragmentMessage$from";
    private static final String DATE = "ChildFragmentMessage$date";
    private static final String TIME = "ChildFragmentMessage$time";
    private static final String SMS = "ChildFragmentMessage$sms";
    private static final String SMS_ID = "ChildFragmentMessage$smsId";
    public static View.OnClickListener onClickListener;
    RecyclerView recyclerView;
    ImageButton composeBtn;
    Bundle bi;

    public static ChildFragmentMessage getAllData(
            ArrayList<CharSequence> reply,ArrayList<CharSequence> sender,ArrayList<CharSequence> subject,
            ArrayList<CharSequence> from,ArrayList<CharSequence> date,ArrayList<CharSequence> time,
            ArrayList<CharSequence> sms,ArrayList<CharSequence> sms_id,
            String sessionId,String twoNames,String threeNames,
            ArrayList<CharSequence> ch_role,ArrayList<CharSequence> ch_id_no,ArrayList<CharSequence> ch_cs,
            ArrayList<CharSequence> ch_pg,ArrayList<CharSequence> ch_yr,ArrayList<CharSequence> ch_sm){
        Bundle bundle = new Bundle();
        bundle.putString(SESSIONID,sessionId);
        bundle.putString(TWONAMES,twoNames);
        bundle.putString(THREENAMES,threeNames);
        bundle.putCharSequenceArrayList(CHROLE,ch_role);
        bundle.putCharSequenceArrayList(CHIDNO,ch_id_no);
        bundle.putCharSequenceArrayList(CHCS,ch_cs);
        bundle.putCharSequenceArrayList(CHPG,ch_pg);
        bundle.putCharSequenceArrayList(CHYR,ch_yr);
        bundle.putCharSequenceArrayList(CHSM,ch_sm);

        bundle.putCharSequenceArrayList(REPLY,reply);
        bundle.putCharSequenceArrayList(SENDER,sender);
        bundle.putCharSequenceArrayList(SUBJECT,subject);
        bundle.putCharSequenceArrayList(FROM,from);
        bundle.putCharSequenceArrayList(DATE,date);
        bundle.putCharSequenceArrayList(TIME,time);
        bundle.putCharSequenceArrayList(SMS,sms);
        bundle.putCharSequenceArrayList(SMS_ID,sms_id);

        ChildFragmentMessage cfm = new ChildFragmentMessage();
        cfm.setArguments(bundle);
        return cfm;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("MNDrawer Master-Child", "Master filtered created");

        RelativeLayout relativeLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_recyclerview,container,false);
        recyclerView = (RecyclerView)relativeLayout.findViewById(R.id.recyclerView);
        composeBtn = (ImageButton)relativeLayout.findViewById(R.id.compose);
        bi = getArguments();

        setupFAB(composeBtn);
        setupRecyclerView(recyclerView, bi.getCharSequenceArrayList(SMS), bi.getCharSequenceArrayList(SUBJECT), bi.getCharSequenceArrayList(TIME));
        return relativeLayout;
    }
    private void setupFAB(ImageButton imageButton){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MaterialNavigationDrawer)getActivity()).setFragmentChild(ChildFragmentCompose.getStatus(null,null,bi.getString(TWONAMES),bi.getString(THREENAMES),bi.getString(SESSIONID),filterRole(bi.getCharSequenceArrayList(CHROLE), bi.getCharSequenceArrayList(CHIDNO), bi.getString(SESSIONID)),bi.getCharSequenceArrayList(CHROLE),bi.getCharSequenceArrayList(CHIDNO),bi.getCharSequenceArrayList(CHCS),bi.getCharSequenceArrayList(CHPG),bi.getCharSequenceArrayList(CHYR),bi.getCharSequenceArrayList(CHSM)),"Compose");
            }
        });
    }
    private void setupRecyclerView(RecyclerView recyclerView, ArrayList<CharSequence> sms, ArrayList<CharSequence> subject, ArrayList<CharSequence> time) {
        onClickListener = new MyOnClickListener(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(createItemList(sms,subject,time));
        recyclerView.setAdapter(recyclerAdapter);
    }
    private List<DataConstructor> createItemList(ArrayList<CharSequence> sms, ArrayList<CharSequence> subject, ArrayList<CharSequence> time) {
        List<DataConstructor> itemList = new ArrayList<>();
        for (int i = 0; i < sms.size(); i++) {
            itemList.add(new DataConstructor(sms.get(i),subject.get(i),time.get(i)));
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
            ((MaterialNavigationDrawer) act_).setFragmentChild(ChildFragmentReadSms.grabValues(bi.getCharSequenceArrayList(REPLY).get(sIP),bi.getCharSequenceArrayList(SENDER).get(sIP),bi.getString(TWONAMES),bi.getString(THREENAMES),bi.getCharSequenceArrayList(SUBJECT).get(sIP), bi.getCharSequenceArrayList(FROM).get(sIP), bi.getCharSequenceArrayList(DATE).get(sIP) + " " + bi.getCharSequenceArrayList(TIME).get(sIP), bi.getCharSequenceArrayList(SMS).get(sIP), bi.getCharSequenceArrayList(SMS_ID).get(sIP), filterRole(bi.getCharSequenceArrayList(CHROLE), bi.getCharSequenceArrayList(CHIDNO), bi.getString(SESSIONID)), bi.getString(SESSIONID),bi.getCharSequenceArrayList(CHROLE),bi.getCharSequenceArrayList(CHIDNO),bi.getCharSequenceArrayList(CHCS),bi.getCharSequenceArrayList(CHPG),bi.getCharSequenceArrayList(CHYR),bi.getCharSequenceArrayList(CHSM)), "Message");
        }
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
}
