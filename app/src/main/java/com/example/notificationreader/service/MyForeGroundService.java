package com.example.notificationreader.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.notificationreader.R;
import com.example.notificationreader.ui.MainActivity;

import java.util.Objects;

import static com.example.notificationreader.misc.Const.ACTION_PAUSE;
import static com.example.notificationreader.misc.Const.ACTION_PLAY;
import static com.example.notificationreader.misc.Const.ACTION_START_FOREGROUND_SERVICE;
import static com.example.notificationreader.misc.Const.ACTION_STOP_FOREGROUND_SERVICE;
import static com.example.notificationreader.misc.Const.CHANNEL_NAME;
import static com.example.notificationreader.misc.Const.TAG_FOREGROUND_SERVICE;

public class MyForeGroundService extends Service {

    public MyForeGroundService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG_FOREGROUND_SERVICE, "My foreground service onCreate().");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null)
        {
            String action = intent.getAction();

            switch (action)
            {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_PLAY:
                    Toast.makeText(getApplicationContext(), "You click Play button.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_PAUSE:
                    Toast.makeText(getApplicationContext(), "You click Pause button.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     *  Used to build and start foreground service.
     */
    private void startForegroundService(){
        Log.d(TAG_FOREGROUND_SERVICE, "Start foreground service.");

        startForeground(1, notificationCreator());


    }

    private void stopForegroundService() {
        Log.d(TAG_FOREGROUND_SERVICE, "Stop foreground service.");

        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }

    private Notification notificationCreator() {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(/*Objects.requireNonNull(getActivity())*/getBaseContext(), CHANNEL_NAME)
                        .setSmallIcon(R.mipmap.mlauncher)
                        .setContentTitle(getResources().getString(R.string.notification_foreground_server_title))
                        .setContentText(getResources().getString(R.string.notification_foreground_server_text))
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentIntent(getPendingIntent())
                        .setAutoCancel(true)
                        .setShowWhen(true)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.mlauncher));
        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_NAME,
                    "Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }
        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
            return notification;
    }

    private PendingIntent getPendingIntent() {
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        return PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

    }
}
