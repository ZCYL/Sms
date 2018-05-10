package linear.sms.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.util.LruCache;

import java.util.regex.Pattern;

import linear.sms.bean.ColumnsMap;
import linear.sms.bean.MessageItem;

public class MessageItemCache extends LruCache<Long, MessageItem> {
    private final String TAG = "MessageItemCache";

    private Context mContext;
    private ColumnsMap mColumnsMap;
    private Pattern mSearchHighlighter;

    public MessageItemCache(Context context, ColumnsMap columnsMap, Pattern searchHighlighter, int maxSize) {
        super(maxSize);

        mContext = context;
        mColumnsMap = columnsMap;
        mSearchHighlighter = searchHighlighter;
    }

    @Override
    protected void entryRemoved(boolean evicted, Long key, MessageItem oldValue,
                                MessageItem newValue) {
//        oldValue.cancelPduLoading();
    }

    /**
     * Generates a unique key for this message item given its type and message ID.
     *
     * @param type
     * @param msgId
     */
    public long getKey(String type, long msgId) {
        if (type.equals("mms")) {
            return -msgId;
        } else {
            return msgId;
        }
    }


    public MessageItem get(String type, long msgId, Cursor c) {
        long key = getKey(type, msgId);
        MessageItem item = get(key);

        if (item == null && CursorUtils.isValid(c)) {
            try {
                item = new MessageItem(mContext, type, c, mColumnsMap);
                key = getKey(item.mType, item.mMsgId);
                put(key, item);
            } catch (RuntimeException e) {
                Log.e(TAG, "getCachedMessageItem: ", e);
            }
        }
        return item;
    }
}
