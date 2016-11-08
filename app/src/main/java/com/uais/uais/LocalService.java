package com.uais.uais;

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
import com.uais.uais.message.Accounts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class LocalService extends Service {

    private final IBinder mBinder = new MyBinder();
    SharedPreferences sharedPrefs;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String sid = sharedPrefs.getString("for_service","");

        Log.d(getClass().getName()," id: "+sid);

        getNewSms(sid);

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
        //Log.d(getClass().getName()," smsid: "+smsId+" sessionid: "+table);
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
                Log.d(getClass().getName()," subject size: "+subject.size()+" sender size: "+sender.size());
                if(subject.size() == 0){
                    //no message
                }else {
                    //notify new message(s)
                    showNotifications(subject.size(), sender, subject);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void showSingleNotification(NotificationManagerCompat nmc, int notify_id, ArrayList<String> subject,ArrayList<String> sms){
        // Set Vibrate
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_VIBRATE;

        Intent intent = new Intent(this, Accounts.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_message_white_18dp)
                .setPriority(NotificationCompat.PRIORITY_HIGH) //set important
                .setOnlyAlertOnce(true)  //set vibrate once
                .setAutoCancel(true)  //when clicked notification is removed in notification bar
                .setDefaults(defaults);  //set vibration

        for(int i=0;i<subject.size(); i++){
            notificationBuilder.setContentTitle(subject.get(i));
            notificationBuilder.setContentText(sms.get(i));
        }
        notificationBuilder.setContentIntent(contentIntent);
        nmc.notify(notify_id, notificationBuilder.build());
    }
    private void showGroupSummaryNotification(NotificationManagerCompat nmc,ArrayList<String> subjects){
        // Set Vibrate
        int defaults = 0;
        defaults = defaults | Notification.DEFAULT_VIBRATE;

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
                .setGroupSummary(true)
                .setAutoCancel(true)  //when clicked notification is removed in notification bar
                .setDefaults(defaults)
                .setGroup("group");
        builder.setContentIntent(contentIntent);
        nmc.notify(123456, builder.build());
    }
    private void showNotifications(int notifyId,ArrayList<String> subject,ArrayList<String> sms){
        NotificationManagerCompat nmc = NotificationManagerCompat.from(this);

        if(notifyId ==1){
            showSingleNotification(nmc, 123456, subject, sms);
        }else if(notifyId > 1){
            showGroupSummaryNotification(nmc, subject);
        }
    }
}
