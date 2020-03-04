package com.usr.usrsimplebleassistent.adapter;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.bean.MService;

import java.util.List;

/**
 * Created by Administrator on 2015-11-17.
 */
public class ServicesAdapter extends BaseAdapter {
    private Context context;
    private List<MService> list;

    public ServicesAdapter(Context context,List<MService> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder ;

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_service,parent,false);
            holder = new ViewHolder();
            holder.tvName = (TextView)convertView.findViewById(R.id.tv_service_name);
            holder.tvUUID = (TextView)convertView.findViewById(R.id.tv_service_uuid);
            convertView.setTag(holder);
        }else
           holder = (ViewHolder)convertView.getTag();

        MService mService = list.get(position);
        BluetoothGattService service = mService.getService();
        holder.tvName.setText(mService.getName());
        holder.tvUUID.setText(service.getUuid().toString());

        System.out.println("serviceAdapter------------------>"+service.getUuid().toString());
        return convertView;
    }


    private static class ViewHolder{
        TextView tvName;
        TextView tvUUID;
    }

}
