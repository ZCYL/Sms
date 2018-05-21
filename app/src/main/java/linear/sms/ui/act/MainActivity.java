package linear.sms.ui.act;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import linear.sms.R;
import linear.sms.adapter.ConversationAdapter;
import linear.sms.adapter.RecyclerCursorAdapter;
import linear.sms.bean.Conversation;
import linear.sms.ui.base.BaseActivity;
import linear.sms.util.BlockedConversationHelper;
import linear.sms.util.SettingsPre;
import linear.sms.util.SmsHelper;

public class MainActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        RecyclerCursorAdapter.ItemClickListener<Conversation>, RecyclerCursorAdapter.MultiSelectListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private ConversationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        initList();
        getLoaderManager().initLoader(0, savedInstanceState, MainActivity.this);
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
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.fab)
    public void onWriteMessageClick(View view) {
        MessageListActivity.launch(this, -1, -1, null, true);
    }

    @OnClick(R.id.settings)
    public void onSettingClick() {
        mDrawer.closeDrawer(GravityCompat.START);
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mContext, SettingActivity.class));
            }
        }, 200, TimeUnit.MILLISECONDS);
    }

    @OnClick(R.id.archived)
    public void onSpamClick(View view) {
        mDrawer.closeDrawer(GravityCompat.START);
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mContext, SpamActivity.class));
            }
        }, 200, TimeUnit.MILLISECONDS);
    }

    @OnClick(R.id.black_list)
    public void onBlackListClick(View view) {
        mDrawer.closeDrawer(GravityCompat.START);
        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mContext, BlackListActivity.class));
            }
        }, 200, TimeUnit.MILLISECONDS);
    }

    @OnClick(R.id.inbox)
    public void onInboxClick() {
        mDrawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onItemClick(Conversation object, View view) {
        MessageListActivity.launch(this, object.getThreadId(), -1, null, true);
    }

    @Override
    public void onItemLongClick(Conversation object, View view) {

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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext, SmsHelper.CONVERSATIONS_CONTENT_PROVIDER, Conversation.ALL_THREADS_PROJECTION,
                BlockedConversationHelper.getCursorSelection(SettingsPre.isBlackListEnable(),SettingsPre.isBlockEnable()),
                null, "date DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAdapter != null) {
            mAdapter.changeCursor(data);
        }

//        mEmptyState.setVisibility(data != null && data.getCount() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.changeCursor(null);
        }
    }
}
