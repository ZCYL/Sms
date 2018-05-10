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
        mMmsView = view.findViewById(R.id.mms_view);
        mImageView = view.findViewById(R.id.image_view);
        mSlideShowButton = view.findViewById(R.id.play_slideshow_button);
    }

    protected void showMmsView(boolean visible) {
        mMmsView.setVisibility(visible ? View.VISIBLE : View.GONE);
        mImageView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    protected void inflateDownloadControls() {
        if (mDownloadButton == null) {
            mRoot.findViewById(R.id.mms_downloading_view_stub).setVisibility(View.VISIBLE);
            mDownloadButton = mRoot.findViewById(R.id.btn_download_msg);
            mDownloadingLabel =  mRoot.findViewById(R.id.label_downloading);
        }
    }
//
//    protected void setLiveViewCallback(LiveView liveViewCallback) {
//        LiveViewManager.registerView(QKPreference.THEME, this, liveViewCallback);
//    }

//    @Override
//    public void setImage(String name, Bitmap bitmap) {
//        if (bitmap == null) {
//            showMmsView(false);
//        } else {
//            showMmsView(true);
//
//            try {
//                mImageView.setImageBitmap(bitmap);
//                mImageView.setVisibility(View.VISIBLE);
//            } catch (OutOfMemoryError e) {
//                Log.e(TAG, "setImage: out of memory: ", e);
//            }
//        }
//    }

//
//    @Override
//    public void setVideoThumbnail(String name, Bitmap bitmap) {
//        showMmsView(true);
//
//        try {
//            mImageView.setImageBitmap(bitmap);
//            mImageView.setVisibility(View.VISIBLE);
//        } catch (OutOfMemoryError e) {
//            Log.e(TAG, "setVideo: out of memory: ", e);
//        }
//    }
//
//    static protected class ImageLoadedCallback implements ItemLoadedCallback<ThumbnailManager.ImageLoaded> {
//        private long mMessageId;
//        private final MessageListViewHolder mListItem;
//
//        public ImageLoadedCallback(MessageListViewHolder listItem) {
//            mListItem = listItem;
//            mListItem.setImage(null, null);
//            mMessageId = listItem.mData.getMessageId();
//        }
//
//        public void reset(MessageListViewHolder listItem) {
//            mMessageId = listItem.mData.getMessageId();
//        }
//
//        public void onItemLoaded(ThumbnailManager.ImageLoaded imageLoaded, Throwable exception) {
//            // Make sure we're still pointing to the same message. The list item could have // been recycled.
//            MessageItem msgItem = mListItem.mData;
//            if (msgItem != null && msgItem.getMessageId() == mMessageId) {
//                if (imageLoaded.mIsVideo) {
//                    mListItem.setVideoThumbnail(null, imageLoaded.mBitmap);
//                } else {
//                    mListItem.setImage(null, imageLoaded.mBitmap);
//                }
//            }
//        }
//    }
}
