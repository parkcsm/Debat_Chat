package com.idealist.www.useopencvwithcmake;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 *
 * 현재 안쓰는 엑티비티
 *
 */

public class MainChat extends AppCompatActivity {

    public MyService mService;
    private boolean mBound;


    private EditText host, port, name, receiver;
    private Button btn;
    private Button service_start_btn, service_stop_btn;
    public static String hostv, namev, receiverv;
    private int portv;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        host = (EditText) findViewById(R.id.chat_host);
        port = (EditText) findViewById(R.id.chat_port);
        name = (EditText) findViewById(R.id.chat_name);
        receiver = (EditText) findViewById(R.id.chat_receiver);

        SharedPreferences sharedPref = getSharedPreferences("Operation", MODE_PRIVATE);
        id = sharedPref.getString("Operation", null);
        name.setText(id);



        btn = (Button) findViewById(R.id.chat_btn);
        service_start_btn = findViewById(R.id.service_start_btn);
        service_stop_btn = findViewById(R.id.service_stop_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hostv = host.getText().toString().trim();
                portv = Integer.parseInt(port.getText().toString().trim());
                namev = name.getText().toString().trim();
                receiverv = receiver.getText().toString().trim();

                Intent intent = new Intent(MainChat.this, ChatRoomList.class);
                Bundle bundle = new Bundle();
                bundle.putString("host", hostv);
                bundle.putString("name", namev);
                bundle.putInt("port", portv);
                bundle.putString("receiver", receiverv);
                intent.putExtras(bundle);
                startActivity(intent);

                Intent mintent = new Intent(MainChat.this, MyService.class);
                mintent.putExtra("hostv",hostv);
                mintent.putExtra("namev",namev);
                mintent.putExtra("portv",portv);
                startService(mintent);


            }
        });

        service_start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainChat.this, MyService.class);
                startService(intent);
            }
        });

        service_stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindService(mConnection);
                Intent intent = new Intent(MainChat.this, MyService.class);
                stopService(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        hostv = host.getText().toString().trim();
//        portv = Integer.parseInt(port.getText().toString().trim());
//        namev = name.getText().toString().trim();
//        receiverv = receiver.getText().toString().trim();
//        Intent intent = new Intent(MainChat.this, MyService.class);
//        intent.putExtra("hostv",hostv);
//        intent.putExtra("namev",namev);
//        intent.putExtra("portv",portv);
//        startService(intent);
//    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
//            unbindService(mConnection);
            mBound = false;
        }
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder binder = (MyService.MyBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 예기치 않은 종료
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("aAAAA", "Pause!!!!!!!!");
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.d("aAAAA","DESTROY!!!!!!!!");
//        if(mBound) {
//            unbindService(mConnection);
//            mBound = false;
//        }
//    }

}

