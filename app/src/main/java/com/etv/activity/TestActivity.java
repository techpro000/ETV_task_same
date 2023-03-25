package com.etv.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.etv.util.serialport.SerialPort;
import com.ys.etv.R;


public class TestActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initSeriport();
        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPortMessage();
            }
        });
    }


    SerialPort mSerialPort;

    private void initSeriport() {
        if (mSerialPort == null) {
            mSerialPort = new SerialPort(this);
        }
        mSerialPort.receive(new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case SerialPort.RESULT_OK:
                        Log.e("cdl", "收到消息=" + msg.obj);
                        break;
                    case SerialPort.RESULT_ERROR:
                        Log.e("cdl", "收到消息error=" + msg.obj);
                        break;
                }
                return true;
            }
        }));
    }

    public void sendPortMessage() {
        String message = "{" + System.currentTimeMillis() + "}";
        mSerialPort.send(message, "");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mSerialPort != null) {
            mSerialPort.release();
            mSerialPort = null;
        }
    }

}
