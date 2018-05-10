package linear.sms.util;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.provider.Telephony.Sms;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import linear.sms.bean.Message;

public class SmsHelper {

    public static final Uri SMS_CONTENT_PROVIDER = Uri.parse("content://sms/");
    public static final Uri MMS_CONTENT_PROVIDER = Uri.parse("content://mms/");
    public static final Uri SENT_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/sent");
    public static final Uri DRAFTS_CONTENT_PROVIDER = Uri.parse("content://sms/draft");
    public static final Uri PENDING_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/outbox");
    public static final Uri RECEIVED_MESSAGE_CONTENT_PROVIDER = Uri.parse("content://sms/inbox");
    public static final Uri CONVERSATIONS_CONTENT_PROVIDER = Uri.parse("content://mms-sms/conversations?simple=true");
    public static final Uri ADDRESSES_CONTENT_PROVIDER = Uri.parse("content://mms-sms/canonical-addresses");

    public static final String MAX_MMS_ATTACHMENT_SIZE_UNLIMITED = "unlimited";
    public static final String MAX_MMS_ATTACHMENT_SIZE_300KB = "300kb";
    public static final String MAX_MMS_ATTACHMENT_SIZE_600KB = "600kb";
    public static final String MAX_MMS_ATTACHMENT_SIZE_1MB = "1mb";

    public static final String sortDateDesc = "date DESC";
    public static final String sortDateAsc = "date ASC";

    public static final byte UNREAD = 0;
    public static final byte READ = 1;

    // Attachment types
    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    public static final int VIDEO = 2;
    public static final int AUDIO = 3;
    public static final int SLIDESHOW = 4;

    // Columns for SMS content providers
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_THREAD_ID = "thread_id";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_RECIPIENT = "recipient_ids";
    public static final String COLUMN_PERSON = "person";
    public static final String COLUMN_SNIPPET = "snippet";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DATE_NORMALIZED = "normalized_date";
    public static final String COLUMN_DATE_SENT = "date_sent";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_ERROR = "error";
    public static final String COLUMN_READ = "read";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_MMS = "ct_t";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_SUB = "sub";
    public static final String COLUMN_MSG_BOX = "msg_box";
    public static final String COLUMN_SUBJECT = "subject";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_SEEN = "seen";

    public static final String UNREAD_SELECTION = COLUMN_READ + " = " + UNREAD;
    public static final String UNSEEN_SELECTION = COLUMN_SEEN + " = " + UNREAD;
    public static final String FAILED_SELECTION = COLUMN_TYPE + " = " + Message.FAILED;

    public static final int ADDRESSES_ADDRESS = 1;

    private static final String TAG = "SMSHelper";
    private static SmsManager sms;

    @SuppressLint("InlinedApi")
    public static final String[] PROJECTION = new String[] {
            Telephony.MmsSms.TYPE_DISCRIMINATOR_COLUMN,
            BaseColumns._ID,
            Telephony.Sms.Conversations.THREAD_ID,
            // For SMS
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE,
            Telephony.Sms.DATE_SENT,
            Telephony.Sms.READ,
            Telephony.Sms.TYPE,
            Telephony.Sms.STATUS,
            Telephony.Sms.LOCKED,
            Telephony.Sms.ERROR_CODE,
            // For MMS
            Telephony.Mms.SUBJECT,
            Telephony.Mms.SUBJECT_CHARSET,
            Telephony.Mms.DATE,
            Telephony.Mms.DATE_SENT,
            Telephony.Mms.READ,
            Telephony.Mms.MESSAGE_TYPE,
            Telephony.Mms.MESSAGE_BOX,
            Telephony.Mms.DELIVERY_REPORT,
            Telephony.Mms.READ_REPORT,
            Telephony.MmsSms.PendingMessages.ERROR_TYPE,
            Telephony.Mms.LOCKED,
            Telephony.Mms.STATUS,
            Telephony.Mms.TEXT_ONLY
    };


    public SmsHelper() {

    }

    /**
     * The quality parameter which is used to compress JPEG images.
     */
    public static final int IMAGE_COMPRESSION_QUALITY = 95;
    /**
     * The minimum quality parameter which is used to compress JPEG images.
     */
    public static final int MINIMUM_IMAGE_COMPRESSION_QUALITY = 50;

    /**
     * Message type: all messages.
     */
    public static final int MESSAGE_TYPE_ALL = 0;

    /**
     * Message type: inbox.
     */
    public static final int MESSAGE_TYPE_INBOX = 1;

    /**
     * Message type: sent messages.
     */
    public static final int MESSAGE_TYPE_SENT = 2;

    /**
     * Message type: drafts.
     */
    public static final int MESSAGE_TYPE_DRAFT = 3;

    /**
     * Message type: outbox.
     */
    public static final int MESSAGE_TYPE_OUTBOX = 4;

    /**
     * Message type: failed outgoing message.
     */
    public static final int MESSAGE_TYPE_FAILED = 5;

    /**
     * Message type: queued to send later.
     */
    public static final int MESSAGE_TYPE_QUEUED = 6;

    /**
     * MMS address parsing data structures
     */
    // allowable phone number separators
    private static final char[] NUMERIC_CHARS_SUGAR = {
            '-', '.', ',', '(', ')', ' ', '/', '\\', '*', '#', '+'
    };

    private static String[] sNoSubjectStrings;

    /**
     * Add incoming SMS to inbox
     *
     * @param context
     * @param address Address of sender
     * @param body    Body of incoming SMS message
     * @param time    Time that incoming SMS message was sent at
     */
    public static Uri addMessageToInbox(Context context, String address, String body, long time) {

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues cv = new ContentValues();

        cv.put("address", address);
        cv.put("body", body);
        cv.put("date_sent", time);

        return contentResolver.insert(RECEIVED_MESSAGE_CONTENT_PROVIDER, cv);
    }

    /**
     * Returns true iff the folder (message type) identifies an
     * outgoing message.
     *
     * @hide
     */
    public static boolean isOutgoingFolder(int messageType) {
        return (messageType == MESSAGE_TYPE_FAILED)
                || (messageType == MESSAGE_TYPE_OUTBOX)
                || (messageType == MESSAGE_TYPE_SENT)
                || (messageType == MESSAGE_TYPE_QUEUED);
    }

    public static int getUnseenSMSCount(Context context, long threadId) {
        Cursor cursor = null;
        int count = 0;
        String selection = UNSEEN_SELECTION + " AND " + UNREAD_SELECTION + (threadId == 0 ? "" : " AND " + COLUMN_THREAD_ID + " = " + threadId);

        try {
            cursor = context.getContentResolver().query(RECEIVED_MESSAGE_CONTENT_PROVIDER, new String[]{COLUMN_ID}, selection, null, null);
            cursor.moveToFirst();
            count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;
    }

    public static long getThreadId(Context context, String address) {
        Cursor cursor = null;
        long threadId = 0;

        try {
            cursor = context.getContentResolver().query(SENT_MESSAGE_CONTENT_PROVIDER, new String[]{COLUMN_THREAD_ID}, COLUMN_ADDRESS + "=" + address, null, sortDateDesc);
            cursor.moveToFirst();
            threadId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_THREAD_ID));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return threadId;
    }

    public static List<Message> getFailedMessages(Context context) {
        Cursor cursor = null;
        List<Message> messages = new ArrayList<>();

        try {
            cursor = context.getContentResolver().query(SMS_CONTENT_PROVIDER, new String[]{COLUMN_ID}, FAILED_SELECTION, null, sortDateDesc);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                messages.add(new Message(context, cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return messages;
    }

    public static List<Message> deleteFailedMessages(Context context, long threadId) {
        Log.d(TAG, "Deleting failed messages");
        Cursor cursor = null;
        List<Message> messages = new ArrayList<>();

        try {
            cursor = context.getContentResolver().query(SMS_CONTENT_PROVIDER, new String[]{COLUMN_ID}, FAILED_SELECTION, null, sortDateDesc);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                messages.add(new Message(context, cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        for (Message m : messages) {
            if (m.getThreadId() == threadId) {
                Log.d(TAG, "Deleting failed message to " + m.getName() + "\n Body: " + m.getBody());
                m.delete();
            }
        }
        return messages;
    }

    /**
     * Add an SMS to the given URI.
     *
     * @param resolver       the content resolver to use
     * @param uri            the URI to add the message to
     * @param address        the address of the sender
     * @param body           the body of the message
     * @param subject        the pseudo-subject of the message
     * @param date           the timestamp for the message
     * @param read           true if the message has been read, false if not
     * @param deliveryReport true if a delivery report was requested, false if not
     * @return the URI for the new message
     * @hide
     */
    public static Uri addMessageToUri(ContentResolver resolver,
                                      Uri uri, String address, String body, String subject,
                                      Long date, boolean read, boolean deliveryReport) {
        return addMessageToUri(resolver, uri, address, body, subject,
                date, read, deliveryReport, -1L);
    }

    /**
     * Add an SMS to the given URI with the specified thread ID.
     *
     * @param resolver       the content resolver to use
     * @param uri            the URI to add the message to
     * @param address        the address of the sender
     * @param body           the body of the message
     * @param subject        the pseudo-subject of the message
     * @param date           the timestamp for the message
     * @param read           true if the message has been read, false if not
     * @param deliveryReport true if a delivery report was requested, false if not
     * @param threadId       the thread_id of the message
     * @return the URI for the new message
     * @hide
     */
    public static Uri addMessageToUri(ContentResolver resolver,
                                      Uri uri, String address, String body, String subject,
                                      Long date, boolean read, boolean deliveryReport, long threadId) {
        ContentValues values = new ContentValues(7);

        values.put(Sms.ADDRESS, address);
        if (date != null) {
            values.put(Sms.DATE, date);
        }
        values.put(Sms.READ, read ? Integer.valueOf(1) : Integer.valueOf(0));
        values.put(Sms.SUBJECT, subject);
        values.put(Sms.BODY, body);
        if (deliveryReport) {
            values.put(Sms.STATUS, Sms.STATUS_PENDING);
        }
        if (threadId != -1L) {
            values.put(Sms.THREAD_ID, threadId);
        }
        return resolver.insert(uri, values);
    }


    /**
     * Is the specified address an email address?
     *
     * @param address the input address to test
     * @return true if address is an email address; false otherwise.
     * @hide
     */
    public static boolean isEmailAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            return false;
        }

        String s = extractAddrSpec(address);
        Matcher match = Patterns.EMAIL_ADDRESS.matcher(s);
        return match.matches();
    }

    /**
     * Regex pattern for names and email addresses.
     * <ul>
     * <li><em>mailbox</em> = {@code name-addr}</li>
     * <li><em>name-addr</em> = {@code [display-name] angle-addr}</li>
     * <li><em>angle-addr</em> = {@code [CFWS] "<" addr-spec ">" [CFWS]}</li>
     * </ul>
     *
     * @hide
     */
    public static final Pattern NAME_ADDR_EMAIL_PATTERN =
            Pattern.compile("\\s*(\"[^\"]*\"|[^<>\"]+)\\s*<([^<>]+)>\\s*");

    /**
     * Helper method to extract email address from address string.
     *
     * @hide
     */
    public static String extractAddrSpec(String address) {
        Matcher match = NAME_ADDR_EMAIL_PATTERN.matcher(address);

        if (match.matches()) {
            return match.group(2);
        }
        return address;
    }

    private static HashMap numericSugarMap = new HashMap(NUMERIC_CHARS_SUGAR.length);

    /**
     * Given a phone number, return the string without syntactic sugar, meaning parens,
     * spaces, slashes, dots, dashes, etc. If the input string contains non-numeric
     * non-punctuation characters, return null.
     */
    private static String parsePhoneNumberForMms(String address) {
        StringBuilder builder = new StringBuilder();
        int len = address.length();

        for (int i = 0; i < len; i++) {
            char c = address.charAt(i);

            // accept the first '+' in the address
            if (c == '+' && builder.length() == 0) {
                builder.append(c);
                continue;
            }

            if (Character.isDigit(c)) {
                builder.append(c);
                continue;
            }

            if (numericSugarMap.get(c) == null) {
                return null;
            }
        }
        return builder.toString();
    }

    public static String extractEncStrFromCursor(Cursor cursor,
                                                 int columnRawBytes, int columnCharset) {
        String rawBytes = cursor.getString(columnRawBytes);
        int charset = cursor.getInt(columnCharset);

        if (TextUtils.isEmpty(rawBytes)) {
            return "";
        } else if (charset == CharacterSets.ANY_CHARSET) {
            return rawBytes;
        } else {
            return new EncodedStringValue(charset, CharacterSets.getBytes(rawBytes)).getString();
        }
    }

    public static String getContactNameByAddr(Context context, String phoneNumber) {
        Uri personUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cur = context.getContentResolver().query(personUri,
                new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cur.moveToFirst()) {
            int nameIdx = cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            String name = cur.getString(nameIdx);
            cur.close();
            return name;
        }
        return phoneNumber;
    }

    public static String getContactNameByThreadId(Context context,long threaId){
        String address = getContactAddress(context,threaId);
        if(TextUtils.isEmpty(address)){
            return "";
        }
        return getContactNameByAddr(context,address);
    }

    public static String getContactAddress(Context context, long threadId) {
        String address = null;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(SMS_CONTENT_PROVIDER, new String[]{SmsHelper.COLUMN_ADDRESS}, COLUMN_THREAD_ID + "=" + threadId, null, null);
            cursor.moveToFirst();
            address = cursor.getString(cursor.getColumnIndexOrThrow(SmsHelper.COLUMN_ADDRESS));
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }
        return address;
    }
}
