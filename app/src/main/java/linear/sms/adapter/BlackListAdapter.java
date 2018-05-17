package linear.sms.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import linear.sms.util.FileUtil;

/**
 * Created by ZCYL on 2018/5/17.
 */
public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.MyViewHolder> {

    List<String> mBlackContact;
    OnBlackLongClickListener mLongClickListener;

    public BlackListAdapter() {
        mBlackContact = FileUtil.readBlackContact();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,null);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTextView.setText(mBlackContact.get(position));
        holder.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mLongClickListener != null){
                    mLongClickListener.onLongClick(position);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBlackContact.size();
    }

    public void setOnLongClickListener(OnBlackLongClickListener listener){
        mLongClickListener = listener;

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(android.R.id.text1);
        }
    }

    public interface OnBlackLongClickListener{
        void onLongClick(int position);
    }
}
