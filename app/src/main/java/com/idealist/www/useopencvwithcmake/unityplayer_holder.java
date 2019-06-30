package com.idealist.www.useopencvwithcmake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.idealist.www.cuberun_lib2.UnityPlayerActivity;

public class unityplayer_holder extends AppCompatActivity {

    boolean bool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unityplayer_holder);
        Intent intent = new Intent(unityplayer_holder.this, UnityPlayerActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(bool){
            finish();
            Log.d("aaa","aaaaa");
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
            Log.d("aaa2","aaaaa2");
        }
        bool = true; //When you put back_button in unity, This Activity will show up. but automatically will be finished.
    }
}
