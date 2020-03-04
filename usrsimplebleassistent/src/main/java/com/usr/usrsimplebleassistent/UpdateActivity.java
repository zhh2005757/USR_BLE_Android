package com.usr.usrsimplebleassistent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.usr.usrsimplebleassistent.adapter.SPP_UpdateListViewAdapter;
import com.usr.usrsimplebleassistent.item.SppUpdateListViewItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * Created by shizhiyuan on 2017/5/26.
 */

public class UpdateActivity extends AppCompatActivity {

    private ProgressBar pb;
    private TextView tv;
    private int fileSize;
    private int downLoadFileSize;
    private String filename;
    private String strUrl = "http://ycsj1.usr.cn/test.php";
    private String saveUrl = "/sdcard/updatedemo/";
    // 声明ListView控件
    private ListView uListView;
    // 声明数组链表，其装载的类型是ListItem()
    private ArrayList<SppUpdateListViewItem> uBeanList;
    SppUpdateListViewItem sppupdatelv;
    String downurl;

    //用来接收线程发送来的文件下载量，进行UI界面的更新
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {//定义一个Handler，用于处理下载线程与UI间通讯
            if (!Thread.currentThread().isInterrupted()) {
                switch (msg.what) {
                    case 0:
                        pb.setMax(fileSize);
                    case 1:
                        pb.setProgress(downLoadFileSize);
                        int result = downLoadFileSize * 100 / fileSize;
                        tv.setText(result + "%");
                        break;
                    case 2:
                        Toast.makeText(UpdateActivity.this, "恭喜你！文件下载完成", Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case -1:
                        String error = msg.getData().getString("error");
                        Toast.makeText(UpdateActivity.this, error, Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case 888:
                        uListView.setAdapter(new SPP_UpdateListViewAdapter(UpdateActivity.this, uBeanList));
                        break;
                    case 890:
                        Toast.makeText(UpdateActivity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update);
        initView();

        //tv.setText("0%");

        new Thread(networkTask).start(); //获取版本号信息的线程

        //uListView的点击事件
        uListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获得选中项的HashMap对象
                LinearLayout childAt = (LinearLayout) uListView.getChildAt(i);
                TextView viewById = (TextView) childAt.findViewById(R.id.tv_versionurl);
                downurl = (String) viewById.getText();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            //下载文件，参数：第一个URL，第二个存放路径
                            //down_file("http://www.usr.cn/Down/Instructions/USR-D2D.pdf", Environment.getExternalStorageDirectory() + File.separator + "/ceshi/");
                            if (downurl != null && !downurl.equals("")) {
                                down_file(downurl,saveUrl);
                                System.out.println(downurl + "下载地址查看");
                                System.out.println(saveUrl + "存储路径查看");
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

    }

    private void initView() {
        pb = (ProgressBar) findViewById(R.id.progressBar);
        tv = (TextView) findViewById(R.id.tv_update);
        uListView = (ListView) findViewById(R.id.listview_update);
    }


    /**
    /**
     * 获取更新信息线程
     */
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            Message message = handler.obtainMessage();
            // 在这里进行 http request.网络请求相关操作
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] data = new byte[1024];
            int len = 0;
            try {
                URL url = new URL(strUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int responseCode = conn.getResponseCode();
                InputStream inStream = conn.getInputStream();
                while ((len = inStream.read(data)) != -1) {
                    outStream.write(data, 0, len);
                }
                inStream.close();
                String josn = new String(outStream.toByteArray());
                getListPersonByArray(josn);
                message.what = 888;
                handler.sendMessage(message);

            }  catch (IOException e) {
                e.printStackTrace();
                message.what = 890;
                handler.sendMessage(message);
                System.out.println("连接失败");
            }
        }


    };


    /**
     * 文件下载
     *
     * @param url：文件的下载地址
     * @param path：文件保存到本地的地址
     * @throws IOException
     */
    public void down_file(String url, String path) throws IOException {
        //下载函数
        filename = url.substring(url.lastIndexOf("/") + 1);
        //获取文件名
        URL myURL = new URL(url);
        URLConnection conn = myURL.openConnection();
        try {
            conn.connect();
            InputStream is = conn.getInputStream();
            this.fileSize = conn.getContentLength();//根据响应获取文件大小
            if (this.fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
            if (is == null) throw new RuntimeException("stream is null");
            File file1 = new File(path);
            File file2 = new File(path + filename);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            if (!file2.exists()) {
                file2.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(path + filename);
            //把数据存入路径+文件名
            byte buf[] = new byte[1024];
            downLoadFileSize = 0;
            sendMsg(0);
            do {
                //循环读取
                int numread = is.read(buf);
                if (numread == -1) {
                    break;
                }
                fos.write(buf, 0, numread);
                downLoadFileSize += numread;

                sendMsg(1);//更新进度条
            } while (true);
            sendMsg(2);//通知下载完成
            is.close();
        } catch (Exception ex) {
            Log.e("tag", "error: " + ex.getMessage(), ex);
        }

    }


    /**
     * 解析json
     *
     * @param jsonString
     */
    public void getListPersonByArray(String jsonString) {
        uBeanList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            System.out.println("JSONArray的长度为----------？"+jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                sppupdatelv = new SppUpdateListViewItem();
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                System.out.println(jsonObject.getString("version_code"));
                System.out.println(jsonObject.getString("version_name"));
                System.out.println(jsonObject.getString("url"));
                sppupdatelv.setSpp_Version_code(jsonObject.getString("version_code"));
                sppupdatelv.setSpp_Version_name(jsonObject.getString("version_name"));
                sppupdatelv.setSpp_Version_url(jsonObject.getString("url"));
                uBeanList.add(sppupdatelv);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //在线程中向Handler发送文件的下载量，进行UI界面的更新
    private void sendMsg(int flag) {
        Message msg = new Message();
        msg.what = flag;
        handler.sendMessage(msg);
    }

    /**
     * 读取文件
     * @param strFilePath
     * @return
     */
    public String readTxtFile(String strFilePath) {
        String path = strFilePath;
        StringBuilder builder = new StringBuilder();
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        builder.append(line + "\n");
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        return builder.toString();
    }

}
