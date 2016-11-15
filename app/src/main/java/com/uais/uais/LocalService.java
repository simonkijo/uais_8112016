package com.uais.uais;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.kbeanie.multipicker.api.entity.ChosenFile;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.uais.uais.Utils.UrlProvider;
import com.uais.uais.academicMaterials.AcademicActivity;
import com.uais.uais.academicMaterials.ExpandableExampleFragment;
import com.uais.uais.academicMaterials.common.fragment.ExampleExpandableDataProviderFragment;
import com.uais.uais.message.Accounts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class LocalService extends Service {

    private final IBinder mBinder = new MyBinder();
    SharedPreferences sharedPrefs;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sid = sharedPrefs.getString("for_service","");

        Log.d(getClass().getName()," id: "+sid);
        //notify new sms
        getNewSms(sid);
        //notify new ad file
        getNewAdFiles(sid);

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public class MyBinder extends Binder {
        public LocalService getService() {
            return LocalService.this;
        }
    }
    public void setRead(final String smsId, final String table){
        RequestParams params = new RequestParams();
        params.put("sms_id",smsId);
        params.put("sms_table",table);
        SyncHttpClient client = new SyncHttpClient();

        client.post(UrlProvider.SET_READ, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(getClass().getName()," statusCode: "+statusCode+" sms of id: "+smsId+" on table: "+table+" set ready");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(getClass().getName()," : "+statusCode);
            }
        });
    }
    public void sendToTrash(final ArrayList<String> smsId, final String table){
        RequestParams params = new RequestParams();
            params.put("sms_table",table);

        for(final String sms_id : smsId) {
            params.put("sms_id[]", sms_id);

            AsyncHttpClient client = new AsyncHttpClient();

            client.post(UrlProvider.SEND_TO_TRASH, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d(getClass().getName()," statusCode: "+statusCode+" sms of id: "+sms_id+" on table: "+table+" sent to trash");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d(getClass().getName()," : "+statusCode);
                }
            });
        }
    }
    public void setUnTrash(final ArrayList<String> smsId, final String table){
        RequestParams params = new RequestParams();
        params.put("sms_table",table);

        for(final String sms_id : smsId) {
            params.put("sms_id[]", sms_id);

            AsyncHttpClient client = new AsyncHttpClient();

            client.post(UrlProvider.SET_UNTRASH, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d(getClass().getName()," statusCode: "+statusCode+" sms of id: "+sms_id+" on table: "+table+" set untrashed");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d(getClass().getName()," : "+statusCode);
                }
            });
        }
    }
    public void uploadFilesAsync(ArrayList<ChosenFile> filepath){
        RequestParams params = new RequestParams();

        for(final ChosenFile file: filepath) {
            try {
                params.put("files[]", new File(file.getOriginalPath()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(UrlProvider.STUDENT_UPLOAD_ASSIGNMENT, params, new AsyncHttpResponseHandler() {

                @Override
                public void onProgress(final long bytesWritten, final long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                    Log.d("PERC","progress: "+(bytesWritten*100)/totalSize );

                    showNotification(file.getDisplayName(),"Upload in progress : "+(int)((bytesWritten*100)/totalSize)+"%",100, (int)((bytesWritten*100)/totalSize), true, 1);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d("FILE_SUCCESS"," : "+statusCode);

                    showNotification(file.getDisplayName(),"Upload complete : 100%",0, 0, false, 0);
                    //delete uploaded file in the directory 'UAIS Documents' to free space
                    new File(file.getOriginalPath()).delete();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d("FILE_SUCCESS"," : "+statusCode);
                    showNotification(file.getDisplayName(),"Upload Fail",0, 0, false, 0);
                }
            });
        }
    }

    public void sendFilenameAndModuleToDb(ArrayList<ChosenFile> files, String moduleTitle){
        RequestParams params = new RequestParams();

        for(ChosenFile file : files){
            params.put("filename",file.getDisplayName());
            params.put("module",moduleTitle);

            AsyncHttpClient client = new AsyncHttpClient();
            client.post(UrlProvider.SEND_FILENAME_AND_MODULE_TO_DB, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.d(getClass().getName(),"success: on service "+statusCode);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.d(getClass().getName(),"fail: on service "+statusCode);
                }
            });
        }
    }
    private void showNotification(String title,String description,int max, int progress, Boolean ongoingflag, int resID){
        // Set Vibrate
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_VIBRATE;

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setSmallIcon((resID == 1 ? android.R.drawable.stat_sys_upload : R.mipmap.ic_file_upload_white_18dp))
                .setTicker(getString(R.string.notification_ticker))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                .setContentText(description)
                .setProgress(max, progress , false) //set progressbar
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)  //set long running on background
                .setOnlyAlertOnce(true)  //set vibrate once
                .setOngoing(ongoingflag)  //ensure not canceled by close button
                .setAutoCancel(false)  //when clicked notification is removed in notification bar
                .setDefaults(defaults);  //set vibration

        notificationManager.notify(1, notificationBuilder.build());
    }
    private void getNewSms(String id){
        final RequestParams params = new RequestParams();
        params.put("stId", id);

        final AsyncHttpClient client = new AsyncHttpClient();
        client.post(UrlProvider.LOAD_INBOX, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                ArrayList<String> subject = new ArrayList<>();
                ArrayList<String> sender = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsondata = response.getJSONObject(i);
                        subject.add(jsondata.getString("subject"));
                        sender.add(jsondata.getString("sender"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ArrayList<String> dup_subject = new ArrayList<>(subject);

                Log.d(getClass().getName()," subject size: "+subject.size()+" sender size: "+sender.size());
                if(subject.size() == 0){
                    //no message
                }else {
                    //notify new message(s)
                    for(int i=0; i<subject.size();i++){
                        Log.d(getClass().getName()," subject: "+subject.get(i)+" sender: "+sender.get(i));
                    }

                    if(sharedPrefs.getStringSet("subject",null) == null){
                        SharedPreferences.Editor ed = sharedPrefs.edit();
                        ed.putStringSet("subject",new LinkedHashSet<>(subject));
                        ed.apply();

                        showNotifications(subject.size(), sender, subject);
                    }else{
                        ArrayList<String> _subject = new ArrayList<>(sharedPrefs.getStringSet("subject", null));

                        subject.removeAll(_subject);
                        Log.d(getClass().getName(),"sp subject size: "+subject.size());

                        ArrayList<String> subj = new ArrayList<>();
                        ArrayList<String> sender_ = new ArrayList<>();

                        if(subject.size() >= 1){
                            for(int i=0;i<subject.size();i++){
                                _subject.add(subject.get(i));
                            }
                            SharedPreferences.Editor ed = sharedPrefs.edit();
                            ed.putStringSet("subject",new LinkedHashSet<>(_subject));
                            ed.apply();

                            ArrayList<String> subject_ = new ArrayList<>(subject);
                            for(int k=0;k<subject_.size();k++){
                                for(int j=0;j<dup_subject.size();j++){
                                    if(dup_subject.get(j).equals(subject_.get(k))){
                                        sender_.add(sender.get(j));
                                        subj.add(dup_subject.get(j));
                                    }
                                }
                            }
                            showNotifications(subj.size(), sender_, subj);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void getNewAdFiles(String id){
        final RequestParams params = new RequestParams();
        params.put("stId", id);

        final AsyncHttpClient client = new AsyncHttpClient();
        client.post(UrlProvider.STUDENT_MODULES, params, new JsonHttpResponseHandler() {  //"http://www.uais.co.nf/mobile/studentModules"

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(getClass().getName()," statusCode: "+statusCode+" modules loaded");

                final String modules_[] = new String[response.length()];

                for (int i = 0; i < modules_.length; i++) {
                    try {
                        JSONObject jsondata = response.getJSONObject(i);
                        modules_[i] = jsondata.getString("module");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                client.post(UrlProvider.STUDENT_ADNOTES, params, new JsonHttpResponseHandler() {  //"http://www.uais.co.nf/mobile/studentAdNotes"

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response_) {
                        super.onSuccess(statusCode, headers, response_);
                        Log.d(getClass().getName()," statusCode: "+statusCode+" ad loaded");


                        String sitems[][] = new String[modules_.length][response_.length()];

                        for (int i = 0; i < sitems.length; i++) {
                            for (int j = 0; j < sitems[i].length; j++) {
                                try {
                                    JSONObject jsondata = response_.getJSONObject(j);
                                    sitems[i][j] = jsondata.getString(modules_[i]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        //notify
                        String files[][] = new String[modules_.length][response_.length()];
                        ArrayList<String> sp_files = new ArrayList<>();
                        ArrayList<String[][]> p = new ArrayList<>();

                        int count = 0;
                        for (int i = 0; i < modules_.length; i++) {
                            for (int j = 0; j < sitems[i].length; j++) {
                                if(sitems[i][j] != null){
                                    if(sitems[i][j].equals("No file")){
                                        //do nothing
                                    }else{
                                        files[i][j] = sitems[i][j];
                                        sp_files.add(sitems[i][j]);
                                        count ++;
                                    }
                                }
                            }
                        }
                        p.add(files);
                        //for testing data
                        /*for (int i = 0; i < files.length; i++) {
                            for (int j = 0; j < files[i].length; j++) {
                                Log.d(getClass().getName()," count: "+count+" mod: " + modules_[i] + " file: " + files[i][j]);
                            }
                        }*/
                        //end of testing data
                        if(sharedPrefs.getStringSet("files",null) == null){
                            SharedPreferences.Editor ed = sharedPrefs.edit();
                            ed.putStringSet("files",new LinkedHashSet<>(sp_files));
                            ed.apply();

                            showNotificationsFile(count, modules_, files);
                        }else{
                            ArrayList<String> _files = new ArrayList<>(sharedPrefs.getStringSet("files", null));

                            sp_files.removeAll(_files);
                            Log.d(getClass().getName(),"remained file size: "+sp_files.size());

                            if(sp_files.size() >= 1){
                                for(int i=0;i<sp_files.size();i++){
                                    _files.add(sp_files.get(i));
                                }
                                SharedPreferences.Editor ed = sharedPrefs.edit();
                                ed.putStringSet("files",new LinkedHashSet<>(_files));
                                ed.apply();

                                ArrayList<String> remained_files = new ArrayList<>(sp_files);
                                String files_new[][] = new String[modules_.length][response_.length()];
                                String mod_new[] = new String[modules_.length];

                                for(int r=0;r<remained_files.size();r++) {
                                    for (int k = 0; k < p.size(); k++) {
                                        for (int j = 0; j < p.get(k).length; j++) {
                                            for(int w=0;w < p.get(k)[j].length; w++) {
                                                if (remained_files.get(r).equals(p.get(k)[j][w])) {
                                                    Log.d(getClass().getName(), "remained: " + remained_files.get(r) + " p mod: " + modules_[j] + " p file: " + p.get(k)[j][w]);
                                                    files_new[j][w] = p.get(k)[j][w];
                                                    mod_new[j] = modules_[j];
                                                }
                                            }
                                        }
                                    }
                                }
                                //for testing data
                                /*for (int t = 0; t < files_new.length; t++) {
                                    for (int e = 0; e < files_new[t].length; e++) {
                                        Log.d(getClass().getName(),"new count: "+remained_files.size()+" new mods: " + mod_new[t] + " new files: " + files_new[t][e]);
                                    }
                                }*/
                                //end of testing data
                                showNotificationsFile(remained_files.size(), mod_new, files_new);
                            }
                        }

                    }
                });
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d(getClass().getName()," statusCode: "+statusCode);
            }
        });
    }
    private void showSingleNotification(NotificationManagerCompat nmc, int notify_id, ArrayList<String> subject,ArrayList<String> sms){
        // Set heads up
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;

        Intent intent = new Intent(this, Accounts.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_message_white_18dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH) //set important
                .setOnlyAlertOnce(true)  //set vibrate once
                .setAutoCancel(true)  //when clicked notification is removed in notification bar
                .setDefaults(defaults);  //set heads up

        for(int i=0;i<subject.size(); i++){
            notificationBuilder.setContentTitle(subject.get(i));
            notificationBuilder.setContentText(sms.get(i));
        }
        notificationBuilder.setContentIntent(contentIntent);
        nmc.notify(notify_id, notificationBuilder.build());
    }
    private void showGroupSummaryNotification(NotificationManagerCompat nmc,int notify_id, ArrayList<String> subjects){
        // Set heads up
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;

        Intent i = new Intent(this, Accounts.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.InboxStyle is = new NotificationCompat.InboxStyle()
                .setSummaryText("Inbox")
                .setBigContentTitle(subjects.size()+" new message"+(subjects.size()==1? "":"s"));

        for (String subs : subjects) {
            is.addLine(subs);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("UAIS")
                .setContentText(subjects.size()+" new message"+(subjects.size()==1? "":"s"))
                .setStyle(is)
                .setNumber(subjects.size())
                .setSmallIcon(R.mipmap.ic_message_white_18dp)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setPriority(NotificationCompat.PRIORITY_HIGH) //set important
                .setGroupSummary(true)
                .setAutoCancel(true)  //when clicked notification is removed in notification bar
                .setDefaults(defaults)
                .setGroup("group");
        builder.setContentIntent(contentIntent);
        nmc.notify(notify_id, builder.build());
    }
    private void showNotifications(int notifyId,ArrayList<String> subject,ArrayList<String> sms){
        NotificationManagerCompat nmc = NotificationManagerCompat.from(this);

        if(notifyId ==1){
            showSingleNotification(nmc, 123456, subject, sms);
        }else if(notifyId > 1){
            Log.d(getClass().getName(),"notifyId: "+notifyId+" sn subject size: "+subject.size()+" sn sms size: "+sms.size());
            showGroupSummaryNotification(nmc,123456, subject);
        }
    }
    //notification for new file
    private void showSingleNotificationFile(NotificationManagerCompat nmc, int notify_id, String module[],String file[][]){
        // Set heads up
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;

        Intent intent = new Intent(this, AcademicActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_insert_drive_file_white_18dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH) //set important
                .setOnlyAlertOnce(true)  //set vibrate once
                .setAutoCancel(true)  //when clicked notification is removed in notification bar
                .setDefaults(defaults);  //set heads up

        for(int i=0;i<module.length; i++){
            for(int j=0;j<file[i].length; j++) {
                if(file[i][j] != null) {
                    notificationBuilder.setContentTitle(module[i]);
                    notificationBuilder.setContentText(file[i][j]);
                }
            }
        }
        notificationBuilder.setContentIntent(contentIntent);
        nmc.notify(notify_id, notificationBuilder.build());
    }
    private void showGroupSummaryNotificationFile(NotificationManagerCompat nmc,int notify_id, String module[],String file[][]){
        // Set heads up
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;

        Intent in = new Intent(this, AcademicActivity.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, in, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.InboxStyle is = new NotificationCompat.InboxStyle();
        int count = 0;
        for(int i=0;i<module.length;i++){
            for(int j=0;j<file[i].length;j++){
                if(file[i][j] != null) {
                    is.addLine(file[i][j]);
                    count ++;
                }
            }
        }
        is.setBigContentTitle(count+" new file"+(count==1? "":"s"));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("UAIS")
                .setContentText(count+" new file"+(count==1? "":"s"))
                .setStyle(is)
                .setSmallIcon(R.mipmap.ic_insert_drive_file_white_18dp)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setPriority(NotificationCompat.PRIORITY_HIGH) //set important
                .setGroupSummary(true)
                .setAutoCancel(true)  //when clicked notification is removed in notification bar
                .setDefaults(defaults)
                .setGroup("group");
        builder.setContentIntent(contentIntent);
        nmc.notify(notify_id, builder.build());
    }
    private void showNotificationsFile(int notifyId, String module[],String file[][]){
        NotificationManagerCompat nmc = NotificationManagerCompat.from(this);

        if(notifyId ==1){
            showSingleNotificationFile(nmc, 1234567, module, file);
        }else if(notifyId > 1){
            showGroupSummaryNotificationFile(nmc,1234567, module, file);
        }
    }
}
