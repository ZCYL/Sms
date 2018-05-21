package linear.sms.ui.base;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import linear.sms.bayes.PriorProbability;

/**
 * Created by ZCYL on 2018/4/11.
 */
public class MyApplication extends Application {

    public static MyApplication instance;
    private SharedPreferences mPrefs;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        PriorProbability.instance.init(this);
    }

    public SharedPreferences getSharedPreferences() {
        if (mPrefs == null) {
            mPrefs =  getSharedPreferences(getPackageName()+"_pre", Context.MODE_MULTI_PROCESS);
        }
        return mPrefs;
    }
}
