package com.mrehya.Nofification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by ashke on 2/28/2018.
 */

public class AlarmNotifReciever extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("boot_broadcast_poc", "starting service...");
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
        Intent alarm = new Intent(context, AlarmReceiver.class);
        boolean alarmRunning = (PendingIntent.getBroadcast(context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if(alarmRunning == false) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            //in ALARM REPEATING : (WAKEUP TIME, STARTTIME, INTERVAL, PENDING INTENT)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10 * 1000, 184000000, pendingIntent);
        }
       }
    }
}
