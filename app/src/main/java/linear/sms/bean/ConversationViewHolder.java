package linear.sms.bean;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import linear.sms.R;
import linear.sms.ui.base.BaseActivity;
import linear.sms.widget.AvatarView;
import linear.sms.widget.NormalTextView;

/**
 * Created by ZCYL on 2018/5/8.
 */
public class ConversationViewHolder extends ClickyViewHolder<Conversation>  {
    private final SharedPreferences mPrefs;

    protected View root;
    protected NormalTextView snippetView;
    protected NormalTextView fromView;
    protected NormalTextView dateView;
    protected ImageView mutedView;
    protected ImageView unreadView;
    protected ImageView errorIndicator;
    protected AvatarView mAvatarView;
    protected ImageView mSelected;
    public ConversationViewHolder(BaseActivity baseActivity,View itemView) {
        super(baseActivity,itemView);

        mPrefs = ((BaseActivity)itemView.getContext()).getPrefs();

        root = itemView;
        fromView = itemView.findViewById(R.id.conversation_list_name);
        snippetView = itemView.findViewById(R.id.conversation_list_snippet);
        dateView =  itemView.findViewById(R.id.conversation_list_date);
        mutedView = itemView.findViewById(R.id.conversation_list_muted);
        unreadView = itemView.findViewById(R.id.conversation_list_unread);
        errorIndicator = itemView.findViewById(R.id.conversation_list_error);
        mAvatarView = itemView.findViewById(R.id.conversation_list_avatar);
        mSelected = itemView.findViewById(R.id.selected);
    }


}
