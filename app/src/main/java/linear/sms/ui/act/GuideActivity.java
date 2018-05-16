package linear.sms.ui.act;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import linear.sms.R;
import linear.sms.bean.Conversation;
import linear.sms.ui.base.BaseActivity;
import linear.sms.util.PermissionUtils;

/**
 * Created by ZCYL on 2018/5/9.
 */
public class GuideActivity extends BaseActivity {

    //第一次打开应用需要申请的权限
    private final String[] PERMISSIONS = new String[]
            {Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS};

    // 多个权限请求Code
    private final int REQUEST_CODE_PERMISSIONS = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_guide);
        PermissionUtils.checkMorePermissions(mContext, PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
            @Override
            public void onHasPermission() {
                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                    @Override
                    public void run() {
                        goToMain();
                    }
                });
            }

            @Override
            public void onUserHasAlreadyTurnedDown(String... permission) {
                showExplainDialog(permission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtils.requestMorePermissions(mContext, PERMISSIONS, REQUEST_CODE_PERMISSIONS);
                    }
                });
            }

            @Override
            public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                PermissionUtils.requestMorePermissions(mContext, PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSIONS:
                PermissionUtils.onRequestMorePermissionsResult(mContext, PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {
                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                            @Override
                            public void run() {
                              goToMain();
                            }
                        });
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                        Toast.makeText(mContext, "我们需要" + Arrays.toString(permission) + "权限", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        Toast.makeText(mContext, "我们需要" + Arrays.toString(permission) + "权限", Toast.LENGTH_SHORT).show();
                        showToAppSettingDialog();
                    }
                });


        }
    }

    /**
     * 显示前往应用设置Dialog
     */
    private void showToAppSettingDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle("需要权限")
                .setMessage("我们需要相关权限，才能实现功能，点击前往，将转到应用的设置界面，请开启应用的相关权限。")
                .setPositiveButton("前往", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtils.toAppSetting(mContext);
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    /**
     * 解释权限的dialog
     */
    private void showExplainDialog(String[] permission, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(mContext)
                .setTitle("申请权限")
                .setMessage("我们需要" + Arrays.toString(permission) + "权限")
                .setPositiveButton("确定", onClickListener)
                .show();
    }

    private void goToMain(){
        GuideActivity.this.startActivity(new Intent(GuideActivity.this, MainActivity.class));
        GuideActivity.this.finish();
        Conversation.init(this);

    }
}
