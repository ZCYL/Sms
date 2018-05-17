package linear.sms.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import linear.sms.R;

/**
 * Created by ZCYL on 2018/5/17.
 */
public class SwitchPreference extends PreferenceView {

    NormalSwitch mNormalSwitch;

    public SwitchPreference(Context context) {
        this(context, null);
    }

    public SwitchPreference(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ViewGroup v = findViewById(R.id.widgetFrame);
        View.inflate(context, R.layout.settings_switch_widget, v);
        mNormalSwitch = (NormalSwitch) v.getChildAt(0);
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener){
        setOnClickListener(v -> {
            mNormalSwitch.setChecked(!mNormalSwitch.isChecked());
            listener.onCheckedChanged(mNormalSwitch,mNormalSwitch.isChecked());
        });
    }

    public void setChecked(boolean checked){
        mNormalSwitch.setChecked(checked);
    }


}
