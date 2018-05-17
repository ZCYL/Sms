package linear.sms.ui.act;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import linear.sms.R;
import linear.sms.ui.base.BaseActivity;
import linear.sms.util.SettingsPre;
import linear.sms.widget.PreferenceView;
import linear.sms.widget.SwitchPreference;

/**
 * Created by ZCYL on 2018/4/14.
 */
public class SettingActivity extends BaseActivity {


    @BindView(R.id.defaultSms)
    PreferenceView mDefaultSmsView;
    @BindView(R.id.spam_filter)
    SwitchPreference mBlockView;
    @BindView(R.id.app_info)
    PreferenceView mAppInfoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBlockView.setChecked(SettingsPre.isBlockEnable());
        mDefaultSmsView.setClickListener(v -> {
            String currentPn = getPackageName();//获取当前程序包名
            String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);//获取手机当前设置的默认短信应用的包名
            if (!defaultSmsApp.equals(currentPn)) {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, currentPn);
                startActivity(intent);
            }
        });
        mBlockView.setOnCheckedChangeListener((buttonView, isChecked) -> SettingsPre.setBlockEnable(isChecked));
        mAppInfoView.setClickListener(v -> new AlertDialog.Builder(mContext)
                .setTitle("帮助及反馈")
                .setMessage("此App为李振初的本科毕业设计作品，有问题请直接联系本人")
                .setPositiveButton("确定", (dialog, which) -> dialog.dismiss())
                .create()
                .show());
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
}
