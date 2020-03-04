package com.usr.usrsimplebleassistent.fragments;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;

import android.os.Bundle;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.usr.usrsimplebleassistent.BlueToothLeService.SppConnectService;
import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.SPPBlueTooth.SppBlueThoothActivity;
import com.usr.usrsimplebleassistent.adapter.DevicesAdapter;
import com.usr.usrsimplebleassistent.bean.MDevice;

import java.util.ArrayList;
import java.util.List;

import materialdialog.MaterialDialog;

public class SppFragment extends Fragment {
    private DevicesAdapter adapter;
    private final List<MDevice> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private View rootView;
    private Handler hander;
    private Bundle bundle = new Bundle();
    /***************************************************************************************************************/
    // 返回时数据标签
    public static String EXTRA_DEVICE_ADDRESS = "设备地址";
    private final static String CONNECTSUCCEED = "CONNECTSUCCEED";   //SPP服务UUID号
    private final static String CONNECTDEFEATED = "CONNECTDEFEATED";   //SPP服务UUID号
    private MaterialDialog progressDialog;
    private Runnable dismssDialogRunnable = new Runnable() {
        @Override
        public void run() {
            if (progressDialog != null)
                progressDialog.dismiss();
        }
    };

    // 成员域
    private BluetoothAdapter mBtAdapter;
    MDevice mDevice = new MDevice();
    private Context mContext;
    ConnnectBroadcastReceiver connnectBroadcastReceiver;

    //onCreateView
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(
                R.layout.spp_fragment, container, false);
        mContext = getActivity();
        hander = new Handler();
        initShow();//初始化试图
        initEvent();//初始化事件
        return rootView;
    }

    private void initShow() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleviewspp);
        //给recyclerView设置样式
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        //获取DevicesAdapter
        adapter = new DevicesAdapter(list, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });


        // 注册接收查找到设备action接收器
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter);

        // 注册查找结束action接收器
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, filter);
    }

    private void initEvent() {
        initBroadcastReceiver();
        adapter.setOnItemClickListener(new DevicesAdapter.OnItemClickListener() {
            public void onItemClick(View itemView, int position) {
                //显示动画
                showProgressDialog();
                // 准备连接设备，关闭服务查找
                mBtAdapter.cancelDiscovery();
                // 得到mac地址
                String address1 = list.get(position).getDevice().getAddress();
                System.out.println(address1 + "_______________________mac地址");
                //得到数量
                int itemCount = adapter.getItemCount();
                System.out.println(itemCount + "_______________________itemCount");
                //两秒后关闭连接动画
                //hander.postDelayed(dismssDialogRunnable, 2000);
                Intent intent1 = new Intent(getActivity(), SppConnectService.class);
                //启动链接服务
                intent1.putExtra(EXTRA_DEVICE_ADDRESS, address1);
                getActivity().startService(intent1);
            }
        });
    }

    private void initBroadcastReceiver() {
         connnectBroadcastReceiver = new ConnnectBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTSUCCEED);
        intentFilter.addAction(CONNECTDEFEATED);
        getActivity().registerReceiver(connnectBroadcastReceiver, intentFilter);
    }


    /**
     * 进度动画
     */
    private void showProgressDialog() {
        progressDialog = new MaterialDialog(getActivity());
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.progressbar_item,
                        null);
        progressDialog.setView(view).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        System.out.println("RunningServiceFragment-------------->onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RunningServiceFragment-------------->onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        System.out.println("RunningServiceFragment-------------->onStop");
        getActivity().unregisterReceiver(connnectBroadcastReceiver);
        getActivity().unregisterReceiver(mReceiver);

        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //停止扫描
        mBtAdapter.cancelDiscovery();
    }

    public class ConnnectBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()){
                case CONNECTSUCCEED:
                    dismssDialogRunnable.run();
                    Intent intent3 = new Intent(getActivity(), SppBlueThoothActivity.class);
                    //启动链接服务
                    getActivity().startActivity(intent3);
                    break;
                case CONNECTDEFEATED:
                    dismssDialogRunnable.run();
                    Toast.makeText(getActivity(),"连接到蓝牙设备失败",Toast.LENGTH_SHORT).show();
                    System.out.println("收到链接失败广播");
                    break;
            }
        }

    }
    // 查找到设备和搜索完成action监听器
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 查找到设备action
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 得到蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //得到信号强度
                int rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                //获取设备类
                MDevice mDev = new MDevice(device, rssi);
                mDevice.setDevice(device);
                mDevice.setRssi(rssi);
                if (list.contains(mDev))
                    return;
                list.add(mDev);
                //获取是否绑定 暂时没用到
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_NONE:
                        //新方法
                      /*  mDevice.setDevice(device);
                        mDevice.setRssi(rssi);
                        if (list.contains(mDev))
                            return;
                        list.add(mDev);*/
                        //老list
                        //iBeanList.add(new SppDeviceListViewItem(R.mipmap.bluetooth_icon_big, mDevice.getDevice().getName(), mDevice.getDevice().getAddress(), mDevice.getRssi()));
                        //iListView.setAdapter(new SPP_DevicesListViewAdapter(getActivity(), iBeanList));
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        //添加到已配对设备列表
                        /*mDevice.setDevice(device);
                        mDevice.setRssi(rssi);*/
                        //iBeanList1.add(new SppDeviceListViewItem(R.mipmap.bluetooth_icon_big, mDevice.getDevice().getName(), mDevice.getDevice().getAddress(), mDevice.getRssi()));
                        //iListView1.setAdapter(new SPP_DevicesListViewAdapter(getActivity(), iBeanList1));
                        break;
                }
                adapter.notifyDataSetChanged();
                TextView tvSearchDeviceCount = (TextView) getActivity().findViewById(R.id.tv_search_device_count);
                tvSearchDeviceCount.setText(getString(R.string.search_device_count, list.size()));
                // 搜索完成action
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                getActivity().setProgressBarIndeterminateVisibility(false);
                getActivity().setTitle("选择要连接的设备");
                //int count = new Internet_ListViewAdapter().getCount();
                   /* if (count==0) {
                        System.out.println(count+"--------->count");*/
//                    String noDevices = "没有找到新设备";
//                    Toast.makeText(DeviceListActivity.this,noDevices,Toast.LENGTH_SHORT).show();

            }
        }
    };

}