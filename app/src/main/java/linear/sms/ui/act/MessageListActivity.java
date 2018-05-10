package linear.sms.ui.act;

import android.Manifest;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import linear.sms.R;
import linear.sms.bean.Message;
import linear.sms.bean.MessageItem;
import linear.sms.bean.MessageListAdapter;
import linear.sms.bean.RecyclerCursorAdapter;
import linear.sms.ui.base.BaseActivity;
import linear.sms.util.SmsHelper;

public class MessageListActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,RecyclerCursorAdapter.ItemClickListener<MessageItem>,RecyclerCursorAdapter.MultiSelectListener {
    private final String TAG = "MessageListActivity";

    public static final String ARG_THREAD_ID = "thread_id";
    public static final String ARG_ROW_ID = "rowId";
    public static final String ARG_HIGHLIGHT = "highlight";
    public static final String ARG_SHOW_IMMEDIATE = "showImmediate";

    private long mThreadId;
    private long mRowId;
    private String mHighlight;
    private boolean mShowImmediate;

    private long mWaitingForThreadId = -1;

    private MessageListAdapter mAdapter;
    @BindView(R.id.recycle_message_list)
    RecyclerView mRecyclerView;

    public static void launch(BaseActivity context, long threadId, long rowId, String pattern, boolean showImmediate) {
        Intent intent = new Intent(context, MessageListActivity.class);
        intent.putExtra(ARG_THREAD_ID, threadId);
        intent.putExtra(ARG_ROW_ID, rowId);
        intent.putExtra(ARG_HIGHLIGHT, pattern);
        intent.putExtra(ARG_SHOW_IMMEDIATE, showImmediate);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_message_list);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        init(getIntent());
        getLoaderManager().initLoader(0, null, this);
        mAdapter = new MessageListAdapter(this);
        mAdapter.setItemClickListener(this);
        mAdapter.setMultiSelectListener(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        TextView textView = findViewById(R.id.toolbar_title);
        textView.setText(SmsHelper.getContactNameByThreadId(this,mThreadId));
    }

    private void init(Intent intent) {
        mThreadId = intent.getLongExtra(ARG_THREAD_ID, -1);
        mRowId = intent.getLongExtra(ARG_ROW_ID, -1);
        mHighlight = intent.getStringExtra(ARG_HIGHLIGHT);
        mShowImmediate = intent.getBooleanExtra(ARG_SHOW_IMMEDIATE, false);
    }

    public void getResultForThreadId(long threadId) {
        mWaitingForThreadId = threadId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.message_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_call:
                makeCall();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeCall() {
        Intent openDialerIntent = new Intent(Intent.ACTION_CALL);
//        openDialerIntent.setData(Uri.parse("tel:" + mConversationLegacy.getAddress()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(openDialerIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext,
                Uri.withAppendedPath(Message.MMS_SMS_CONTENT_PROVIDER, String.valueOf(mThreadId)),
                SmsHelper.PROJECTION, null, null, "normalized_date ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAdapter != null) {
            // Swap the new cursor in.  (The framework will take care of closing the, old cursor once we return.)
            mAdapter.changeCursor(data);
            mRecyclerView.smoothScrollToPosition(data.getCount());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.changeCursor(null);
        }
    }

    @Override
    public void onItemClick(MessageItem object, View view) {

    }

    @Override
    public void onItemLongClick(MessageItem object, View view) {

    }

    @Override
    public void onMultiSelectStateChanged(boolean enabled) {

    }

    @Override
    public void onItemAdded(long id) {

    }

    @Override
    public void onItemRemoved(long id) {

    }

    public static void sendMessage(Context context, String content, String phoneNumber) {
        SmsManager sms = SmsManager.getDefault();
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(), 0);
        sms.sendTextMessage(phoneNumber, null, content, pi, null);
    }
}