package linear.sms.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import java.util.Objects;

import linear.sms.bayes.PriorProbability;

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
