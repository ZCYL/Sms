package linear.sms.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import linear.sms.R;

/**
 * Created by ZCYL on 2018/4/11.
 */
public class NormalTextView extends AppCompatTextView {

    public static final int COLOR_PRIMARY = 0;
    public static final int COLOR_SECONDARY = 1;
    public static final int COLOR_TERTIARY = 2;
    public static final int COLOR_PRIMARY_ON_THEME = 3;
    public static final int COLOR_SECONDARY_ON_THEME = 4;
    public static final int COLOR_TERTIARY_ON_THEME = 5;


    public static final int SIZE_PRIMARY = 0;
    public static final int SIZE_SECONDARY = 1;
    public static final int SIZE_TERTIARY = 2;
    public static final int SIZE_TOOLBAR = 3;

    public NormalTextView(Context context) {
        super(context);
    }

    public NormalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NormalTextView);
        int colorAttr = typedArray.getInt(R.styleable.NormalTextView_textColor, -1);
        int textSizeAttr = typedArray.getInt(R.styleable.NormalTextView_textSize, -1);

        int color;
        switch (colorAttr) {
            case COLOR_PRIMARY:
                color = R.color.textPrimary;
                break;
            case COLOR_SECONDARY:
                color = R.color.textSecondary;
                break;
            case COLOR_TERTIARY:
                color = R.color.textTertiary;
                break;
            case COLOR_PRIMARY_ON_THEME:
                color = R.color.textPrimaryDark;
                break;
            case COLOR_SECONDARY_ON_THEME:
                color = R.color.textSecondaryDark;
                break;
            case COLOR_TERTIARY_ON_THEME:
                color = R.color.textTertiaryDark;
                break;
            default:
                color = R.color.textPrimary;
        }

        float size;
        switch (textSizeAttr) {
            case SIZE_PRIMARY:
                size = 16f;
                break;
            case SIZE_SECONDARY:
                size = 14f;
                break;
            case SIZE_TERTIARY:
                size = 12f;
                break;
            case SIZE_TOOLBAR:
                size = 20f;
                break;
            default:
                size = textSizeAttr;
        }

        setTextSize(size);
        setTextColor(color);
        typedArray.recycle();
    }
}
