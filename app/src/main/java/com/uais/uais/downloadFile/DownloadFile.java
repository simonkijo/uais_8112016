package com.uais.uais.downloadFile;


import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

import java.io.File;

import cz.msebera.android.httpclient.Header;


public class DownloadFile {

    Context ctt;
    private File cacheDir;

    public DownloadFile(Context context){
        this.ctt = context;
    }
    public static boolean isDownloadManagerAvailable() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }
    public void queueFileForDownload(String url,String file,String title,String description){
        //String url = "url you want to download";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(description);
        request.setTitle(title);
        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        //check if shared media mounted
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(),"Download");
        }
        Boolean success = true;
        if(!cacheDir.exists()) {
            success = cacheDir.mkdirs(); //make directory
        }
        if(success){
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file);
        }

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) ctt.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
    public void downloadingUsingHttp(String url){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new FileAsyncHttpResponseHandler(ctt) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {

            }

        });
    }
}
