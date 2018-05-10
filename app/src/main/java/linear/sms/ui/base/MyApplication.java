package linear.sms.ui.base;

import android.app.Application;

import linear.sms.bean.Conversation;

/**
 * Created by ZCYL on 2018/4/11.
 */
public class MyApplication extends Application {

    public static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Conversation.init(this);
    }
}
