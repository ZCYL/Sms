package linear.sms.ui.act;

import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import linear.sms.R;
import linear.sms.bean.Message;
import linear.sms.bean.MessageItem;
import linear.sms.bean.MessageListAdapter;
import linear.sms.bean.RecyclerCursorAdapter;
import linear.sms.ui.base.BaseActivity;
import linear.sms.util.SmsHelper;

public class MessageListActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerCursorAdapter.ItemClickListener<MessageItem>, RecyclerCursorAdapter.MultiSelectListener {
    private final String TAG = "MessageListActivity";

    public static final String ARG_THREAD_ID = "thread_id";
    public static final String ARG_ROW_ID = "rowId";
    public static final String ARG_HIGHLIGHT = "highlight";
    public static final String ARG_SHOW_IMMEDIATE = "showImmediate";

    private long mThreadId;

    private MessageListAdapter mAdapter;
    @BindView(R.id.recycle_message_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.text_message_send)
    EditText mEditMessageSend;
    @BindView(R.id.toolbar_edit_text)
    EditText mEditAddress;
    @BindView(R.id.toolbar_title)
    TextView mTextAddress;

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
        mAdapter = new MessageListAdapter(this);
        mAdapter.setItemClickListener(this);
        mAdapter.setMultiSelectListener(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void init(Intent intent) {
        mThreadId = intent.getLongExtra(ARG_THREAD_ID, -1);
        if (mThreadId != -1) {
            getLoaderManager().initLoader(0, null, this);
            mTextAddress.setText(SmsHelper.getContactNameByThreadId(this, mThreadId));
        } else {
            mTextAddress.setVisibility(View.GONE);
            mEditAddress.setVisibility(View.VISIBLE);
        }
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
        if (mThreadId == -1){
            showToast("新建短信不能拨号！");
            return;
        }
        Intent openDialerIntent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + SmsHelper.getContactAddress(this, mThreadId));
        openDialerIntent.setData(data);
        startActivity(openDialerIntent);
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
            mRecyclerView.scrollToPosition(data.getCount() - 1);
        }
    }

    @OnClick(R.id.bn_message_send)
    public void onMessageSendClick(View view) {
        String text = mEditMessageSend.getText().toString();
        if (TextUtils.isEmpty(text)) {
            showToast("请先编辑短信");
            mEditMessageSend.requestFocus();
            return;
        }
        String address = mThreadId == -1 ? mEditAddress.getText().toString()
                : SmsHelper.getContactAddress(this, mThreadId);
        if (TextUtils.isEmpty(address)){
            showToast("请输入收件人号码");
            mEditAddress.requestFocus();
            return;
        }
        mEditMessageSend.setText("");
        sendMessage(this, text, address);
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
