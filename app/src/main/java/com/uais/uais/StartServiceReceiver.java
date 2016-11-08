package com.uais.uais;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class StartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context,LocalService.class);
        context.startService(service);
    }
}
