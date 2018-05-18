package linear.sms.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.Objects;

import linear.sms.bayes.PriorProbability;
import linear.sms.ui.base.MyApplication;
import linear.sms.util.BlockedConversationHelper;
import linear.sms.util.SettingsPre;

/**
 * Created by ZCYL on 2018/5/10.
 */
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            Object pdus[] = (Object[]) bundle.get("pdus");
            if (pdus == null) return;
            for (Object pdu : pdus) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                String address = smsMessage.getOriginatingAddress();
                String content = smsMessage.getMessageBody();

                //先进行黑名单拦截
                for (String s : BlockedConversationHelper.getBlackListAddress(MyApplication.instance.getSharedPreferences())){
                    if (s.equals(address)){
                        //在黑名单列表中，直接拦截

                        return;
                    }
                }

                if (address.length() <= 5){
                    //5位数以下的号码不拦截，可能是银行之类的号码

                    return;
                }


                //用户未开启垃圾短信拦截
                if (!SettingsPre.isBlockEnable()){
                    return;
                }
                PriorProbability.instance.isHarmMessage(content, new PriorProbability.OnBayesAnalyseFinishListener() {
                    @Override
                    public void onFinish(Boolean isHarmMessage) {

                    }
                });
                abortBroadcast();
            }
        }
    }
}
