package com.usr.usrsimplebleassistent.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService;
import com.usr.usrsimplebleassistent.MainActivity;
import com.usr.usrsimplebleassistent.MyApplication;
import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.SPPBlueTooth.SppBlueThoothActivity;
import com.usr.usrsimplebleassistent.ServicesActivity;
import com.usr.usrsimplebleassistent.Utils.AnimateUtils;
import com.usr.usrsimplebleassistent.Utils.GattAttributes;
import com.usr.usrsimplebleassistent.Utils.Utils;
import com.usr.usrsimplebleassistent.adapter.DevicesAdapter;
import com.usr.usrsimplebleassistent.bean.MDevice;
import com.usr.usrsimplebleassistent.bean.MService;
import com.usr.usrsimplebleassistent.views.RevealBackgroundView;
import com.usr.usrsimplebleassistent.views.RevealSearchView;

import java.util.ArrayList;
import java.util.List;

import materialdialog.MaterialDialog;


public class BleFragment extends Fragment {
    private Context mContext;
    private ActivityManager mActivityManager;
    private PackageManager mPackageManager;
    private OnRunningAppRefreshListener onRunningAppRefreshListener;
    private View rootView;
    private RecyclerView recyclerView;
    private String currentDevAddress;
    private String currentDevName;
    private DevicesAdapter adapter;

    private final List<MDevice> list = new ArrayList<>();
    private static BluetoothAdapter mBluetoothAdapter;
    private Handler hander;
    boolean isShowingDialog = false;
    private MaterialDialog alarmDialog;
    private MaterialDialog progressDialog;
    private RevealSearchView revealSearchView;
    private RevealBackgroundView revealBackgroundView;
    private int[] fabStartPosition;
    private TextView tvSearchDeviceCount;
    private RelativeLayout rlSearchInfo;
    private boolean scaning;
    private FloatingActionButton fabSearch;
    private FloatingActionButton searchDevice;
    private Button stopSearching;
    private String mode;
    private Runnable dismssDialogRunnable = new Runnable() {
        @Override
        public void run() {
            if (progressDialog != null)
                progressDialog.dismiss();
            disconnectDevice();
        }
    };
    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothAdapter != null)
                mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    };

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onRunningAppRefreshListener = (OnRunningAppRefreshListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRunningAppRefreshListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        mPackageManager = (PackageManager) mContext.getPackageManager();
        rootView = inflater.inflate(
                R.layout.ble_fragment, container, false);
        hander = new Handler();
       // initView();
        //initShow();
    /*    searchDevice = (FloatingActionButton) getActivity().findViewById(R.id.fab_search);
        searchDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("mode", MainActivity.MODE_PRIVATE);
                mode = mSharedPreferences.getString("mode", "");
                if (mode.equals("BLE")) {
                    scaning = true;
                    //如果有连接先关闭连接
                    disconnectDevice();
                    searchAnimate();

                } else {
                }
            }
        });*/

      /*  stopSearching = (Button) getActivity().findViewById(R.id.btn_stop_searching);
        stopSearching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scaning = false;
                stopScan();
            }
        });*/

       /* revealSearchView.setOnStateChangeListener(new RevealSearchView.OnStateChangeListener() {
            @Override
            public void onStateChange(int state) {
                if (state == RevealSearchView.STATE_FINISHED) {
                    revealSearchView.setVisibility(View.GONE);
                    revealBackgroundView.endFromEdge();
                }
            }
        });

        revealBackgroundView.setOnStateChangeListener(new RevealBackgroundView.OnStateChangeListener() {
            @Override
            public void onStateChange(int state) {

                if (state == RevealBackgroundView.STATE_FINISHED) {
                    revealSearchView.setVisibility(View.VISIBLE);
                    revealSearchView.startFromLocation(fabStartPosition);

                    tvSearchDeviceCount.setText(getString(R.string.search_device_count, 0));
                    rlSearchInfo.setVisibility(View.VISIBLE);
                    rlSearchInfo.setTranslationY(Utils.dpToPx(70));
                    rlSearchInfo.setAlpha(0);
                    AnimateUtils.translationY(rlSearchInfo, 0, 300, 0);
                    AnimateUtils.alpha(rlSearchInfo, 1.0f, 300, 0);

                    onRefresh();
                }

                if (state == RevealBackgroundView.STATE_END_FINISHED) {
                    revealBackgroundView.setVisibility(View.GONE);
                    rlSearchInfo.setVisibility(View.GONE);
                    scaning = false;
                    adapter.notifyDataSetChanged();
                }
            }
        });
*/

        return rootView;
    }

    /*private void searchAnimate() {
        revealBackgroundView.setVisibility(View.VISIBLE);

        int[] position1 = new int[2];
        fabSearch.getLocationOnScreen(position1);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fabStartPosition = new int[]{(position1[0] + fabSearch.getWidth() / 2),
                    (position1[1] + fabSearch.getHeight() / 4)};
        } else {
            fabStartPosition = new int[]{(position1[0] + fabSearch.getWidth() / 2),
                    position1[1]};
        }

        revealBackgroundView.startFromLocation(fabStartPosition);
    }*/


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("-------------->onRefresh");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onRefresh() {
        System.out.println("-------------->onRefresh");
        // Prepare list view and initiate scanning
        if (adapter != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        startScan();

    }

    private void startScan() {
        scanPrevious21Version();
    }

    /**
     * 版本号21之前的调用该方法搜索
     */
    private void scanPrevious21Version() {
        //10秒后停止扫描
        //hander.postDelayed(stopScanRunnable,10000);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    /**
     * Call back for BLE Scan
     * This call back is called when a BLE device is found near by.
     * 发现设备时回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MDevice mDev = new MDevice(device, rssi);
                    if (list.contains(mDev))
                        return;
                    list.add(mDev);
                    tvSearchDeviceCount.setText(getString(R.string.search_device_count, list.size()));
                }
            });
        }
    };


    private void stopScan() {
        revealSearchView.setVisibility(View.GONE);
        //停止雷达动画
        revealSearchView.stopAnimate();
        //涟漪动画回缩
        revealBackgroundView.endFromEdge();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        hander.removeCallbacks(stopScanRunnable);
    }

    private void initView() {
       // recyclerView = (RecyclerView) rootView.findViewById(R.id.recycleview);
        revealSearchView = (RevealSearchView) getActivity().findViewById(R.id.realsearchiew);
        revealBackgroundView = (RevealBackgroundView) getActivity().findViewById(R.id.reveal_background_view);
        tvSearchDeviceCount = (TextView) getActivity().findViewById(R.id.tv_search_device_count);
        rlSearchInfo = (RelativeLayout) getActivity().findViewById(R.id.rl_search_info);
        fabSearch = (FloatingActionButton) getActivity().findViewById(R.id.fab_search);
    }

    private void initShow() {
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(llm);

        adapter = new DevicesAdapter(list, mContext);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                adapter.setDelayStartAnimation(false);
                return false;
            }
            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
/*
        adapter.setOnItemClickListener(new DevicesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                if (!scaning) {
                    isShowingDialog = true;
                    showProgressDialog();
                    hander.postDelayed(dismssDialogRunnable, 20000);
                    connectDevice(list.get(position).getDevice());
                }
            }
        });*/


        checkBleSupportAndInitialize();

        /*//注册广播接收者，接收消息
        mContext.registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());

        Intent gattServiceIntent = new Intent(mContext.getApplicationContext(),
                BluetoothLeService.class);
        mContext.startService(gattServiceIntent);*/


    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("-------------->onActivityCreated");

    }

    public void onStart() {
        super.onStart();
        System.out.println("RunningAppFragment-------------->onActivityCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("RunningAppFragment-------------->onResume");
        //如果有连接先关闭连接
        disconnectDevice();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("RunningAppFragment-------------->onPause");
    }

    @Override
    public void onStop() {
        System.out.println("RunningAppFragment-------------->onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("RunningAppFragment-------------->onDestroyView");
    }

    @Override
    public void onDestroy() {
        System.out.println("-------------->onDestroyView");
        super.onDestroy();
        //getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onDetach() {
        System.out.println("-------------->onDestroyView");
        super.onDetach();
    }

    private void disconnectDevice() {
        isShowingDialog = false;
        BluetoothLeService.disconnect();
    }

    private void showProgressDialog() {
        progressDialog = new MaterialDialog(getActivity());
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.progressbar_item,
                        null);
        progressDialog.setView(view).show();
    }

    private void connectDevice(BluetoothDevice device) {
        currentDevAddress = device.getAddress();
        currentDevName = device.getName();
        //如果是连接状态，断开，重新连接
        if (BluetoothLeService.getConnectionState() != BluetoothLeService.STATE_DISCONNECTED)
            BluetoothLeService.disconnect();

        BluetoothLeService.connect(currentDevAddress, currentDevName, getActivity());
    }

    /**
     * 获得蓝牙适配器
     */
    private void checkBleSupportAndInitialize() {
        // Use this check to determine whether BLE is supported on the device.
        if (!mContext.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(mContext, R.string.device_ble_not_supported,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // Initializes a Blue tooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Blue tooth
            Toast.makeText(mContext,
                    R.string.device_ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }


 /*   *//**
     * BroadcastReceiver for receiving the GATT communication status
     *//*
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            // Status received when connected to GATT Server
            //连接成功
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                System.out.println("--------------------->连接成功");
                //搜索服务
                BluetoothLeService.discoverServices();
            }
            // Services Discovered from GATT Server
            else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                hander.removeCallbacks(dismssDialogRunnable);
                progressDialog.dismiss();
                prepareGattServices(BluetoothLeService.getSupportedGattServices());
            } else if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                progressDialog.dismiss();
                //connect break (连接断开)
                showDialog(getString(R.string.conn_disconnected_home));
            }

        }
    };

    private void showDialog(String info) {
        if (!isShowingDialog)
            return;
        if (alarmDialog != null)
            return;
        alarmDialog = new MaterialDialog(mContext);
        alarmDialog.setTitle(getString(R.string.alert))
                .setMessage(info)
                .setPositiveButton(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alarmDialog.dismiss();
                        alarmDialog = null;
                    }
                });
        alarmDialog.show();
    }

    *//**
     * Getting the GATT Services
     * 获得服务
     *
     * @param gattServices
     *//*
    private void prepareGattServices(List<BluetoothGattService> gattServices) {
        prepareData(gattServices);

        Intent intent = new Intent(mContext, ServicesActivity.class);
        intent.putExtra("dev_name", currentDevName);
        intent.putExtra("dev_mac", currentDevAddress);
        startActivity(intent);
        getActivity().overridePendingTransition(0, 0);
    }

    *//**
     * Prepare GATTServices data.
     *
     * @param
     *//*
    private void prepareData(List<BluetoothGattService> gattServices) {

        if (gattServices == null)
            return;

        List<MService> list = new ArrayList<>();

        for (BluetoothGattService gattService : gattServices) {
            String uuid = gattService.getUuid().toString();
            if (uuid.equals(GattAttributes.GENERIC_ACCESS_SERVICE) || uuid.equals(GattAttributes.GENERIC_ATTRIBUTE_SERVICE))
                continue;
            String name = GattAttributes.lookup(gattService.getUuid().toString(), "UnkonwService");
            MService mService = new MService(name, gattService);
            list.add(mService);
        }

        ((MyApplication) getActivity().getApplication()).setServices(list);
    }
*/



    public interface OnRunningAppRefreshListener {
        public void onRunningAppRefreshed();
    }


}
