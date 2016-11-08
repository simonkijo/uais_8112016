package com.uais.uais.message.master_child;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.uais.uais.LocalService;
import com.uais.uais.R;
import com.uais.uais.Utils.UrlProvider;
import com.uais.uais.detectinternetconnection.ConnectionDetector;
import com.uais.uais.message.DataConstructor;
import com.uais.uais.message.RecyclerAdapterRead;

import java.util.ArrayList;
import java.util.List;

import it.neokree.materialnavdrawer.MaterialNavigationDrawer;

/**
 * Created by HP on 11/8/2016.
 */

public class ChildFragmentReadMaster extends Fragment {
    private static final String SESSIONID = "ChildFragmentReadMaster$session_id";
    private static final String TWONAMES = "ChildFragmentReadMaster$twoNames";
    private static final String THREENAMES = "ChildFragmentReadMaster$threeNames";

    private static final String CHROLE = "ChildFragmentReadMaster$ch_role";
    private static final String CHIDNO = "ChildFragmentReadMaster$ch_id_no";
    private static final String CHCS = "ChildFragmentReadMaster$ch_cs";
    private static final String CHPG = "ChildFragmentReadMaster$ch_pg";
    private static final String CHYR = "ChildFragmentReadMaster$ch_yr";
    private static final String CHSM = "ChildFragmentReadMaster$ch_sm";
    private static final String REPLY = "ChildFragmentReadMaster$reply";
    private static final String SENDER = "ChildFragmentReadMaster$sender";
    private static final String SUBJECT = "ChildFragmentReadMaster$subject";
    private static final String FROM = "ChildFragmentReadMaster$from";
    private static final String DATE = "ChildFragmentReadMaster$date";
    private static final String TIME = "ChildFragmentReadMaster$time";
    private static final String SMS = "ChildFragmentReadMaster$sms";
    private static final String SMS_ID = "ChildFragmentReadMaster$smsId";

    public static View.OnClickListener onClickListener;
    public static View.OnLongClickListener onLongClickListener;

    RecyclerView recyclerView;
    ImageButton composeBtn;
    Bundle bi;
    RecyclerAdapterRead recyclerAdapter;
    boolean isItemChecked = false;
    boolean noItemRemained = false;
    Menu menu;
    ArrayList<DataConstructor> itemsRemained = new ArrayList<>();
    private LocalService s;

    public static ChildFragmentReadMaster getAllDataRead(
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

        ChildFragmentReadMaster cfrm = new ChildFragmentReadMaster();
        cfrm.setArguments(bundle);
        return cfrm;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("MNDrawer Master-Child", "Master filtered read created");

        RelativeLayout relativeLayout = (RelativeLayout)inflater.inflate(R.layout.fragment_recyclerview,container,false);
        recyclerView = (RecyclerView)relativeLayout.findViewById(R.id.recyclerView);
        composeBtn = (ImageButton)relativeLayout.findViewById(R.id.compose);

        onClickListener = new MyOnClickListener(getActivity());
        onLongClickListener = new MyOnLongClickListener(getActivity());

        bi = getArguments();
        noItemRemained = false;  //reset flag

        setupFAB(composeBtn);
        setupRecyclerView(recyclerView, false, false);
        return relativeLayout;
    }
    private void setupFAB(ImageButton imageButton){
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    menu.findItem(R.id.action_delete).setVisible(false); //set menu delete invisible
                    menu.findItem(R.id.action_checkAll).setIcon(R.mipmap.ic_check_box_outline_blank_white_24dp); //set default icon
                    menu.findItem(R.id.action_checkAll).setVisible(false);  //set menu checkbox invisible
                    isItemChecked = false;
                }catch (NullPointerException e){e.printStackTrace();}

                ((MaterialNavigationDrawer)getActivity()).setFragmentChild(ChildFragmentCompose.getStatus(null,null,bi.getString(TWONAMES),bi.getString(THREENAMES),bi.getString(SESSIONID),filterRole(bi.getCharSequenceArrayList(CHROLE), bi.getCharSequenceArrayList(CHIDNO), bi.getString(SESSIONID)),bi.getCharSequenceArrayList(CHROLE),bi.getCharSequenceArrayList(CHIDNO),bi.getCharSequenceArrayList(CHCS),bi.getCharSequenceArrayList(CHPG),bi.getCharSequenceArrayList(CHYR),bi.getCharSequenceArrayList(CHSM)),"Compose");
            }
        });
    }
    private void setupRecyclerView(RecyclerView recyclerView, boolean isCheck, boolean isVisible) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAdapter = new RecyclerAdapterRead(createItemList(isCheck,isVisible));
        recyclerView.setAdapter(recyclerAdapter);
    }

    private List<DataConstructor> createItemList(boolean isChecked,boolean isVisible) {
        List<DataConstructor> itemList = new ArrayList<>();
        if (itemsRemained.size() == 0 && !noItemRemained) {  //handles user new entry to fragment read
            for (int i = 0; i < bi.getCharSequenceArrayList(SMS).size(); i++) {
                itemList.add(new DataConstructor(bi.getCharSequenceArrayList(REPLY).get(i),bi.getCharSequenceArrayList(SENDER).get(i),bi.getCharSequenceArrayList(FROM).get(i),bi.getCharSequenceArrayList(DATE).get(i),bi.getCharSequenceArrayList(SMS_ID).get(i),bi.getCharSequenceArrayList(SMS).get(i), bi.getCharSequenceArrayList(SUBJECT).get(i), bi.getCharSequenceArrayList(TIME).get(i), isChecked, isVisible));
            }
        } else if(itemsRemained.size() != 0){  //handles if user delete some sms
            for (int i = 0; i < itemsRemained.size(); i++) {
                itemList.add(itemsRemained.get(i));
            }
            for(int i = 0; i < itemsRemained.size(); i++){
                itemList.get(i).setVisibled(isVisible);
            }
            for(int i = 0; i < itemsRemained.size(); i++){
                itemList.get(i).setSelected(isChecked);
            }
            Log.d(getClass().getName(), "adapter items: " + itemList.size());
        }else if(noItemRemained){
            bi.getCharSequenceArrayList(SENDER).clear(); //clear sender data to handle/set click listener to null
            itemList.add(new DataConstructor("","","","","","No Message","", "", false, false));
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
            if(bi.getCharSequenceArrayList(SENDER).size() == 0){
                //do nothing
            }else if(bi.getCharSequenceArrayList(SENDER).size() != 0){
                try {
                    menu.findItem(R.id.action_delete).setVisible(false); //set menu delete invisible
                    menu.findItem(R.id.action_checkAll).setIcon(R.mipmap.ic_check_box_outline_blank_white_24dp); //set default icon
                    menu.findItem(R.id.action_checkAll).setVisible(false);  //set menu checkbox invisible
                    isItemChecked = false;
                }catch (NullPointerException e){e.printStackTrace();}
                if(itemsRemained.size() == 0 && !noItemRemained) {
                    ((MaterialNavigationDrawer) act_).setFragmentChild(ChildFragmentReadSms.grabValues(bi.getCharSequenceArrayList(REPLY).get(sIP), bi.getCharSequenceArrayList(SENDER).get(sIP), bi.getString(TWONAMES), bi.getString(THREENAMES), bi.getCharSequenceArrayList(SUBJECT).get(sIP), bi.getCharSequenceArrayList(FROM).get(sIP), bi.getCharSequenceArrayList(DATE).get(sIP) + " " + bi.getCharSequenceArrayList(TIME).get(sIP), bi.getCharSequenceArrayList(SMS).get(sIP), bi.getCharSequenceArrayList(SMS_ID).get(sIP), filterRole(bi.getCharSequenceArrayList(CHROLE), bi.getCharSequenceArrayList(CHIDNO), bi.getString(SESSIONID)),  bi.getString(SESSIONID),bi.getCharSequenceArrayList(CHROLE),bi.getCharSequenceArrayList(CHIDNO),bi.getCharSequenceArrayList(CHCS),bi.getCharSequenceArrayList(CHPG),bi.getCharSequenceArrayList(CHYR),bi.getCharSequenceArrayList(CHSM)), "Message");
                }else if(itemsRemained.size() != 0){
                    ((MaterialNavigationDrawer) act_).setFragmentChild(ChildFragmentReadSms.grabValues(itemsRemained.get(sIP).getReplyId(), itemsRemained.get(sIP).getPName(), bi.getString(TWONAMES), bi.getString(THREENAMES), itemsRemained.get(sIP).getmSubject(), itemsRemained.get(sIP).getFromPerson(), itemsRemained.get(sIP).getDate() + " " + itemsRemained.get(sIP).getmTime(), itemsRemained.get(sIP).getmSms_(), itemsRemained.get(sIP).getSmsId(), filterRole(bi.getCharSequenceArrayList(CHROLE), bi.getCharSequenceArrayList(CHIDNO),  bi.getString(SESSIONID)),  bi.getString(SESSIONID), bi.getCharSequenceArrayList(CHROLE),bi.getCharSequenceArrayList(CHIDNO),bi.getCharSequenceArrayList(CHCS),bi.getCharSequenceArrayList(CHPG),bi.getCharSequenceArrayList(CHYR),bi.getCharSequenceArrayList(CHSM)), "Message");
                }
            }
        }
    }

    public class MyOnLongClickListener implements View.OnLongClickListener{

        Activity activity;
        public MyOnLongClickListener(Activity act){activity = act;}
        @Override
        public boolean onLongClick(View v) {
            int selected = recyclerView.getChildLayoutPosition(v);
            Log.d(getClass().getName(),"LongClick : sms_id "+bi.getCharSequenceArrayList(SMS_ID).get(selected)+" selected: "+selected);

            if(bi.getCharSequenceArrayList(SENDER).size() == 0){
                //do nothing
            }else if(bi.getCharSequenceArrayList(SENDER).size() != 0) {

                setupRecyclerView(recyclerView, false, true);  //show checkbox
                recyclerAdapter.notifyDataSetChanged();  //notify changes

                menu = ((MaterialNavigationDrawer) activity).menu_toggle;  //get menu reference
                MenuItem mi_delete = menu.findItem(R.id.action_delete).setVisible(true); //set menu delete visible
                mi_delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {  //register click listener
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_delete) {
                            itemsRemained.clear(); //clear previous remained data
                            ArrayList<String> smsToBeDeleted = new ArrayList<>();

                            List<DataConstructor> mListItemChecked = recyclerAdapter.getCheckedItem();
                            for (int i = 0; i < mListItemChecked.size(); i++) {
                                DataConstructor ci = mListItemChecked.get(i);
                                if (ci.isSelected()) {
                                    smsToBeDeleted.add(String.valueOf(ci.getSmsId()));  //grab sms to be delete
                                } else {
                                    itemsRemained.add(ci);  //grab sms remained
                                }
                            }
                            if (itemsRemained.size() == 0) {
                                noItemRemained = true;
                            }
                            //checking data
                            for (int i = 0; i < smsToBeDeleted.size(); i++) {
                                Log.d(getClass().getName(), "item unchecked: " + itemsRemained.size() + "\r\n item checked: " + smsToBeDeleted.size() + "\r\n name: " + smsToBeDeleted.get(i) + "\r\n");
                            }

                            menu.findItem(R.id.action_delete).setVisible(false); //set menu delete invisible
                            menu.findItem(R.id.action_checkAll).setIcon(R.mipmap.ic_check_box_outline_blank_white_24dp); //set default icon
                            menu.findItem(R.id.action_checkAll).setVisible(false);  //set menu checkbox invisible
                            isItemChecked = false;  //restore default flag for checkbox

                            setupRecyclerView(recyclerView, false, false);  //hide checkbox and show remained data
                            recyclerAdapter.notifyDataSetChanged();  //notify changes

                            //call for sending data to trash in server online
                            s.sendToTrash(smsToBeDeleted, bi.getString(SESSIONID));

                            return true;
                        }
                        return false;
                    }
                });

                MenuItem mi_checkAll = menu.findItem(R.id.action_checkAll).setVisible(true);  //set menu checkbox visible
                mi_checkAll.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {  //register click listener
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_checkAll) {
                            if (!isItemChecked) {
                                item.setIcon(R.mipmap.ic_check_box_white_24dp);  //check the checkbox itself
                                setupRecyclerView(recyclerView, true, true);    //check all checkbox
                                recyclerAdapter.notifyDataSetChanged();   //notify changes
                                isItemChecked = true;
                            } else if (isItemChecked) {
                                item.setIcon(R.mipmap.ic_check_box_outline_blank_white_24dp);  //uncheck the checkbox itself
                                setupRecyclerView(recyclerView, false, true);    //uncheck all checkbox
                                recyclerAdapter.notifyDataSetChanged();       //notify changes
                                isItemChecked = false;
                            }
                            return true;
                        }
                        return false;
                    }
                });

            }
            return true;
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

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            menu.findItem(R.id.action_delete).setVisible(false); //set menu delete invisible
            menu.findItem(R.id.action_checkAll).setIcon(R.mipmap.ic_check_box_outline_blank_white_24dp); //set default icon
            menu.findItem(R.id.action_checkAll).setVisible(false);  //set menu checkbox invisible
            isItemChecked = false;
        }catch (NullPointerException e){e.printStackTrace();}
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
