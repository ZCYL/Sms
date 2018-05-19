package linear.sms.ui.act;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import linear.sms.R;
import linear.sms.bayes.PriorProbability;
import linear.sms.bean.Message;
import linear.sms.ui.base.BaseActivity;
import linear.sms.util.BlockedConversationHelper;
import linear.sms.util.SettingsPre;
import linear.sms.util.SmsHelper;
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
    @BindView(R.id.spam_sync)
    PreferenceView mSyncSpamSms;
    @BindView(R.id.block_address_filter)
    SwitchPreference mBlackBlock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBlockView.setChecked(SettingsPre.isBlockEnable());
        mBlackBlock.setChecked(SettingsPre.isBlackListEnable());
        mBlockView.setOnCheckedChangeListener((buttonView, isChecked) -> SettingsPre.setBlockEnable(isChecked));
        mBlackBlock.setOnCheckedChangeListener((buttonView, isChecked) -> SettingsPre.setBlackListEnable(isChecked));
        mSyncSpamSms.setClickListener(v -> {
            showProgressBar();
            Schedulers.io().scheduleDirect(() -> {
                BlockedConversationHelper.cleanBayesConversation(mPrefs);
                Cursor cursor = getContentResolver().query(SmsHelper.CONVERSATIONS_CONTENT_PROVIDER,
                        null,null,null,"date DESC");
                while (cursor.moveToNext()){
                    long threadId = cursor.getLong(0);
                    Cursor messageCursor = getContentResolver()
                            .query(Uri.withAppendedPath(Message.MMS_SMS_CONTENT_PROVIDER, String.valueOf(threadId)),
                                    SmsHelper.PROJECTION, null, null, "normalized_date ASC");
                    while (messageCursor.moveToNext()){
                        int bodyColumn = messageCursor.getColumnIndexOrThrow(Telephony.Sms.BODY);
                        int addressColumn = messageCursor.getColumnIndexOrThrow(  Telephony.Sms.ADDRESS);
                        String body = messageCursor.getString(bodyColumn);
                        String address = messageCursor.getString(addressColumn);
                        PriorProbability.instance.isHarmMessage(body, isHarmMessage -> {
                            if (isHarmMessage) {
                                BlockedConversationHelper.blockConversation(mPrefs, address);
                            }
                        });
                    }
                    messageCursor.close();
                }
                cursor.close();
                AndroidSchedulers.mainThread().scheduleDirect(this::hideProgressBar);
            });
        });
        mDefaultSmsView.setClickListener(v -> {
            String currentPn = getPackageName();//获取当前程序包名
            String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);//获取手机当前设置的默认短信应用的包名
            if (!defaultSmsApp.equals(currentPn)) {
                Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, currentPn);
                startActivity(intent);
            }
        });
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
