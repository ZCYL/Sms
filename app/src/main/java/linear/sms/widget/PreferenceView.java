package linear.sms.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import linear.sms.R;

/**
 * Created by ZCYL on 2018/4/14.
 */
public class PreferenceView extends LinearLayout {
    public PreferenceView(Context context) {
        this(context, null);
    }

    public PreferenceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(getContext()).inflate(R.layout.preference_view, this);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PreferenceView);

        String title = typedArray.getString(R.styleable.PreferenceView_title);
        String summary = typedArray.getString(R.styleable.PreferenceView_summary);

        TextView textTitle = findViewById(R.id.titleView);
        textTitle.setText(title);
        TextView textSummary = findViewById(R.id.summaryView);
        textSummary.setText(summary);

        int widgetId = typedArray.getResourceId(R.styleable.PreferenceView_widget, -1);
        if (widgetId != -1) {
            ViewGroup v = findViewById(R.id.widgetFrame);
            View.inflate(context, widgetId, v);
        }

        int iconId = typedArray.getResourceId(R.styleable.PreferenceView_icon, -1);
        if (iconId != -1) {
            ImageView i = findViewById(R.id.set_icon);
            i.setVisibility(VISIBLE);
            i.setImageResource(iconId);
        }
        typedArray.recycle();
    }

    public void setClickListener(View.OnClickListener listener) {
        setOnClickListener(listener);
    }

}
