package com.mrehya.Nofification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ashke on 3/1/2018.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
            Intent background = new Intent(context, Alarm_BackgroundService.class);
            context.startService(background);
    }
}