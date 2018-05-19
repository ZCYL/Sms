package linear.sms.ui.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by ZCYL on 2018/4/11.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected SharedPreferences mPrefs;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mPrefs = MyApplication.instance.getSharedPreferences();
    }

    public SharedPreferences getPrefs() {
        return MyApplication.instance.getSharedPreferences();
    }

    public void showToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }


    /**
     * 在屏幕上添加一个转动的ProgressBar，默认为隐藏状态
     */
    public void showProgressBar() {
        if (mProgressBar == null) {
            // 给progressbar准备一个FrameLayout的LayoutParams
            FrameLayout rootContainer = (FrameLayout) this.findViewById(android.R.id.content);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            mProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyle);
            mProgressBar.setVisibility(View.GONE);// 默认隐藏其显示
            mProgressBar.setLayoutParams(lp);
            // 将progressBar添加到FrameLayout中
            rootContainer.addView(mProgressBar);
        }
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar(){
        if (mProgressBar != null){
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
