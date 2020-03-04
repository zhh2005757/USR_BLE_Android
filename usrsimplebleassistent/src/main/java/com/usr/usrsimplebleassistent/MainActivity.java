package com.usr.usrsimplebleassistent;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService;
import com.usr.usrsimplebleassistent.Utils.AnimateUtils;
import com.usr.usrsimplebleassistent.Utils.GattAttributes;
import com.usr.usrsimplebleassistent.Utils.Utils;
import com.usr.usrsimplebleassistent.adapter.DevicesAdapter;
import com.usr.usrsimplebleassistent.bean.MDevice;
import com.usr.usrsimplebleassistent.bean.MService;
import com.usr.usrsimplebleassistent.fragments.BleFragment;
import com.usr.usrsimplebleassistent.fragments.SppFragment;
import com.usr.usrsimplebleassistent.views.RevealBackgroundView;
import com.usr.usrsimplebleassistent.views.RevealSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import materialdialog.MaterialDialog;


public class MainActivity extends MyBaseActivity implements BleFragment.OnRunningAppRefreshListener,View.OnClickListener {
    @BindView(R.id.coll_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    private static BluetoothAdapter mBluetoothAdapter;
    private Handler hander;
    boolean isShowingDialog = false;
    private ViewPager vpContainer;
    private RadioGroup rgTabButtons;
    private int mCurrentFragment;
    private String[] fragmetns = new String[]{
            BleFragment.class.getName(),
            SppFragment.class.getName()};
    private MDevice mDevice;
    private String mode;
    /**
     * BLE  // 成员域
     */
    private boolean scaning;
    private MaterialDialog progressDialog;
    private RevealSearchView revealSearchView;
    private RevealBackgroundView revealBackgroundView;
    private FloatingActionButton fabSearch;
    private int[] fabStartPosition;
    private TextView tvSearchDeviceCount;
    private RelativeLayout rlSearchInfo;
    private Button stopSearching;
    private RecyclerView recyclerView;
    private String currentDevAddress;
    private String currentDevName;

    private MaterialDialog alarmDialog;


    //停止扫描
    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothAdapter != null)
                mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    };
    private Runnable dismssDialogRunnable = new Runnable() {
        @Override
        public void run() {
            if (progressDialog != null)
                progressDialog.dismiss();
            disconnectDevice();
        }
    };


    /**
     * spp
     */

    private BluetoothAdapter mBtAdapter;
    private final List<MDevice> list = new ArrayList<>();
    private DevicesAdapter adapter;

    /**
     * 构造
     */
    public MainActivity() {
        hander = new Handler();
        mDevice = new MDevice();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //如果有连接先关闭连接
        disconnectDevice();
    }


    /**
     * onCreate 入口
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission(this);
        setContentView(R.layout.activity_main);
        //必须调用，其在setContentView后面调用
        bindToolBar();
        //标题栏
        toolbar.setNavigationIcon(R.mipmap.ic_bluetooth_disabled_white_48dp);
        collapsingToolbarLayout.setTitle(getString(R.string.devices));
        //设置一个监听，否则会报错，support library design的bug
        collapsingToolbarLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        //检查蓝牙
        checkBleSupportAndInitialize();
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        initView(); //初始化视图
        initComponents(); //初始化view pager; 默认选中的为0
        initCartoon();//初始化动画
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //搜索按钮点击事件
            case R.id.fab_search:
                SharedPreferences mSharedPreferences = getSharedPreferences("mode", MainActivity.MODE_PRIVATE);
                mode = mSharedPreferences.getString("mode", "");
                if (mode.equals("SPP")) {
                    ((RadioButton) rgTabButtons.getChildAt(1)).setChecked(true);
                    searchAnimate();
                } else if (mode.equals("BLE")) {
                    ((RadioButton) rgTabButtons.getChildAt(0)).setChecked(true);
                    scaning = true;
                    //如果有连接先关闭连接
                    disconnectDevice();
                    //开始扫面动画
                    searchAnimate();
                    //初始化blefragment
                    initbleFragment();
                }
                break;
            //停止搜索按钮点击事件
            case R.id.btn_stop_searching:
                scaning = false;
                stopScan();
                //停止扫描
                mBtAdapter.cancelDiscovery();
                break;

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        initEvent();//初始化事件
        initBroadcast();//初始化广播
        initService();//初始化服务
    }

    /**
     * 初始化服务
     */
    private void initService() {
        Intent gattServiceIntent = new Intent(getApplicationContext(),
                BluetoothLeService.class);
        startService(gattServiceIntent);
    }

    /**
     * 初始化广播
     */
    private void initBroadcast() {
        //注册广播接收者，接收消息
        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //搜索按钮点击事件
        fabSearch.setOnClickListener(this);
        //停止搜索按钮点击事件
        stopSearching.setOnClickListener(this);
    }

    /**
     * 初始化动画 与动画的状态监听
     */
    private void initCartoon() {

        //动画效果
        collapsingToolbarLayout.setTranslationY(160f);
        toolbar.setTranslationY(-Utils.dpToPx(60));
        collapsingToolbarLayout.setAlpha(0.0f);
        AnimateUtils.translationY(collapsingToolbarLayout, 0, 400, 100);
        AnimateUtils.translationY(toolbar, 0, 400, 200);
        AnimateUtils.alpha(collapsingToolbarLayout, 1f, 400, 100);


        //revealSearchView 动画状态监听
        revealSearchView.setOnStateChangeListener(new RevealSearchView.OnStateChangeListener() {
            public void onStateChange(int state) {
                if (state == RevealSearchView.STATE_FINISHED) {
                    revealSearchView.setVisibility(View.GONE);
                    revealBackgroundView.endFromEdge();
                }
            }
        });
        // revealBackgroundView 动画状态监听
        revealBackgroundView.setOnStateChangeListener(new RevealBackgroundView.OnStateChangeListener() {
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
                    //准备列表视图并开始扫描
                    if (mode.equals("SPP")) {
                        doDiscovery();
                    }else if (mode.equals("BLE")){
                        onRefresh();
                    }
                }
                if (state == RevealBackgroundView.STATE_END_FINISHED) {
                    revealBackgroundView.setVisibility(View.GONE);
                    rlSearchInfo.setVisibility(View.GONE);
                    scaning = false;
                    if (mode.equals("BLE")) {
                        adapter.notifyDataSetChanged();
                    }

                }
            }
        });


    }

    /**
     * ble 停止扫描
     */
    private void stopScan() {
        revealSearchView.setVisibility(View.GONE);
        //停止雷达动画
        revealSearchView.stopAnimate();
        //涟漪动画回缩
        revealBackgroundView.endFromEdge();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        hander.removeCallbacks(stopScanRunnable);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        // 获得ViewPager
        vpContainer = (ViewPager) findViewById(R.id.vpContainer);
        revealSearchView = (RevealSearchView)findViewById(R.id.realsearchiew);
        revealBackgroundView = (RevealBackgroundView)findViewById(R.id.reveal_background_view);
        tvSearchDeviceCount = (TextView) findViewById(R.id.tv_search_device_count);
        rlSearchInfo = (RelativeLayout)findViewById(R.id.rl_search_info);
        fabSearch = (FloatingActionButton)findViewById(R.id.fab_search);
        stopSearching = (Button) findViewById(R.id.btn_stop_searching);
    }
    /**
     * 初始化blefragment
     */
    private void initbleFragment() {
        //获的recyclerView
        recyclerView = (RecyclerView)findViewById(R.id.recycleviewble);
        //给recyclerView   设置布局样式
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        //获取并设置设备适配器
        adapter = new DevicesAdapter(list, this);
        recyclerView.setAdapter(adapter);
        //recyclerView  添加条目效果
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                adapter.setDelayStartAnimation(false);
                return false;
            }
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        //adapter 点击事件
        adapter.setOnItemClickListener(new DevicesAdapter.OnItemClickListener() {
            public void onItemClick(View itemView, int position) {
                if (!scaning) {
                    isShowingDialog = true;
                    showProgressDialog();
                    hander.postDelayed(dismssDialogRunnable, 20000);
                    connectDevice(list.get(position).getDevice());
                }
            }
        });
    }

    /**
     * 显示连接动画
     */
    private void showProgressDialog() {
        progressDialog = new MaterialDialog(this);
        View view = LayoutInflater.from(this)
                .inflate(R.layout.progressbar_item,
                        null);
        progressDialog.setView(view).show();
    }

    /**
     * /准备列表视图并开始扫描
     */
    public void onRefresh() {
        if (adapter != null) {
            adapter.clear();
            adapter.notifyDataSetChanged();
        }
        startScan();//开始扫描
    }

    /**
     * 开始扫面入口
     */
    private void startScan() {
        scanPrevious21Version();
    }

    /**
     * 版本号21之前的调用该方法搜索
     */
    private void scanPrevious21Version() {
        //10秒后停止扫描
        hander.postDelayed(stopScanRunnable,10000);
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    /**
     * 发现设备时 处理方法
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
           runOnUiThread(new Runnable() {
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

    /**
     * 检查蓝牙是否可用
     */
    private void checkBleSupportAndInitialize() {
        // Use this check to determine whether BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.device_ble_not_supported,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // Initializes a Blue tooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            // Device does not support Blue tooth
            Toast.makeText(this,
                    R.string.device_ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }


    }
    /**
     * ble 搜索动画启动
     */
    private void searchAnimate() {
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
    }

    /**
     * spp 搜索启动事件
     */
    public void doDiscovery() {
        // 在窗口显示查找中信息
        //getActivity().setProgressBarIndeterminateVisibility(true);
        //getActivity().setTitle("查找设备中...");
        // 显示其它设备（未配对设备）列表
        //rootView.findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // 关闭再进行的服务查找
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        //并重新开始
        mBtAdapter.startDiscovery();
    }

    /**
     * ble 连接
     */
    private void connectDevice(BluetoothDevice device) {
        currentDevAddress = device.getAddress();
        currentDevName = device.getName();
        //如果是连接状态，断开，重新连接
        if (BluetoothLeService.getConnectionState() != BluetoothLeService.STATE_DISCONNECTED)
            BluetoothLeService.disconnect();
        BluetoothLeService.connect(currentDevAddress, currentDevName,this);
    }


    /**
     * ble 取消连接
     */
    private void disconnectDevice() {
        isShowingDialog = false;
        BluetoothLeService.disconnect();
    }


    /**
     * 菜单键监听
     */
    @Override
    protected void menuHomeClick() {
        Uri uri = Uri.parse("http://www.usr.cn/Product/cat-86.html");
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }

    /**
     * 返回键监听
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * 销毁MainActivity 的方法
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    /**
     * 初始化view pager
     */
    private void initComponents() {
        rgTabButtons = (RadioGroup) findViewById(R.id.rgTabButtons);
        KickerFragmentAdapter adpater = new KickerFragmentAdapter(getSupportFragmentManager(), this);
        vpContainer.setOnPageChangeListener(onPageChangeListener);
        vpContainer.setAdapter(adpater);
        vpContainer.setCurrentItem(mCurrentFragment);
        rgTabButtons.setOnCheckedChangeListener(onCheckedChangeListener);
        ((RadioButton) rgTabButtons.getChildAt(0)).setChecked(true);
    }

    /***/
    public void onRunningAppRefreshed() {
        SppFragment fragment = (SppFragment) getSupportFragmentManager().getFragments().get(1);
        if (fragment != null) {
            // fragment.refresh();
        }
    }



    class KickerFragmentAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public KickerFragmentAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        public Fragment getItem(int arg0) {
            return Fragment.instantiate(mContext, fragmetns[arg0]);
        }

        public int getCount() {
            return fragmetns.length;
        }

    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            mCurrentFragment = arg0;
            ((RadioButton) rgTabButtons.getChildAt(arg0)).setChecked(true);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            SharedPreferences mSharedPreferences = getSharedPreferences("mode", MainActivity.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            int checkedItem = 0;
            switch (checkedId) {
                case R.id.rbRunningApp:
                    checkedItem = 0;
                    editor.putString("mode", "BLE");
                    editor.commit();
                    String mode1 = mSharedPreferences.getString("mode", "");
                    System.out.println(mode1);
                    new SppFragment().onPause();
                    break;
                case R.id.rbRunningService:
                    checkedItem = 1;
                    editor.putString("mode", "SPP");
                    editor.commit();
                    String mode = mSharedPreferences.getString("mode", "");
                    System.out.println(mode);
                    new BleFragment().onPause();
                    break;
            }
            vpContainer.setCurrentItem(checkedItem);
            mCurrentFragment = checkedItem;
        }
    };


    /**
     * BroadcastReceiver for receiving the GATT communication status
     */
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
        alarmDialog = new MaterialDialog(this);
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

    /**
     * Getting the GATT Services
     * 获得服务
     *
     * @param gattServices
     */
    private void prepareGattServices(List<BluetoothGattService> gattServices) {
        prepareData(gattServices);

        Intent intent = new Intent(this, ServicesActivity.class);
        intent.putExtra("dev_name", currentDevName);
        intent.putExtra("dev_mac", currentDevAddress);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    /**
     * Prepare GATTServices data.
     *
     * @param gattServices
     */
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

        ((MyApplication) getApplication()).setServices(list);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mGattUpdateReceiver);
    }

    public static void requestPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity.getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }
}
