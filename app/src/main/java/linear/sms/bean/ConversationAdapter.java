package linear.sms.bean;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import linear.sms.R;
import linear.sms.ui.base.BaseActivity;
import linear.sms.util.DateFormatter;

/**
 * Created by ZCYL on 2018/5/8.
 */
public class ConversationAdapter extends RecyclerCursorAdapter<ConversationViewHolder, Conversation> {

    private Conversation[] mConversationArray;

    public ConversationAdapter(BaseActivity context) {
        super(context);
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_conversation, null);

        ConversationViewHolder holder = new ConversationViewHolder(mContext, view);
        holder.mutedView.setImageResource(R.drawable.ic_notifications_muted);
        holder.unreadView.setImageResource(R.drawable.ic_unread_indicator);
        holder.errorIndicator.setImageResource(R.drawable.ic_error);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        final Conversation conversation = getItem(position);

        holder.mData = conversation;
        holder.mContext = mContext;
        holder.mClickListener = mItemClickListener;
        holder.root.setOnClickListener(holder);
        holder.root.setOnLongClickListener(holder);

//        holder.mutedView.setVisibility(new ConversationPrefsHelper(mContext, conversation.getThreadId())
//                .getNotificationsEnabled() ? View.GONE : View.VISIBLE);
        holder.mutedView.setVisibility(View.VISIBLE);

        holder.errorIndicator.setVisibility(conversation.hasError() ? View.VISIBLE : View.GONE);

        final boolean hasUnreadMessages = conversation.hasUnreadMessages();
        if (hasUnreadMessages) {
            holder.unreadView.setVisibility(View.VISIBLE);
//            holder.snippetView.setTextColor(ThemeManager.getTextOnBackgroundPrimary());
//            holder.dateView.setTextColor(ThemeManager.getColor());
//            holder.fromView.setType(FontManager.TEXT_TYPE_PRIMARY_BOLD);
            holder.snippetView.setMaxLines(5);
        } else {
            holder.unreadView.setVisibility(View.GONE);
//            holder.snippetView.setTextColor(ThemeManager.getTextOnBackgroundSecondary());
//            holder.dateView.setTextColor(ThemeManager.getTextOnBackgroundSecondary());
//            holder.fromView.setType(FontManager.TEXT_TYPE_PRIMARY);
            holder.snippetView.setMaxLines(1);
        }

//        LiveViewManager.registerView(QKPreference.THEME, this, key -> {
//            holder.dateView.setTextColor(hasUnreadMessages ? ThemeManager.getColor() : ThemeManager.getTextOnBackgroundSecondary());
//        });

        holder.mSelected.setVisibility(View.GONE);
//        LiveViewManager.registerView(QKPreference.HIDE_AVATAR_CONVERSATIONS, this, key ->
//                holder.mAvatarView.setVisibility(mContext.getBoolean(QKPreference.HIDE_AVATAR_CONVERSATIONS) ? View.GONE : View.VISIBLE));

        // Date
        holder.dateView.setText(DateFormatter.getConversationTimestamp(mContext, conversation.getDate()));

        holder.mAvatarView.setVisibility(View.VISIBLE);
        // Subject
        String emojiSnippet = conversation.getSnippet();
//        if (mPrefs.getBoolean(SettingsFragment.AUTO_EMOJI, false)) {
//            emojiSnippet = EmojiRegistry.parseEmojis(emojiSnippet);
//        }
        holder.snippetView.setText(emojiSnippet);

        holder.fromView.setText(conversation.getContactName());

//        Contact.addListener(holder);

        // Update the avatar and name
//        holder.onUpdate(conversation.getRecipients().size() == 1 ? conversation.getRecipients().get(0) : null);
    }

    protected Conversation getItem(int position) {
        Conversation conversation = null;
        if (mConversationArray != null) {
            conversation = mConversationArray[position];
        }
        if (conversation == null) {
            mCursor.moveToPosition(position);
            conversation = Conversation.from(mContext, mCursor);
            mConversationArray[position] = conversation;
        }
        return conversation;
    }

    @Override
    protected void onCursorChange(Cursor cursor) {
        mConversationArray = new Conversation[cursor.getCount()];
    }
}
