package linear.sms.ui.act;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import linear.sms.R;
import linear.sms.adapter.ConversationAdapter;
import linear.sms.adapter.RecyclerCursorAdapter;
import linear.sms.bean.Conversation;
import linear.sms.ui.base.BaseActivity;
import linear.sms.util.BlockedConversationHelper;
import linear.sms.util.SettingsPre;
import linear.sms.util.SmsHelper;

/**
 * Created by ZCYL on 2018/5/17.
 */
public class SpamActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        RecyclerCursorAdapter.ItemClickListener<Conversation>, RecyclerCursorAdapter.MultiSelectListener  {

    @BindView(R.id.recy_message_spam)
    RecyclerView mRecyclerView;
    private ConversationAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_spam);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initList();
        getLoaderManager().initLoader(0, savedInstanceState, this);
    }

    private void initList() {
        mAdapter = new ConversationAdapter(this);
        mAdapter.setItemClickListener(this);
        mAdapter.setMultiSelectListener(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext, SmsHelper.CONVERSATIONS_CONTENT_PROVIDER, Conversation.ALL_THREADS_PROJECTION,
                BlockedConversationHelper.getSpamActivitySelection(SettingsPre.isBlackListEnable(),
                        SettingsPre.isBlockEnable()),
                null, "date DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAdapter != null) {
            mAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.changeCursor(null);
        }
    }

    @Override
    public void onItemClick(Conversation object, View view) {
        MessageListActivity.launch(this, object.getThreadId(), -1, null, true);
    }

    @Override
    public void onItemLongClick(Conversation object, View view) {
        showRemoveDialog(object);
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

    private void showRemoveDialog(Conversation conversation) {
        final EditText et = new EditText(this);
        et.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(this)
                .setTitle("将该号码移出")
                .setView(et)
                .setPositiveButton("确定", (dialog, which) -> {
                    BlockedConversationHelper.blockConversation(mPrefs, conversation.getContactNumper());
                    AndroidSchedulers.mainThread().scheduleDirect(() -> {
                        mAdapter.notifyDataSetChanged();
                    }, 200, TimeUnit.MILLISECONDS);
                    dialog.dismiss();
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
