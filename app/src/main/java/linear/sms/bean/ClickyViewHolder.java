package linear.sms.bean;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import linear.sms.ui.base.BaseActivity;

public abstract class ClickyViewHolder<DataType> extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public RecyclerCursorAdapter.ItemClickListener<DataType> mClickListener;
    public DataType mData;
    public BaseActivity mContext;

    public ClickyViewHolder(BaseActivity context, View itemView) {
        super(itemView);
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) {
            mClickListener.onItemClick(mData, v);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mClickListener != null) {
            mClickListener.onItemLongClick(mData, v);
        }

        return true;
    }
}
