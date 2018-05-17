package linear.sms.adapter;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import linear.sms.R;
import linear.sms.bean.Conversation;
import linear.sms.ui.base.BaseActivity;
import linear.sms.util.DateFormatter;

/**
 * Created by ZCYL on 2018/5/8.
 */
public class ConversationAdapter extends RecyclerCursorAdapter<ConversationViewHolder, Conversation> {

    private Conversation[] mConversationArray;
    private boolean isHarm;
    private static Conversation[] sSpamConversationArray;//用于存放垃圾短信
    private int itemCount;

    public ConversationAdapter(BaseActivity context) {
        this(context,false);
    }

    public ConversationAdapter(BaseActivity context, boolean isHarm) {
        super(context);
        this.isHarm = isHarm;
        sSpamConversationArray = new Conversation[0];
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
        holder.mutedView.setVisibility(View.GONE);
        holder.errorIndicator.setVisibility(View.GONE);

        final boolean hasUnreadMessages = conversation.hasUnreadMessages();
        if (hasUnreadMessages) {
            holder.unreadView.setVisibility(View.VISIBLE);
            holder.snippetView.setMaxLines(5);
        } else {
            holder.unreadView.setVisibility(View.GONE);

            holder.snippetView.setMaxLines(1);
        }

        holder.mSelected.setVisibility(View.GONE);
        holder.dateView.setText(DateFormatter.getConversationTimestamp(mContext, conversation.getDate()));
        holder.mAvatarView.setVisibility(View.VISIBLE);
        String emojiSnippet = conversation.getSnippet();
        holder.snippetView.setText(emojiSnippet);
        holder.fromView.setText(conversation.getContactName());
    }

    protected Conversation getItem(int position) {
        if (isHarm){
            return sSpamConversationArray[position];
        }
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
        if (isHarm){
            itemCount = sSpamConversationArray.length;
        } else {
            itemCount = cursor.getCount() - sSpamConversationArray.length;
        }
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }
}
