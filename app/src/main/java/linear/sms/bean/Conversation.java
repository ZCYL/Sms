package linear.sms.bean;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.text.TextUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import linear.sms.util.SmsHelper;

/**
 * Created by ZCYL on 2018/5/8.
 */
public class Conversation {

    public static final Uri sAllThreadsUri =
            Telephony.Threads.CONTENT_URI.buildUpon().appendQueryParameter("simple", "true").build();

    public static final String[] ALL_THREADS_PROJECTION = {
            Telephony.Threads._ID, Telephony.Threads.DATE, Telephony.Threads.MESSAGE_COUNT,
            Telephony.Threads.RECIPIENT_IDS, Telephony.Threads.SNIPPET, Telephony.Threads.SNIPPET_CHARSET,
            Telephony.Threads.READ, Telephony.Threads.ERROR, Telephony.Threads.HAS_ATTACHMENT
    };
    public static final String[] UNREAD_PROJECTION = {
            Telephony.Threads._ID,
            Telephony.Threads.READ
    };

    private static final String UNREAD_SELECTION = "(read=0 OR seen=0)";

    private static final String[] SEEN_PROJECTION = new String[]{
            "seen"
    };

    public static final int ID = 0;
    public static final int DATE = 1;
    public static final int MESSAGE_COUNT = 2;
    public static final int RECIPIENT_IDS = 3;
    public static final int SNIPPET = 4;
    public static final int SNIPPET_CS = 5;
    public static final int READ = 6;
    public static final int ERROR = 7;
    public static final int HAS_ATTACHMENT = 8;

    private final Context mContext;

    // The thread ID of this conversation.  Can be zero in the case of a
    // new conversation where the recipient set is changing as the user
    // types and we have not hit the database yet to create a thread.
    private long mThreadId;

    private long mDate;                 // The last update time.
    private int mMessageCount;          // Number of messages.
    private String mSnippet;            // Text of the most recent message.
    private boolean mHasUnreadMessages; // True if there are unread messages.
    private boolean mHasAttachment;     // True if any message has an attachment.
    private boolean mHasError;          // True if any message is in an error state.
    private boolean mIsChecked;         // True if user has selected the conversation for a
    private String mContactName;//联系人名称
    private String mContactNumper;//联系人电话
    // multi-operation such as delete.

    private static boolean sLoadingThreads;
    private static boolean sDeletingThreads;
    private static Object sDeletingThreadsLock = new Object();
    private boolean mMarkAsReadBlocked;
    private boolean mMarkAsReadWaiting;

    private Conversation(Context context) {
        mContext = context;
//        mRecipients = new ContactList();
        mThreadId = 0;
    }

    private Conversation(Context context, long threadId, boolean allowQuery) {
        mContext = context;
        if (!loadFromThreadId(threadId, allowQuery)) {
//            mRecipients = new ContactList();
            mThreadId = 0;
        }
    }

    private Conversation(Context context, Cursor cursor, boolean allowQuery) {
        mContext = context;
        fillFromCursor(context, this, cursor, allowQuery);
    }

    /**
     * Returns a snippet of text from the most recent message in the conversation.
     */
    public synchronized String getSnippet() {
        return mSnippet;
    }

    /**
     * Returns true if there are any unread messages in the conversation.
     */
    public boolean hasUnreadMessages() {
        synchronized (this) {
            return mHasUnreadMessages;
        }
    }

    /**
     * Returns the time of the last update to this conversation in milliseconds,
     * on the {@link System#currentTimeMillis} timebase.
     */
    public synchronized long getDate() {
        return mDate;
    }

    /**
     * Returns true if any messages in the conversation are in an error state.
     */
    public synchronized boolean hasError() {
        return mHasError;
    }

    public static Conversation createNew(Context context) {
        return new Conversation(context);
    }

    /**
     * Find the conversation matching the provided thread ID.
     */
    public static Conversation get(Context context, long threadId, boolean allowQuery) {
        Conversation conv = Cache.get(threadId);
        if (conv != null)
            return conv;

        conv = new Conversation(context, threadId, allowQuery);
        try {
            Cache.put(conv);
        } catch (IllegalStateException e) {
        }
        return conv;
    }


    /**
     * Set up the conversation cache.  To be called once at application
     * startup time.
     */
    public static void init(final Context context) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                cacheAllThreads(context);
            }
        }, "Conversation.init");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    private static void cacheAllThreads(Context context) {
        synchronized (Cache.getInstance()) {
            if (sLoadingThreads) {
                return;
            }
            sLoadingThreads = true;
        }

        // Keep track of what threads are now on disk so we
        // can discard anything removed from the cache.
        HashSet<Long> threadsOnDisk = new HashSet<>();

        // Query for all conversations.
        Cursor c = context.getContentResolver().query(sAllThreadsUri,
                ALL_THREADS_PROJECTION, null, null, null);
        try {
            if (c != null) {
                while (c.moveToNext()) {
                    long threadId = c.getLong(ID);
                    threadsOnDisk.add(threadId);

                    // Try to find this thread ID in the cache.
                    Conversation conv;
                    synchronized (Cache.getInstance()) {
                        conv = Cache.get(threadId);
                    }

                    if (conv == null) {
                        // Make a new Conversation and put it in
                        // the cache if necessary.
                        conv = new Conversation(context, c, true);
                        try {
                            synchronized (Cache.getInstance()) {
                                Cache.put(conv);
                            }
                        } catch (IllegalStateException e) {
                        }
                    } else {
                        // Or update in place so people with references
                        // to conversations get updated too.
                        fillFromCursor(context, conv, c, true);
                    }
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
            synchronized (Cache.getInstance()) {
                sLoadingThreads = false;
            }
        }

        // Purge the cache of threads that no longer exist on disk.
        Cache.keepOnly(threadsOnDisk);
    }

    private boolean loadFromThreadId(long threadId, boolean allowQuery) {
        Cursor c = mContext.getContentResolver().query(sAllThreadsUri, ALL_THREADS_PROJECTION,
                "_id=" + Long.toString(threadId), null, null);
        try {
            if (c.moveToFirst()) {
                fillFromCursor(mContext, this, c, allowQuery);
            } else {
                return false;
            }
        } finally {
            c.close();
        }
        return true;
    }

    /**
     * Fill the specified conversation with the values from the specified
     * cursor, possibly setting recipients to empty if value allowQuery
     * is false and the recipient IDs are not in cache.  The cursor should
     * be one made via {@link #}.
     */
    private static void fillFromCursor(Context context, Conversation conv,
                                       Cursor c, boolean allowQuery) {
        synchronized (conv) {
            conv.mThreadId = c.getLong(ID);
            conv.mDate = c.getLong(DATE);
            conv.mMessageCount = c.getInt(MESSAGE_COUNT);

            // Replace the snippet with a default value if it's empty.
            String snippet = SmsHelper.extractEncStrFromCursor(c, SNIPPET, SNIPPET_CS);
            if (TextUtils.isEmpty(snippet)) {
                snippet = "无主题";
            }
            conv.mSnippet = snippet;
            conv.mContactNumper = SmsHelper.getContactAddress(context,conv.getThreadId());
            conv.mContactName = SmsHelper.getContactNameByAddr(context,conv.mContactNumper);

            conv.setHasUnreadMessages(c.getInt(READ) == 0);
            conv.mHasError = (c.getInt(ERROR) != 0);
            conv.mHasAttachment = (c.getInt(HAS_ATTACHMENT) != 0);
        }
        // Fill in as much of the conversation as we can before doing the slow stuff of looking
        // up the contacts associated with this conversation.
        String recipientIds = c.getString(RECIPIENT_IDS);
//        ContactList recipients = ContactList.getByIds(recipientIds, allowQuery);
//        synchronized (conv) {
//            conv.mRecipients = recipients;
//        }
    }

    public String getContactName() {
        if (TextUtils.isEmpty(mContactName)){
            return mContactNumper;
        }
        return mContactName;
    }


    public synchronized long getThreadId() {
        return mThreadId;
    }

    private void setHasUnreadMessages(boolean flag) {
        synchronized (this) {
            mHasUnreadMessages = flag;
        }
    }

    /**
     * Returns a temporary Conversation (not representing one on disk) wrapping
     * the contents of the provided cursor.  The cursor should be the one
     * returned to your AsyncQueryHandler passed in t
     * The recipient list of this conversation can be empty if the results
     * were not in cache.
     */
    public static Conversation from(Context context, Cursor cursor) {
        long threadId = cursor.getLong(ID);
        if (threadId > 0) {
            Conversation conv = Cache.get(threadId);
            if (conv != null) {
                fillFromCursor(context, conv, cursor, false);   // update the existing conv in-place
                return conv;
            }
        }
        Conversation conv = new Conversation(context, cursor, false);
        try {
            Cache.put(conv);
        } catch (IllegalStateException e) {
        }
        return conv;
    }

    /**
     * Private cache for the use of the various forms of Conversation.get.
     */
    private static class Cache {
        private static Cache sInstance = new Cache();

        static Cache getInstance() {
            return sInstance;
        }

        private final HashSet<Conversation> mCache;

        private Cache() {
            mCache = new HashSet<Conversation>(10);
        }

        /**
         * Return the conversation with the specified thread ID, or
         * null if it's not in cache.
         */
        static Conversation get(long threadId) {
            synchronized (sInstance) {
                for (Conversation c : sInstance.mCache) {
                    if (c.getThreadId() == threadId) {
                        return c;
                    }
                }
            }
            return null;
        }

        /**
         * Return the conversation with the specified recipient
         * list, or null if it's not in cache.
         */
//        static Conversation get(ContactList list) {
//            synchronized (sInstance) {
//                for (Conversation c : sInstance.mCache) {
//                    if (c.getRecipients().equals(list)) {
//                        return c;
//                    }
//                }
//            }
//            return null;
//        }

        /**
         * Put the specified conversation in the cache.  The caller
         * should not place an already-existing conversation in the
         * cache, but rather update it in place.
         */
        static void put(Conversation c) {
            synchronized (sInstance) {

                if (sInstance.mCache.contains(c)) {
                    throw new IllegalStateException("cache already contains " + c +
                            " threadId: " + c.mThreadId);
                }
                sInstance.mCache.add(c);
            }
        }

        /**
         * Replace the specified conversation in the cache. This is used in cases where we
         * lookup a conversation in the cache by threadId, but don't find it. The caller
         * then builds a new conversation (from the cursor) and tries to add it, but gets
         * an exception that the conversation is already in the cache, because the hash
         * is based on the recipients and it's there under a stale threadId. In this function
         * we remove the stale entry and add the new one. Returns true if the operation is
         * successful
         */
        static boolean replace(Conversation c) {
            synchronized (sInstance) {

                if (!sInstance.mCache.contains(c)) {
                    return false;
                }
                // Here it looks like we're simply removing and then re-adding the same object
                // to the hashset. Because the hashkey is the conversation's recipients, and not
                // the thread id, we'll actually remove the object with the stale threadId and
                // then add the the conversation with updated threadId, both having the same
                // recipients.
                sInstance.mCache.remove(c);
                sInstance.mCache.add(c);
                return true;
            }
        }

        static void remove(long threadId) {
            synchronized (sInstance) {
                for (Conversation c : sInstance.mCache) {
                    if (c.getThreadId() == threadId) {
                        sInstance.mCache.remove(c);
                        return;
                    }
                }
            }
        }

        /**
         * Remove all conversations from the cache that are not in
         * the provided set of thread IDs.
         */
        static void keepOnly(Set<Long> threads) {
            synchronized (sInstance) {
                Iterator<Conversation> iter = sInstance.mCache.iterator();
                while (iter.hasNext()) {
                    Conversation c = iter.next();
                    if (!threads.contains(c.getThreadId())) {
                        iter.remove();
                    }
                }
            }
        }
    }

    public String getContactNumper() {
        return mContactNumper;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "mThreadId=" + mThreadId +
                ", mDate=" + mDate +
                ", mMessageCount=" + mMessageCount +
                ", mSnippet='" + mSnippet + '\'' +
                ", mHasUnreadMessages=" + mHasUnreadMessages +
                ", mHasAttachment=" + mHasAttachment +
                ", mHasError=" + mHasError +
                ", mIsChecked=" + mIsChecked +
                ", mContactName='" + mContactName + '\'' +
                ", mContactNumper='" + mContactNumper + '\'' +
                ", mMarkAsReadBlocked=" + mMarkAsReadBlocked +
                ", mMarkAsReadWaiting=" + mMarkAsReadWaiting +
                '}';
    }
}
