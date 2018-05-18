package linear.sms.util;

import android.content.SharedPreferences;

import linear.sms.ui.base.MyApplication;

public class SettingsPre {


    public static final String HIDE_AVATAR_SENT = "pref_key_hide_avatar_sent";
    public static final String HIDE_AVATAR_RECEIVED = "pref_key_hide_avatar_received";

    public static final String FORCE_TIMESTAMPS = "pref_key_force_timestamps";
    public static final String SHOW_NEW_TIMESTAMP_DELAY = "pref_key_timestamp_delay";

    public static final String BLOCKED_ENABLED = "pref_key_blocked_enabled";
    public static final String BLOCKED_BLACK_LIST_ENABLED = "pref_key_black_list_enabled";
    public static final String BLOCKED_SENDERS = "pref_key_blocked_senders";
    public static final String BLOCKED_FUTURE = "pref_key_block_future";

    public static final String TIMESTAMPS_24H = "pref_key_24h";


    public static boolean isBlockEnable(){
        return MyApplication.instance.getSharedPreferences().getBoolean(BLOCKED_ENABLED,true);
    }

    public static void setBlockEnable(boolean enable){
        SharedPreferences.Editor editor = MyApplication.instance.getSharedPreferences().edit().
                putBoolean(BLOCKED_ENABLED, enable);
        editor.apply();
    }

    public static boolean isBlackListEnable(){
        return MyApplication.instance.getSharedPreferences().getBoolean(BLOCKED_BLACK_LIST_ENABLED,true);
    }

    public static void setBlackListEnable(boolean enable){
        SharedPreferences.Editor editor = MyApplication.instance.getSharedPreferences().edit().
                putBoolean(BLOCKED_BLACK_LIST_ENABLED, enable);
        editor.apply();
    }
}
