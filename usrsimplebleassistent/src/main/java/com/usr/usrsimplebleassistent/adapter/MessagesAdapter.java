package com.usr.usrsimplebleassistent.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.bean.Message;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015-07-28.
 */
public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_LEFT = 0 ;
    public static final int TYPE_RIGHT = 1;

    private List<Message> list;
    private LayoutInflater inflater;
    private Context context;

    public MessagesAdapter(Context context, List<Message> list){
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getItemViewType(int position) {
        Message msg = list.get(position);
        if (msg.getType() == Message.MESSAGE_TYPE.RECEIVE)
            return TYPE_LEFT;
        else
           return  TYPE_RIGHT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_LEFT){
            View view = inflater.inflate(R.layout.layout_item_message_left,parent,false);
            MessageViewHolderLeft viewHolderLeft = new MessageViewHolderLeft(view);
            return viewHolderLeft;
        }else {
            View view = inflater.inflate(R.layout.layout_item_message_right,parent,false);
            MessageViewHolderRight viewHolderRight = new MessageViewHolderRight(view);
            return  viewHolderRight;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message msg = list.get(position);
        int type = getItemViewType(position);
        if (type == TYPE_LEFT){
            MessageViewHolderLeft holderLeft = (MessageViewHolderLeft) holder;
            holderLeft.tvMsgContent.setText(msg.getContent());
        }else {
            MessageViewHolderRight holderRight = (MessageViewHolderRight) holder;
            holderRight.tvMsgContent.setText(msg.getContent());
            if (msg.isDone())
                holderRight.tvMsgContent.setTextColor(Color.parseColor("#0277bd"));
            else
                holderRight.tvMsgContent.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void notifyLastItem(){
        notifyItemInserted(getItemCount()-1);
    }

    public static class MessageViewHolderLeft extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_msg_content)
        TextView tvMsgContent;

        public MessageViewHolderLeft(final View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class MessageViewHolderRight extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_msg_content)
        TextView tvMsgContent;

        public MessageViewHolderRight(final View view){
            super(view);
            ButterKnife.bind(this,view);
        }
    }
}
