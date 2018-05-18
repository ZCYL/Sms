package linear.sms.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.util.Log;
import android.view.MenuItem;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import linear.sms.bean.Conversation;
import linear.sms.ui.base.MyApplication;

/**
 * A set of helper methods to group the logic related to blocked conversation
 */
public class BlockedConversationHelper {

    public static boolean isConversationBlocked(SharedPreferences prefs, String address) {
        Set<String> idStrings = prefs.getStringSet(SettingsPre.BLOCKED_SENDERS, new HashSet<String>());
        return idStrings.contains(address);
    }

    public static void blockConversation(SharedPreferences prefs, String address) {
        Set<String> idStrings = prefs.getStringSet(SettingsPre.BLOCKED_SENDERS, new HashSet<String>());
        idStrings.add(address);
        prefs.edit().putStringSet(SettingsPre.BLOCKED_SENDERS, idStrings).apply();
    }

    public static void unblockConversation(SharedPreferences prefs,String address) {
        Set<String> idStrings = prefs.getStringSet(SettingsPre.BLOCKED_SENDERS, new HashSet<String>());
        idStrings.remove(address);
        prefs.edit().putStringSet(SettingsPre.BLOCKED_SENDERS, idStrings).apply();
    }

    //获取Bayes短信拦截
    public static Set<String> getBlockedConversations(SharedPreferences prefs) {
        return prefs.getStringSet(SettingsPre.BLOCKED_SENDERS, new HashSet<String>());
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

    public static String getCursorSelection(boolean needBlackList) {
        StringBuilder selection = new StringBuilder();
        selection.append(Telephony.Threads.MESSAGE_COUNT);
        selection.append(" != 0");
        if (needBlackList) {
            selection.append(" AND ");
            selection.append(Telephony.Sms.ADDRESS);
            selection.append(" NOT IN (");
            Set<String> idStrings = getBlackListAddress(MyApplication.instance.getSharedPreferences());
            for (int i = 0; i < idStrings.size(); i++) {
                selection.append("?");
                if (i < idStrings.size() - 1) {
                    selection.append(",");
                }
            }
            selection.append(")");
        }

        return selection.toString();
    }

    public static String getSpamActivitySelection(boolean needBlackList,boolean needBlockSpam){
        StringBuilder selection = new StringBuilder();
        selection.append(Telephony.Threads.MESSAGE_COUNT);
        selection.append(" != 0");
        if (!needBlackList && !needBlockSpam){
            return   selection.append(" = 0").toString();
        }
        selection.append(" != 0");
        Set<String> addressString = new HashSet<>();

        if (needBlackList){
            addressString.addAll(getBlackListAddress(MyApplication.instance.getSharedPreferences()));
        }
        if (needBlockSpam){
            addressString.addAll(getBlockedConversations(MyApplication.instance.getSharedPreferences()));
        }

        selection.append(" AND ");
        selection.append(Telephony.Sms.ADDRESS);
        selection.append(" IN (");
        for (int i = 0; i < addressString.size(); i++) {
            selection.append("?");
            if (i < addressString.size() - 1) {
                selection.append(",");
            }
        }
        selection.append(")");

        return selection.toString();
    }

    /**
     * If the user has message blocking enabled, then in the menu of the conversation list, there's an item that says
     * Blocked (#). This method will find the number of blocked unread messages to show in that menu item and bind it
     */
    public static void bindBlockedMenuItem(final Context context, final SharedPreferences prefs, final MenuItem item, boolean showBlocked) {
        if (item == null) {
            return;
        }

        new BindMenuItemTask(context, prefs, item, showBlocked).execute((Void[]) null);
    }

    private static class BindMenuItemTask extends AsyncTask<Void, Void, Integer> {

        private Context mContext;
        private SharedPreferences mPrefs;
        private MenuItem mMenuItem;
        private boolean mShowBlocked;

        private BindMenuItemTask(Context context, SharedPreferences prefs, MenuItem item, boolean showBlocked) {
            mContext = context;
            mPrefs = prefs;
            mMenuItem = item;
            mShowBlocked = showBlocked;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMenuItem.setVisible(mPrefs.getBoolean(SettingsPre.BLOCKED_ENABLED, false));
//            mMenuItem.setTitle(mContext.getString(mShowBlocked ? R.string.menu_messages : R.string.menu_blocked));
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int unreadCount = 0;

            // Create a cursor for the conversation list
            Cursor conversationCursor = mContext.getContentResolver().query(
                    SmsHelper.CONVERSATIONS_CONTENT_PROVIDER, Conversation.ALL_THREADS_PROJECTION,
                    getCursorSelection(!mShowBlocked), getBlockedConversationArray(mPrefs), SmsHelper.sortDateDesc);

//            if (conversationCursor.moveToFirst()) {
//                do {
//                    Uri threadUri = Uri.withAppendedPath(Message.MMS_SMS_CONTENT_PROVIDER, conversationCursor.getString(Conversation.ID));
//                    Cursor messageCursor = mContext.getContentResolver().query(threadUri, MessageColumns.PROJECTION,
//                            SmsHelper.UNREAD_SELECTION, null, SmsHelper.sortDateDesc);
//                    unreadCount += messageCursor.getCount();
//                    messageCursor.close();
//                } while (conversationCursor.moveToNext());
//            }

            conversationCursor.close();
            return unreadCount;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Log.d("BindMenuItemTask", "onPostExecute: " + integer);
//            mMenuItem.setTitle(mContext.getString(mShowBlocked ? R.string.menu_unblocked_conversations : R.string.menu_blocked_conversations, integer));
        }
    }

    public static class FutureBlockedConversationObservable extends Observable {
        private static FutureBlockedConversationObservable sInstance = new FutureBlockedConversationObservable();

        public static FutureBlockedConversationObservable getInstance() {
            return sInstance;
        }

        public void futureBlockedConversationReceived() {
            synchronized (this) {
                setChanged();
                notifyObservers();
            }
        }
    }
}
