package linear.sms.ui.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by ZCYL on 2018/4/11.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected SharedPreferences mPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mPrefs = MyApplication.instance.getSharedPreferences();
    }

    public SharedPreferences getPrefs() {
        return MyApplication.instance.getSharedPreferences();
    }

    public void showToast(String string){
        Toast.makeText(this,string,Toast.LENGTH_SHORT).show();
    }


}
