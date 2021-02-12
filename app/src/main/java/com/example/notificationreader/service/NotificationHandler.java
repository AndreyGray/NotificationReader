package com.example.notificationreader.service;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.service.notification.StatusBarNotification;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.notificationreader.misc.Const;
import com.example.notificationreader.misc.DatabaseHelper;

public class NotificationHandler {
    public static final String BROADCAST = "com.example.notificationreader.update";
    public static final String LOCK = "lock";

    private Context context;
    private SharedPreferences sp;

    NotificationHandler(Context context) {
        this.context = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    void handlePosted(StatusBarNotification sbn) {
        if(sbn.isOngoing() && !sp.getBoolean(Const.PREF_ONGOING, false)) {
            if(Const.DEBUG) System.out.println("posted ongoing!");
            return;
        }
        boolean text = sp.getBoolean(Const.PREF_TEXT, true);
        NotificationObject no = new NotificationObject(context, sbn, text, -1);
        log(DatabaseHelper.PostedEntry.TABLE_NAME, DatabaseHelper.PostedEntry.COLUMN_NAME_CONTENT, no.toString());
    }

    void handleRemoved(StatusBarNotification sbn, int reason) {
        if(sbn.isOngoing() && !sp.getBoolean(Const.PREF_ONGOING, false)) {
            if(Const.DEBUG) System.out.println("removed ongoing!");
            return;
        }
        NotificationObject no = new NotificationObject(context, sbn, false, reason);
        log(DatabaseHelper.RemovedEntry.TABLE_NAME, DatabaseHelper.RemovedEntry.COLUMN_NAME_CONTENT, no.toString());
    }

    private void log(String tableName, String columnName, String content) {
        try {
            if(content != null) {
                synchronized (LOCK) {
                    DatabaseHelper dbHelper = new DatabaseHelper(context);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(columnName, content);
                    db.insert(tableName, "null", values);
                    db.close();
                    dbHelper.close();
                }

                Intent local = new Intent();
                local.setAction(BROADCAST);
                LocalBroadcastManager.getInstance(context).sendBroadcast(local);
            }
        } catch (Exception e) {
            if(Const.DEBUG) e.printStackTrace();
        }
    }
}
