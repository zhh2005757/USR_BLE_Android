package com.usr.usrsimplebleassistent.SPPBlueTooth;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.usr.usrsimplebleassistent.BlueToothLeService.SppConnectService;
import com.usr.usrsimplebleassistent.Interface.Initialize;
import com.usr.usrsimplebleassistent.MyApplication;
import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.Utils.Utils;

import java.text.SimpleDateFormat;



/**
 * 经典蓝牙调试界面
 * Created by shizhiyuan on 2017/5/22.
 */

public class SppBlueThoothActivity extends AppCompatActivity implements View.OnClickListener, Initialize {
    private Button sendbutton;
    private Button btn;
    private EditText edit0;    //发送数据输入句柄
    private TextView dis;       //接收数据显示句柄
    private TextView spp_tv_message;       //接收数据信息的
    private TextView spp_tv_rssi;       //接收数据信息的
    private TextView spp_tv_type;       //接收数据信息的
    private TextView spp_tv_adress;       //接收数据信息的
    private TextView spp_tv_rxdata;       //接收数据信息的
    private TextView spp_tv_txdata;       //接收数据信息的
    private TextView spp_tv_time;       //接收数据信息的
    private TextView spp_tv_uuid;       //接收数据信息的
    private LinearLayout linearLayout;
    private LinearLayout linearLayout_options;
    private ScrollView sv;      //翻页句柄
    private Button btn_mode;
    private Button button7;
    private ImageView tool_back;
    static int temp;
    private int mode;
    private int AHmode = 3;
    private long ltiming = 0L;
    private boolean opints;
    private CheckBox checkbox;
    private MyApplication application;
    private SppConnectService.MyBinder myBinder;
    private long timeMillis;
    MessageBroadcastReceiver messageBroadcastReceiver;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            myBinder = (SppConnectService.MyBinder) iBinder;

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    //消息处理队列
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dis.setText(String.valueOf(msg.obj));   //显示数据
            spp_tv_rxdata.setText(String.valueOf(msg.arg1));
            sv.scrollTo(0, dis.getMeasuredHeight()); //跳至数据最后一页
        }

    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spp_main);   //设置画面为主画面 spp_mainmain.xml
        initView();
        initEvent();
        initParm();
        Intent intent = new Intent(this, SppConnectService.class);
        intent.putExtra("设备地址", getIntent().getStringExtra("设备地址"));
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        initBroadcast();
        timeMillis = System.currentTimeMillis();
    }

    private void initParm() {
       application = (MyApplication) this.getApplication();

    }

    //onStart   开始计时
    protected void onStart() {
        super.onStart();
        timeRunnable.run();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clean_count://清除计数
                spp_tv_rxdata.setText("0");
                spp_tv_txdata.setText("0");
                application.setClearflag(true);
                temp=0;
                break;
            case R.id.Button07://定时按钮
                onTimingButtonClicked();
                break;
            case R.id.dropdownbutton://下拉按钮
                if (opints == false) {
                    linearLayout_options.setVisibility(View.VISIBLE);
                    opints = true;
                } else {
                    linearLayout_options.setVisibility(View.GONE);
                    opints = false;
                }
                break;
            case R.id.tool_back://顶部后退按钮
                finish();
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void initView() {
        edit0 = (EditText) findViewById(R.id.Edit0);   //得到输入框句柄
        sv = (ScrollView) findViewById(R.id.ScrollView01);  //得到翻页句柄
        dis = (TextView) findViewById(R.id.in);      //得到数据显示句柄
        linearLayout_options = (LinearLayout) findViewById(R.id.options);
        linearLayout = (LinearLayout) findViewById(R.id.timingoption);
        spp_tv_message = (TextView) findViewById(R.id.spp_tv_name);      //得到数据显示句柄
        spp_tv_rssi = (TextView) findViewById(R.id.spp_tv_cod);      //得到数据显示句柄
        spp_tv_type = (TextView) findViewById(R.id.spp_tv_type);      //得到数据显示句柄
        spp_tv_adress = (TextView) findViewById(R.id.spp_tv_adress);      //得到数据显示句柄
        spp_tv_rxdata = (TextView) findViewById(R.id.spp_tv_rxdata);      //得到数据显示句柄
        spp_tv_txdata = (TextView) findViewById(R.id.spp_tv_txdata);      //得到数据显示句柄
        spp_tv_time = (TextView) findViewById(R.id.spp_tv_time);      //得到数据显示句柄
        spp_tv_uuid = (TextView) findViewById(R.id.spp_tv_uuid);      //得到数据显示句柄
        button7 = (Button) findViewById(R.id.Button07);
        tool_back = (ImageView) findViewById(R.id.tool_back);
        checkbox = (CheckBox) findViewById(R.id.checkbox_time);
        sendbutton = (Button) findViewById(R.id.Button02);
        btn = (Button) findViewById(R.id.Button03);
    }

    @Override
    public void initEvent() {
        button7.setTag(R.id.onTiming);
        button7.setOnClickListener(this);
        tool_back.setOnClickListener(this);
        findViewById(R.id.clean_count).setOnClickListener(this);
        findViewById(R.id.dropdownbutton).setOnClickListener(this);
        //定时按钮点击事件
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    sureTimingButtonClicked();//确定定时按键响应函数
                    sendbutton.setEnabled(false);
                    sendbutton.setText("定时中..");
                    sendbutton.setTextColor(Color.RED);
                    linearLayout.setVisibility(View.GONE);

                }
            }
        });
    }

    @Override
    public void initService() {

    }

    @Override
    public void initBroadcast() {
        messageBroadcastReceiver = new MessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("message");
        intentFilter.addAction("content");
        registerReceiver(messageBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        System.out.println("SppBlueThoothActivity生命周期-----------》onPause()");
        super.onPause();
    }

    //发送按键响应
    public void onSendButtonClicked(View v) {
        sendData();
    }

    //断开按键响应函数
    public void onConnectButtonClicked(View v) {
        Button btn = (Button) findViewById(R.id.Button03);
//            Toast.makeText(this,"请重新连接蓝牙",Toast.LENGTH_SHORT).show();
//            Intent serverIntent = new Intent(this, MainActivity.class); //跳转程序设置
//            startActivity(serverIntent);
        //关闭连接socket
        btn.setEnabled(false);
            myBinder.closeConnect();
            spp_tv_message.setText("");
            spp_tv_adress.setText("");
            spp_tv_type.setText("");
            spp_tv_rssi.setText("");
            spp_tv_uuid.setText("");
            ltiming = 0L;
            handler.removeCallbacks(timeRunnable);


        return;
    }

    //定时按键响应函数
    public void onTimingButtonClicked() {
        int tag = (Integer) button7.getTag();
        switch (tag) {
            case R.id.onTiming:
                //linearLayout_options.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                opints = false;
                button7.setTag(R.id.outTiming);
                button7.setText("取消");
                break;
            case R.id.outTiming:
                button7.setText("定时");
                sendbutton.setText("SEND");
                sendbutton.setTextColor(Color.WHITE);
                sendbutton.setEnabled(true);
                checkbox.setChecked(false);
                handler.removeCallbacks(senddata);
                button7.setTag(R.id.onTiming);
                ltiming = 0L;
                linearLayout.setVisibility(View.GONE);
                break;
        }
    }

    //确定定时按键响应函数
    public void sureTimingButtonClicked() {
        EditText et_timing = (EditText) findViewById(R.id.et_timing);
        String timing = String.valueOf(et_timing.getText());
        if (timing.equals("") && timing == null) {
            return;
        }
        try {
            ltiming = Long.parseLong(timing);
            System.out.println("定时时间为" + ltiming);
            handler.postDelayed(senddata, ltiming);
        } catch (Exception e) {
        }
        linearLayout.setVisibility(View.GONE);
    }

    //命令行按键响应函数
    public void onSaveButtonClicked(View v) {
        Modeconversion();
    }

    //清除按键响应函数
    public void onClearButtonClicked(View v) {
        dis.setText("");
        return;
    }

    //A/H按键响应函数
    public void onQuitButtonClicked(View v) {
        Button button6
                = (Button) findViewById(R.id.Button06);
        if (AHmode == 2) {
            edit0.setText("");
            button6.setText("ASCII");
            AHmode = 3;
        } else if (AHmode == 3) {
            button6.setText("HEX");
            AHmode = 2;
            String str = String.valueOf(edit0.getText());
            edit0.setText(Utils.str2HexStr(str));
        }
    }

    //发送数据
    public void sendData() {
        //tx计数计算d
        String str = String.valueOf(edit0.getText());
        int temp1 = str.length();
        temp+=temp1;
        spp_tv_txdata.setText(String.valueOf(temp));
        senddata.run();
    }

    //命令行模式转换实现
    private void Modeconversion() {
        //显示对话框输入文件名
        btn_mode = (Button) findViewById(R.id.btn_mode);
        if (mode == 1) {
            btn_mode.setText("普通");
            mode = 0;
        } else if (mode == 0) {
            btn_mode.setText("命令行");
            mode = 1;
        }
    }

    //关闭程序掉用处理部分
    protected void onStop() {
        super.onStop();
        unregisterReceiver(messageBroadcastReceiver);
        handler.removeCallbacks(timeRunnable);
        //关闭连接socket
        ltiming = 0L;
        unbindService(serviceConnection);
    }

    public void onDestroy() {
        super.onDestroy();

        stopService(new Intent(this, SppConnectService.class));
    }

    //获取运行时间
    public String getTime() {
        long time = System.currentTimeMillis() - timeMillis;
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String hms = sdf.format(time);
        return hms;
    }

    //定时发送
    Runnable senddata = new Runnable() {
        @Override
        public void run() {
            String str = String.valueOf(edit0.getText());
            myBinder.sendData(str, AHmode, mode);
            if (ltiming != 0L) {
                handler.postDelayed(this, ltiming);
            }
        }
    };


    Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 1000);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    spp_tv_time.setText(getTime());
                }
            });

        }
    };

    public class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            btn.setText("断开");
            switch (intent.getAction()) {
                case "message":
                    spp_tv_message.setText(bundle.getString("name"));
                    spp_tv_adress.setText(bundle.getString("adress"));
                    spp_tv_type.setText(bundle.getString("type"));
                    spp_tv_rssi.setText(bundle.getString("deviceclass"));
                    spp_tv_uuid.setText(bundle.getString("uuids"));
                    break;
                case "content":
                    dis.setText(bundle.getString("messagecontent"));   //显示数据
                    spp_tv_rxdata.setText(bundle.getString("messagecontentlength"));
                    sv.scrollTo(0, dis.getMeasuredHeight()); //跳至数据最后一页
                    break;
            }

        }

    }

}
