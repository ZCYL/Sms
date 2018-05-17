package linear.sms.ui.act;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import linear.sms.R;
import linear.sms.adapter.BlackListAdapter;
import linear.sms.ui.base.BaseActivity;
import linear.sms.util.FileUtil;

/**
 * Created by ZCYL on 2018/5/17.
 */
public class BlackListActivity extends BaseActivity {

    @BindView(R.id.recy_message_black_list)
    RecyclerView mRecyclerView;
    BlackListAdapter mBlackListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_black_list);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBlackListAdapter = new BlackListAdapter();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mBlackListAdapter.setOnLongClickListener(position -> new AlertDialog.Builder(mContext)
                .setTitle("将该号码移出黑名单？")
                .setMessage("移除之后，该号码的短信将不会被拦截")
                .setPositiveButton("确定", (dialog, which) -> {
                    FileUtil.removeBlackContact(FileUtil.readBlackContact().get(position));
                    AndroidSchedulers.mainThread().scheduleDirect(() -> {
                        mBlackListAdapter.notifyDataSetChanged();
                    }, 200, TimeUnit.MILLISECONDS);
                    dialog.dismiss();
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .create()
                .show());
        mRecyclerView.setAdapter(mBlackListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_black_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_black_add:
                showAddDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddDialog() {
        final EditText et = new EditText(this);
        et.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(this)
                .setTitle("请输入黑名单号码")
                .setView(et)
                .setPositiveButton("确定", (dialog, which) -> {
                    String num = et.getText().toString();
                    FileUtil.saveBlackContact(num);
                    AndroidSchedulers.mainThread().scheduleDirect(() -> {
                        mBlackListAdapter.notifyDataSetChanged();
                    }, 200, TimeUnit.MILLISECONDS);
                    dialog.dismiss();
                })
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
