package com.usr.usrsimplebleassistent;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.usr.usrsimplebleassistent.BlueToothLeService.BluetoothLeService;
import com.usr.usrsimplebleassistent.Utils.AnimateUtils;
import com.usr.usrsimplebleassistent.Utils.Constants;
import com.usr.usrsimplebleassistent.Utils.GattAttributes;
import com.usr.usrsimplebleassistent.Utils.Utils;
import com.usr.usrsimplebleassistent.adapter.MessagesAdapter;
import com.usr.usrsimplebleassistent.adapter.OptionsSelectAdapter;
import com.usr.usrsimplebleassistent.bean.Message;
import com.usr.usrsimplebleassistent.bean.Option;
import com.usr.usrsimplebleassistent.views.OptionsMenuManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import materialdialog.MaterialDialog;

public class GattDetailActivity extends MyBaseActivity {

    @BindView(R.id.btn_options)
    ImageButton btnOptions;
    @BindView(R.id.btn_option)
    Button btnOption;
    @BindView(R.id.lv_msg)
    RecyclerView rvMsg;
    @BindView(R.id.tv_properties)
    TextView tvProperties;
    @BindView(R.id.et_write)
    EditText etWrite;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.rl_write)
    RelativeLayout rlWrite;
    @BindView(R.id.rl_content)
    RelativeLayout rlContent;
    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;
    @BindView(R.id.view_bottom_shadow)
    View bottomShadow;
    @BindView(R.id.view_top_shadow)
    View topShadow;
    @BindView(R.id.view_filter)
    View filterView;

    private final List<Message> list = new ArrayList<>();

    private MessagesAdapter adapter;

    private BluetoothGattCharacteristic notifyCharacteristic;
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    private BluetoothGattCharacteristic indicateCharacteristic;

    private MyApplication myApplication;
    private String properties;
    private OptionsMenuManager optionsMenuManager;

    private List<Option> options = new ArrayList<>();
    private Option currentOption;

    private boolean isHexSend;

    private boolean nofityEnable;
    private boolean indicateEnable;
    private boolean isDebugMode;


    private BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            //There are four basic operations for moving data in BLE: read, write, notify,
            // and indicate. The BLE protocol specification requires that the maximum data
            // payload size for these operations is 20 bytes, or in the case of read operations,
            // 22 bytes. BLE is built for low power consumption, for infrequent short-burst data transmissions.
            // Sending lots of data is possible, but usually ends up being less efficient than classic Bluetooth
            // when trying to achieve maximum throughput.  从google查找的，解释了为什么android下notify无法解释超过
            //20个字节的数据
            Bundle extras = intent.getExtras();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                // Data Received
                if (extras.containsKey(Constants.EXTRA_BYTE_VALUE)) {
                    if (extras.containsKey(Constants.EXTRA_BYTE_UUID_VALUE)) {
                        if (myApplication != null) {
                            BluetoothGattCharacteristic requiredCharacteristic = myApplication.getCharacteristic();
                            String uuidRequired = requiredCharacteristic.getUuid().toString();
                            String receivedUUID = intent.getStringExtra(Constants.EXTRA_BYTE_UUID_VALUE);
                            if (isDebugMode){
                                byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                                Message msg = new Message(Message.MESSAGE_TYPE.RECEIVE,formatMsgContent(array));
                                notifyAdapter(msg);
                            }else if (uuidRequired.equalsIgnoreCase(receivedUUID)) {
                                byte[] array = intent.getByteArrayExtra(Constants.EXTRA_BYTE_VALUE);
                                Log.i("byteToString",Utils.ByteArrToIntStr(array));
                                Message msg = new Message(Message.MESSAGE_TYPE.RECEIVE,formatMsgContent(array,MyApplication.serviceType));
                                notifyAdapter(msg);
                            }
                        }
                    }
                }
                if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE)) {
                    if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID)) {
                        BluetoothGattCharacteristic requiredCharacteristic = myApplication.
                                getCharacteristic();
                        String uuidRequired = requiredCharacteristic.getUuid().toString();
                        String receivedUUID = intent.getStringExtra(
                                Constants.EXTRA_DESCRIPTOR_BYTE_VALUE_CHARACTERISTIC_UUID);

                        byte[] array = intent
                                .getByteArrayExtra(Constants.EXTRA_DESCRIPTOR_BYTE_VALUE);

//                        System.out.println("GattDetailActivity---------------------->descriptor:" + Utils.ByteArraytoHex(array));
                        if (isDebugMode){
                            updateButtonStatus(array);
                        }else if (uuidRequired.equalsIgnoreCase(receivedUUID)) {
                            updateButtonStatus(array);
                        }

                    }
                }
            }

            if (action.equals(BluetoothLeService.ACTION_GATT_DESCRIPTORWRITE_RESULT)){
                if (extras.containsKey(Constants.EXTRA_DESCRIPTOR_WRITE_RESULT)){
                    int status = extras.getInt(Constants.EXTRA_DESCRIPTOR_WRITE_RESULT);
                    if (status != BluetoothGatt.GATT_SUCCESS){
                        Snackbar.make(rlContent,R.string.option_fail,Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            if (action.equals(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_ERROR)) {
                if (extras.containsKey(Constants.EXTRA_CHARACTERISTIC_ERROR_MESSAGE)) {
                    String errorMessage = extras.
                            getString(Constants.EXTRA_CHARACTERISTIC_ERROR_MESSAGE);
                    System.out.println("GattDetailActivity---------------------->err:" + errorMessage);
                    showDialog(errorMessage);
                }

            }

            //write characteristics succcess
            if (action.equals(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_WRITE_SUCCESS)){
                list.get(list.size()-1).setDone(true);
                adapter.notifyItemChanged(list.size()-1);
            }

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
//                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
//                if (state == BluetoothDevice.BOND_BONDING) {}
//                else if (state == BluetoothDevice.BOND_BONDED) {}
//                else if (state == BluetoothDevice.BOND_NONE) {}
            }

            //connect break (连接断开)
            if (action.equals(BluetoothLeService.ACTION_GATT_DISCONNECTED)){
                showDialog(getString(R.string.conn_disconnected));
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gatt_detail);
        ButterKnife.bind(this);
        bindToolBar();
        myApplication = (MyApplication) getApplication();
        optionsMenuManager = OptionsMenuManager.getInstance();



        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvMsg.setLayoutManager(llm);

        adapter = new MessagesAdapter(this, list);
        rvMsg.setAdapter(adapter);

        initCharacteristics();
        initProperties();

        registerReceiver(mGattUpdateReceiver, Utils.makeGattUpdateIntentFilter());

        rvMsg.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if (optionsMenuManager.getOptionsMenu()!=null)
                    dismissMenu();
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        if (savedInstanceState == null) {
            filterView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    filterView.getViewTreeObserver().removeOnPreDrawListener(this);
                    startEndAnimation();
                    return true;
                }
            });
        }

        int sdkInt = Build.VERSION.SDK_INT;
        System.out.println("sdkInt------------>"+sdkInt);
        if (sdkInt>=21){
            //设置最大发包、收包的长度为512个字节
            if(BluetoothLeService.requestMtu(512)){
                Toast.makeText(this,getString(R.string.transmittal_length,"512"),Toast.LENGTH_LONG).show();
            }else
                Toast.makeText(this,getString(R.string.transmittal_length,"20"),Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,getString(R.string.transmittal_length,"20"),Toast.LENGTH_LONG).show();
        }
    }

    private void initCharacteristics(){
        BluetoothGattCharacteristic characteristic = myApplication.getCharacteristic();
        if (characteristic.getUuid().toString().equals(GattAttributes.USR_SERVICE)){
            isDebugMode = true;
            List<BluetoothGattCharacteristic> characteristics = ((MyApplication)getApplication()).getCharacteristics();

            for (BluetoothGattCharacteristic c :characteristics){
                if (Utils.getPorperties(this,c).equals("Notify")){
                    notifyCharacteristic = c;
                    continue;
                }

                if (Utils.getPorperties(this,c).equals("Write")){
                    writeCharacteristic = c;
                    continue;
                }
            }

            properties = "Notify & Write";

        }else {
            properties = Utils.getPorperties(this, characteristic);

            notifyCharacteristic = characteristic;
            readCharacteristic = characteristic;
            writeCharacteristic = characteristic;
            indicateCharacteristic = characteristic;
        }
    }


    private void initProperties() {
        if (TextUtils.isEmpty(properties))
            return;
        tvProperties.setText(properties);
        String[] property = properties.split("&");

        if (property.length == 1) {
            btnOptions.setVisibility(View.GONE);
            Option option = new Option(properties.trim(),Option.OPTIONS_MAP.get(properties.trim()));
            setOption(option);
        } else {
            for (int i=0;i<property.length;i++){
                String p = property[i];
                Option option = new Option();
                option.setName(p.trim());
                option.setPropertyType(Option.OPTIONS_MAP.get(p.trim()));
                options.add(option);
                if (i==0){
                  setOption(option);
                }
            }
        }
    }


    private void setOption(Option option){
        currentOption = option;
        switch (option.getPropertyType()){
            case PROPERTY_NOTIFY:
                if (!nofityEnable)
                    btnOption.setText(Option.NOTIFY);
                else
                    btnOption.setText(Option.STOP_NOTIFY);
                showViewIsEdit(false);
                break;
            case PROPERTY_READ:
                btnOption.setText(Option.READ);
                showViewIsEdit(false);
                break;
            case PROPERTY_INDICATE:
                if (!indicateEnable)
                   btnOption.setText(Option.INDICATE);
                else
                   btnOption.setText(Option.STOP_INDICATE);
                showViewIsEdit(false);
                break;
            case PROPERTY_WRITE:
                showViewIsEdit(true);
                break;
        }
    }


    private void showViewIsEdit(boolean isEdit){
        if (isEdit){
            btnOption.setVisibility(View.GONE);
            rlWrite.setVisibility(View.VISIBLE);
        }else {
            btnOption.setVisibility(View.VISIBLE);
            rlWrite.setVisibility(View.GONE);
        }
    }



    @OnClick(R.id.btn_options)
    public void onOptionsClick() {
        optionsMenuManager.toggleContextMenuFromView(options, btnOptions, new OptionsSelectAdapter.OptionsOnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                dismissMenu();
                setOption(options.get(position));
            }
        });
    }


    @OnClick(R.id.btn_option)
    public void onOptionClick() {
        if (optionsMenuManager.getOptionsMenu()!=null){
            dismissMenu();
            return;
        }
        switch (currentOption.getPropertyType()){
            case PROPERTY_NOTIFY:
                notifyOption();
                break;
            case PROPERTY_INDICATE:
                indicateOption();
                break;
            case PROPERTY_READ:
                readOption();
                break;
            case PROPERTY_WRITE:
                break;
        }
    }


    @OnClick(R.id.btn_send)
    public void onSendClick(){
        writeOption();
    }

    private void notifyOption(){
       if (nofityEnable){
           nofityEnable = false;
           btnOption.setText(Option.NOTIFY);
           stopBroadcastDataNotify(notifyCharacteristic);
           Message msg = new Message(Message.MESSAGE_TYPE.SEND,Option.STOP_NOTIFY);
           notifyAdapter(msg);
       }else {
           nofityEnable = true;
           btnOption.setText(Option.STOP_NOTIFY);
           prepareBroadcastDataNotify(notifyCharacteristic);
           Message msg = new Message(Message.MESSAGE_TYPE.SEND,Option.NOTIFY);
           notifyAdapter(msg);
       }
    }


    private void indicateOption(){
        if (indicateEnable){
            indicateEnable = false;
            btnOption.setText(Option.INDICATE);
            stopBroadcastDataIndicate(indicateCharacteristic);
            Message msg = new Message(Message.MESSAGE_TYPE.SEND,Option.STOP_INDICATE);
            notifyAdapter(msg);
        }else {
            nofityEnable = true;
            btnOption.setText(Option.STOP_INDICATE);
            prepareBroadcastDataIndicate(indicateCharacteristic);
            Message msg = new Message(Message.MESSAGE_TYPE.SEND,Option.INDICATE);
            notifyAdapter(msg);
        }
    }



    private void readOption(){
        Message msg = new Message(Message.MESSAGE_TYPE.SEND,Option.READ);
        notifyAdapter(msg);
        prepareBroadcastDataRead(readCharacteristic);
    }

    private void writeOption(){
        String text = etWrite.getText().toString();
        if (TextUtils.isEmpty(text)){
            AnimateUtils.shake(etWrite);
            return;
        }

        if (isHexSend){
            text = text.replace(" ","");
            if (!Utils.isRightHexStr(text)){
                AnimateUtils.shake(etWrite);
                return;
            }
            byte[] array = Utils.hexStringToByteArray(text);
            writeCharacteristic(writeCharacteristic, array);
        }else {

            if(Utils.isAtCmd(text))
                text = text + "\r\n";
            try {
                byte[] array = text.getBytes("US-ASCII");
                writeCharacteristic(writeCharacteristic,array);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                System.out.println("--------------------->write text exception");
                return;
            }

        }

        Message msg = new Message(Message.MESSAGE_TYPE.SEND,text);
        notifyAdapter(msg);
    }


    /**
     * update option button status (更新Option按钮的操作状态)
     * @param array
     */
    private void updateButtonStatus(byte[] array) {
        int status=array[0];
        switch (status) {
            case 0:
                if(btnOption.getText().toString().equals(Option.STOP_NOTIFY)){
                    btnOption.setText(Option.NOTIFY);
                    Message msg = new Message(Message.MESSAGE_TYPE.RECEIVE,Option.STOP_NOTIFY);
                    notifyAdapter(msg);
                }

                if (btnOption.getText().toString().equals(Option.STOP_INDICATE)){
                    btnOption.setText(Option.INDICATE);
                    Message msg = new Message(Message.MESSAGE_TYPE.RECEIVE,Option.STOP_INDICATE);
                    notifyAdapter(msg);
                }
                break;
            case 1:
                if (btnOption.getText().toString().equals(Option.NOTIFY)){
                    btnOption.setText(Option.STOP_NOTIFY);
                    Message msg = new Message(Message.MESSAGE_TYPE.RECEIVE,Option.NOTIFY);
                    notifyAdapter(msg);
                }
                break;
            case 2:
                if (btnOption.getText().toString().equals(Option.INDICATE)){
                    btnOption.setText(Option.STOP_INDICATE);
                    Message msg = new Message(Message.MESSAGE_TYPE.RECEIVE,Option.INDICATE);
                    notifyAdapter(msg);
                }
                break;
        }
    }





    private void startEndAnimation() {

        filterView.setAlpha(0.0f);
        filterView.setVisibility(View.VISIBLE);
        filterView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        ObjectAnimator animator1 = ObjectAnimator.ofInt(filterView,"backgroundColor",
                Color.parseColor("#0277bd"),Color.parseColor("#009688"));
        animator1.setDuration(200);
        animator1.setEvaluator(new ArgbEvaluator());


        filterView.animate()
                .alpha(0.6f)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        tvProperties.setVisibility(View.VISIBLE);
                        rvMsg.setVisibility(View.VISIBLE);
                        rlBottom.setVisibility(View.VISIBLE);
                        topShadow.setVisibility(View.VISIBLE);
                        bottomShadow.setVisibility(View.VISIBLE);

                        tvProperties.setTranslationY(-Utils.dpToPx(40));
                        topShadow.setTranslationY(-Utils.dpToPx(40));
                        bottomShadow.setAlpha(0.0f);
                        rlBottom.setTranslationY(Utils.dpToPx(56));
                        btnOptions.setTranslationY(Utils.dpToPx(56));
                        AnimateUtils.translationY(rlBottom,0,300,200);
                        AnimateUtils.alpha(bottomShadow,0.3f,100,450);
                        AnimateUtils.translationY(btnOptions,0,300,300);
                        AnimateUtils.translationY(tvProperties,0,300,300);
                        AnimateUtils.translationY(topShadow,0,300,300);
                        if (currentOption.getPropertyType() == Option.OPTION_PROPERTY.PROPERTY_WRITE){
                            etWrite.setTranslationY(Utils.dpToPx(56));
                            btnSend.setTranslationY(Utils.dpToPx(56));
                            AnimateUtils.translationY(etWrite,0,300,400);
                            AnimateUtils.translationY(btnSend,0,300,500);
                        }else {
                            btnOption.setTranslationY(Utils.dpToPx(56));
                            AnimateUtils.translationY(btnOption,0,300,500);
                        }

                        animate2();
                    }
                })
                .start();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            toolbar.setAlpha(0.0f);
            AnimateUtils.alpha(toolbar,1.0f,200,0);
        }

        animator1.start();
    }

    private void animate2(){
        filterView.animate()
                .alpha(0.0f)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        filterView.setLayerType(View.LAYER_TYPE_NONE, null);
                        filterView.setVisibility(View.GONE);
                    }
                })
                .start();
    }






    private void notifyAdapter(Message msg){
        list.add(msg);
        adapter.notifyLastItem();
        rvMsg.smoothScrollToPosition(adapter.getItemCount() - 1);
    }





    private void dismissMenu() {
        if (optionsMenuManager.getOptionsMenu() != null) {
            optionsMenuManager.toggleContextMenuFromView(null, null, null);
        }
    }



    private String formatMsgContent(byte[] data){
        return "HEX:"+Utils.ByteArraytoHex(data)+"  (ASSCII:"+Utils.byteToASCII(data)+")";
    }


    private String formatMsgContent(byte[] data,MyApplication.SERVICE_TYPE type){
        String res = "ASSCII:"+Utils.byteToASCII(data);
        switch (type){
            case TYPE_STR:
                res += "  (ASSCII:"+Utils.byteToASCII(data)+")";
                break;
            case TYPE_USR_DEBUG:
                res += "  (ASSCII:"+Utils.byteToASCII(data)+")";
                break;
            case TYPE_NUMBER:
                res+= "  (int:"+Utils.ByteArrToIntStr(data)+")";
                break;
            case TYPE_OTHER:
                res += " (HEX:"+Utils.ByteArraytoHex(data)+")";
                break;
        }
        return res;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (optionsMenuManager.getOptionsMenu()!=null){
            dismissMenu();
            return false;
        }
        super.onOptionsItemSelected(item);
        String text = etWrite.getText().toString();
        switch (item.getItemId()){
            case R.id.menu_hex_send:
                isHexSend = true;
                if (!TextUtils.isEmpty(text)){
                    if(Utils.isAtCmd(text))
                        text = text + "\r\n";
                    try {
                        etWrite.setText(Utils.ByteArraytoHex(text.getBytes("US-ASCII")));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        etWrite.setText("");
                    }
                }
                break;
            case R.id.menu_asscii_send:
                isHexSend = false;
                etWrite.setText("");
                break;
            case R.id.menu_clear_display:
                list.clear();
                adapter.notifyDataSetChanged();
                break;
            case R.id.menu_update:
                readDataFromSD();
                break;

        }

        return false;
    }

    /**
     * 读取指定位置的文件的大小
     * @param
     */
    public static double getDirSize(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {//如果是文件则直接返回其大小,以“兆”为单位
                double size = (double) file.length() / 1024 / 1024;
                System.out.println("文件的大小为"+size+"MB");
                return size;
            }
        } else {
            System.out.println("文件或者文件夹不存在，请检查路径是否正确！");
            return 0.0;
        }
    }

    /**
     * 读取指定位置的文件
     * @param
     */
    public void readDataFromSD(){
        try{
            String saveUrl = "/sdcard/updatedemo/";
        /* 创建File对象，确定需要读取文件的信息 */
            File file = new File(saveUrl,"test.txt");

        /* FileInputSteam 输入流的对象， */
            FileInputStream fis = new FileInputStream(file);

        /* 准备一个字节数组用户装即将读取的数据 */
            byte[] buffer = new byte[fis.available()];

        /* 开始进行文件的读取 */
            fis.read(buffer);

        /* 关闭流  */
            fis.close();

        /* 将字节数组转换成16进制 */
            //String res = Utils.ByteArrToIntStr(buffer);
           // String str = new String(bytes);  //.java  文件默认的编码
            String str = new String(buffer, "utf-8");  //unicode 2位
            //String str = new String(bytes, "gbk"); //中文简体
            updateWrite(str);
            //Toast.makeText(this, "文件读取成功，您读取的数据为："+str, Toast.LENGTH_SHORT).show();

        }catch(Exception ex){
            Toast.makeText(this, "文件读取失败！", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWrite(String text){
        //String text = etWrite.getText().toString();
        if (TextUtils.isEmpty(text)){
            AnimateUtils.shake(etWrite);
            return;
        }

        if (isHexSend){
            text = text.replace(" ","");
            if (!Utils.isRightHexStr(text)){
                AnimateUtils.shake(etWrite);
                return;
            }
            byte[] array = Utils.hexStringToByteArray(text);
            writeCharacteristic(writeCharacteristic, array);
        }else {
            if(Utils.isAtCmd(text))
                text = text + "\r\n";
            try {
                byte[] array = text.getBytes("US-ASCII");
                writeCharacteristic(writeCharacteristic,array);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                System.out.println("--------------------->write text exception");
                return;
            }

        }
       Message msg = new Message(Message.MESSAGE_TYPE.SEND,text);
        notifyAdapter(msg);
    }





    /**
     * Preparing Broadcast receiver to broadcast read characteristics
     *
     * @param characteristic
     */
    void prepareBroadcastDataRead(
            BluetoothGattCharacteristic characteristic) {
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            BluetoothLeService.readCharacteristic(characteristic);
        }
    }

    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param characteristic
     */
    void prepareBroadcastDataNotify(
            BluetoothGattCharacteristic characteristic) {
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BluetoothLeService.setCharacteristicNotification(characteristic, true);
        }

    }

    /**
     * Stopping Broadcast receiver to broadcast notify characteristics
     *
     * @param characteristic
     */
    void stopBroadcastDataNotify(
            BluetoothGattCharacteristic characteristic) {
        final int charaProp = characteristic.getProperties();
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BluetoothLeService.setCharacteristicNotification(characteristic, false);
        }
    }

    /**
     * Preparing Broadcast receiver to broadcast indicate characteristics
     *
     * @param characteristic
     */
    void prepareBroadcastDataIndicate(
            BluetoothGattCharacteristic characteristic) {
        final int charaProp = characteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            BluetoothLeService.setCharacteristicIndication(characteristic, true);
        }
    }

    /**
     * Stopping Broadcast receiver to broadcast indicate characteristics
     *
     * @param characteristic
     */
    void stopBroadcastDataIndicate(
            BluetoothGattCharacteristic characteristic) {
        final int charaProp = characteristic.getProperties();

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            BluetoothLeService.setCharacteristicIndication(characteristic, false);
        }

    }


    private void writeCharacteristic(BluetoothGattCharacteristic characteristic, byte[] bytes) {
        // Writing the hexValue to the characteristics
        try {
            BluetoothLeService.writeCharacteristicGattDb(characteristic,
                    bytes);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }




    private void showDialog(String info){

        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setTitle(getString(R.string.alert))
                .setMessage(info)
                .setPositiveButton(R.string.ok,new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }



    private void stopNotifyOrIndicate(){
        if (nofityEnable)
            stopBroadcastDataNotify(notifyCharacteristic);
        if (indicateEnable)
            stopBroadcastDataIndicate(indicateCharacteristic);
    }


    @Override
    public void onBackPressed() {
        if (optionsMenuManager.getOptionsMenu()!=null){
            dismissMenu();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopNotifyOrIndicate();
        unregisterReceiver(mGattUpdateReceiver);
    }
}
