package com.example.notificationreader.misc;

import com.example.notificationreader.BuildConfig;

public class Const {

    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final long VERSION  = BuildConfig.VERSION_CODE;

    // Feature flags
    public static final boolean ENABLE_ACTIVITY_RECOGNITION = true;
    public static final boolean ENABLE_LOCATION_SERVICE     = true;

    // Preferences shown in the UI
    public static final String PREF_STATUS  = "pref_status";
    public static final String PREF_BROWSE  = "pref_browse";
    public static final String PREF_TEXT    = "pref_text";
    public static final String PREF_ONGOING = "pref_ongoing";
    public static final String PREF_ABOUT   = "pref_about";
    public static final String PREF_VERSION = "pref_version";

    // Preferences not shown in the UI
    public static final String PREF_LAST_ACTIVITY  = "pref_last_activity";
    public static final String PREF_LAST_LOCATION  = "pref_last_location";

    // Preferences for foreground service
    public static final String TAG_FOREGROUND_SERVICE = "FOREGROUND_SERVICE";
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String CHANNEL_NAME = "my_channel_01";


    public static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    public static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";

    public static  enum  SORT_BY {ALL, PER_HOUR,PER_DAY,PER_MONTH};
}