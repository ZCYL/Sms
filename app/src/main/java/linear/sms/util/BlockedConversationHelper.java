package linear.sms.util;

import android.content.SharedPreferences;
import android.provider.Telephony;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import linear.sms.ui.base.MyApplication;

/**
 * A set of helper methods to group the logic related to blocked conversation
 */
public class BlockedConversationHelper {

    public static boolean isConversationBlocked(SharedPreferences prefs, String address) {
        Set<String> idStrings =  new HashSet<String>(prefs.getStringSet(SettingsPre.BLOCKED_SPAM_ADDRESS, new HashSet<String>()));
        return idStrings.contains(address);
    }

    //bayes不通过
    public static void blockConversation(SharedPreferences prefs, String address) {
        Set<String> idStrings =  new HashSet<String>(prefs.getStringSet(SettingsPre.BLOCKED_SPAM_ADDRESS, new HashSet<String>()));
        idStrings.add(address);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(SettingsPre.BLOCKED_SPAM_ADDRESS, idStrings);
        editor.apply();
    }

    public static void cleanBayesConversation(SharedPreferences preferences){
        Set<String> idStrings = new HashSet<>();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(SettingsPre.BLOCKED_SPAM_ADDRESS, idStrings);
        editor.apply();
    }

    public static void unblockConversation(SharedPreferences prefs,String address) {
        Set<String> idStrings =  new HashSet<String>(prefs.getStringSet(SettingsPre.BLOCKED_SPAM_ADDRESS, new HashSet<String>()));
        idStrings.remove(address);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(SettingsPre.BLOCKED_SPAM_ADDRESS, idStrings);
        editor.apply();
    }

    //获取Bayes短信拦截
    public static Set<String> getBlockedConversations(SharedPreferences prefs) {
        return new HashSet<String>(prefs.getStringSet(SettingsPre.BLOCKED_SPAM_ADDRESS, new HashSet<String>()));
//        return prefs.getStringSet(SettingsPre.BLOCKED_SPAM_ADDRESS, new HashSet<String>());
    }

    public static void blockBlackListAddress(SharedPreferences prefs, String address) {
        Set<String> idStrings = prefs.getStringSet(SettingsPre.BLOCKED_FUTURE, new HashSet<String>());
        idStrings.add(address);
        prefs.edit().putStringSet(SettingsPre.BLOCKED_FUTURE, idStrings).apply();
    }

    public static void unblockBlackListAddress(SharedPreferences prefs, String address) {
        Set<String> idStrings2 = prefs.getStringSet(SettingsPre.BLOCKED_FUTURE, new HashSet<String>());
        idStrings2.remove(address);
        prefs.edit().putStringSet(SettingsPre.BLOCKED_FUTURE, idStrings2).apply();
    }

    public static Set<String> getBlackListAddress(SharedPreferences prefs) {
        return prefs.getStringSet(SettingsPre.BLOCKED_FUTURE, new HashSet<String>());
    }

    public static boolean isFutureBlocked(SharedPreferences prefs, String address) {
//        for (String s : getBlackListAddress(prefs)) {
//            if (PhoneNumberUtils.compareLoosely(s, address)) {
//                return true;
//            }
//        }

        return false;
    }

    public static String[] getBlockedConversationArray(SharedPreferences prefs) {
        Set<String> idStrings = getBlockedConversations(prefs);
        return idStrings.toArray(new String[idStrings.size()]);
    }

    public static String getCursorSelection(boolean needBlackList,boolean needBlockSpam) {
        StringBuilder selection = new StringBuilder();
        selection.append(Telephony.Threads.MESSAGE_COUNT);
        selection.append(" != 0");
        if (needBlackList) {
            selection.append(" AND ");
            selection.append(Telephony.Threads._ID);
            selection.append(" NOT IN (");
            List<String> addressString = new ArrayList<>();
            if (needBlackList){
                addressString.addAll(getBlackListAddress(MyApplication.instance.getSharedPreferences()));
            }
            if (needBlockSpam){
                addressString.addAll(getBlockedConversations(MyApplication.instance.getSharedPreferences()));
            }
            for (int i = 0; i < addressString.size(); i++) {
                selection.append(SmsHelper.getThreadId(MyApplication.instance,addressString.get(i)));
                if (i < addressString.size() - 1) {
                    selection.append(",");
                }
            }
            selection.append(")");
        }

        Log.e("XXXXXX",selection.toString());

        return selection.toString();
    }

    public static String getSpamActivitySelection(boolean needBlackList,boolean needBlockSpam){
        StringBuilder selection = new StringBuilder();
        selection.append(Telephony.Threads.MESSAGE_COUNT);
        if (!needBlackList && !needBlockSpam){
            return   selection.append(" = -1").toString();
        }
        selection.append(" != 0");
        List<String> addressString = new ArrayList<>();

        if (needBlackList){
            addressString.addAll(getBlackListAddress(MyApplication.instance.getSharedPreferences()));
        }
        if (needBlockSpam){
            addressString.addAll(getBlockedConversations(MyApplication.instance.getSharedPreferences()));
        }

        selection.append(" AND ");
        selection.append(Telephony.Threads._ID);
        selection.append(" IN (");
        for (int i = 0; i < addressString.size(); i++) {
            selection.append(SmsHelper.getThreadId(MyApplication.instance,addressString.get(i)));
            if (i < addressString.size() - 1) {
                selection.append(",");
            }
        }
        selection.append(")");

        return selection.toString();
    }
}
