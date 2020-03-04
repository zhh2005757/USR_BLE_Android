package com.usr.usrsimplebleassistent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.bean.MDevice;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2015-11-13.
 */
public class DevicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private List<MDevice> list;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private int lastPosition=-1;
    private boolean isDelayStartAnimation = true;
    public DevicesAdapter(List<MDevice> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_device, parent, false);
        CellViewHolder cellViewHolder = new CellViewHolder(view);
        cellViewHolder.itemView.setOnClickListener(this);
        return cellViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CellViewHolder cellViewHolder = (CellViewHolder) holder;
        cellViewHolder.itemView.setTag(position);
        MDevice mDev = list.get(position);
        cellViewHolder.tvDevName.setText(mDev.getDevice().getName());
        cellViewHolder.tvDevSignal.setText(String.valueOf(mDev.getRssi())+"dBm");
        cellViewHolder.tvDevMac.setText(mDev.getDevice().getAddress());
        if (position> lastPosition){
            lastPosition = position;
            animatroItem(cellViewHolder,position);
        }
    }


    private void animatroItem(CellViewHolder holder,int position){
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom);
        animation.setStartOffset(isDelayStartAnimation?position*150:0);
        holder.itemView.startAnimation(animation);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public void clear() {
        lastPosition = -1;
        isDelayStartAnimation = true;
        list.clear();
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, (Integer) v.getTag());
        }

    }



    public interface OnItemClickListener {
        public void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_device.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class CellViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_dev_name)
        TextView tvDevName;
        @BindView(R.id.tv_dev_signal)
        TextView tvDevSignal;
        @BindView(R.id.tv_dev_mac)
        TextView tvDevMac;

        public CellViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setDelayStartAnimation(boolean delayStartAnimation) {
        isDelayStartAnimation = delayStartAnimation;
    }
}
