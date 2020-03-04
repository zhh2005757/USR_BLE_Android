package com.usr.usrsimplebleassistent.adapter;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.Utils.GattAttributes;
import com.usr.usrsimplebleassistent.Utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015-11-18.
 */
public class CharacteristicsAdapter extends BaseAdapter {

    private final int TYPE_NORMAL = 0;
    private final int TYPE_VIRTUAL = 1;

    private Context context;
    private List<BluetoothGattCharacteristic> list;

    public CharacteristicsAdapter(Context context, List<BluetoothGattCharacteristic> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getUuid().toString().equals(GattAttributes.USR_SERVICE)){
            return TYPE_VIRTUAL;
        }else {
            return TYPE_NORMAL;
        }
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
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_characteristic, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        BluetoothGattCharacteristic characteristic = list.get(position);

        String name = GattAttributes.lookup(characteristic.getUuid().toString(), characteristic
                .getUuid().toString());

        int type = getItemViewType(position);
        if (type == TYPE_NORMAL){
            holder.tvCharacteristicName.setText(name);
            holder.tvCharacteristicProperties.setText(Utils.getPorperties(context,characteristic));
        }else {
            holder.tvCharacteristicName.setText("This is not a characteristic just for debug");
            holder.tvCharacteristicProperties.setText("USR Debug Mode ");
        }


        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_characteristic.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @BindView(R.id.tv_characteristic_name)
        TextView tvCharacteristicName;
        @BindView(R.id.tv_characteristic_properties)
        TextView tvCharacteristicProperties;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
