package com.usr.usrsimplebleassistent.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.UpdateActivity;
import com.usr.usrsimplebleassistent.item.SppDeviceListViewItem;
import com.usr.usrsimplebleassistent.item.SppUpdateListViewItem;

import java.util.List;

/**
 * Created by shizhiyuan on 2017/5/24.
 */

public class SPP_UpdateListViewAdapter extends BaseAdapter {
    private List<SppUpdateListViewItem> ulist;
    //LayoutInflater
    private LayoutInflater mInflater;
    /**
     * 在构造方法中 初始化List
     * 通过Context 对象初始化LayoutInflater
     * @param context  :要使用当前的Adapter的界面对象
     *
     * @param list
     */
    public SPP_UpdateListViewAdapter(Context context, List<SppUpdateListViewItem> list) {
        ulist=list;
        mInflater= LayoutInflater.from(context);
    }

    /**
     *返回item的个数
     * @return  返回ListView 需要显示的数据量
     */
    public int getCount() {
        return ulist.size();
    }
    /**
     * 指定索引所对应的数据项
     * @param i  从ulist 中去除对应索引的数据项
     * @return
     */
    @Override
    public Object getItem(int i) {
        return ulist.get(i);
    }

    /**
     * 获取指定行的ID
     * @param i
     * @return
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * 获取每个Item对应的View
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        // 初始化item view
        if (convertView == null) {
            // 通过LayoutInflater将xml中定义的视图实例化到一个View中
            // View view1 = mInflater.inflate(R.layout.ListViewItems,null);
            convertView = mInflater.inflate(R.layout.spp_update_listviewitems, null);
            // 实例化一个封装类ListItemView，并实例化它的两个域
            viewHolder= new  ViewHolder();
            viewHolder.code=(TextView) convertView.findViewById(R.id.tv_versioncode);
            viewHolder.name=(TextView) convertView.findViewById(R.id.tv_versionname);
            viewHolder.url=(TextView) convertView.findViewById(R.id.tv_versionurl);
            // 将ListItemView对象传递给convertView
            convertView.setTag(viewHolder);
        } else {
            // 从converView中获取ListItemView对象
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 获取到ulist中指定索引位置的资源
        SppUpdateListViewItem bean = ulist.get(position);
        /* 将资源传递给ListItemView的两个域对象 */
        viewHolder.code.setText(bean.Spp_Version_code);
        viewHolder.name.setText(bean.Spp_Version_name);
        viewHolder.url.setText(bean.Spp_Version_url);
        // 返回convertView对象
        return convertView;
    }
    class ViewHolder{
        public   TextView code;
        public   TextView name;
        public   TextView url;

    }
}
