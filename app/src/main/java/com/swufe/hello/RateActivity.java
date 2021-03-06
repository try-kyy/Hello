package com.swufe.hello;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RateActivity extends AppCompatActivity implements Runnable {
    private final String TAG = "Rate";
    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;

    EditText rmb;
    TextView show;
    Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        rmb = findViewById(R.id.rmb);
        show = findViewById(R.id.showOut);

        //获取SharedPreferences里储存的数据
        SharedPreferences SharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);//获取文件
        dollarRate = SharedPreferences.getFloat("dollar_rate", 0.0f);
        euroRate = SharedPreferences.getFloat("euro_rate", 0.0f);
        wonRate = SharedPreferences.getFloat("won_rate", 0.0f);//0.0为默认值
        Log.i(TAG, "onCreate:sp dollarRate" + dollarRate);//日志
        Log.i(TAG, "onCreate:sp euroRate" + euroRate);
        Log.i(TAG, "onCreate:sp wonRate" + wonRate);

        //开启子线程
        Thread t = new Thread(this);
        t.start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 5) {
                    String str = (String) msg.obj;
                    Log.i(TAG, "handleMessage: getMessage msg=" + str);
                    show.setText(str);
                }
                super.handleMessage(msg);
            }
        };
    }

    public void onClick(View btn) {
        String str = rmb.getText().toString();
        float r = 0;
        if (str.length() > 0) {
            r = Float.parseFloat(str);
        } else {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG, "onClick:r=" + r);

        if (btn.getId() == R.id.btn_dollar) {
            show.setText(String.format("%.2f", r * dollarRate));
        } else if (btn.getId() == R.id.btn_euro) {
            show.setText(String.format("%.2f", r * euroRate));
        } else if (btn.getId() == R.id.btn_won) {
            show.setText(String.format("%.2f", r * wonRate));
        }
    }

    //public void openOne(View btn){
    //打开一个新窗口
    //Log.i("open","openOne:");
    //Intent hello=new Intent(this,SecondActivity.class);
    //Intent web=new Intent (Intent.ACTION_VIEW, Uri.parse("http://www.jd.com"));
    //Intent intent=new Intent (Intent.ACTION_DIAL, Uri.parse("tel:13461380833"));
    //startActivity(intent);//调用方法
    //}
    public void openOne(View btn) {  //openOne 接收按钮事件，调用openConfig方法
        openConfig();
    }

    //打开方法
    private void openConfig() {
        Intent config = new Intent(this, ConfigActivity.class);
        config.putExtra("dollar_rate_key", dollarRate);
        config.putExtra("euro_rate_key", euroRate);
        config.putExtra("won_rate_key", wonRate);
        Log.i(TAG, "openOne: dollarRate=" + dollarRate);
        Log.i(TAG, "openOne: euroRate=" + euroRate);
        Log.i(TAG, "openOne: wonRate=" + wonRate);

        //startActivity(config);
        startActivityForResult(config, 1);//调用方法
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 2) {
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar", 0.1f); //0.1从bundle中获取数据的默认值
            euroRate = bundle.getFloat("key_euro", 0.1f);
            wonRate = bundle.getFloat("key_won", 0.1f);
            Log.i(TAG, "onActivityResult: dollarRate" + dollarRate);
            Log.i(TAG, "onActivityResult: euroRate" + euroRate);
            Log.i(TAG, "onActivityResult: wonRate" + wonRate);

            //将新设置的汇率写到SP里,myrate是配置文件，保存和获取的文件要一样
            SharedPreferences SharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = SharedPreferences.edit();
            editor.putFloat("dollar_rate", dollarRate);
            editor.putFloat("euro_rate", euroRate);
            editor.putFloat("won_rate", wonRate);
            editor.commit();
            Log.i(TAG, "onActivityResult: 数据已保存到SharedPreferences");

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_set) {
            openConfig();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void run() {
        Log.i(TAG, "run:run()......");
        for (int i = 1; i < 3; i++) {
            Log.i(TAG, "run:i=" + i);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //获取massage对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        //msg.what=5;
        msg.obj = "Hello from run()";
        handler.sendMessage(msg);

        //获取网络数据
        URL url = null;
        try {
            url = new URL("http://quote.eastmoney.com/center/gridlist.html#forex_exchange_icbc");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            InputStream in = http.getInputStream();
            String html = inputStream2String(in);
            Log.i(TAG, "run:html" + html);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
                out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}


