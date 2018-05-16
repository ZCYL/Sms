package linear.sms.bean;

import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import linear.sms.R;
import linear.sms.ui.base.BaseActivity;
import linear.sms.widget.AvatarView;
import linear.sms.widget.NormalTextView;

public class MessageListViewHolder extends ClickyViewHolder<MessageItem> {
    private final String TAG = "MessageListViewHolder";

    // Views
    protected View mRoot;
    protected NormalTextView mBodyTextView;
    protected NormalTextView mDateView;
    protected ImageView mLockedIndicator;
    protected ImageView mDeliveredIndicator;
    protected ImageView mDetailsIndicator;
    protected AvatarView mAvatarView;
    protected LinearLayout mMessageBlock;
    protected View mSpace;
    protected FrameLayout mMmsView;
    protected ImageView mImageView;
    protected ImageButton mSlideShowButton;
    protected Button mDownloadButton;
    protected NormalTextView mDownloadingLabel;

//    protected ImageLoadedCallback mImageLoadedCallback;

    public MessageListViewHolder(BaseActivity context, View view) {
        super(context, view);

        mRoot = view;
        mBodyTextView =  view.findViewById(R.id.text_view);
        mDateView =view.findViewById(R.id.date_view);
        mLockedIndicator = view.findViewById(R.id.locked_indicator);
        mDeliveredIndicator = view.findViewById(R.id.delivered_indicator);
        mDetailsIndicator = view.findViewById(R.id.details_indicator);
        mAvatarView = view.findViewById(R.id.avatar);
        mMessageBlock = view.findViewById(R.id.message_block);
        mSpace = view.findViewById(R.id.space);
    }

}
