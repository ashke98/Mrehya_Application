package com.mrehya.Nofification;

import android.content.Intent;
import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.NotificationCompat;

import com.mrehya.R;
import com.mrehya.Resume.ResumeMainActivity;

/**
 * Created by ashke on 3/1/2018.
 */

public class Alarm_BackgroundService extends Service  {

    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            // Do something here
            Intent i =  new Intent(context, ResumeMainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            PendingIntent pendingintent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setAutoCancel(true)
                    .setContentTitle("آزمون احیا")
                    .setContentText("به آزمون ها جواب بده، خودتو محک بزن!")
                    .setSmallIcon(R.drawable.ic_notif)
                    .setContentIntent(pendingintent);

            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());

            stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }
}
