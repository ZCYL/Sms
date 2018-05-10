package linear.sms.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import linear.sms.R;

/**
 * Created by ZCYL on 2018/4/11.
 */
public class SearchEditText extends AppCompatEditText {


    private static final int COLOR_PRIMARY = 0;
    private static final int COLOR_SECONDARY = 1;
    private static final int COLOR_TERTIARY = 2;
    private static final int COLOR_PRIMARY_ON_THEME = 3;
    private static final int COLOR_SECONDARY_ON_THEME = 4;
    private static final int COLOR_TERTIARY_ON_THEME = 5;

    private static final int SIZE_PRIMARY = 0;
    private static final int SIZE_SECONDARY = 1;
    private static final int SIZE_TERTIARY = 2;
    private static final int SIZE_TOOLBAR = 3;


    public SearchEditText(Context context) {
        this(context, null);
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchEditText);
        int colorAttr = typedArray.getInt(R.styleable.SearchEditText_textColor, -1);
        int colorHintAttr = typedArray.getInt(R.styleable.SearchEditText_textColorHint, -1);
        int textSizeAttr = typedArray.getInt(R.styleable.SearchEditText_textSize, -1);


//        switch (colorAttr) {
//            case COLOR_PRIMARY : colors.textPrimary;
//            case COLOR_SECONDARY : colors.textSecondary;
//            case COLOR_TERTIARY : colors.textTertiary;
//            case COLOR_PRIMARY_ON_THEME : colors.textPrimaryOnTheme;
//            case COLOR_SECONDARY_ON_THEME : colors.textSecondaryOnTheme;
//            case COLOR_TERTIARY_ON_THEME : colors.textTertiaryOnTheme;
//            default:
//        }
//
//        textColorHintObservable = when(colorHintAttr) {
//            COLOR_PRIMARY -> colors.textPrimary
//            COLOR_SECONDARY -> colors.textSecondary
//            COLOR_TERTIARY -> colors.textTertiary
//                    else ->null
//        }

        setTextColor(ContextCompat.getColor(getContext(),R.color.textPrimary));
        setHintTextColor(ContextCompat.getColor(getContext(),R.color.textTertiary));
//        setTextSize(textSizeAttr);

        typedArray.recycle();

    }
}
