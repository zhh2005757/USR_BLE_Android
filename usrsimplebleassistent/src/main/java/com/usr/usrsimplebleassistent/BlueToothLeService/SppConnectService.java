package com.usr.usrsimplebleassistent.BlueToothLeService;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;

import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.usr.usrsimplebleassistent.MyApplication;
import com.usr.usrsimplebleassistent.R;
import com.usr.usrsimplebleassistent.SPPBlueTooth.SppBlueThoothActivity;
import com.usr.usrsimplebleassistent.Utils.ChangeCharset;
import com.usr.usrsimplebleassistent.Utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 经典蓝牙连接的service
 * Created by shizhiyuan on 2017/5/31.
 */

public class SppConnectService extends Service {


    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP服务UUID号
    private final static String CONNECTSUCCEED = "CONNECTSUCCEED";   //SPP服务UUID号
    private final static String CONNECTDEFEATED = "CONNECTDEFEATED";   //SPP服务UUID号
    private final static String MESSAGE = "message";   //SPP服务UUID号
    private final static String NAME = "name";   //SPP服务UUID号
    private final static String ADRESS = "adress";   //SPP服务UUID号
    private final static String DEVICECLASS = "deviceclass";   //SPP服务UUID号
    private final static String TYPE = "type";   //SPP服务UUID号
    private final static String UUIDS = "uuids";   //SPP服务UUID号
    private final static String GETUUIDDEFEATED = "未获取到uuid信息";   //SPP服务UUID号
    private final static String MESSAGECONTENT = "messagecontent";   //SPP服务UUID号
    private final static String MESSAGECONTENTLENGTH = "messagecontentlength";   //SPP服务UUID号
    boolean rSendDataflag = false;
    private int mode;
    private int AHmode = 3;
    private String data;

    private InputStream is;    //输入流，用来接收蓝牙数据
    private BluetoothDevice _device = null;     //蓝牙设备
    private BluetoothSocket _socket = null;      //蓝牙通信socket
    boolean bRun = true;

    private StringBuilder sbmsg;    //显示用数据缓存
    private String fmsg = "";    //保存用数据缓存
    private int smsglength = 0;
    boolean bThread = false;
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //获取本地蓝牙适配器，即蓝牙设备
    private String address1;
    private MyBinder mBinder = new MyBinder();
    private Intent intent2 = new Intent();
    private Bundle bundle = new Bundle();
    MyApplication application;

    //onBind
    public IBinder onBind(Intent intent) {
        System.out.println("SppConnectService生命周期之-----------onBind");
        intent2.setAction(MESSAGE);
        int type = _device.getType();
        int deviceClass = _device.getBluetoothClass().getDeviceClass();
        try {
            String uuids = _device.getUuids()[0].getUuid().toString();
            bundle.putString(UUIDS, uuids);
        } catch (Exception e) {
            bundle.putString(UUIDS, GETUUIDDEFEATED);
        }
        bundle.putString(NAME, _device.getName());
        bundle.putString(ADRESS, _device.getAddress());
        bundle.putString(DEVICECLASS, String.valueOf(deviceClass));
        bundle.putString(TYPE, String.valueOf(type));
        intent2.putExtras(bundle);
        sendBroadcast(intent2);
        return mBinder;
    }

    //onCreate
    public void onCreate() {
        System.out.println("SppConnectService生命周期之-----------onCreate");
        application = (MyApplication) SppConnectService.this.getApplication();

        super.onCreate();
    }

    //onStartCommand
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("SppConnectService生命周期之-----------onStartCommand");
        address1 = intent.getStringExtra("设备地址");
        if (address1 == null) {
            System.out.println("sadhakslhdjkahdkjahkjsdajkdkjadjhakj");
        }

        new Thread() {
            @Override
            public void run() {
                Intent intent1 = new Intent();
                super.run();
                // 得到蓝牙设备句柄
                _device = _bluetooth.getRemoteDevice(address1);
                // 用服务号得到socket
                try {
                    _socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                    _socket.connect();
                    System.out.println("connect chenggong");
                    intent1.setAction(CONNECTSUCCEED);
                    //打开接收线程
                    try {
                        is = _socket.getInputStream();   //得到蓝牙数据输入流
                    } catch (IOException e) {
                        System.out.println("接收数据失败");
                    }
                    if (bThread == false) {
                        ReadThread.start();
                        bThread = true;
                    } else {
                        bRun = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        _socket.close();
                        _socket = null;
                    } catch (IOException e1) {
                        intent1.setAction(CONNECTDEFEATED);
                        System.out.println("真遗憾！连接失败");
                        e1.printStackTrace();
                    }
                    intent1.setAction(CONNECTDEFEATED);
                    sendBroadcast(intent1);
                    System.out.println("真遗憾！连接失败");
                } finally {
                    sendBroadcast(intent1);
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    //onDestroy
    public void onDestroy() {
        System.out.println("SppConnectService生命周期之-----------onDestroy");
        super.onDestroy();
    }

    //接收数据线程
    Thread ReadThread = new Thread() {
        public void run() {
            int num = 0;
            byte[] buffer = new byte[1024];
            byte[] buffer_new = new byte[1024];
            int i = 0;
            int n = 0;
            bRun = true;
            sbmsg = new StringBuilder();
            //接收线程
            while (true) {

                try {
                    while (is.available() == 0) {
                        while (bRun == false) {
                        }
                    }
                    while (true) {
                        num = is.read(buffer);         //读入数据
                        n = 0;
                        String s0 = new String(buffer, 0, num);
                        fmsg += s0;    //保存收到数据
                        for (i = 0; i < num; i++) {
                            if ((buffer[i] == 0x0d) && (buffer[i + 1] == 0x0a)) {
                                buffer_new[n] = 0x0a;
                                i++;
                            } else {
                                buffer_new[n] = buffer[i];
                            }
                            n++;
                        }
                        String s = new String(buffer_new, 0, n, "utf-8");
                        if (application.isClearflag() == true) {
                            sbmsg = new StringBuilder();
                            application.setClearflag(false);
                        }
                        sbmsg.append(s);
                        smsglength = sbmsg.length();
                        intent2.setAction("content");
                        bundle.putString(MESSAGECONTENT, String.valueOf(sbmsg));
                        bundle.putString(MESSAGECONTENTLENGTH, String.valueOf(smsglength));
                        intent2.putExtras(bundle);
                        sendBroadcast(intent2);
                        if (is.available() == 0) break;  //短时间没有数据才跳出进行显示
                    }
                } catch (IOException e) {
                }
            }
        }
    };


    public class MyBinder extends Binder {

        public void sendData(String data, int AHmode, int mode) {
            SppConnectService.this.data = data;
            SppConnectService.this.AHmode = AHmode;
            SppConnectService.this.mode = mode;
            rSendData.run();
        }

        public void closeConnect() {
            try {
                is.close();
                _socket.close();
                _socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //消息处理队列
        Handler handler = new Handler() {
        };

        //发送数据县城
        Runnable rSendData = new Runnable() {
            public void run() {
                byte[] bos;
                int temp;
                OutputStream os = null;   //蓝牙连接输出流
                if (!rSendDataflag) {
                    try {
                        os = _socket.getOutputStream();
                        if (data.equals(""))
                            return;
                        String gbk = new ChangeCharset().changeCharset(data, "gbk");
                        bos = gbk.getBytes();
                        if (AHmode == 3) {
                            System.out.println("ASCII模式发送");
                            if (mode == 1) {
                                System.out.println("命令行模式发送");
                                data = data + "\r\n";
                            }
                            bos = data.getBytes();
                        }
                        if (AHmode == 2) {
                            if (data.length() < 2)
                                return;
                            System.out.println("HEX模式发送");
                            if (mode == 1) {
                                System.out.println("命令行模式发送");
                                bos = Utils.hexStringToString2(data).getBytes();
                            } else {
                                bos = Utils.hexStringToString(data).getBytes();
                            }

                        }
                        os.write(bos);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        };
    }

    }
